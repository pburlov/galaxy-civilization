/*
 * $Id: ForeignColonyDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.colony;

import java.awt.EventQueue;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import mou.core.db.DBQueryResult;
import mou.core.starmap.PositionableDB;
import mou.net2.starmap.msg.PutColonyData;

public class ForeignColonyDB extends PositionableDB
{

	static final private long EXPIRED_TIME = 60 * 60 * 24 * 30;// ins Sekunden 30 Tage

	/**
	 * 
	 */
	public ForeignColonyDB(Map data)
	{
		super(data);
	}

	@Override
	public String getDBName()
	{
		return "FremdekolonienDB";
	}

	@Override
	protected Class getDBObiectImplClass()
	{
		return ForeignColonyPersistent.class;
	}

	/**
	 * Liefert ein threadsichere Map mit Kolonien bei diesen Koordinaten
	 * 
	 * @param pos
	 * @return
	 */
	public Map<Long, ForeignColonyPersistent> getObjectsAt(Point pos)
	{
		DBQueryResult res = getDataWhere(ForeignColonyPersistent.POS, pos);
		if(res.resultSize() == 0) return Collections.EMPTY_MAP;
		Map<Long, ForeignColonyPersistent> ret = new HashMap<Long, ForeignColonyPersistent>();
		for(Iterator iter = res.getIterator(); iter.hasNext();)
		{
			ForeignColonyPersistent fkol = (ForeignColonyPersistent) iter.next();
			if(!isCivOnline(fkol.getCivSerialNumber()))
			{
				/*
				 * Zivilisation ist nicht online. Datenalter prüfen
				 */
				if(fkol.computeDataAge() >= EXPIRED_TIME) removeData(fkol.getID());
				continue;
			}
			ret.put(fkol.getCivSerialNumber(), fkol);
		}
		return ret;
	}

	private ForeignColonyPersistent getFKolonie(Point pos, Long civ)
	{
		for(Iterator iter = getDataWhere(ForeignColonyPersistent.POS, pos).getIterator(); iter.hasNext();)
		{
			ForeignColonyPersistent fkol = (ForeignColonyPersistent) iter.next();
			if(fkol.getCivSerialNumber().equals(civ)) return fkol;
		}
		return null;
	}

	public void putKolonie(final Point pos, final PutColonyData kol)
	{
		if(!EventQueue.isDispatchThread())
		{
			EventQueue.invokeLater(new Runnable()
			{

				public void run()
				{
					putKolonie(pos, kol);
				}
			});
			return;
		}
		synchronized(getLockObject())
		{
			ForeignColonyPersistent fkol = getFKolonie(pos, kol.getCivId());
			if(fkol != null)
			{
				/*
				 * Wenn Datensatz exisitiert, dann nur Population neu setzen
				 */
				fkol.setPopulation(kol.getPopulation());
			} else
			{
				fkol = new ForeignColonyPersistent(pos, kol);
				putData(fkol, true);
			}
		}
	}

	public void removeKolonieForCiv(final Point pos, final Long civ)
	{
		if(!EventQueue.isDispatchThread())
		{
			EventQueue.invokeLater(new Runnable()
			{

				public void run()
				{
					removeKolonieForCiv(pos, civ);
				}
			});
			return;
		}
		ForeignColonyPersistent fkol = getFKolonie(pos, civ);
		if(fkol != null) removeData(fkol.getID());
	}

	public void removeDataForPos(final Point pos)
	{
		if(!EventQueue.isDispatchThread())
		{
			EventQueue.invokeLater(new Runnable()
			{

				public void run()
				{
					removeDataForPos(pos);
				}
			});
			return;
		}
		synchronized(getLockObject())
		{
			for(Iterator iter = getDataWhere(ForeignColonyPersistent.POS, pos).getIterator(); iter.hasNext();)
			{
				ForeignColonyPersistent fkol = (ForeignColonyPersistent) iter.next();
				removeData(fkol.getID());
			}
		}
	}

	private boolean isCivOnline(Long id)
	{
		//TODO
		return true;
	}
}
