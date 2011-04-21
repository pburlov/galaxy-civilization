/*
 * $Id: ResearchCenterScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.ResearchCenter;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

public class ResearchCenterScienceView extends DefaultResearchableScienceView
{

	public ResearchCenterScienceView(ResearchCenter res)
	{
		super(res);
		removeComponent(ENERGIEBALANCE);
		addComponent("Forschung: ", new JLabel(GUI.formatSmartDouble(res.computeResearchPoints())), "");
	}
}
