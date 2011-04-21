/*
 * $Id: StarsystemInfoPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import mou.core.starmap.StarSystem;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;

public class StarsystemInfoPanel extends JPanel
{

	private JList listRessources = new JList();
	private JLabel labelMaxPopulation = new JLabel();
	private JLabel labelProduction = new JLabel();
	private JLabel labelFarming = new JLabel();
	private JLabel labelMining = new JLabel();
	private JLabel labelPopulationGrow = new JLabel();
	private JLabel labelScience = new JLabel();

	public StarsystemInfoPanel()
	{
		setBorder(new TitledBorder("Sternsystem"));
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new TableLayout(2, "NW"));
		add(panel, BorderLayout.NORTH);
		panel.add(new JLabel("Max. Bevölkerung: "));
		panel.add(labelMaxPopulation);
		panel.add(new JLabel("Bevölkerungswachstum: "));
		panel.add(labelPopulationGrow);
		panel.add(new JLabel("Landwirtschaft: "));
		panel.add(labelFarming);
		panel.add(new JLabel("Production: "));
		panel.add(labelProduction);
		panel.add(new JLabel("Bergbau: "));
		panel.add(labelMining);
		panel.add(new JLabel("Forschung"));
		panel.add(labelScience);
		JScrollPane scroll = new JScrollPane();
		// scroll.setMinimumSize(new Dimension(1000, 200));
		add(scroll, BorderLayout.CENTER);
		scroll.setViewportView(listRessources);
		scroll.setBorder(new TitledBorder("Natürlichen Ressourcen"));
		listRessources.setOpaque(false);
		listRessources.setBackground(getBackground());
	}

	public void showStar(StarSystem star)
	{
		labelMaxPopulation.setText(GUI.formatLong(star.getMaxPopulation()));
		labelPopulationGrow.setText(star.getPopulationGrowBonusString());
		labelFarming.setText(star.getFarmingFaktorString());
		labelProduction.setText(star.getProductionFaktorString());
		labelMining.setText(star.getMiningFaktorString());
		labelScience.setText(star.getScienceFaktorString());
		listRessources.setListData(star.getNatRessources().toArray());
	}
}
