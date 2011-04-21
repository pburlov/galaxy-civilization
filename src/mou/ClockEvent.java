/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

/**
 * @author pb
 */
public class ClockEvent
{

	private long time;

	/**
	 * 
	 */
	public ClockEvent(long time)
	{
		this.time = time;
	}

	/**
	 * @return
	 */
	public long getTime()
	{
		return time;
	}
}
