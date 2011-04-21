/*
 * $Id: ShipsOverviewPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.fleetscreen;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import mou.Main;
import mou.core.ship.Ship;
import mou.core.ship.ShipDB;
import mou.core.trade.TraderDB;
import mou.gui.GUI;
import mou.gui.MainFrame;
import mou.gui.fleetscreen.shiptable.ShipTable;
import mou.gui.starmapscreen.StarmapScreen;

/**
 * Klasse zeigt Raumschiffe eigener Zivilisation
 */
public class ShipsOverviewPanel extends JPanel
{

	protected ShipTable shipTable = new ShipTable(true);
	protected ShipDB shipDB = Main.instance().getMOUDB().getShipDB();

	public ShipsOverviewPanel()
	{
		shipTable.setComponentPopupMenu(new ContextMenu());
		setLayout(new BorderLayout());
		add(shipTable, BorderLayout.CENTER);
		// new Timer(1000, new ActionListener()
		// {
		//
		// public void actionPerformed(ActionEvent e)
		// {
		// if(!isShowing())return;
		// List selectedShips = shipTable.getSelectedShips();
		// List ships = new ArrayList(shipDB.getAllShips().values());
		// shipTable.showShips(ships);
		// shipTable.selectShips(selectedShips);
		// }
		// }).start();
	}

	private class ContextMenu extends JPopupMenu
	{

		JMenuItem itemShowOnMap = new JMenuItem("Zeige auf der Karte");
		JMenuItem itemSelectTarget = new JMenuItem("Flügziel wählen");
		JMenuItem itemScrap = new JMenuItem("Schiff(e) verschrotten");
		JMenuItem itemSell = new JMenuItem("Schiff(e) verkaufen");

		// ShipTable table;
		public ContextMenu()
		{
			// this.table = tb;
			itemShowOnMap.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					setVisible(false);
					List ships = shipTable.getSelectedShips();
					if(ships.isEmpty()) return;
					Ship ship = (Ship) ships.get(0);
					Point point = ship.computeApproxMapPosition();
					if(point == null) return;
					StarmapScreen scr = Main.instance().getGUI().getMainFrame().getStarmapScreen();
					scr.centerPosition(point);
					scr.goTo(point);
					Main.instance().getGUI().getMainFrame().selectScreen(MainFrame.SCREEN_STARMAP);
				}
			});
			add(itemShowOnMap);
			itemSelectTarget.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					Main.instance().getGUI().getMainFrame().getStarmapScreen().switchToTargetSelectMode(shipTable.getSelectedShips());
					Main.instance().getGUI().getMainFrame().selectScreen(MainFrame.SCREEN_STARMAP);
				}
			});
			add(itemSelectTarget);
			add(itemScrap);
			itemScrap.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					setVisible(false);
					Collection ships = shipTable.getSelectedShips();
					if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(shipTable, ships.size() + " Schiff(e) verschrotten?", "Verschrottung bestätigen",
							JOptionPane.OK_CANCEL_OPTION)) return;
					for(Iterator iter = ships.iterator(); iter.hasNext();)
					{
						Ship ship = (Ship) iter.next();
						ship.scrapShip();
					}
					Main.instance().getGUI().promtMessage("Schiffe verschrottet", ships.size() + " Schiff(e) " + " verschrottet", GUI.MSG_PRIORITY_NORMAL);
				}
			});
			add(itemSell);
			itemSell.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					setVisible(false);
					List<Ship> ships = shipTable.getSelectedShips();
					TraderDB db = Main.instance().getMOUDB().getTraderDB();
					if(JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(shipTable, ships.size() + " Schiff(e) zum Verkauf anbieten?",
							"Verkauf bestätigen", JOptionPane.OK_CANCEL_OPTION)) return;
					db.sellShips(ships);
				}
			});
		}
	}
}