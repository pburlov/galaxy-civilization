/*
 * $Id: MOUDateLabel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import javax.swing.JLabel;
import mou.ClockEvent;
import mou.ClockListener;
import mou.Main;

/**
 * Element zum Anzeigen der MOU-Zeit
 * 
 * @author pb
 */
public class MOUDateLabel extends JLabel
		implements ClockListener
{

	/**
	 * 
	 */
	public MOUDateLabel()
	{
		Main.instance().getClockGenerator().addClockListener(this);
	}

	public void showDate(long date)
	{
		setText(GUI.formatDate(date));
	}

	public void dailyEvent(ClockEvent event)
	{
		showDate(event.getTime());
	}

	public void yearlyEvent(ClockEvent event)
	{
	}
}
