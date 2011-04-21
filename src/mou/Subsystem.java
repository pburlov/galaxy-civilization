/*
 * $Id: Subsystem.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

abstract public class Subsystem extends Modul
{

	private ShutdownableManager shutdownableManager = new ShutdownableManager();
	private List children = new ArrayList();
	private Logger logger;

	public Subsystem(Subsystem parent)
	{
		super(parent);
		// getLogger();
	}

	public void shutdown()
	{
		if(isShutdown()) return;
		shutdownableManager.shutdown(); // Zuerst alle registrierten Modulen reunterfahren
		super.shutdown();
	}

	/**
	 * Diese Methode ruft nacheinander startMedul() Methoden der registrierten Modulen inklusive
	 * sich selbst
	 */
	public void startModul() throws Exception
	{
		super.startModul();
		Iterator iter = children.iterator();
		while(iter.hasNext())
		{
			((Modul) iter.next()).startModul();
		}
	}

	/**
	 * Module werden nur regisitriert, gestartet werden sie mit dem Aufruf der Methode startModul()
	 */
	public void registerModul(Modul child)
	{
		children.add(child);
		shutdownableManager.addShutdownable(child);
	}

	public void unregisterModul(Modul modul)
	{
		children.remove(modul);
		shutdownableManager.removeShutdownable(modul);
	}

	public Logger getLogger()
	{
		if(logger != null) return logger;
		try
		{
			logger = Logger.getLogger(getFullPath());
			File logdir = new File(Main.APPLICATION_USER_DATA_DIR, "log");
			if(!logdir.exists()) logdir.mkdirs();
			Handler handler = new FileHandler(new File(logdir, getFullPath() + ".log").getAbsolutePath(), 200000, 1, false);
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(getDefaultLoggerLevel());
			logger.addHandler(handler);
			logger.setLevel(getDefaultLoggerLevel());
		} catch(Throwable th)
		{
			Logger.getLogger("").log(Level.SEVERE, "Initialisation von Logger für das Subsystem '" + getFullPath() + "' ist fehlgeschlagen", th);
			throw new RuntimeException();
		}
		return logger;
	}

	/**
	 * @return Level mit dem Logger für diese Subsystem initialisiert werden soll
	 */
	abstract protected Level getDefaultLoggerLevel();
}