/*
 * $Id: CivInfo.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.net.diplomacy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import mou.Main;
import mou.core.civilization.Civilization;

/**
 * @author pb
 */
public class CivInfo
{

	private int colonyCount;
	private double population;
	private double money;
	private double bsp;
	private int shipCount;
	private long foundationTime;
	private String name;

	/**
	 * 
	 */
	public CivInfo(Civilization civ)
	{
		super();
		colonyCount = civ.getKolonienAnzahl().intValue();
		shipCount = civ.getSchiffsanzahl().intValue();
		population = civ.getBevolkerung().doubleValue();
		money = civ.getMoney().doubleValue();
		foundationTime = civ.getFoundationTime();
		name = civ.getName();
		bsp = Main.instance().getMOUDB().getCivilizationDB().getCivDayReport().getBSP();
	}

	protected CivInfo()
	{
	}

	public int getColonyCount()
	{
		return colonyCount;
	}

	public long getFoundationTime()
	{
		return foundationTime;
	}

	public double getMoney()
	{
		return money;
	}

	public double getPopulation()
	{
		return population;
	}

	public int getShipCount()
	{
		return shipCount;
	}

	public double getBsp()
	{
		return bsp;
	}

	public String getName()
	{
		return name;
	}

//	public void serialize(DataOutput out) throws IOException
//	{
//		out.writeInt(colonyCount);
//		out.writeDouble(population);
//		out.writeDouble(money);
//		out.writeInt(shipCount);
//		out.writeLong(foundationTime);
//		out.writeDouble(bsp);
//		StringSerializer.serializeString(out, name);
//	}
//
//	static public CivInfo deserialize(DataInput in) throws IOException
//	{
//		CivInfo ret = new CivInfo();
//		ret.colonyCount = in.readInt();
//		ret.population = in.readDouble();
//		ret.money = in.readDouble();
//		ret.shipCount = in.readInt();
//		ret.foundationTime = in.readLong();
//		ret.bsp = in.readDouble();
//		ret.name = StringSerializer.deserializeString(in);
//		return ret;
//	}
}
