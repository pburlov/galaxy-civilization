/*
 * $Id: Mine.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.mine;

import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.gui.GUI;
import mou.storage.ser.ID;

public class Mine extends BuildingAbstract
{
	static final private double INITIAL_MINING = 100;
	static final private double FACT_A = 100;
	static final private double FACT_B = 20;
	

	public Mine()
	{
		super();
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
		col.addBruttoMining(getMining());
	}

	protected @Override
	BuildingUiAbstract getBuildingUiIntern()
	{
		return new MineUI(this);
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new MineScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Bergbaukomplex";
	}

	@Override
	public String getShortDescription()
	{
		return "";
	}

	public ID getID()
	{
		return ID_BUILDING_HARVESTER;
	}

	@Override
	public double computeCustomValue(int number)
	{
		double stufe = getMaterialien().size();
		double ret = INITIAL_MINING * (Math.log(stufe) /(1+ FACT_A* Math.exp(-stufe))*FACT_B+1);
		ret *= getQualityFaktor().doubleValue()/4;
		
		return ret;
	}
	
	public double getNormalizedMining()
	{
		return computeCustomValue(1);
	}

	/**
	 * Liefert die Endleistung der Mine, KPD einberechnet
	 * 
	 * @return
	 */
	public double getMining()
	{
		return getNormalizedMining() * getSize().doubleValue() * getKPD();
	}
	
	@Override
	public double getMaxSize()
	{
		if(getColony().getNaturalResourcesSize() <= 0)	return 0;	//Kolonie hat kein Bergbau
		return super.getMaxSize();
	}
	
	@Override
	public String getNoBuildMessage()
	{
		if(getColony().getNaturalResourcesSize() <= 0)
			return "Keine Rohstoffe zum Abbau vorhanden";
		return super.getNoBuildMessage();
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		String ret = "<b>Bergbau: </b>" + GUI.formatSmartDouble(getMining());
		return ret;
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/bergbau.png";
	}
}
