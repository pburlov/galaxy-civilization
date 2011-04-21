/*
 * $Id: ColonyCenterUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony.ColonyCenter;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;

public class ColonyCenterUI extends DefaultBuildingUI<ColonyCenter>
{

	private JLabel labelProduction = new JLabel();
	private JLabel labelMining = new JLabel();
	private JLabel labelScience = new JLabel();
	private JLabel labelLivingSpace = new JLabel();
	private JLabel labelFarming = new JLabel();
	private JLabel labelMaterialStored = new JLabel();

	public ColonyCenterUI(ColonyCenter building)
	{
		super(building);
		addSliderPanel(1);
		addField("Production:", labelProduction);
		addField("Bergbau:", labelMining);
		addField("Forschung:", labelScience);
		addField("Wohnraum:", labelLivingSpace);
		addField("Nahrungsproduktion: ", labelFarming);
		if(building.getMaterialStorage().getStorageData().size() > 0)
			addField("Materialvorrat: ", labelMaterialStored);
	}

	@Override
	protected void refreshValuesIntern(ColonyCenter building)
	{
		labelProduction.setText(GUI.formatSmartDouble(building.getProduction()));
		labelMining.setText(GUI.formatSmartDouble(building.getMining()));
		labelScience.setText(GUI.formatSmartDouble(building.getScience()));
		labelLivingSpace.setText(GUI.formatLong(building.getLivingSpace()));
		labelFarming.setText(GUI.formatLong(building.getFarming()));
		if(building.getMaterialStorage().getStorageData().size() > 0)
		{
			labelMaterialStored.setText(GUI.formatSmartDouble(building.getMaterialStorage().computeTotalStored()/1E6)+"mio T");
			labelMaterialStored.setToolTipText(building.getMaterialStorage().getMaterialStoredHTMLInfo());
		}
	}
}
