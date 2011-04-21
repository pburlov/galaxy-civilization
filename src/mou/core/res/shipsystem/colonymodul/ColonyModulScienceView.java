/*
 * $Id: ColonyModulScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem.colonymodul;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

public class ColonyModulScienceView extends DefaultResearchableScienceView
{

	public ColonyModulScienceView(ColonyModul res)
	{
		super(res);
		removeComponent(CREW);
		addComponent("Kolonisten", new JLabel(GUI.formatSmartDouble(res.computeKolonistenzahl())), "");
	}
}
