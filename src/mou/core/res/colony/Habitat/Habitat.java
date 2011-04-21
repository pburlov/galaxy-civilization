/*
 * $Id: Habitat.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.Habitat;

import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.gui.GUI;
import mou.storage.ser.ID;

public class Habitat extends BuildingAbstract
{

	//static final private double LIVING_SPACE_FAKTOR = 1000;
	/* Faktoren zum beeinflußen des Wachstumsverhaltens des Gebäudes */
	static final private double FACT_A = 1000;
	static final private double FACT_B = 200;
	static final private double INITIAL_LIVING_SPACE = 50000;

	public Habitat()
	{
		super();
	}

	// @Override
	// public Number getNormalizedBuildCost()
	// {
	// /*
	// * Je hoher entwickelt, desto kleiner die Baukosten
	// */
	// return new Double(1d / super.getNormalizedBuildCost().doubleValue());
	// }
	//
	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
		col.setLivingSpace(col.getLivingSpace() + computeLivingSpace());
	}

	@Override
	protected BuildingUiAbstract getBuildingUiIntern()
	{
		return new HabitatUI(this);
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		return "<b>Wohnraum: </b>" + GUI.formatSmartDouble(computeLivingSpace());
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new HabitatScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Habitat";
	}

	@Override
	public String getShortDescription()
	{
		return "Bietet Wohnraum für die Kolonisten";
	}

	public ID getID()
	{
		return ID_BUILDING_HABITAT;
	}

	@Override
	public double computeCustomValue(int number)
	{
		int stufe = getMaterialien().size();
		double ret;
		
		//Wachstumsformel
		ret = INITIAL_LIVING_SPACE * (Math.log(stufe) /(1+ FACT_A* Math.exp(-stufe))*FACT_B+1);
		//Zufälliger faktor
		ret *= getQualityFaktor().doubleValue()/4;
		
		return ret;
	}
	
	@Override
	public int getCustomCounter()
	{
		return (int) computeCustomValue(1)/1000;
	}
	
	/**
	 * Pro Tonne Masse wird immer ein Koloniset untergebracht. Die Entwicklung des Habitats bewegt
	 * sich immer iin Richtung weniger Baukosten.
	 */
	public double getNormalizedLivingSpace()
	{
		return computeCustomValue(1);
	}
	
	@Override
	public double getMaxSize()
	{
		return getColony().getMaxBevoelkerung()/computeLivingSpace();
	}

	public double computeLivingSpace()
	{
		return getNormalizedLivingSpace() * getSize().doubleValue() * getKPD();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/habitat.png";
	}

	/**
	 * Wohnungen brauchen keine Arbeiter
	 */
	@Override
	public double computeNormalizedCrew()
	{
		return DOUBLE_0;
	}

	/**
	 * Wohnungen verursachen keine Unterhaltskosten.
	 */
	@Override
	public double computeNormalizedSupportCost()
	{
		return DOUBLE_0;
	}
}
