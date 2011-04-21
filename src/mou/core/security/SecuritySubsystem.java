/*
 * $Id: SecuritySubsystem.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.security;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.logging.Level;
import mou.Main;
import mou.Subsystem;

/**
 * @author pb
 */
public class SecuritySubsystem extends Subsystem
{

	static private SecuritySubsystem instance = null;
	private MessageDigest md;
	private long serial;
	private String password = "";

	/**
	 * @param parent
	 */
	public SecuritySubsystem(Subsystem parent)
	{
		super(parent);
		instance = this;
	}

	static public SecuritySubsystem instance()
	{
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Subsystem#getDefaultLoggerLevel()
	 */
	protected Level getDefaultLoggerLevel()
	{
		return Level.ALL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#getModulName()
	 */
	public String getModulName()
	{
		return "SecuritySubsystem";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#shutdownIntern()
	 */
	protected void shutdownIntern()
	{
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
//		md = MessageDigest.getInstance("SHA-1");
//		LoginDialog loginDialog = new LoginDialog();
//		if(!loginDialog.showDialog())
//		{
//			Main.instance().forceExit("Login abgebrochen");
//		}
//		password = loginDialog.getPassword();
//		serial = loginDialog.getSerialNumber();
	}

	public long getSerialNumber()
	{
		return serial;
	}

	public String getPassword()
	{
		return password;
	}

	/**
	 * Generiert mit Hilfe von Hashes einen Long Wert. Dien in erste Linie dazu aus für Menschen
	 * leichter einprägsamer Benutzernamen den ClientID zu berechnen.
	 * 
	 * @param material
	 * @return
	 */
	synchronized long generateSerialNumber(String material)
	{
		md.reset();
		md.update(material.getBytes());
		byte[] digest = md.digest();
		return new BigInteger(digest).longValue();
	}
}
