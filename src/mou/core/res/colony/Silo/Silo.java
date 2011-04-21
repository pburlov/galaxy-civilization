/*
 * $Id$
 * Created on 01.04.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.Silo;

import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.ResourceAbstract;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.core.research.ResearchableDesign;
import mou.gui.GUI;
import mou.storage.ser.ID;


/**
 * @author Dominik
 *
 */
public class Silo extends BuildingAbstract
{
	static final String ATTR_STORED="ATTR_STORED";
	
	/* Faktoren zum beeinflußen des Wachstumsverhaltens des Gebäudes */
	static final private double FACT_A = 400;
	static final private double FACT_B = 100;
	static final private double INITIAL_STORAGE_TIME = 50;
	static final private double INITIAL_STORAGE_SIZE = 50E6;
	
	public Silo()
	{
		super();
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
	}

	@Override
	protected BuildingUiAbstract getBuildingUiIntern()
	{
		return new SiloUI(this);
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		return "<b>Lagergröße: </b>" + GUI.formatLong(computeCapacity()/1E6)+"mio T"
				+ "<br><b>Maximale Lagerdauer: </b>"+ GUI.formatLong(computeCustomValue(2))+"Tage";
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new SiloScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Getreidesilo";
	}

	@Override
	public String getShortDescription()
	{
		return "Ein Lager für Lebensmittel";
	}

	public ID getID()
	{
		return ID_BUILDING_SILO;
	}
	
	@Override
	public void cleanUp()
	{
		super.cleanUp();
		/* Nahrungsmittel auf andere Lagerhäuser verteilen */
		getColony().addFoodToStorage(getFood().doubleValue());
	}
	
	@Override
	public double computeCustomValue(int number)
	{
		double stufe = getMaterialien().size();
		double ret;
		
		switch (number)
		{
			/* Erster Wert: Lagergröße */
			case 1:
				ret = INITIAL_STORAGE_SIZE * (1+Math.log(stufe));
				ret *= getQualityFaktor().doubleValue()/4;
				return ret;
			/* Zweiter Wert: Maximale Lagerzeit */
			case 2:
				ret = INITIAL_STORAGE_TIME * (Math.log(stufe) /(1+ FACT_A* Math.exp(-stufe))*FACT_B+1);
 				ret = Math.floor(ret/10)*10;
 				return ret;
		}
		
		/* Unknown, use default value */
		return computeCustomValue();
	}
	
	@Override
	public int getCustomCounter()
	{
		return (int) (computeCustomValue(1)/1E6);
	}
	
	public double getNormalizedStorage()
	{
		return computeCustomValue(1);
	}

	@Override
	public double getMaxSize()
	{
		double ret = computeCustomValue(2)* getColony().getMaxBevoelkerung();
		ret -= computeTotalCapacityWithLevelHigherThan(getMaterialien().size());
		ret /= computeCustomValue(1);
		return ret;
	}
	
	/**
	 * @return usable capacity of Silo
	 */
	public double computeCapacity()
	{
		return computeRealCapacity()*getKPD();
	}
	/**
	 * @return Wahre Kapazität des Silos, nicht durch Lagerzeit oder Auslastung beschränkt
	 */
	public double computeRealCapacity()
	{
		return getNormalizedStorage()*getSize().doubleValue();
	}
	
	private double computeTotalCapacityOnLevel(int Stufe)
	{
		double ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getColony().getBuildingsFromType(ResourceAbstract.ID_BUILDING_SILO))
		{
			Silo silo = (Silo) building.getResearchableResource();
			if(silo.getMaterialien().size() == Stufe)
				ret += silo.computeRealCapacity();
		}
		
		return ret;
	}
	
	private double computeTotalCapacityWithLevelHigherThan(int Stufe)
	{
		double ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getColony().getBuildingsFromType(ResourceAbstract.ID_BUILDING_SILO))
		{
			Silo silo = (Silo) building.getResearchableResource();
			if(silo.getMaterialien().size() > Stufe)
				ret += silo.computeRealCapacity();
		}
		
		return ret;
		
	}

	/**
	 * @return # of stored food
	 */
	public Number getFood()
	{
		//Wenn mehr gelagert als erlaubt, verwerfe Restmenge
		Number ret = (Number) getAttribute(ATTR_STORED, DOUBLE_0);
		double storageSize = computeCapacity();
		
		if(ret.doubleValue() > storageSize)
		{
			setFood(storageSize);
			/* Nahrung auf andere Lagerhäuser verteilen */
			getColony().addFoodToStorage(ret.doubleValue()-storageSize);
			ret = storageSize;
		}
			
		return ret;
	}
	
	/**
	 * 
	 * @param amount amount of food to add to or take from the storage
	 * @return amount of food actually added to or taken from the storage
	 */
	public double addFood(double amount)
	{
		double ret = 0;
		
		if(cleanup)	return 0;

		if(amount == 0) return 0;
		
		/* hinzufügen oder abziehen? */
		if(amount > 0)
		{
			/* Lagerplatz überprüfen */
			if(amount <= computeFreeSpace())
			{
				ret = amount;
				setFood(getFood().doubleValue()+ amount);
			}else
			{
				ret = computeFreeSpace();
				setFood(computeCapacity());
			}
		}else
		{
			/* Nahrungsvorrat prüfen */
			if(-amount <= getFood().doubleValue())
			{
				ret = amount;
				setFood(getFood().doubleValue()+ amount);
			}else
			{
				/* Minus, weil Menge entnommen wird! */
				ret = -getFood().doubleValue();
				setFood(0);
			}
		}
		
		return ret;
	}
	
	/* Use add to add or remove food from storage */
	protected synchronized void setFood(Number amount)
	{
		if(amount == null || amount.doubleValue() < 0)
			removeAttribute(ATTR_STORED);
		else if(amount.doubleValue() <= computeCapacity())
			setAttribute(ATTR_STORED, amount);
		else
			setAttribute(ATTR_STORED, computeCapacity());
	}
	
	public double computeFreeSpace()
	{
		return computeCapacity() - getFood().doubleValue();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/getreidesilo.png";
	}
	
	/**
	 * Getreidesilo braucht keine Arbeiter
	 */
	@Override
	public double computeNormalizedCrew()
	{
		return DOUBLE_0;
	}
	

	@Override
	public double[] getUtilizationValues()
	{
		double[] ret = new double[1];
		double capacity = computeCapacity();
		if(capacity > 0)
			ret[0] = getFood().doubleValue()/capacity;
		else
			ret[0] = 1;
		
		return ret;
	}
	
	@Override
	public double getKPD()
	{
		if(!isBuilded()) return 1;
		double ret = computeCustomValue(2)* getColony().getPopulation().doubleValue();
		ret -= computeTotalCapacityWithLevelHigherThan(getMaterialien().size());
		ret /= computeTotalCapacityOnLevel(getMaterialien().size());
		if(ret < 0)	ret = 0;
		ret = Math.min(super.getKPD(), ret);
		return ret;
	}
}
