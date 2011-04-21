/*
 * $Id: ShipTableDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.starmapscreen;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import mou.Main;
import mou.core.ship.Ship;
import mou.gui.GUI;
import mou.gui.MainFrame;
import mou.gui.fleetscreen.shiptable.ShipTable;

/**
 * @author pb
 */
public class ShipTableDialog extends JDialog
{

	private ShipTable shipTable = new ShipTable(false);
	private boolean firstTime = true;

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public ShipTableDialog(Frame owner) throws HeadlessException
	{
		super(owner, true);
		/*
		 * Keine statische PopupMenu installieren, sondern einen MouseListener. Wenn meherere
		 * Schiffe wurden ausgewählt, dann eine allgemeine PopupMenu. Wenn nur ein Schiff, dann
		 * werden Schiffsspezifische Actions zu PopupMenu hinzugefügt.
		 */
		shipTable.setComponentPopupMenu(new ContextMenu());
		// shipTable.addMouseListener(new MouseAdapter()
		// {
		//
		// public void mouseClicked(MouseEvent e)
		// {
		// if(!SwingUtilities.isRightMouseButton(e))return;
		// ContextMenu popup = new ContextMenu();
		// List<Ship> selected = shipTable.getSelectedShips();
		// if(selected.size() == 0)return;
		// if(selected.size() == 1)
		// {
		// /*
		// * Schiffspezifische PopupMenu konstruieren
		// */
		// for(Action act : selected.get(0).getActions())
		// {
		// popup.add(act);
		// }
		//					
		// }else
		// {
		// /*
		// * Es wurden mehrere Schiffe ausgewählt, also nur
		// * ein allgemeine PopupMenu zeigen
		// */
		// }
		// popup.show(e.getComponent(), e.getX(), e.getY());
		// }
		// });
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(shipTable, BorderLayout.CENTER);
		// setAlwaysOnTop(isAlwaysOnTop());
		shipTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		shipTable.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		pack();
	}

	public void showShipsAtPosition(Point pos)
	{
		if(firstTime)
		{
			/*
			 * Beim ersten Mal Dialog zentrieren
			 */
			GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		}
		shipTable.showShipsAtPosition(pos);
	}

	private class ContextMenu extends JPopupMenu
	{

		JMenuItem itemSelectTarget = new JMenuItem("Flügziel wählen");

		public ContextMenu()
		{
			itemSelectTarget.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					Main.instance().getGUI().getMainFrame().getStarmapScreen().switchToTargetSelectMode(shipTable.getSelectedShips());
					Main.instance().getGUI().getMainFrame().selectScreen(MainFrame.SCREEN_STARMAP);
				}
			});
			// add(itemSelectTarget);
		}

		public void setVisible(boolean b)
		{
			if(!b)
			{
				/*
				 * 
				 */
				super.setVisible(b);
				removeAll();
				return;
			}
			add(itemSelectTarget);
			/*
			 * Actionen von selektierten Schiffe hinzufügen
			 */
			List<Ship> selected = shipTable.getSelectedShips();
			if(selected.size() == 0) return;
			if(selected.size() == 1)
			{
				/*
				 * Schiffspezifische PopupMenu konstruieren
				 */
				for(Action act : selected.get(0).getActions())
				{
					add(act);
				}
			}
			super.setVisible(b);
		}
	}
}
