/*
 * $Id: NaturalResource.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.natural;

import mou.core.res.ResourceAbstract;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class NaturalResource extends ResourceAbstract
{

	static final String IMAGE_PATH = "";// "/res/images/kristall.gif";
	private String name;
	private ID id;
	private float probality;

	/**
	 * 
	 */
	public NaturalResource(String name, long idLong, float probality)
	{
		super();
		this.name = name;
		id = new ID(idLong, 0l);
		this.probality = probality;
	}

	public float getProbality()
	{
		return probality;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IDable#getID()
	 */
	public ID getID()
	{
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.res.ResourceAbstract#getImagePath()
	 */
	public String getImagePath()
	{
		return IMAGE_PATH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.res.ResourceAbstract#getName()
	 */
	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.res.ResourceAbstract#getShortDescription()
	 */
	public String getShortDescription()
	{
		return "";
	}
}
