/*
 * $Id: ForeignColonyPersistent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.colony;

import java.awt.Point;
import java.util.Hashtable;
import mou.Main;
import mou.core.MOUDB;
import mou.core.civilization.CivilizationDB;
import mou.core.db.DBObjectImpl;
import mou.core.starmap.Positionable;
import mou.net2.starmap.msg.PutColonyData;
import mou.storage.ser.ID;

public class ForeignColonyPersistent extends DBObjectImpl
		implements Positionable
{

	static final public String CIV_ID = "CIV";
	static final private String POPULATION = "POP";
	static final public String POS = "POS";

	/**
	 * 
	 */
	public ForeignColonyPersistent()
	{
		super();
	}

	public ForeignColonyPersistent(Point pos, PutColonyData kol)
	{
		super(new Hashtable());
		setAttribute(CIV_ID, kol.getCivId(), false);
		setAttribute(POPULATION, kol.getPopulation(), false);
		setAttribute(POS, pos, false);
	}

	public void setPopulation(Integer pop)
	{
		setAttribute(POPULATION, pop, true);
	}

	public Number getPopulation()
	{
		return (Number)getAttributLazy(POPULATION, Main.ZERO_NUMBER);
	}
	
	public Long getCivSerialNumber()
	{
		return (Long) getAttribute(CIV_ID);
	}

	public ID getCivId()
	{
		return CivilizationDB.createCivID(getCivSerialNumber());
	}
	
	public Point getPosition()
	{
		return (Point) getAttribute(POS);
	}
}