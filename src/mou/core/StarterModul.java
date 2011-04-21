/*
 * $Id: StarterModul.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core;

import java.io.File;
import java.util.TimerTask;
import mou.Main;
import mou.Modul;
import mou.Subsystem;


/**
 * Dieser Modul führt die Operationen durch die nach dem Start aller übrigen
 * Modulen und Subsystemen durchgeführt werden sollen.
 * @author pb
 */
public class StarterModul extends Modul
{
	private TimerTask saveDbTask;
	/**
	 * @param parent
	 */
	public StarterModul(Subsystem parent)
	{
		super(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#getModulName()
	 */
	public String getModulName()
	{
		return "StarterModul";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#shutdownIntern()
	 */
	protected void shutdownIntern()
	{
		saveDbTask.cancel();
		Main.instance().getClockGenerator().stop();
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
		saveDbTask = new TimerTask()
		{
			@Override
			public void run()
			{
				Main.instance().getExecutorService().execute(new Runnable()
				{
				
					public void run()
					{
						Main.instance().getMOUDB().saveData();
					}
				});
			}
		};
		/*
		 * Timer zum regelmaessigen Speicher der Spielstandes starten
		 */
		Main.instance().getGlobalTimer().schedule(saveDbTask, 1000 * 60 *60, 1000 * 60 *60);
		/*
		 * Den ClockGenerator starten
		 */
		Main.instance().getClockGenerator().start();
	}
	
}
