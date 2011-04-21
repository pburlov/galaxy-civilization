/*
 * $Id: ID.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.storage.ser;

import java.io.IOException;
import java.io.Serializable;
import mou.Main;

public class ID
		implements Serializable, Comparable
{

	static final public int CONSTANT_PART = 0;
	static final public int VARIABLE_PART = 1;
	static final private long serialVersionUID = 1;
	static final public ID VOID_ID = new ID(-1, -1);
	transient private int hashCode;
	private long id[] = new long[2];// id[0]= Konstante Anteil f?r eine Zivilization (wird zentral
									// ?ber burlov.de vergeben) id[1]=Variable Anteil der ID;

	/**
	 * Konstruiert eine ID aus einem fortlaufeden, eindeutigen f?r diesen Client, Nummer (variables
	 * Teil) und dem Seriennummer des ClientAuthTokens (konstantes Teil).
	 */
	public ID()
	{
		id[CONSTANT_PART] = Main.instance().getClientSerNumber().longValue();
		id[VARIABLE_PART] = Main.instance().getMOUDB().getMaintenaceDB().getNextLong();
		computeHashCode();
	}

	public ID(long variableID, long constID)
	{
		id[VARIABLE_PART] = variableID;
		id[CONSTANT_PART] = constID;
		computeHashCode();
	}

	/**
	 * Erzeugt eine ID mit der ConstantPart der eigener Zivilisation
	 * 
	 * @param variablePart
	 */
	public ID(long variablePart)
	{
		id[VARIABLE_PART] = variablePart;
		id[CONSTANT_PART] = Main.instance().getClientSerNumber().longValue();
		computeHashCode();
	}

	public long getConstantPart()
	{
		return getID()[CONSTANT_PART];
	}

	public long getVariablePart()
	{
		return getID()[VARIABLE_PART];
	}

	private void computeHashCode()
	{
		long temp = getConstantPart() ^ getVariablePart();
		hashCode = 0xFB6C065E ^ (int) (temp >>> 16) ^ (int) (temp);
	}

	private long[] getID()
	{
		return id;
	}

	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		try
		{
			ID i = (ID) obj;
			return (i.getID()[CONSTANT_PART] == getID()[CONSTANT_PART] && i.getID()[VARIABLE_PART] == getID()[VARIABLE_PART]);
		} catch(ClassCastException e)
		{
			return false;
		}
	}

	public int compareTo(Object obj) throws ClassCastException
	{
		ID _id = (ID) obj;
		if(_id == null) throw new NullPointerException("Kann null nicht vergleichen.");
		long diff = id[CONSTANT_PART] - _id.id[CONSTANT_PART];
		if(diff < 0) return -1;
		if(diff > 0) return 1;
		diff = id[VARIABLE_PART] - _id.id[VARIABLE_PART];
		if(diff < 0) return -1;
		if(diff > 0) return 1;
		return 0;
	}

	public int hashCode()
	{
		return hashCode;
	}

	public String toString()
	{
		return "mou.storage.ID[" + id[0] + ":" + id[1] + "]";
	}

	public String toText()
	{
		return "[" + id[0] + ":" + id[1] + "]";
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeLong(id[0]);
		out.writeLong(id[1]);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		id = new long[2];
		id[0] = in.readLong();
		id[1] = in.readLong();
		computeHashCode();
	}
}