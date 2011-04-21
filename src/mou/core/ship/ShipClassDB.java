/*
 * $Id: ShipClassDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.ship;

import java.util.Map;
import mou.core.db.AbstractDB;
import mou.storage.ser.ID;

/**
 * @author pbu
 */
public class ShipClassDB extends AbstractDB
{

	static final private Class DB_DATA_OBJECT = ShipClass.class;

	/**
	 * @param data
	 */
	public ShipClassDB(Map data)
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
		return "ShipClassDB";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	final protected Class getDBObiectImplClass()
	{
		return DB_DATA_OBJECT;
	}

	public ShipClass getShipClass(ID id)
	{
		ShipClass shipClass = (ShipClass) getData(id);
		return shipClass;
	}

	public void addNewShipClass(ShipClass ship)
	{
		ship.insertInDB(this, true);
	}

	public void deleteShipClass(ID id)
	{
		removeData(id);
	}

	public Map getAllShipClasses()
	{
		return getAllDBObjects();
	}
}
