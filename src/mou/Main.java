/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import mou.ai.AI;
import mou.core.ClockGenerator;
import mou.core.MOUDB;
import mou.core.StarterModul;
import mou.core.security.SecuritySubsystem;
import mou.gui.GUI;
import mou.net2.NetSubsystem;
import org.apache.commons.lang.SystemUtils;
import burlov.net.UrlCommunicator;
import burlov.swing.ErrorDialog;

public class Main extends Subsystem
{

	static final public Number ZERO_NUMBER = 0;
	static final public int VERSION = 16;// Aktuell 15
	static final public String APPLICATION_NAME = "GalaxyCivilization";
	static final private long CHECK_VERSION_INTERVAL = 3 * 60 * 60 * 1000;
	// static final public String GAME_SERVER_URL =
	// "http://tux-server.homenetwork:8080/galaxy-civilization.de/system/";
	static final public String GAME_SERVER_URL = "http://galaxy-civilization.de/system/";
	/*
	 * Zeigt auf Verzeichnis wo benutzerabhängige Daten und Einstellungen gespeichert werden sollen
	 */
	static public String APPLICATION_USER_DATA_DIR;
	static public String LOG_DIR;
	static private Main instance;
	static private DefaultLogger defaultLogger;
	private MOUDB moudb; // Referenz zu DB-Subsystem
	private GUI gui; // Referenz zu Grafik-Subsystem
	private NetSubsystem mNetSubsystem;
	private ClockGenerator clockGenerator;
	private SecuritySubsystem securitySubsystem;
	private AI ai;
	private Timer globalTimer = new Timer();
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

	/**
	 * Bei true erlaubt cheats und einige Hilfsinformationen
	 * 
	 * @return
	 */
	static public boolean isDebugMode()
	{// DEBUG: Für public Release auf false setzen
		return true;
	}

	static public boolean isOnlineMode()
	{// DEBUG
		if(!isDebugMode()) return true;
		return false;
	}

	public Main() throws Exception
	{
		super(null);
		APPLICATION_USER_DATA_DIR = System.getProperty("user.home") + File.separator + APPLICATION_NAME + File.separator;
		LOG_DIR = APPLICATION_USER_DATA_DIR + File.separator + "log" + File.separator;
		defaultLogger = new DefaultLogger(new File(LOG_DIR), "default.log", Level.ALL);
		testJREVersion();
		getLogger().setLevel(Level.FINE);
		getLogger().info(new Date().toString() + " Starting Main...");
		instance = this;
		securitySubsystem = new SecuritySubsystem(this);
		clockGenerator = new ClockGenerator(this);
		moudb = new MOUDB(this);
		mNetSubsystem = new NetSubsystem(this);
		ai = new AI(this);
		// GUI als letzte Instanz starten, weil GUI keine Core-Funktionalitäten bereitstellt
		// und nur Funktionalitäten von anderen Modulen verwendet
		gui = new GUI(this);
		new StarterModul(this);
	}

	protected void startModulIntern() throws Exception
	{
		if(isOnlineMode())
		{
			/*
			 * als Allererste nach aktuellen Version prüfen
			 */
			UrlCommunicator com = new UrlCommunicator(GAME_SERVER_URL + "check-version.php");
			Map<String, String> param = new HashMap<String, String>(1);
			param.put("version", Integer.toString(VERSION));
			com.sendCommand(param);
			List<String> ret = com.getReplyValues();
			if(ret.size() > 0)
			{
				int opt = JOptionPane.showConfirmDialog(null, ret.get(0), "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if(opt == JOptionPane.YES_OPTION) forceExit("Start durch den Benutzer abgebrochen");
			}
			/*
			 * Versionsprüfung jede 8. Stunde durchführen
			 */
			globalTimer.schedule(new TimerTask()
			{

				public void run()
				{
					/*
					 * als Allererste nach aktuellen Version prüfen
					 */
					UrlCommunicator com = new UrlCommunicator(GAME_SERVER_URL + "check-version.php");
					Map<String, String> param = new HashMap<String, String>(1);
					param.put("version", Integer.toString(VERSION));
					try
					{
						com.sendCommand(param);
					} catch(IOException e)
					{
						getLogger().log(Level.WARNING, "Versionsprüfung ist fehlgeschlagen", e);
					}
					List<String> ret = com.getReplyValues();
					if(ret.size() > 0)
					{
						int opt = JOptionPane.showConfirmDialog(null, ret.get(0), "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
						if(opt == JOptionPane.YES_OPTION) shutdown();
					}
				}
			}, CHECK_VERSION_INTERVAL, CHECK_VERSION_INTERVAL);
		}
	}

	static public Main instance()
	{
		// if(instance != null) return instance;
		// instance = new Main();
		return instance;
	}

	public java.util.Timer getGlobalTimer()
	{
		return globalTimer;
	}

	public AI getAI()
	{
		return ai;
	}

	public ClockGenerator getClockGenerator()
	{
		return clockGenerator;
	}

	public MOUDB getMOUDB()
	{
		if(moudb == null) throw new IllegalStateException("DB wurde noch nicht gestartet");
		return moudb;
	}

	public GUI getGUI()
	{
		if(gui == null) throw new IllegalStateException("GUI wurde noch nicht gestartet");
		return gui;
	}

	public NetSubsystem getNetSubsystem()
	{
		return mNetSubsystem;
	}

	public File getPreferencesFile()
	{
		return null;
	}

	public String getModulName()
	{
		return "Main";
	}

	/**
	 * Methode gibt die synchronisierte Zeit im Spiel-Universum zurück.
	 */
	public long getTime()
	{
		return getClockGenerator().getMouTimeSek();
	}

	/**
	 * @return Logger der für alle Fälle da ist für die keine andere Logger da sind
	 */
	static public Logger getDefaultLogger()
	{
		return defaultLogger;
	}

	public Long getClientSerNumber()
	{
		if(SecuritySubsystem.instance() == null) return null;
		return SecuritySubsystem.instance().getSerialNumber();
	}

	private void testJREVersion()
	{
		if(!SystemUtils.isJavaVersionAtLeast(1.5f))
		{
			JOptionPane.showMessageDialog(null, "Dieses Programm benoetigt Java-Runtime ab Version 1.5.\n" + "Auf Ihrem Rechner wird zur Zeit die Version: "
					+ System.getProperty("java.version") + " verwendet.", "Falsche JRE-Version", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		// try
		// {
		// Class cl = Class.forName("java.util.logging.Logger");
		// }catch(ClassNotFoundException e)
		// {
		// JOptionPane.showMessageDialog(null,"Dieses Programm benoetigt Java-Runtime ab Version
		// 1.5.\n"+
		// "Auf Ihrem Rechner wird zur Zeit die Version: "+System.getProperty("java.version")+"
		// verwendet.","Falsche JRE-Version", JOptionPane.ERROR_MESSAGE);
		// System.exit(0);
		// }
	}

	protected void shutdownIntern()
	{
		getLogger().info("Fahre das Spiel herunter.");
		executorService.shutdown();
		getLogger().info("Alle Systeme erfolgreich runtergefahren. Beende das Programm. Ciao. :-)");
		System.exit(0);
	}

	protected Level getDefaultLoggerLevel()
	{
		return Level.ALL;
	}

	static public void start()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{

				public void run()
				{
					try
					{
						Thread.currentThread().setUncaughtExceptionHandler(new MOUUncaughtExceptionHandler());
						new Main().startModul();
					} catch(Throwable e)
					{
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, e.getClass().getName() + "\n" + e.getLocalizedMessage(), "", JOptionPane.ERROR_MESSAGE);
						System.exit(-1);
					}
				}
			});
		} catch(Throwable e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}
	
	public void restart() throws Exception
	{
		//GUI, Datenbank, Clockgenerator und AI müssen neu gestartet werden, SecurityManager und Logger können weiterlaufen
		gui.shutdown();
		gui.getMainFrame().setVisible(false);
		ai.shutdown();
		mNetSubsystem.shutdown();
		moudb.shutdown();
		clockGenerator.shutdown();

		clockGenerator = new ClockGenerator(this);
		moudb = new MOUDB(this);
		mNetSubsystem = new NetSubsystem(this);
		ai = new AI(this);
		gui = new GUI(this);

		clockGenerator.startModul();
		moudb.startModul();
		mNetSubsystem.startModul();
		ai.startModul();
		gui.startModul();
		clockGenerator.start();
	}

	/**
	 * Diese MEthode wird aufgerufen wenn ein schweres Fehler auftritt welche die Aufmerksamkeit des
	 * Benutzers erfordert.
	 * 
	 * @param cause
	 * @param comment
	 *            HTML-formatiertes Text, aber ohne den anleitenden und abschliessenden html-Tags
	 * @param exit
	 *            Wenn true dann wid das Spiel nach einer Fehlermeldung beendet, wenn false dann
	 *            wird nur eine Fehlermeldung ausgegeben.
	 */
	public void severeErrorOccured(Throwable cause, String comment, boolean exit)
	{
		/*
		 * Bereits evet. vorhandene <html> Tags entfernen
		 */
		comment.replaceAll("html>", "");
		comment.replaceAll("HTML>", "");
		getLogger().log(Level.SEVERE, comment, cause);
		// FileCompressor compressor = new FileCompressor();
		// try
		// {
		// compressor.compress(LOG_DIR, APPLICATION_USER_DATA_DIR+"error-log.zip");
		// } catch(IOException e1)
		// {
		// getLogger().log(Level.SEVERE, "Kann die Logdateien nicht zippen", cause);
		// }
		String msg = "<html><font color=red>Schwerer Programmfehler:</font> " + comment;
		if(exit) msg += "<br>Das Programm wird heruntergefahren.";
		msg += "<br>Bitte helfen Sie diesen Fehler zu finden und zu beseitigen indem Sie"
				+ "<br>eine EMail mit der kurzen Beschreibung der Fehlersituation und den angehängten"
				+ "<br>gezippten log Verzeichnis an feedback@galaxy-civilization.de schicken."
				+ "<br>Den log Verzeichnis finden Sie in Ihrem Benutzerverzeichnis im Unterverzeichnis"
				+ "<br>GalaxyCivilization. Unter Windows lautet der Dateipfad etwa so:"
				+ "<br><bold>C:\\Dokumente und Einstellungen\\{Benutzername}\\GalaxyCivilization\\log</bold></html>";
		ErrorDialog dlg = new ErrorDialog(null, cause, msg, null);
		dlg.setTitle("Programmfehler");
		GUI.centreWindow(null, dlg);
		dlg.setVisible(true);
		// getLogger().severe(msg);
		// cause.printStackTrace();
		if(exit) shutdown();
	}

	/**
	 * Methode wird aufgerufen wenn das Programm sofort zu beende ist. Also ohne den normalen Weg
	 * über shutdownIntern()
	 * 
	 * @param cause
	 */
	public void forceExit(String cause)
	{
		getLogger().severe("Beende das Programm. Ursache: " + cause);
		System.exit(-1);
	}

	/**
	 * Fuehrt zeitversetzt das Runnable-Objekt mit Executor Service aus.
	 * Methode blockiert nicht und returned sofort.
	 * @param run
	 * @param delay
	 */
	public void executeDelayed(final Runnable run, long delay)
	{
		executorService.schedule(run, delay, TimeUnit.MILLISECONDS);
//		getGlobalTimer().schedule(new TimerTask()
//		{
//		
//			@Override
//			public void run()
//			{
//				getExecutorService().execute(run);
//			}
//		}, delay);
	}
	
	public ScheduledExecutorService getExecutorService()
	{
		return executorService;
	}

	
	public SecuritySubsystem getSecuritySubsystem()
	{
		return securitySubsystem;
	}
}