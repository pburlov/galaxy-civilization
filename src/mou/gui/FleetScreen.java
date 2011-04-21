/*
 * $Id: FleetScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import mou.gui.fleetscreen.ShipsOverviewPanel;

public class FleetScreen extends JPanel
{

	private ShipsOverviewPanel shipsOverviewPanel = new ShipsOverviewPanel();

	public FleetScreen()
	{
		this.setLayout(new BorderLayout());
		add(shipsOverviewPanel, BorderLayout.CENTER);
	}
}