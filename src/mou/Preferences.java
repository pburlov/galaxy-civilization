/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasse zum Verwalten von Programmeinstellungen
 */
public class Preferences
{

	private Properties data = new Properties();
	private File file;
	private boolean changed = false;

	public Preferences(File file)
	{
		try
		{
			this.file = file;
			if(file == null) return;
			File parent = file.getParentFile();
			if(parent != null) parent.mkdirs();
			if(!file.exists()) return;// if(!file.createNewFile()) throw new
										// RuntimeException("Kann die Preference-Datei
										// ("+file.getAbsolutePath()+") nicht erstellen.");
			InputStream in = new FileInputStream(file);
			data.load(in);
			in.close();
		} catch(Exception e)
		{
			Logger.getLogger("").log(Level.SEVERE, "Kann die Preferences nicht instanzieren.", e);
		}
	}

	public String getProperty(String name, String default_value)
	{
		String value = data.getProperty(name);
		if(value == null && default_value != null)
		{
			value = default_value;
			setProperty(name, default_value);
		}
		return value;
	}

	public Long getAsLong(String name, Long default_value)
	{
		String value = null;
		if(default_value != null)
			value = getProperty(name, default_value.toString());
		else
			value = data.getProperty(name);
		if(value == null) return default_value;
		Long ret = default_value;
		try
		{
			ret = new Long(value);
		} catch(NumberFormatException e)
		{
			Logger.getLogger("").severe(
					"Die Property '" + name + "' enthält ungültigen Wert '" + value + ". Überschreibe ihn mit dem Dafault-Wert '" + default_value.toString()
							+ ".");
		}
		return ret;
	}

	public Integer getAsInteger(String name, Integer default_value)
	{
		String value = null;
		if(default_value != null)
			value = getProperty(name, default_value.toString());
		else
			value = data.getProperty(name);
		if(value == null) return default_value;
		Integer ret = null;
		try
		{
			ret = new Integer(value);
		} catch(NumberFormatException e)
		{
			Logger.getLogger("").severe(
					"Die Property '" + name + "' enthält ungültigen Wert '" + value + ". Überschreibe ihn mit dem Dafault-Wert '" + default_value.toString()
							+ ".");
			ret = default_value;
		}
		return ret;
	}

	/**
	 * Die neugesetzte Wert wird sofort in einer Datei gespeichert. Also nicht zu oft aufrufen
	 */
	public Object setProperty(String name, String value)
	{
		changed = true;
		Object ret = data.setProperty(name, value);
		return ret;
	}

	public void savePreferences() throws IOException
	{
		if(!changed) return;
		if(file == null) throw new IllegalStateException("file-Variable in Preferences ist null");
		if(!file.exists()) if(!file.createNewFile()) { throw new IOException("Kann die Preference-Datei (" + file.getAbsolutePath() + ") nicht erstellen."); }
		OutputStream out = new FileOutputStream(file);
		data.store(out, "");
		out.flush();
		out.close();
		changed = false;
	}
}
