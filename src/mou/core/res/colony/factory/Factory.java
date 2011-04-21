/*
 * $Id: Factory.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.factory;

import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.gui.GUI;
import mou.storage.ser.ID;

public class Factory extends BuildingAbstract
{

	public Factory()
	{
		super();
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
		col.addBruttoProduction(getProduction());
	}

	protected @Override
	BuildingUiAbstract getBuildingUiIntern()
	{
		return new FactoryUI(this);
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		return "<b>Produktion: </b>" + GUI.formatSmartDouble(getProduction());
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new FactoryScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Industriekomplex";
	}

	@Override
	public String getShortDescription()
	{
		return "";
	}

	public ID getID()
	{
		return ID_BUILDING_FACTORY;
	}

	public double getNormalizedProduction()
	{
		return computeCustomValue(1);
	}

	public double getProduction()
	{
		return getNormalizedProduction() * getSize().doubleValue() * getKPD();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/industrie.png";
	}
}
