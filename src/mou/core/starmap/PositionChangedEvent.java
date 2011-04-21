/*
 * $Id: PositionChangedEvent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.starmap;

import java.awt.Point;
import mou.core.db.DBObjectImpl;
import mou.core.db.ObjectChangedEvent;
import mou.core.ship.Ship;

/**
 * @author PB
 */
public class PositionChangedEvent extends ObjectChangedEvent
{

	/**
	 * @param Ursprungsevent
	 */
	public PositionChangedEvent(DBObjectImpl obj, Point oldPos, Point newPos)
	{
		super(obj, Ship.ATTR_POSITION, oldPos, newPos);
	}

	public Point getOldPosition()
	{
		return (Point) getOldValue();
	}

	public Point getNewPosition()
	{
		return (Point) getNewValue();
	}
}
