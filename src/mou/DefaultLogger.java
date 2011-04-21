/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Klasse übernimmt alle Log-Aufgaben die zu keinem anderen Logger zugeordnet werden können
 */
public class DefaultLogger extends Logger
{

	public DefaultLogger(File dir, String name, Level level)
	{
		super(name, null);
		try
		{
			if(!dir.exists()) dir.mkdirs();
			Handler handler = new FileHandler(dir.getAbsolutePath() + "/" + name, 200000, 1, false);
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(level);
			addHandler(handler);
		} catch(Throwable th)
		{
			Logger.getLogger("").log(Level.SEVERE, "Initialisation von Default-Logger ist fehlgeschlagen", th);
			throw new RuntimeException();
		}
	}
}