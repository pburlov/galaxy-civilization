/*
 * $Id: ShipDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.ship;

import java.awt.Point;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mou.Main;
import mou.core.civilization.CivDayReport;
import mou.core.civilization.CivYearReport;
import mou.core.civilization.CivilizationDB;
import mou.core.civilization.CivilizationMember;
import mou.core.colony.Colony;
import mou.core.starmap.PositionableDB;
import mou.storage.ser.ID;
import org.apache.commons.lang.SerializationUtils;

/**
 * @author pbu
 */
public class ShipDB extends PositionableDB
		implements CivilizationMember
{

	/**
	 * @param data
	 */
	public ShipDB(Hashtable data)
	{
		super(data);
		Main.instance().getMOUDB().getCivilizationDB().registerCivilizationMember(this);
	}

	/**
	 * Liefert Koordinate bei dem gekaufte und sonstige Schiffe stationiert werden sollen.
	 * Normaleweise sind es die Koordinaten von der Hauptplanet, oder wenn keine Kolonien da sind,
	 * dann Koordinaten eines der eigener Schiffe.
	 * 
	 * @return Point(0,0) wenn keine Kolonien und keine Schiffe mehr
	 */
	public Point getDefaultFleetCollectorPoint()
	{
		Colony col = Main.instance().getMOUDB().getCivilizationDB().getCapitalColony();
		if(col != null) return col.getPosition();
		Set<Point> points = getPositionsWithObjects();
		if(points.size() > 0) return points.iterator().next();
		return Main.instance().getMOUDB().getStarmapDB().getRandomStarPosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "ShipDB";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		return Ship.class;
	}

	public Ship getShip(ID id)
	{
		Ship ship = (Ship) getData(id);
		if(ship == null) return null;
		return ship;
	}

	/**
	 * Liste mit Ship-Objecten die sich gerade in dieser Sternensystem befinden. Es werden Schiffe
	 * aller Zivilizationen aufgelistet. Um nach Zivilizationen gruppierte Liste zu bekommen
	 * benutzen Sie die Methode
	 * 
	 * @see #getFleetsInStarsystem(Point)
	 * @param position
	 * @return List mit Ship-Objecten
	 */
	public List<Ship> getShipsInStarsystem(Point position)
	{
		List ret = getDataWhere(Ship.ATTR_POSITION, position).getList();
		return ret;
	}

	/**
	 * @param star
	 * @return Collection mit ID Objecten
	 */
	public Set<ID> getShipIDsInStarsystem(Point star)
	{
		return getIDsWhere(Ship.ATTR_POSITION, star);
	}

	/**
	 * Liefert Liste mit ID Objekten der Zivilisationen deren Schiffe sich bei den angegeben
	 * Koordinaten aufhalten.
	 * 
	 * @param star
	 * @return
	 */
	public Set<ID> getCivIDsOfShipsInStarsystem(Point star)
	{
		HashSet ret = new HashSet();
		for(Iterator iter = getShipsInStarsystem(star).iterator(); iter.hasNext();)
		{
			ret.add(((Ship) iter.next()).getCivID());
		}
		return ret;
	}

	public int getShipCountInStarsystem(Point star)
	{
		return getCountWhere(Ship.ATTR_POSITION, star);
	}

	// /**
	// * Liste mit Fleet-Objekten aus ein Sternensystem. Zu Schiffen einer
	// Zivilization wird genau ein Fleet-Objekt
	// * zugeordnet, über den dann einzelne Schiffen angesprochen werden können,
	// oder zusammengefasste
	// * Informationen zur Schiffen abgelesen werden kann.
	// * @param position
	// * @return Liste Mit Flotte Objekten
	// */
	// public List getFleetsInStarsystem(Point position)
	// {
	// HashMap fleets = new HashMap(20);
	// List ships = getShipsInStarsystem(position);
	// Iterator iter = ships.iterator();
	// while(iter.hasNext())
	// {
	// Ship ship = (Ship)iter.next();
	// ID civID = ship.getCivID();
	// Flotte fleet = (Flotte)fleets.get(civID);
	// if(fleet == null)
	// {
	// fleet = new Flotte(position, civID);
	// fleets.put(civID, fleet);
	// }
	// fleet.addShip(ship);
	// }
	// return new ArrayList(fleets.values());
	// }
	// public Ship createNewShip(ShipClass shipClass)
	// {
	// ShipImpl ship = new ShipImpl(this, (ShipClassImpl)shipClass);
	// return ship;
	// }
	public void addNewShip(ShipClass shipClass, Point position)
	{
		synchronized(getLockObject())
		{
			Ship shipImpl = new Ship(new ShipClass((Map) SerializationUtils.clone((Serializable) shipClass.getObjectData())), position);
			addNewShip(shipImpl, position);
		}
	}

	public void addNewShip(Ship ship, Point position)
	{
		synchronized(getLockObject())
		{
			ship.setCurrentStruktur(ship.getStruktur());
			ship.setPosition(position);
			ship.insertInDB(this, true);
		}
		Main.instance().getMOUDB().getStarmapDB().markAsVisited(position);
	}

	public void deleteShip(ID id)
	{
		// Ship ship = getShip(id);
		removeData(id);
	}

	/**
	 * Löscht alle Schiffe in dieser Sternensystem
	 * 
	 * @param star
	 */
	public void deleteShipsInStarsystem(Point star)
	{
		synchronized(getLockObject())
		{
			Iterator iter = getShipIDsInStarsystem(star).iterator();
			while(iter.hasNext())
			{
				removeData((ID) iter.next());
			}
		}
	}

	/**
	 * Löscht alle Schiffe deren IDs in dem PAramaeter Collection steht.
	 * 
	 * @param ids
	 */
	public void deleteShips(Collection ids)
	{
		synchronized(getLockObject())
		{
			for(Iterator iter = ids.iterator(); iter.hasNext();)
			{
				ID id = (ID) iter.next();
				deleteShip(id);
			}
		}
	}

	public Map<ID, Ship> getAllShips()
	{
		return getAllDBObjects();
	}

	public void doDailyWork(CivDayReport dayValues)
	{
		for(Ship ship : getAllShips().values())
			ship.doDailyWork(dayValues);
	}

	public void doYearlyWork(CivYearReport yearValues)
	{
		for(Ship ship : getAllShips().values())
			ship.doYearlyWork(yearValues);
	}
}