/*
 * $Id: EnergyGenerator.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.shipsystem.energy;

import javax.swing.JComponent;
import mou.core.res.DefaultResearchableScienceView;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.storage.ser.ID;

/**
 * Superklasse aller Energiegeneratoren
 * 
 * @author pb
 */
public class EnergyGenerator extends ShipsystemAbstract
{

	/**
	 * 
	 */
	public EnergyGenerator()
	{
		super();
	}

	public String getImagePath()
	{
		return "/res/images/energiegenerator.png";
	}

	public JComponent getScienceViewComponent()
	{
		return new DefaultResearchableScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Energiegenerator";
	}

	@Override
	public String getShortDescription()
	{
		return "Generiert Energie für die restliche Schiffsysteme";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IDable#getID()
	 */
	public ID getID()
	{
		return ID_SHIPSYSTEM_ENERGYGENERATOR;
	}

	protected void computeShipPointsIntern(ShipClass ship)
	{
	}

	@Override
	public double computeNormalizedEnergyBalance()
	{
		return computeCustomValue(1);
	}
	
	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "";
	}
}
