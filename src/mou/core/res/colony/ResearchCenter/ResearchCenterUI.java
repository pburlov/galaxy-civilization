/*
 * $Id: ResearchCenterUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony.ResearchCenter;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;

public class ResearchCenterUI extends DefaultBuildingUI<ResearchCenter>
{

	private JLabel labelResearch = new JLabel();

	public ResearchCenterUI(ResearchCenter building)
	{
		super(building);
		addSliderPanel(1);
		addField("Forschung:", labelResearch);
	}

	@Override
	protected void refreshValuesIntern(ResearchCenter b)
	{
		labelResearch.setText(GUI.formatSmartDouble(b.computeResearchPoints()));
	}
}
