/*
 * $Id: MineScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony.mine;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;

public class MineScienceView extends DefaultBuildingScienceView
{

	public MineScienceView(Mine res)
	{
		super(res);
		removeComponent(ENERGIEBALANCE);
		JLabel label = new JLabel(GUI.formatLong(res.getMining()));
		addComponent("Bergbau:", label, "T");
	}
}
