/*
 * $Id: ShipMovementOrderDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.ship;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mou.Main;
import mou.core.db.AbstractDB;
import mou.storage.ser.ID;

/**
 * Klasse speichert die geplanten Schiffsbewegungen
 * 
 * @author pb
 */
public class ShipMovementOrderDB extends AbstractDB
{

	private Collection tempOrders = new ArrayList();

	/**
	 * @param data
	 */
	public ShipMovementOrderDB(Map data)
	{
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "ShipMovementOrderDB";
	}

	/**
	 * Findet MovementOrder der für das gegebene Schiff erteilt wurde
	 * 
	 * @param shipID
	 * @return Gefundenen MovementOrder oder null, wenn keine exisitiert
	 */
	public ShipMovementOrder getOrderForShip(ID shipID)
	{
		synchronized(getLockObject())
		{
			Iterator iter = getAllDBObjects().values().iterator();
			while(iter.hasNext())
			{
				ShipMovementOrder mo = (ShipMovementOrder) iter.next();
				if(!mo.getShipIDs().contains(shipID)) continue;
				return mo;
			}
			return null;
		}
	}

	/**
	 * MEthode geht alle gespeicherte MovementOrders durch und entfern den Datensatzt, der für das
	 * angegebene Schiff gilt.
	 * 
	 * @param shipID
	 */
	public void removeOrderForShip(ID shipID)
	{
		ShipMovementOrder order = getOrderForShip(shipID);
		if(order != null) removeData(order.getID());
	}

	/**
	 * Methode geht alle gespeicherte MovementOrders durch und entfern die, die für angegebene
	 * Schiffe gelten.
	 * 
	 * @param shipIDs
	 */
	public void removeOrderForShips(Set shipIDs)
	{
		Iterator iter = shipIDs.iterator();
		while(iter.hasNext())
		{
			ID id = (ID) iter.next();
			removeOrderForShip(id);
		}
	}

	/**
	 * Methode geht alle gespeicherte MovementOrders durch und entfern das angegebene Schiff aus dem
	 * gefundenem Datensatz. Wenn nach dem Entfernen des Schiffes keine weiteren Schiffe in dem
	 * betroffenenm Datensatz drin sind, wird er ebenfalls gelöscht.
	 * 
	 * @param shipIDs
	 */
	public void removeShipsFromOrder(List ships)
	{
		synchronized(getLockObject())
		{
			Iterator iter = ships.iterator();
			while(iter.hasNext())
			{
				Ship ship = (Ship) iter.next();
				removeShipFromOrder(ship.getID());
			}
		}
	}

	public void removeShipFromOrder(ID shipID)
	{
		synchronized(getLockObject())
		{
			ShipMovementOrder order = getOrderForShip(shipID);
			if(order != null)
			{
				order.getShipIDs().remove(shipID);
				if(order.getShipIDs().isEmpty()) removeData(order.getID());
			}
		}
	}

	/**
	 * Berechnet maximale gemeinsame Spungweite für die Schiffe und Flotten
	 * 
	 * @return
	 */
	private double computeMaxSpeed(List ships)
	{
		double maxRange = Integer.MAX_VALUE;
		Iterator iter = ships.iterator();
		while(iter.hasNext() && maxRange > 0)
		{
			double range = Double.MAX_VALUE;
			Ship ship = (Ship) iter.next();
			if(ship != null) range = ship.getSpeed();
			if(maxRange > range) maxRange = range;
		}
		return maxRange;
	}

	// synchronized public void createShipMovementOrder(Point start, Point
	// target, Set shipIDs, List route)
	// {
	// removeShipsFromOrder(shipIDs);
	// if(start.equals(target))return;
	// ShipDB shipDB = Main.instance().getMOUDB().getShipDB();
	// Iterator iter = shipDB.getShipsInStarsystem(start).iterator();
	// Set allowedShips = new HashSet();
	// while(iter.hasNext())
	// {//Prüfen, ob alle angegebenen Schiffen in dem Startsystem befinden
	// //Schiffe die nicht in Startsystem befinden werden automatisch
	// aussortiert
	// ID id = ((Ship)iter.next()).getID();
	// if(shipIDs.contains(id))allowedShips.add(id);
	// }
	// ShipMovementOrder order = new
	// ShipMovementOrder(allowedShips,target,Collections.synchronizedList(route));
	// order.insertInDB(this, true);
	// }
	/**
	 * Erzeugt temporär MovementOrders. So erzeugte MovementOrders können angesehen werden und dann
	 * entweder bestätigt oder verworfen.
	 * 
	 * @param target
	 * @param ships
	 *            Liste mit Ship Objekten
	 * @return true wenn es gelungen ist eine Route zu finden
	 */
	public boolean createTempShipMovementOrders(Point target, List ships)
	{
		removeTempShipMovementOrders();
		if(!canCreateNewMovementOrder(ships)) return false;
		removeShipsFromOrder(ships);
		Map groupByPos = groupShipsByPosition(ships);
		Iterator iter = groupByPos.keySet().iterator();
		while(iter.hasNext())
		{
			Point pos = (Point) iter.next();
			if(pos.equals(target)) continue;
			List setShips = (List) groupByPos.get(pos);
			double speed = computeMaxSpeed(setShips);
			if(speed < 0.001) return false;
			ShipMovementOrder order = new ShipMovementOrder(setShips, pos, target, new Double(speed));
			tempOrders.add(order);
		}
		return true;
	}

	/**
	 * Prüft ob ein neu Marschbefehl erstellt werden kann für die gegebene Schiffe
	 * 
	 * @param ships
	 * @return
	 */
	public boolean canCreateNewMovementOrder(Collection ships)
	{
		if(ships.isEmpty()) return false;
		for(Iterator iter = ships.iterator(); iter.hasNext();)
		{
			Ship ship = (Ship) iter.next();
			if(ship.getPosition() == null) return false;// Dieses Schiff fliegt bereits
		}
		return true;
	}

	/**
	 * Groupiert Schiffe und Flotten nach ihre gegenwärtige Position
	 * 
	 * @return Map Key: Position(Point); Value: List mit Ship Objekten
	 */
	private Map groupShipsByPosition(List ships)
	{
		Map ret = new HashMap();
		// ShipDB shipDB = Main.instance().getMOUDB().getShipDB();
		Iterator iter = ships.iterator();
		while(iter.hasNext())
		{
			Point pos = null;
			Ship ship = (Ship) iter.next();
			if(ship != null) pos = ship.getPosition();
			if(pos != null)
			{
				List ids = (List) ret.get(pos);
				if(ids == null)
				{
					ids = new ArrayList();
					ret.put(pos, ids);
				}
				ids.add(ship);
			}
		}
		return ret;
	}

	public void removeTempShipMovementOrders()
	{
		tempOrders = new ArrayList();
	}

	public Collection getTempMovementOrders()
	{
		return new ArrayList(tempOrders);
	}

	/**
	 * Überfürt temporäre ShipMovementOrders in permanenten Status (werden ausgeführt).
	 */
	public void acceptTempMovementOrders()
	{
		ShipDB shipDB = getMOUDB().getShipDB();
		Iterator iter = getTempMovementOrders().iterator();
		while(iter.hasNext())
		{
			ShipMovementOrder order = (ShipMovementOrder) iter.next();
			for(Iterator it = order.getShipIDs().iterator(); it.hasNext();)
			{
				Ship ship = shipDB.getShip((ID) it.next());
				if(ship != null)
				{
					// ship.setFlying(true);
					ship.setPosition(null);
				}
			}
			order.setStartTime(new Long(Main.instance().getTime()));
			order.insertInDB(this, true);
		}
		removeTempShipMovementOrders();
	}

	public Map getAllOrders()
	{
		return getAllDBObjects();
	}

	public void deleteMovementOrder(ID id)
	{
		removeData(id);
	}

	public ShipMovementOrder getMovementOrder(ID id)
	{
		return (ShipMovementOrder) getData(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		return ShipMovementOrder.class;
	}
}