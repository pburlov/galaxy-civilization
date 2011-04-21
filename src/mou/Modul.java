/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
abstract public class Modul
		implements Shutdownable
{

	private Subsystem parent;
	private boolean shutdown = false;
	private Preferences preferences;
	private boolean started = false;

	public Modul(Subsystem parent)
	{
		setParent(parent);
		preferences = new Preferences(getPreferencesFile());
	}

	public boolean istStarted()
	{
		return started;
	}

	public Logger getLogger()
	{
		if(parent != null)
		{
			Logger ret = parent.getLogger();
			if(ret != null) return ret;
		}
		return Logger.getLogger("");
	}

	public boolean isShutdown()
	{
		return shutdown;
	}

	public void setParent(Subsystem parent)
	{
		if(parent == null) return;
		parent.registerModul(this);
		this.parent = parent;
	}

	public Subsystem getParent()
	{
		return parent;
	}

	public Preferences getPreferences()
	{
		return preferences;
	}

	public void shutdown()
	{
		if(isShutdown()) return;
		try
		{
			if(istStarted())
			{
				getLogger().info("Fahre Modul herunter: " + getModulName());
				/*
				 * Nur dann shutdown Methode aufrufen, wenn dieser Modul schon tatsächlich gestartet
				 * wurde
				 */
				shutdownIntern();
				preferences.savePreferences();
			}
		} catch(Throwable th)
		{
			getLogger().log(Level.SEVERE, "Fehler beim Runterfahren des Moduls " + getModulName() + " : " + th.getLocalizedMessage(), th);
		}
		shutdown = true;
		started = false;
	}

	/**
	 * @return Volle Path zum Subsystem mit allen Eltern-Subsystemen z.B "MOU.GUI.Starmap"
	 */
	public String getFullPath()
	{
		if(parent == null) return getModulName();
		return parent.getFullPath() + "." + getModulName();
	}

	public void logThrowable(String meldung, Throwable th)
	{
		getLogger().log(Level.SEVERE, meldung, th);
		// th.printStackTrace();
	}

	public void startModul() throws Exception
	{
		getLogger().info("starte Modul: " + getModulName());
		startModulIntern();
		started = true;
	}
	
	protected void logException(Throwable th)
	{
		getLogger().warning(th.getLocalizedMessage());
		getLogger().throwing(getModulName(), "", th);
	}

	/**
	 * @return Names des Moduls z.B "GUI"
	 */
	public abstract String getModulName();

	abstract protected void shutdownIntern();

	abstract protected File getPreferencesFile();

	/**
	 * Startet Modul. Hier sollen alle Initialisierungsarbeiten durchgeführt werden. Nur die
	 * unbedingt notwendigen Sache dürfen im Konstruktor erledigt werden
	 */
	abstract protected void startModulIntern() throws Exception;
}