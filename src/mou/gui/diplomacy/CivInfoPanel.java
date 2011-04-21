/*
 * $Id: CivInfoPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy;

import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mou.core.civilization.Civilization;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;

/**
 * @author pb
 */
public class CivInfoPanel extends JPanel
{

	private JLabel labelName = new JLabel();
	private JLabel labelCreateDate = new JLabel();
	private JLabel labelPopulation = new JLabel();
	private JLabel labelColonyCount = new JLabel();
	private JLabel labelShipCount = new JLabel();
	private JLabel labelMoney = new JLabel();
	private JLabel labelDiplStatus = new JLabel();
	private JLabel labelDataTime = new JLabel();
	private JLabel labelBsp = new JLabel();
	private JLabel labelBspPerCapita = new JLabel();

	public CivInfoPanel()
	{
		LayoutManager layout = new TableLayout(2, "W");
		setLayout(layout);
		add(new JLabel("Name: "));
		add(labelName);
		add(new JLabel("Gegründet: "));
		add(labelCreateDate);
		add(new JLabel("Bevölkerung: "));
		add(labelPopulation);
		add(new JLabel("BSP: "));
		add(labelBsp);
		add(new JLabel("BSP pro Kopf: "));
		add(labelBspPerCapita);
		add(new JLabel("Kolonien: "));
		add(labelColonyCount);
		add(new JLabel("Schiffe: "));
		add(labelShipCount);
		add(new JLabel("Geld: "));
		add(labelMoney);
		add(new JLabel("Status: "));
		add(labelDiplStatus);
		add(new JLabel("Datenalter: "));
		add(labelDataTime);
	}

	public void showCiv(Civilization civ)
	{
		labelName.setText(civ.getName());
		labelCreateDate.setText(GUI.formatDate(civ.getFoundationTime()));
		labelPopulation.setText(GUI.formatLong(civ.getBevolkerung()));
		labelColonyCount.setText(GUI.formatLong(civ.getKolonienAnzahl()));
		labelShipCount.setText(GUI.formatLong(civ.getSchiffsanzahl()));
		labelMoney.setText(GUI.formatLong(civ.getMoney()));
		labelDiplStatus.setText(civ.getCivStatusString());
		labelDataTime.setText(GUI.formatDate(civ.getDatenalter()));
		labelBsp.setText(GUI.formatSmartDouble(civ.getBSP()));
		labelBspPerCapita.setText(GUI.formatSmartDouble((civ.getBSP().doubleValue() / civ.getBevolkerung().doubleValue())));
	}
}
