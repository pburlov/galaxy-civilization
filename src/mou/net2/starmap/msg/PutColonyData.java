/*
 * $Id$
 * Created on Jul 1, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.starmap.msg;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class PutColonyData extends StarmapMsg
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int population;
	
	public PutColonyData()
	{
		super();
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		super.readExternal(in);
		population = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		out.writeInt(population);
	}

	public int getPopulation()
	{
		return population;
	}
	
	public void setPopulation(int population)
	{
		this.population = population;
	}
}
