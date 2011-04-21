/*
 * $Id: ColonyCenterScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.ColonyCenter;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;

public class ColonyCenterScienceView extends DefaultBuildingScienceView
{

	public ColonyCenterScienceView(ColonyCenter building)
	{
		super(building);
		removeComponent(ENERGIEBALANCE);
		JLabel labelProduction = new JLabel();
		JLabel labelMining = new JLabel();
		JLabel labelScience = new JLabel();
		JLabel labelLivingSpace = new JLabel();
		JLabel labelFarming = new JLabel();
		addComponent("Production:", labelProduction, "");
		addComponent("Bergbau:", labelMining, "");
		addComponent("Forschung:", labelScience, "");
		addComponent("Wohnraum:", labelLivingSpace, "");
		addComponent("Farming:", labelFarming, "");
		labelProduction.setText(GUI.formatDouble(building.getProduction()));
		labelMining.setText(GUI.formatDouble(building.getMining()));
		labelScience.setText(GUI.formatDouble(building.getScience()));
		labelLivingSpace.setText(GUI.formatLong(building.getLivingSpace()));
		labelFarming.setText(GUI.formatSmartDouble(building.getFarming()));
	}
}
