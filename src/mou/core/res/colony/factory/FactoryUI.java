/*
 * $Id: FactoryUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.factory;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;

public class FactoryUI extends DefaultBuildingUI<Factory>
{

	private JLabel labelProduction = new JLabel();

	// private Factory fac;
	public FactoryUI(Factory building)
	{
		super(building);
		addSliderPanel(1);
		// fac = building;
		addField("Produktion: ", labelProduction);
	}

	@Override
	protected void refreshValuesIntern(Factory building)
	{
		labelProduction.setText(GUI.formatSmartDouble(building.getProduction()));
	}
}
