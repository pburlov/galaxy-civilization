/*
 * $Id$
 * Created on May 17, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe;

import mou.net2.PeerHandle;


public class PeerEntry
{
	private PeerHandle handle;
	private long time;
	
	/**
	 * @param handle
	 */
	public PeerEntry(PeerHandle handle, long time)
	{
		super();
		this.handle = handle;
		this.time = time;
	}

	
	public long getTime()
	{
		return time;
	}

	
	public void setTime(long time)
	{
		this.time = time;
	}

	
	public PeerHandle getHandle()
	{
		return handle;
	}


	@Override
	public boolean equals(Object obj)
	{
		return handle.equals(((PeerEntry)obj).handle);
	}


	@Override
	public int hashCode()
	{
		return handle.hashCode();
	}

}
