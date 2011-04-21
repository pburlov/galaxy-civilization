/*
 * $Id: ColonyInfoPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.core.colony.Colony;
import mou.gui.GUI;
import mou.gui.civilizationscreen.InfluenceFaktorHtmlRenderer;
import mou.gui.layout.TableLayout;
import mou.storage.ser.ID;

/**
 * Zeig alle Kolonieeigenschaften
 * 
 * @author pbu
 */
public class ColonyInfoPanel extends JPanel
{

	// private ID currentColony;//ID der Kolonie, die gerade angezeigt wird;
	private JLabel labelName = new JLabel();
	private JLabel labelCiv = new JLabel();
	private JLabel labelPopulation = new JLabel();
	private JLabel labelLivingSpace = new JLabel();
	private JLabel labelPopulationGrow = new JLabel();
	private JLabel labelProduction = new JLabel();
	private JLabel labelFarming = new JLabel();
	private JLabel labelFood = new JLabel();
	private JLabel labelFoodStored = new JLabel();
	private JLabel labelMaterialStored = new JLabel();
	private JLabel labelIncomeNetto = new JLabel();
	private JLabel labelIncomeBrutto = new JLabel();
	private JLabel labelMining = new JLabel();
	private JLabel labelSupportCost = new JLabel();
	private JLabel labelUnemployed = new JLabel();
	private JLabel labelScience = new JLabel();
	private JLabel labelMoral = new JLabel();
	private JButton buttonAbandonColony = new JButton("Kolonie aufgeben");
	
	private Colony mKolonie = null;

	public ColonyInfoPanel()
	{
		setBorder(new TitledBorder("Kolonie"));
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new TableLayout(2, "NW"));
		add(panel, BorderLayout.NORTH);
		panel.add("NW,CS=2", labelName);
		panel.add(new JLabel("Bevölkerung: "));
		panel.add(labelPopulation);
		panel.add(new JLabel("Zufriedenheit: "));
		panel.add(labelMoral);
		panel.add(new JLabel("Bevölkerungswachstum: "));
		panel.add(labelPopulationGrow);
		panel.add(new JLabel("Arbeitslos: "));
		panel.add(labelUnemployed);
		panel.add(new JLabel("Wohnraum für: "));
		panel.add(labelLivingSpace);
		panel.add(new JLabel("Nahrungsproduktion: "));
		panel.add(labelFarming);
		panel.add(new JLabel("Lebensmittelversorgung: "));
		panel.add(labelFood);
		panel.add(new JLabel("Lebensmittelvorräte: "));
		panel.add(labelFoodStored);
		panel.add(new JLabel("Materialvorrat: "));
		panel.add(labelMaterialStored);
		// panel.add(new JLabel("Einkommen (Netto): "));
		// panel.add(labelIncomeNetto);
		//		
		// panel.add(new JLabel("Einkommen (Brutto): "));
		// panel.add(labelIncomeBrutto);
		//		
		// panel.add(new JLabel("Unterhaltskosten: "));
		// panel.add(labelSupportCost);
		//		
		panel.add(new JLabel("Prodution: "));
		panel.add(labelProduction);
		panel.add(new JLabel("Bergbau: "));
		panel.add(labelMining);
		panel.add(new JLabel("Forschung: "));
		panel.add(labelScience);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.PAGE_END);
		buttonPanel.add(buttonAbandonColony);
		buttonAbandonColony.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(mKolonie!=null)
				{
					int option = JOptionPane.showConfirmDialog(Main.instance().getGUI().getColonyDialog(),
							"Wollen Sie die Kolonie auf dem Planeten: " + mKolonie.getName() + " wirklich aufgeben?", "Achtung!",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(option != JOptionPane.OK_OPTION) return;

					Main.instance().getMOUDB().getKolonieDB().removeKolonie(mKolonie.getID());
				}
			}
		});

	}

	/**
	 * Zeigt eine Kolonie mit der gewünschter ID. Es kann eigen als auch fremde Kolonie sein.
	 * 
	 * @param kolonieID
	 */
	public void showColony(ID kolonieID)
	{
		// currentColony = kolonieID;
		if(kolonieID == null)
		{
			// reset();
			return;
		}
		Colony kolonie = Main.instance().getMOUDB().getKolonieDB().getKolonie(kolonieID);
		showKolonie(kolonie);
	}

	/**
	 * Zeigt eigene Kolonie
	 * 
	 * @param kol
	 */
	private void showKolonie(Colony kolonie)
	{
		if(kolonie == null)
		{
			// reset();
			return;
		}
		mKolonie = kolonie;
		labelCiv.setText(kolonie.getCivName());
		String name = kolonie.getName();
		if(kolonie.isRebelled())
		{
			name = "<html>" + name + "<font color=red> (rebelliert)</font></html>";
		}
		labelName.setText(name);
		labelIncomeBrutto.setText(GUI.formatLong(kolonie.getBruttoIncome()));
		labelSupportCost.setText(GUI.formatLong(kolonie.getSupportCost()));
		double income = kolonie.getIncomeBalance();
		if(income < 0)
			labelIncomeNetto.setForeground(Color.RED);
		else
			labelIncomeNetto.setForeground(Color.BLACK);
		labelIncomeNetto.setText(GUI.formatLong(income));
		labelPopulation.setText(GUI.formatLong(kolonie.getPopulation().longValue()));
		double grow = kolonie.getDayPopulationGrowPercent();
		if(grow < 0)
			labelPopulationGrow.setForeground(Color.RED);
		else
			labelPopulationGrow.setForeground(Color.BLACK);
		labelPopulationGrow.setText(GUI.formatProzentSigned(grow) + " p.a.");
		labelPopulationGrow.setToolTipText(InfluenceFaktorHtmlRenderer.renderSignedPercent(kolonie.getGrowFactors()));
		labelProduction.setText(GUI.formatLong((long) kolonie.getProduction()));
		labelMining.setText(GUI.formatLong((long) kolonie.getMiningPoints()));
		labelLivingSpace.setText(GUI.formatLong(kolonie.getLivingSpace()));
		labelUnemployed.setText(GUI.formatLong(kolonie.getUnemployed()));
		labelScience.setText(GUI.formatLong(kolonie.getSciencePoints()));
		labelFarming.setText(GUI.formatLong(kolonie.getFarming()));
		double food = kolonie.getFoodSupplyFactor();
		if(food < 1)
			labelFood.setForeground(Color.RED);
		else
			labelFood.setForeground(Color.BLACK);
		labelFood.setText(GUI.formatProzent(food * 100));
		double foodStorageTime = kolonie.computeFoodStorageTime();
		labelFoodStored.setText(GUI.formatDouble(foodStorageTime, 1) + " Tage");
		labelFoodStored.setToolTipText(GUI.formatSmartDouble(kolonie.computeFoodInStorage()/1E6) +" mio T");
		double val = kolonie.getMoral() * 100;
		if(val < 0)
			labelMoral.setForeground(Color.RED);
		else
			labelMoral.setForeground(Color.BLACK);
		labelMoral.setText(GUI.formatProzentSigned(val));
		labelMoral.setToolTipText(InfluenceFaktorHtmlRenderer.renderSignedPercent(kolonie.getAllMoralFactors()));
		if(kolonie.getNaturalResourcesSize() > 0)
		{
			labelMaterialStored.setText(GUI.formatSmartDouble(kolonie.computeTotalMaterialInStorage()/1E6)+" mio T");
			labelMaterialStored.setToolTipText(kolonie.getMaterialStoredHTMLInfo());
		}else
		{
			labelMaterialStored.setText("-");
			labelMaterialStored.setToolTipText("");
		}
			
	}
	/**
	 * Setzt alle Textfelder auf leere Werte
	 */
	/*
	 * public void reset() { currentColony = null; }
	 */
}
