/*
 * $Id: MineUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.mine;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;

public class MineUI extends DefaultBuildingUI<Mine>
{

	private JLabel labelMining = new JLabel();

	public MineUI(Mine building)
	{
		super(building);
		addSliderPanel(1);
		addField("Bergbau:", labelMining);
	}

	@Override
	protected void refreshValuesIntern(Mine building)
	{
		double val = building.getMining();
		String str = GUI.formatLong(val);
		labelMining.setText(str);
	}
}
