/*
 * $Id$
 * Created on Mar 26, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

public class PrioritableItem<O> implements Comparable<PrioritableItem<O>>
{
	private O message;
	private int priority;
	
	public PrioritableItem(O msg, int priority)
	{
		if(msg == null)throw new IllegalArgumentException("Message is null");
		message = msg;
		this.priority = priority;
	}
	public O getItem()
	{
		return message;
	}
	public int compareTo(PrioritableItem o)
	{
		return priority - o.priority;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
}
