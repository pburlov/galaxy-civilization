/*
 * $Id: CivDayInfoPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import mou.Main;
import mou.core.civilization.CivDayReport;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;

/**
 * @author pb
 */
public class CivDayInfoPanel extends JPanel
{

	private JLabel labelIncomeBrutto = new JLabel();
	private JLabel labelSupportCostShips = new JLabel();
	private JLabel labelSupportCostBuildings = new JLabel();
	private JLabel labelIncomeNetto = new JLabel();
	private JLabel labelResearch = new JLabel();
	// private JLabel labelGeldreserve = new JLabel();
	private JLabel labelMining = new JLabel();
	private JLabel labelProduction = new JLabel();
	private JLabel labelFarming = new JLabel();
	private JLabel labelBSP = new JLabel();
	private JLabel labelBspPerCapita = new JLabel();

	/**
	 * 
	 */
	public CivDayInfoPanel()
	{
		super();
		setLayout(new TableLayout(2, "NW"));
		JLabel label = new JLabel("Einkommen (Brutto): ");
		add(label);
		add(labelIncomeBrutto);
		label = new JLabel("Unterhaltskosten (Schiffe): ");
		add(label);
		add(labelSupportCostShips);
		label = new JLabel("Unterhaltskosten (Gebäude): ");
		add(label);
		add(labelSupportCostBuildings);
		label = new JLabel("Einkommen (Netto): ");
		add(label);
		add(labelIncomeNetto);
		add("CS=2", Box.createVerticalStrut(10));
		label = new JLabel("BSP: ");
		add(label);
		add(labelBSP);
		label = new JLabel("BSP pro Kopf: ");
		add(label);
		add(labelBspPerCapita);
		label = new JLabel("Produktion: ");
		add(label);
		add(labelProduction);
		label = new JLabel("Bergbau: ");
		add(label);
		add(labelMining);
		label = new JLabel("Forschung: ");
		add(label);
		add(labelResearch);
		label = new JLabel("Nahrungsproduktion: ");
		add(label);
		add(labelFarming);
		new Timer(1000, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				showCivDayInfo(Main.instance().getMOUDB().getCivilizationDB().getCivDayReport());
			}
		}).start();
	}

	protected void showCivDayInfo(CivDayReport info)
	{
		labelIncomeBrutto.setText(GUI.formatSmartDouble(info.getBruttoIncome()));
		labelSupportCostShips.setText(GUI.formatSmartDouble(info.getSupportCostShips()));
		labelSupportCostBuildings.setText(GUI.formatSmartDouble(info.getSupportCostBuildings()));
		labelIncomeNetto.setText(GUI.formatSmartDouble(info.computeNettoIncome()));
		if(info.computeNettoIncome() < 0)
		{
			labelIncomeNetto.setForeground(Color.RED);
		} else
			labelIncomeNetto.setForeground(Color.BLACK);
		labelProduction.setText(GUI.formatSmartDouble(info.getProduction()));
		labelMining.setText(GUI.formatSmartDouble(info.getMining()));
		labelResearch.setText(GUI.formatSmartDouble(info.getSciencePoints()));
		labelFarming.setText(GUI.formatSmartDouble(info.getFarming()));
		labelBSP.setText(GUI.formatSmartDouble(info.getBSP()));
		labelBspPerCapita.setText(GUI.formatSmartDouble(info.getBspPerCapita()));
	}
}
