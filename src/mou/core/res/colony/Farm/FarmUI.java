/*
 * $Id: FarmUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.Farm;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;

public class FarmUI extends DefaultBuildingUI<Farm>
{

	JLabel label = new JLabel();

	public FarmUI(Farm b)
	{
		super(b);
		addSliderPanel(1);
		addField("Nahrungsproduktion: ", label);
	}

	@Override
	protected void refreshValuesIntern(Farm b)
	{
		label.setText(GUI.formatLong(b.computeFarming()));
	}
}
