/*
 * $Id: FremdeObjectsDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.db;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import mou.SerialNumbered;

/**
 * @author pb
 */
public class FremdeObjectsDB<O extends SerialNumbered>
{

	private Map<Point, Map<Long, O>> data = new HashMap<Point, Map<Long, O>>();

	/**
	 * 
	 */
	public FremdeObjectsDB()
	{
		super();
	}

	public Set<Point> getAllPointsWithObjects()
	{
		synchronized(data)
		{
			return new HashSet<Point>(data.keySet());
		}
	}

	/**
	 * Liefert ein threadsichere Set mit Objecten bei diesen Koordinaten, oder null wenn keine Werte
	 * bekannt
	 * 
	 * @param pos
	 * @return
	 */
	public Map<Long, O> getObjectsAt(Point pos)
	{
		synchronized(data)
		{
			Map<Long, O> ret = data.get(pos);
			if(ret == null) return null;
			return new HashMap<Long, O>(ret);
		}
	}

	/**
	 * Setzt Daten f?r eine Position. Wenn bereits Daten f?r diese Position vorliegen dann werden
	 * sie mit neuen Daten ?berschrieben.
	 * 
	 * @param pos
	 *            wenn null, dann einfach Daten l?schen
	 * @param kolonien
	 */
	public void setObjecte(Point pos, Set<O> objecte)
	{
		synchronized(data)
		{
			if(objecte == null)
				removeDataForPos(pos);
			else
			{
				for(O o : objecte)
					putObject(pos, o);
			}
		}
	}

	private Map<Long, O> getLazyPosData(Point pos)
	{
		synchronized(data)
		{
			Map<Long, O> posData = data.get(pos);
			if(posData == null)
			{
				posData = new HashMap<Long, O>();
				data.put(pos, posData);
			}
			return posData;
		}
	}

	public void putObject(Point pos, O obj)
	{
		if(pos == null || obj == null) return;
		synchronized(data)
		{
			getLazyPosData(pos).put(new Long(obj.getSerialNumber()), obj);
		}
	}

	public void removeObjecteForCiv(Point pos, long civ)
	{
		synchronized(data)
		{
			Map<Long, O> posData = data.get(pos);
			if(posData == null) return;
			posData.remove(new Long(civ));
		}
	}

	public void removeDataForPos(Point pos)
	{
		synchronized(data)
		{
			data.remove(pos);
		}
	}

	/**
	 * Entfernt alle Kolonien der Zivilisation
	 * 
	 * @param civ
	 */
	public void removeDataForCiv(long civ)
	{
		synchronized(data)
		{
			for(Iterator<Point> iter = getAllPointsWithObjects().iterator(); iter.hasNext();)
			{
				removeObjecteForCiv(iter.next(), civ);
			}
		}
	}
}
