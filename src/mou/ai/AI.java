/*
 * $Id: AI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.ai;

import java.io.File;
import java.util.logging.Level;
import mou.Subsystem;

/**
 * AI Subsystem (Artificial Intelligence) enthält alle Module die irgendwie für automatisierte
 * Vorgänge stehen.
 * 
 * @author pb
 */
public class AI extends Subsystem
{

	private ShipMovementManager mShipMovementManager;

	/**
	 * @param parent
	 */
	public AI(Subsystem parent)
	{
		super(parent);
		mShipMovementManager = new ShipMovementManager(this);
		// new CivilizationDeveloper(this);
	}

	public ShipMovementManager getShipMovementManager()
	{
		return mShipMovementManager;
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
		return "AI";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#shutdownIntern()
	 */
	protected void shutdownIntern()
	{
		// TODO Auto-generated method stub
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
	}
}
