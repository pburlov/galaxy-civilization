/*
 * $Id: ResearchCenter.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony.ResearchCenter;

import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.gui.GUI;
import mou.storage.ser.ID;

public class ResearchCenter extends BuildingAbstract
{

	public ResearchCenter()
	{
		super();
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
		col.addBruttoResearch(computeResearchPoints());
	}

	@Override
	protected BuildingUiAbstract getBuildingUiIntern()
	{
		return new ResearchCenterUI(this);
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		return "<b>Forschung: </b>" + GUI.formatSmartDouble(computeResearchPoints());
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new ResearchCenterScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Forschungslabor";
	}

	@Override
	public String getShortDescription()
	{
		return "Generiert Forschungspunkte";
	}

	public ID getID()
	{
		return ID_BUILDING_RESEARCH_CENTER;
	}

	public double getNormalizedResearchPoints()
	{
		return computeCustomValue(1);
	}

	public double computeResearchPoints()
	{
		return getNormalizedResearchPoints() * getSize().doubleValue() * getKPD();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/forschung.png";
	}
}
