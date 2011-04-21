/*
 * $Id: ShipBuildJob.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.colony;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import mou.Main;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.res.ResourceMenge;
import mou.core.ship.ShipClass;

/**
 * @author pbu
 */
public class ShipBuildJob extends BuildJobAbstract
{

	static final private Integer ATTR_SHIPCLAS_DATA = new Integer(1);
	transient private ShipClass shipClass;

	public ShipBuildJob(Map data)
	{
		super(data);
	}

	public ShipBuildJob(ShipClass ship)
	{
		this(ship, true);
	}
	
	public ShipBuildJob(ShipClass ship, boolean showMessage)
	{
		super(BuildJobAbstract.TYP_SHIP, showMessage);
		setAttribute(ATTR_SHIPCLAS_DATA, ship.getObjectData());		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.BuildQueueItemAbstract#getText()
	 */
	public String getName()
	{
		return getShipClass().getName();
	}

	public ShipClass getShipClass()
	{
		if(shipClass == null)
		{
			shipClass = new ShipClass((Map) getAttribute(ATTR_SHIPCLAS_DATA));
			// shipClass.computeWerte();
		}
		return shipClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.BuildQueueItemAbstract#computeNeededWorkPoints()
	 */
	public double computeNeededWorkPoints()
	{
		return getShipClass().getBuildCost();
	}

	/**
	 * Methode wird aufgerufen wenn das Bau eines Schiffes dieses Types gestartet wird
	 * 
	 * @param colony
	 */
	public BuildAllowed startBuild(Colony colony)
	{
		// if(getMOUDB().getCivilizationDB().getMoney().longValue() < 0) { return new BuildAllowed(
		// false, "Nicht genugende Geldreserve"); }
		ShipClass ship = getShipClass();
		if(colony.getPopulation().longValue() < ship.getCrew()) { return new BuildAllowed(false, "Es fehlen Crewmitglieder!"); }
		Collection res = ship.getNeededRessources();
		for(Iterator iter = res.iterator(); iter.hasNext();)
		{
			ResourceMenge menge = (ResourceMenge) iter.next();
			if(getMOUDB().getStorageDB().getMenge(menge.getRessource().getID()).doubleValue() < menge.getMenge()) { return new BuildAllowed(false,
					"Unzureichende Menge von " + menge.getRessource().getName()); }
		}
		/*
		 * ### Benötigte Ressource abziehen ###
		 */
		for(Iterator iter = res.iterator(); iter.hasNext();)
		{
			ResourceMenge menge = (ResourceMenge) iter.next();
			getMOUDB().getStorageDB().takeMenge(menge.getRessource().getID(), (int) menge.getMenge());
		}
		return new BuildAllowed(true, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.BuildQueueItemAbstract#cancelBuild(mou.db.Colony)
	 */
	public void cancelBuild(Colony colony)
	{
		NaturalRessourcesStorageDB storage = getMOUDB().getStorageDB();
		long putBack = 0;
		/*
		 * Abgezogene Materialien zurückgeben
		 */
		for(ResourceMenge menge : getShipClass().getNeededRessources())
		{
			putBack = (long) (menge.getMenge()*Math.min(MAX_PUT_BACK, 1 - getProgress()));
			storage.addMenge(menge.getRessource().getID(), putBack , false);
		}
	}

	@Override
	public void completeBuild(Colony col)
	{
		Point position = col.getPosition();
		/*
		 * Bevölkerung erst beim Bauende abziehen
		 */
		col.setPopulation(col.getPopulation().doubleValue() - getShipClass().getCrew());
		Main.instance().getMOUDB().getShipDB().addNewShip(getShipClass(), position);
	}
}
