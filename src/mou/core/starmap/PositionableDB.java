/*
 * $Id: PositionableDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.starmap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import mou.core.db.AbstractDB;
import mou.core.db.DBEventListener;
import mou.core.db.DBObjectImpl;
import mou.core.db.ObjectChangedEvent;
import burlov.collections.Index;

/**
 * Klasse unterhält Indexstrukturen um die Zuordnung der Positionable Objekten zu ihren Positionen
 * und umgekehrt schnell zu ermitteln, ohne den Gesamdatenbestand durchzusuchen. Im Konstruktor
 * werden die Indexe initialisiert. Dabei werden alle in DB enthalten Objekte die Positionable
 * Interface implementieren in die Indexe aufgenommen. Beim Einfügen, Löschen, ändern der Position
 * werden die Indexe automatisch angepasst.
 * 
 * @author PB
 */
abstract public class PositionableDB extends AbstractDB
{

	private final Index indexPositionToID = new Index();
	private final ArrayList positionEventListener = new ArrayList();

	/**
	 * @param data
	 */
	public PositionableDB(Map data)
	{
		super(data);
		addDBEventListener(new DBEventListener()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.core.DBEventListener#objectRemoved(mou.core.DBObjectImpl)
			 */
			public void objectRemoved(DBObjectImpl obj)
			{
				synchronized(getLockObject())
				{
					if(obj instanceof Positionable)
					{
						Point oldPos = (Point) indexPositionToID.getKeyForValue(obj.getID());
						indexPositionToID.remove(obj.getID());
						firePositionChangedEvent(obj, oldPos, null);
					}
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.core.DBEventListener#objectAdded(mou.core.DBObjectImpl)
			 */
			public void objectAdded(DBObjectImpl obj)
			{
				synchronized(getLockObject())
				{
					if(obj instanceof Positionable)
					{
						Positionable po = (Positionable) obj;
						Point pos = po.getPosition();
						if(pos == null) { return; }
						indexPositionToID.put(po.getPosition(), obj.getID());
						firePositionChangedEvent(obj, null, po.getPosition());
					}
				}
			}

			public void objectChanged(ObjectChangedEvent event)
			{
				synchronized(getLockObject())
				{
					DBObjectImpl obj = event.getDbObject();
					if(obj instanceof Positionable)
					{
						Positionable po = (Positionable) obj;
						Point oldPos = (Point) indexPositionToID.getKeyForValue(obj.getID());
						Point newPos = po.getPosition();
						// if(oldPos == null) oldPos = po.getPosition();
						if(oldPos != null && newPos != null && oldPos.equals(newPos)) return;
						indexPositionToID.put(po.getPosition(), obj.getID());
						firePositionChangedEvent(obj, oldPos, po.getPosition());
					}
				}
			}
		});
		initIndexes();
	}

	private void initIndexes()
	{
		Iterator iter = getAllDBObjects().values().iterator();
		while(iter.hasNext())
		{
			DBObjectImpl obj = (DBObjectImpl) iter.next();
			if(obj instanceof Positionable)
			{
				Positionable pos = (Positionable) obj;
				indexPositionToID.put(pos.getPosition(), obj.getID());
			}
		}
	}

	final public void addPositionChangedEventListener(PositionChangedEventListener listener)
	{
		synchronized(positionEventListener)
		{
			positionEventListener.add(listener);
		}
	}

	final public void removePositionChangedEventListener(PositionChangedEventListener listener)
	{
		synchronized(positionEventListener)
		{
			positionEventListener.remove(listener);
		}
	}

	/**
	 * Methode ruft die shipPositionChangedMethode bei allen registrierten Listener. Die Listeners
	 * werden in einem extra Thread benachritigt
	 * 
	 * @param event
	 */
	final private void firePositionChangedEvent(DBObjectImpl obj, Point oldPos, Point newPos)
	{
		final PositionChangedEvent event = new PositionChangedEvent(obj, oldPos, newPos);
		synchronized(positionEventListener)
		{
			for(Iterator iter = positionEventListener.iterator(); iter.hasNext();)
			{
				PositionChangedEventListener listener = (PositionChangedEventListener) iter.next();
				listener.positionChanged(event);
			}
		}
	}

	/**
	 * Fragt ob bei angegebene Koordinaten irgendwelche Objecte gespeichert sind.
	 * 
	 * @param pos
	 * @return
	 */
	public boolean hasObjectsAtPosition(Point pos)
	{
		Set objects = indexPositionToID.getValues(pos);
		if(objects == null) return false;
		return !objects.isEmpty();
	}

	/**
	 * Liefet iteratorsichere Set mit allen nicht leeren Positionen
	 * 
	 * @return Set mit Point Objekten.
	 */
	public Set<Point> getPositionsWithObjects()
	{
		return new HashSet<Point>(indexPositionToID.keys());
	}
}