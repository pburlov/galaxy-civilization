/*
 * $Id: Civilization.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.civilization;

import java.awt.Color;
import java.util.Hashtable;
import mou.Main;
import mou.core.db.AbstractDB;
import mou.core.db.DBObjectImpl;
import mou.net.diplomacy.CivInfo;

/**
 * Diese Objekt dient lediglich zur Haltung allgemeiner Zivilisationsdaten und deren Austausch
 * zwischen Zivilisationen.
 * 
 * @author pb
 */
public class Civilization extends DBObjectImpl
{

	static final private String ATTR_CIV_NAME = "ATTR_CIV_NAME";
	static final private String ATTR_FOUNDATION_TIME = "ATTR_FOUNDATION_TIME";
	private static final String ATTR_MONEY = "MONEY";
	// private static final String ATTR_TRADE_PARTNER = "TRADE_PARTNER";
	// static private final String ATTR_ALIIERT_CIVS = "ALIIERT_CIVS";
	// static private final String ATTR_FEINDLICH_CIVS = "FEINDLICH_CIVS";
	static private final String ATTR_BEVOLKERUNG = "BEVOLKERUNG";
	static private final String ATTR_KOLONIEN = "KOLONIEN";
	static private final String ATTR_SCHIFFE = "SCHIFFE";
	static public final String ATTR_STATUS_ALLY = "ATTR_STATUS_ALLY";
	static public final String ATTR_STATUS_ENEMY = "ATTR_STATUS_ENEMY";
	static public final String ATTR_STATUS_IGNORED = "ATTR_STATUS_IGNORED";
	static public final String ATTR_BSP = "ATTR_BSP";

	/**
	 * 
	 */
	public Civilization()
	{
		super(new Hashtable());
	}

	public Civilization(AbstractDB db, String name, long serial)
	{
		super(new Hashtable());
		setAttribute(ATTR_ID, CivilizationDB.createCivID(serial), false);//
		setCivName(name);
		// setCreateTime(Main.instance().getTime());
	}

	public Number getBSP()
	{
		return (Number) getAttribute(ATTR_BSP, ZERO_DOUBLE);
	}

	public void setBSP(double val)
	{
		setAttribute(ATTR_BSP, val, false);
	}

	public void setFoundationTime(Long time)
	{
		setAttribute(ATTR_FOUNDATION_TIME, time, false);
	}

	public void setCivName(String name)
	{
		setAttribute(ATTR_CIV_NAME, name, false);
	}

	public Long getFoundationTime()
	{
		return (Long) getAttribute(ATTR_FOUNDATION_TIME, new Long(0));
	}

	public String getName()
	{
		String name = (String) getAttribute(ATTR_CIV_NAME);
		return name == null ? "Unbekannt" : name;
	}

	public String toString()
	{
		return getName();
	}

	public void updateWithRemoteData(CivInfo remoteData)
	{
		synchronized(getLockObject())
		{
			setFoundationTime(remoteData.getFoundationTime());
			setKolonienAnzahl(remoteData.getColonyCount());
			setSchiffsanzahl(remoteData.getShipCount());
			setMoney(remoteData.getMoney());
			setBevolkerung((long) remoteData.getPopulation());
			setCivName(remoteData.getName());
			setBSP(remoteData.getBsp());
		}
	}

	private Boolean getLazyBoolean(String key, boolean lazyVal)
	{
		Boolean ret = (Boolean) getAttribute(key);
		if(ret == null) ret = new Boolean(lazyVal);
		return ret;
	}

	public boolean isAlly()
	{
		return getLazyBoolean(ATTR_STATUS_ALLY, false);
	}

	public boolean isEnemy()
	{
		return getLazyBoolean(ATTR_STATUS_ENEMY, false);
	}

	/**
	 * @return true wenn Kommunikationsversuche von dieser Civ ignoriert werden sollen
	 */
	public boolean isIgnored()
	{
		return getLazyBoolean(ATTR_STATUS_IGNORED, false);
	}

	public void setIgnored(boolean val)
	{
		synchronized(getLockObject())
		{
			setAttribute(ATTR_STATUS_IGNORED, val, true);
		}
	}

	/**
	 * Setzt gleichzeitig den Ally Wert auf false
	 */
	public void setEnemy(boolean val)
	{
		synchronized(getLockObject())
		{
			/*
			 * Gleichzeiting den Ally Wert auf false setzen
			 */
			if(val)
			{
				setAttribute(ATTR_STATUS_ALLY, false, false);
			}
			setAttribute(ATTR_STATUS_ENEMY, val, true);
		}
	}

	/**
	 * Setzt gleichzeitig den Feindlich Wert auf false
	 */
	public void setAlly(boolean val)
	{
		synchronized(getLockObject())
		{
			if(val)
			{
				setAttribute(ATTR_STATUS_ENEMY, false, false);
			}
			setAttribute(ATTR_STATUS_ALLY, val, true);
		}
	}

	/**
	 * Liefert Darstellungsfarbe für diese Civilization. Farbe hängt von der Status der
	 * Civilization.
	 * 
	 * @see #getCivStatus()
	 * @return
	 */
	public Color getCivStatusColor()
	{
		return ((CivilizationDB) getDB()).getGuiColorForCiv(getID());
	}

	public String getCivStatusString()
	{
		return ((CivilizationDB) getDB()).getStringStatus(getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.DBObjectImpl#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		Civilization civ = (Civilization) o;
		return getName().compareTo(civ.getName());
	}

	// public long getCivAlter()
	// {
	// return Main.instance().getTime() - getFoundationTime();
	// }
	//	
	/**
	 * Liefert den Kontostand
	 * 
	 * @return
	 */
	public Number getMoney()
	{
		return (Number) getAttribute(ATTR_MONEY, ZERO_DOUBLE);
	}

	/**
	 * Für Setzen der Geldmenge bitte Methode aus CivilizationDB benutzen
	 * 
	 * @param money
	 */
	public void setMoney(Number money)
	{
		setAttribute(ATTR_MONEY, money, false);
	}

	public Number getKolonienAnzahl()
	{
		return (Number) getAttribute(ATTR_KOLONIEN, ZERO_DOUBLE);
	}

	void setKolonienAnzahl(Number anzahl)
	{
		setAttribute(ATTR_KOLONIEN, anzahl, false);
	}

	public Number getSchiffsanzahl()
	{
		return (Number) getAttribute(ATTR_SCHIFFE, ZERO_DOUBLE);
	}

	void setSchiffsanzahl(Number anzahl)
	{
		setAttribute(ATTR_SCHIFFE, anzahl, false);
	}

	public Number getBevolkerung()
	{
		return (Number) getAttribute(ATTR_BEVOLKERUNG, ZERO_DOUBLE);
	}

	public void setBevolkerung(Number bev)
	{
		setAttribute(ATTR_BEVOLKERUNG, bev, true);
	}

	/**
	 * Liefert alter dieser Daten
	 * 
	 * @return
	 */
	public long getDatenalter()
	{
		return Main.instance().getTime() - getTimestamp().longValue();
	}
}