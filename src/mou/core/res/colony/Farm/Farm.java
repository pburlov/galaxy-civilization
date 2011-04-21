/*
 * $Id: Farm.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.Farm;

import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.gui.GUI;
import mou.storage.ser.ID;

public class Farm extends BuildingAbstract
{

	//static final double FARMING_FAKTOR = 1000;
	/* Faktoren zum beeinfluﬂen des Wachstumsverhaltens des Geb‰udes */
	static final private double FACT_A = 100;
	static final private double FACT_B = 20;
	static final private double INITIAL_FARMING = 50000;

	public Farm()
	{
		super();
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
		col.addBruttoFarming(computeFarming());
	}

	@Override
	protected BuildingUiAbstract getBuildingUiIntern()
	{
		return new FarmUI(this);
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		return "<b>Nahrungsproduktion: </b>" + GUI.formatLong(computeFarming());
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new FarmScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Nahrungsreplikator";
	}

	@Override
	public String getShortDescription()
	{
		return "Produziert Lebensmittel";
	}

	public ID getID()
	{
		return ID_BUILDING_FARM;
	}
	
	@Override
	public double computeCustomValue(int number)
	{
		int Stufe = getMaterialien().size();
		double ret;
		
		//Wachstumsformel
		ret = INITIAL_FARMING * (Stufe * Math.log(Stufe) /(1+ FACT_A* Math.exp(-Stufe))*FACT_B+1);
		//Zuf‰lliger faktor
		ret *= getQualityFaktor().doubleValue()/4;
		
		return ret;
	}
	
	@Override
	public int getCustomCounter()
	{
		return (int) computeCustomValue(1)/1000;
	}
	
	public double getNormalizedFarming()
	{
		return computeCustomValue(1);
	}

	public double computeFarming()
	{
		return getNormalizedFarming() * getSize().doubleValue() * getKPD();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/food.png";
	}
}
