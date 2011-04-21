/*
 * $Id: ColonyInfo.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.net.diplomacy;

import java.awt.Point;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import mou.core.colony.Colony;

/**
 * @author pb
 */
public class ColonyInfo
{

	private Point position;
	private int population;

	/**
	 * 
	 */
	public ColonyInfo(Colony col)
	{
		super();
		position = col.getPosition();
		population = col.getPopulation().intValue();
	}

	protected ColonyInfo()
	{
	}

	public int getPopulation()
	{
		return population;
	}

	public Point getPosition()
	{
		return position;
	}

	public void serialize(DataOutput out) throws IOException
	{
		out.writeInt(position.x);
		out.writeInt(position.y);
		out.writeInt(population);
	}

	static public ColonyInfo deserialize(DataInput in) throws IOException
	{
		ColonyInfo ret = new ColonyInfo();
		ret.position = new Point(in.readInt(), in.readInt());
		ret.population = in.readInt();
		return ret;
	}
}
