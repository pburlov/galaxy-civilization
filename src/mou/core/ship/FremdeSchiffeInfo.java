/*
 * $Id: FremdeSchiffeInfo.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.ship;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import mou.SerialNumbered;
import mou.core.civilization.CivilizationDB;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class FremdeSchiffeInfo implements SerialNumbered
{

	private long civId;
	private int shipQuantity;
	private long gesamttonnage;

	/**
	 * @param shipQuantity
	 */
	public FremdeSchiffeInfo(long civId, int shipQuantity, long gesamttonnage)
	{
		this.civId = civId;
		this.shipQuantity = shipQuantity;
		this.gesamttonnage = gesamttonnage;
	}

	/**
	 * 
	 */
	public FremdeSchiffeInfo()
	{
		super();
	}

	public long getSerialNumber()
	{
		return civId;
	}

	public ID getCivID()
	{
		return CivilizationDB.createCivID(getSerialNumber());
	}

	public int getShipQuantity()
	{
		return shipQuantity;
	}

	public long getGesamttonnage()
	{
		return gesamttonnage;
	}

	public void setGesamttonnage(long gesamttonnage)
	{
		this.gesamttonnage = gesamttonnage;
	}
}
