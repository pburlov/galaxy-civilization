/*
 * $Id$
 * Created on Jul 1, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.starmap.msg;

import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class StarmapMsg implements Externalizable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Point position;
	private Long civId;
	
	public StarmapMsg()
	{
		super();
	}

	
	public Long getCivId()
	{
		return civId;
	}

	
	public void setCivId(Long civId)
	{
		this.civId = civId;
	}

	
	public Point getPosition()
	{
		return position;
	}

	
	public void setPosition(Point position)
	{
		this.position = position;
	}

	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(position.x);
		out.writeInt(position.y);
		out.writeLong(civId);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		position = new Point(in.readInt(), in.readInt());
		civId = new Long(in.readLong());
	}
}
