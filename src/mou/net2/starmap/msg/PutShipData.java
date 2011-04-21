/*
 * $Id$
 * Created on Jul 1, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.starmap.msg;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class PutShipData extends StarmapMsg
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int shipQuantity;
	private long totalMass;

	public PutShipData()
	{
		super();
	}

	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		super.readExternal(in);
		shipQuantity = in.readInt();
		totalMass = in.readLong();
	}


	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		out.writeInt(shipQuantity);
		out.writeLong(totalMass);
	}


	public int getShipQuantity()
	{
		return shipQuantity;
	}

	
	public void setShipQuantity(int shipQuantity)
	{
		this.shipQuantity = shipQuantity;
	}

	
	public long getTotalMass()
	{
		return totalMass;
	}

	
	public void setTotalMass(long totalMass)
	{
		this.totalMass = totalMass;
	}
	
}
