/*
 * $Id: FactoryScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res.colony.factory;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;

public class FactoryScienceView extends DefaultBuildingScienceView
{

	public FactoryScienceView(Factory res)
	{
		super(res);
		removeComponent(ENERGIEBALANCE);
		addComponent("Produktion: ", new JLabel(GUI.formatSmartDouble(res.getProduction())), "");
	}
}
