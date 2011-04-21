/*
 * $Id: FarmScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony.Farm;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;

public class FarmScienceView extends DefaultBuildingScienceView
{

	public FarmScienceView(Farm res)
	{
		super(res);
		JLabel label = new JLabel();
		label.setText(GUI.formatLong(res.computeFarming()));
		addComponent("Nahrungsproduktion: ", label, "");
	}
}
