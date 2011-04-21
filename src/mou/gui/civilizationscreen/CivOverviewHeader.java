/*
 * $Id: CivOverviewHeader.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.core.colony.ColonyDB;
import mou.core.ship.ShipDB;
import mou.gui.GUI;

/**
 * GUI-Element zu zusammengefasster Darstellung der wichtigsten Civilizationsdaten (z.B. Latium
 * Menge) die immer in der Statusleiste sichtbar werden.
 * 
 * @author PB
 */
public class CivOverviewHeader extends JPanel
		implements ActionListener
{

	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();
	private ColonyDB kolonieDB = Main.instance().getMOUDB().getKolonieDB();
	private ShipDB shipDB = Main.instance().getMOUDB().getShipDB();
	private JLabel labelGeld = new JLabel();
	private JLabel labelColonies = new JLabel();
	private JLabel labelPopulation = new JLabel();
	private JLabel labelShips = new JLabel();
	private JLabel labelFood = new JLabel();
	private JLabel labelMoral = new JLabel();
	private double money = 0;

	/**
	 * 
	 */
	public CivOverviewHeader()
	{
		add(new JLabel("Geld: "));
		add(labelGeld);
		add(Box.createHorizontalStrut(10));
		add(new JLabel("Kolonien: "));
		add(labelColonies);
		add(Box.createHorizontalStrut(10));
		add(new JLabel("Bevölkerung: "));
		add(labelPopulation);
		add(Box.createHorizontalStrut(10));
		add(new JLabel("Lebensmittel: "));
		add(labelFood);
		add(Box.createHorizontalStrut(10));
		add(new JLabel("Schiffe: "));
		add(labelShips);
		add(Box.createHorizontalStrut(10));
		add(new JLabel("Zufriedenheit: "));
		add(labelMoral);
		new Timer(1000, this).start();
		// :CHEAT: Geldmenge manipulieren
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK), "Cheat_money");
		getActionMap().put("Cheat_money", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(!Main.isDebugMode()) return;
				String res = JOptionPane.showInputDialog(null, "Geld ", civDB.getMoney());
				try
				{
					civDB.setMoney(Long.parseLong(res));
				} catch(Exception ex)
				{
				}
			}
		});
	}

	/**
	 * Aktualisiert die Anzeige mit neuen daten
	 */
	private void refresh()
	{
		double newMoney = civDB.getMoney();
		if(money < newMoney)
			labelGeld.setForeground(Color.BLACK);
		else
			labelGeld.setForeground(Color.RED);
		money = newMoney;
		labelGeld.setText(GUI.formatLong(money));
		String cols = Integer.toString(kolonieDB.getColoniesCount());
		if(kolonieDB.getRebelledColoniesCount() > 0)
		{
			cols = "<html>" + cols + "<font color=red>(" + Integer.toString(kolonieDB.getRebelledColoniesCount()) + ")</font></html>";
		}
		labelColonies.setText(cols);
		labelShips.setText(GUI.formatLong(shipDB.getDBSize()));
		labelPopulation.setText(GUI.formatLong(kolonieDB.computePopulation()));
		double food = civDB.getCivDayReport().getFoodBalance();
		if(food < 0)
			labelFood.setForeground(Color.RED);
		else
			labelFood.setForeground(Color.BLACK);
		labelFood.setText(GUI.formatLong(food));
		double val = civDB.getCivDayReport().getMoralFactor() * 100;
		if(val < 0)
			labelMoral.setForeground(Color.RED);
		else
			labelMoral.setForeground(Color.BLACK);
		labelMoral.setText(GUI.formatProzentSigned(val));
		labelMoral.setToolTipText(InfluenceFaktorHtmlRenderer.renderSignedPercent(Main.instance().getMOUDB().getCivilizationDB().getMoralFactors()));
		setMaximumSize(getPreferredSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		refresh();
	}
}
