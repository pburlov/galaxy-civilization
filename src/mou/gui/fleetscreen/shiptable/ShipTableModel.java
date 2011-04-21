/*
 * $Id: ShipTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.fleetscreen.shiptable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import mou.Main;
import mou.core.db.DBEventListener;
import mou.core.db.DBObjectImpl;
import mou.core.db.ObjectChangedEvent;
import mou.core.ship.Ship;
import mou.core.ship.ShipMovementOrder;
import mou.core.ship.ShipMovementOrderDB;
import mou.core.starmap.StarmapDB;

/**
 * @author pb
 */
public class ShipTableModel extends AbstractTableModel
		implements DBEventListener
{

	static final public int SHIP_OBJECT = 0;
	static final public int NAME = 1;
	static final public int POSITION = 2;
	static final public int SPEED = 3;
	static final public int MASSE = 4;
	static final public int STRUKTUR = 5;
	static final public int SKILL = 6;
	static final public int WEAPON = 7;
	static final public int PANZER = 8;
	static final public int SHILD = 9;
	static final public int SUPPORT = 10;
	static final public int CREW = 11;
	private ShipMovementOrderDB movDB = Main.instance().getMOUDB().getShipMovementOrderDB();
	private StarmapDB starDB = Main.instance().getMOUDB().getStarmapDB();
	// static final private Class[] CLASSES = {String.class,Point.class,Double.class};
	private List rowData;
	private Point showedPos;

	/**
	 * 
	 */
	public ShipTableModel()
	{
		super();
		Main.instance().getMOUDB().getShipDB().addDBEventListener(this);
		showShipsAt(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		if(rowData == null) return 0;
		return rowData.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Ship ship = (Ship) rowData.get(rowIndex);
		if(ship == null) return null;
		switch(columnIndex)
		{
			case SHIP_OBJECT:
				return ship;
			case NAME:
				return ship.getShipClassName();// Name
			case POSITION:
				// Point[] ret = new Point[2];
				// ret[0] = ship.getPosition();
				// if(ret[0] == null)
				// {
				// ShipMovementOrder order = movDB.getOrderForShip(ship.getID());
				// if(order == null) return null;
				// ret[0] = order.getStart();
				// ret[1] = order.getTarget();
				// }
				if(ship.getPosition() == null)
				{
					ShipMovementOrder order = movDB.getOrderForShip(ship.getID());
					if(order == null) return "";
					Point start = order.getStart();
					Point ende = order.getTarget();
					return starDB.getStarSystemAt(start).toString() + " >>> " + starDB.getStarSystemAt(ende).toString();
				}
				return starDB.getStarSystemAt(ship.getPosition()).toString();
			case SPEED:
				return new Double(ship.getSpeed());// Geschwindigkeit
			case MASSE:
				return new Long((long) ship.getMasse());// Masse
			case STRUKTUR:
				return ship;// Struktur
			case SKILL:
				return 0;// new Integer(ship.getErfahrung());//Erfahrung
			case CREW:
				return new Integer((int) ship.getCrew());
			case PANZER:
				return new Long((long) ship.getArmor());
			case SHILD:
				return new Long((long) ship.getShild());
			case WEAPON:
				return new Long((long) ship.getWeapon());
			case SUPPORT:
				return new Long((long) ship.getSupportCost());
		}
		return null;
	}

	// /**
	// * @param ships
	// * Liste mit Ship Objekten
	// */
	// private void showShips(List ships)
	// {
	// rowData = ships;
	// initIndexMap(ships);
	// }
	/**
	 * Zeigt Schiffe bei dieser Position. Wenn Position null, dann werden alle Schiffe gezeigt
	 */
	public void showShipsAt(Point pos)
	{
		if(pos != null)
		{
			rowData = Main.instance().getMOUDB().getShipDB().getShipsInStarsystem(pos);
		} else
		{
			rowData = new ArrayList(Main.instance().getMOUDB().getShipDB().getAllShips().values());
		}
		showedPos = pos;
		fireTableDataChanged();
	}

	public Ship getShipAtRow(int row)
	{
		return (Ship) rowData.get(row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.DBEventListener#objectRemoved(mou.core.DBObjectImpl)
	 */
	public void objectRemoved(DBObjectImpl obj)
	{
		Ship ship = (Ship) obj;
		int index = rowData.indexOf(ship);
		if(index < 0) return;
		rowData.remove(index);
		fireTableRowsDeleted(index, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.DBEventListener#objectAdded(mou.core.DBObjectImpl)
	 */
	public void objectAdded(DBObjectImpl obj)
	{
		Ship ship = (Ship) obj;
		if(showedPos != null)
		{
			if(!showedPos.equals(ship.getPosition())) return;
		}
		rowData.add(ship);
		int index = rowData.size() - 1;
		fireTableRowsInserted(index, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.ObjectChangedEventListener#objectChanged(mou.core.ObjectChangedEvent)
	 */
	public void objectChanged(ObjectChangedEvent event)
	{
		Ship ship = (Ship) event.getDbObject();
		int index = rowData.indexOf(ship);
		if(index < 0) return;
		fireTableRowsUpdated(index, index);
	}
}