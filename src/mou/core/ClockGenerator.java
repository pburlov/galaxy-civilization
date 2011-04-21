/*
 * $Id: ClockGenerator.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import javax.swing.Timer;
import mou.ClockEvent;
import mou.ClockListener;
import mou.Main;
import mou.Modul;
import mou.RFC868Time;
import mou.Subsystem;
import mou.core.civilization.Civilization;

/**
 * Der zentrale Taktgeber f?r das ganze Spiel. Beim starten wird sich der Taktgeber mit einem
 * Zeitserver im Internet synchronisieren
 * 
 * @author pbu
 */
public class ClockGenerator extends Modul
{

	// static final int SNTP_PORT = 123;
	// static final int DAYTIME_PORT = 13;
	static final long SYNC_PERIODE = 1000 * 60 * 60;// Zeitperiode in dem die Zeitsynchronization
													// erneut durchgef?hrt wird
	static final long ZERO_TIME = 3275660900000L; // Zeitpunkt Zero im Spiel in Millisekunden in
													// RFC868 Zeitkoordinaten
	static final public int PRIMARY_CLOCK_INTERVAL = 1000;// Millis f?r die prim?re Taktfrequenz
	static final public int SECONDARY_TO_PRIMARY_RATIO = 1000; // Anzahl der prim?ren Ticks in
																// einem sekund?ren Tick
	static final public Integer PRIMARY_CLOCK = new Integer(0);
	static final public Integer SECONDARY_CLOCK = new Integer(1);
	private List<ClockListener> clocklisteners = new ArrayList<ClockListener>();
	private Timer primaryTimer;
	// private Timer secondaryTimer;
	private java.util.Timer supportTimer = new java.util.Timer();
	private RFC868Time netTimeClient;
	private boolean paused = true;
	/*
	 * Zwei Variable die die absolute Netzzeit mit der lokaler Zeit verbinden. Beide Werte
	 * entsprechen einem und denselben Zeitpunkt, nur halt in zwei Zeitsystemen.
	 */
	volatile private long netTime = 0; // in Sekunden
	volatile private long localTime = 0;// in Sekunden
	volatile private long delta = 0;

	public ClockGenerator(Subsystem parent)
	{
		super(parent);
	}

	/**
	 * Methode abfragt die Netzzeit und belegt die member Variablen netTime und localTime
	 * 
	 * @throws Exception
	 */
	final void synchronizeTime() throws Exception
	{
		try
		{
			Main.instance().getLogger().info("Abfrage Zeitserver");
			long[] times = null;
			if(Main.isOnlineMode())
				times = netTimeClient.getTime();
			else
				times = new long[] { System.currentTimeMillis(), System.currentTimeMillis()};
			localTime = times[0];
			netTime = times[1];
			delta = netTime - localTime;
			// localTime = netTime = System.currentTimeMillis() / 1000;
			return;
		} catch(Exception e)
		{
			Main.instance().severeErrorOccured(e,
					"Kann die lokale Zeit nicht mit der Netzzeit synchronisieren.\n" + "Bitte pr?fen Sie ob Ihr Rechner mit dem Internet verbunden ist.", true);
		}
	}

	/**
	 * Methode liefert global g?ltige MOU-Zeit in Sekunden
	 * 
	 * @return
	 */
	public long getMouTimeSek()
	{
		long time = System.currentTimeMillis() + delta;
		time = (time - ZERO_TIME) / 1000L;// Gesamtzeit seit Zeit-ZERO
		return time;
	}

	// synchronized public long getSNTPTime()
	// {
	// return ((System.currentTimeMillis() / 1000)- localTime)+netTime;
	// }
	/**
	 * Registriert ClockListener für den Spielzeitgeber
	 * 
	 * @param listener
	 */
	synchronized public void addClockListener(ClockListener listenerl)
	{
		clocklisteners.add(listenerl);
	}

	/**
	 * Startet das Versenden von Zeitereignissen
	 */
	public void start()
	{
		primaryTimer.start();
		paused = false;
	}

	/**
	 * Unterbriecht das Versenden von Zeitereignissen
	 */
	public void stop()
	{
		paused = true;
		primaryTimer.stop();
	}

	synchronized final void fireDailyClockEvent(long time)
	{
		if(paused) return;
		ClockEvent event = new ClockEvent(time);
		for(ClockListener listener : clocklisteners)
			listener.dailyEvent(event);
	}

	synchronized final void fireYearlyClockEvent(long time)
	{
		if(paused) return;
		if(Main.isDebugMode()) System.out.println("########### ClockGenerator: Secondary ClockEvent " + time);
		ClockEvent event = new ClockEvent(time);
		for(ClockListener listener : clocklisteners)
			listener.yearlyEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#getModulName()
	 */
	public String getModulName()
	{
		return "Clock Generator";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#shutdownIntern()
	 */
	protected void shutdownIntern()
	{
		stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#getPreferencesFile()
	 */
	protected File getPreferencesFile()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#startModulIntern()
	 */
	protected void startModulIntern() throws Exception
	{
		netTimeClient = new RFC868Time();
		synchronizeTime();
		supportTimer.schedule(new TimerTask()
		{

			public void run()
			{
				try
				{
					synchronizeTime();
				} catch(final Exception e)
				{
					Main.instance().severeErrorOccured(e, "Fehler bei Zeitsynchronization:" + e.getLocalizedMessage(), true);
				}
			}
		}, SYNC_PERIODE, SYNC_PERIODE);
		primaryTimer = new Timer(PRIMARY_CLOCK_INTERVAL, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if(paused) return;
					long time = getMouTimeSek();
					fireDailyClockEvent(time);
					/*
					 * Damit nicht alle Zivilisationen ihre Daten gleichzeitig ?bers Netz
					 * auktualisieren, wird f?r jede Zivilisation der Zeitpunkt ihrer Entstehung als
					 * Nullpunk in der Berechnung der Sekund?ren Time-Events genommen.
					 */
					Civilization myCiv = Main.instance().getMOUDB().getCivilizationDB().getMyCivilization();
					long createdTime = 0;
					if(myCiv != null) createdTime = myCiv.getFoundationTime();
					if(((time + createdTime) % SECONDARY_TO_PRIMARY_RATIO) == 0) fireYearlyClockEvent(time);
				} catch(Exception ex)
				{
					Main.instance().severeErrorOccured(ex, "Fehler:" + ex.getLocalizedMessage(), false);
				}
			}
		});
		/*
		 * Gestartet wird von einem Extramodul. So wird es sichergestellt, dass ClockEvents nur ganz
		 * am Ender der Startphase beginnen zu feuern.
		 */
		// start();
	}
}
