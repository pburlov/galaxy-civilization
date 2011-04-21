/*
 * $Id: ResourceMenge.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res;

import mou.Main;
import mou.core.civilization.NaturalRessourceDescriptionDB;
import mou.core.res.natural.NaturalResource;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pbu
 */
public class ResourceMenge
		implements Comparable<Object>
{

	static final private NaturalRessourceDescriptionDB resDB = Main.instance().getMOUDB().getNaturalRessourceDescriptionDB();
	private NaturalResource mRessourceDescription;
	private double mMenge;

	/**
	 * 
	 */
	public ResourceMenge(NaturalResource res, double menge)
	{
		mRessourceDescription = res;
		mMenge = menge;
	}

	/**
	 * 
	 */
	public ResourceMenge(ID res, double menge)
	{
		mRessourceDescription = resDB.getNaturalResource(res);
		mMenge = menge;
	}

	/**
	 * @return
	 */
	public double getMenge()
	{
		return mMenge;
	}

	public void addMenge(double menge)
	{
		mMenge += menge;
	}

	/**
	 * @return
	 */
	public NaturalResource getRessource()
	{
		return mRessourceDescription;
	}

	public String toString()
	{
		return getRessource().getName() + ": " + GUI.formatSmartDouble(getMenge()) + "T";
	}

	public int compareTo(Object o)
	{
		return toString().compareToIgnoreCase(o.toString());
	}
}
