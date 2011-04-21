/*
 * $Id: MaintenanceDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.Hashtable;
import mou.core.db.AbstractDB;

/**
 * Datenbank für alle nicht anderswo zugeordneten Daten
 */
public class MaintenanceDB extends AbstractDB
{

	// private long sequenz; // Fortlaufende Zähler. Z.B für unique IDs nutzlich
	private static final String KEY_SEQUENZ = "seq";
	private static final String KEY_STARMAP_POSITION = "KEY_STARMAP_POSITION";
	// private static final String IMMUTABLE_ID_POSTFIX = "postfix";
	private static final long FIRST_SEQ_VALUE = 1000000; // Werte darunter
	// wurden für statische
	// Ressource vergeben
	private static final Class DB_DATA_OBJECT = MaintenanceObjectImpl.class;
	private static final String CLIENT_UID = "uid";

	public MaintenanceDB(Hashtable data)
	{
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "MaintenanceDB";
	}

	/**
	 * Methode liefert beim beenden des Spiels Position auf die Sternenkarte zetriert wurde
	 * 
	 * @return
	 */
	public Point getStarmapPosition()
	{
		return (Point) getSecondaryMapData().get(KEY_STARMAP_POSITION);
	}

	/**
	 * Methode sichert aktuelle Anzeigeposition der Sternenkarte für das nächste Start
	 * 
	 * @param pos
	 */
	public void setStarmapPosition(Point pos)
	{
		getSecondaryMapData().put(KEY_STARMAP_POSITION, pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		return DB_DATA_OBJECT;
	}

	public Long getClientUID()
	{
		Long ret = (Long) getSecondaryMapData().get(CLIENT_UID);
		if(ret == null)
		{
			SecureRandom rand = new SecureRandom();
			ret = new Long(rand.nextLong());
			getSecondaryMapData().put(CLIENT_UID, ret);
		}
		return ret;
	}

	/**
	 * Gibt nächste long-Wert aus einer Sequenz. Mit jedem Aufruf dieser Methode, wird die Sequenz
	 * inkrementiert.
	 */
	public long getNextLong()
	{
		synchronized(getLockObject())
		{
			Long seq = (Long) getSecondaryMapData().get(KEY_SEQUENZ);
			if(seq == null)
				seq = new Long(FIRST_SEQ_VALUE);
			else
				seq = new Long(seq.longValue() + 1);
			getSecondaryMapData().put(KEY_SEQUENZ, seq);
			return seq.longValue();
		}
	}
}