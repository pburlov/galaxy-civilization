/*
 * $Id: NaturalRessourcesStorageDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.civilization;

import java.util.Hashtable;
import java.util.Map;
import mou.core.db.AbstractDB;
import mou.storage.ser.ID;

/**
 * Datenbank zur Speicherung der gelagerten Materialien und Ressourcen
 * 
 * @author pb
 */
public class NaturalRessourcesStorageDB extends AbstractDB
{

	static final private Integer NULL_WERT = new Integer(0);

	/**
	 * @param data
	 */
	public NaturalRessourcesStorageDB(Map data)
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
		return "NaturalRessourcesStorageDB";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		return NaturalRessourcesStorageItem.class;
	}

	public Number getMenge(ID ressource)
	{
		Number ret = null;
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item == null)
			ret = NULL_WERT;
		else
			ret = item.getMenge();
		return ret;
	}
	
	/* Vorher muß eigenständig geprüft werden, ob genug Rohstoffe vorhanden */
	public synchronized void takeMenge(ID ressource, long amount)
	{
		if(amount == 0) return;
		
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item == null) return;
		
		item.takeMenge(amount);
	}
	
	/* Hinzufügen oder herausnehmen von Materialien */
	public synchronized void addMenge(ID ressource, long menge, boolean allowOversize)
	{
		if(menge < 0)
			takeMenge(ressource, menge);
		
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item == null)
		{
			item = new NaturalRessourcesStorageItem(new Hashtable(), ressource);
			item.insertInDB(this, true);
		}

		item.addMenge(menge, allowOversize);
	}
	
	public synchronized void setOversizeMenge(ID ressource, long menge)
	{
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item == null)
		{
			item = new NaturalRessourcesStorageItem(new Hashtable(), ressource);
			item.insertInDB(this, true);
		}

		item.setOversizeMenge(menge);
	}

	public synchronized void registerStorage(ID ressource, ID storage)
	{
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item == null)
		{
			item = new NaturalRessourcesStorageItem(new Hashtable(), ressource);
			item.insertInDB(this, true);
		}
		item.addToStorageList(storage);
	}
	
	public synchronized void removeStorage(ID ressource, ID storage)
	{
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item != null)
		{
			item.removeFromStorageList(storage);
		}
	}
	
	public long getOversizeMenge(ID ressource)
	{
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
		if(item == null) return 0;
		
		return item.getOversizeMenge().longValue();
	}
	
//	public void addMenge(ID ressource, long menge)
//	{
//		/*
//		 * Überlaufwert abfragen
//		 */
//		if(menge > 0 && (getMenge(ressource).longValue() + menge) < 0)
//			setMenge(ressource, MAX_WERT);
//		else
//			setMenge(ressource, new Long(getMenge(ressource).longValue() + menge));
//	}
//
//	public void setMenge(ID ressource, Number menge)
//	{
//		synchronized(getLockObject())
//		{
//			if(menge == null || menge.longValue() < 0)
//				removeData(ressource);
//			else
//			{
//				NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) getData(ressource);
//				if(item == null)
//				{
//					item = new NaturalRessourcesStorageItem(new Hashtable(), ressource);
//					item.insertInDB(this, true);
//				}
//				item.setMenge(menge);
//			}
//		}
//	}

	/**
	 * Liefert Iterator sichere Collection mit NaturalRessourcesStorageItem Objekten
	 * 
	 * @return
	 */
	public Map getAllRessources()
	{
		return getAllDBObjects();
	}
}