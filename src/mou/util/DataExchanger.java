/*
 * $Id$
 * Created on Apr 16, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.util;

/**
 * Klasse zum Austauschen der DAten zwischen zwei Threads
 * @author paul
 */
public class DataExchanger<O>
{
	private O data;
	
	public DataExchanger()
	{
		super();
	}
	
	public O getData()
	{
		return data;
	}
	
	public void setData(O data)
	{
		this.data = data;
	}
}
