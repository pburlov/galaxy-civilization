/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

/**
 * @author pbu
 */
public interface ClockListener
{

	public void dailyEvent(ClockEvent event);

	public void yearlyEvent(ClockEvent event);
}
