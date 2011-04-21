/*
 * $Id: NaturalRessourcesStorageItem.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.civilization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mou.Main;
import mou.core.db.DBObjectImpl;
import mou.core.res.natural.NaturalResource;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class NaturalRessourcesStorageItem extends DBObjectImpl
{
	static final private String ATTR_OVERSIZE = "ATTR_OVERSIZE";
	static final private String ATTR_STORAGE_LIST = "ATTR_STORAGE_LIST";

	static final public long MAX_OVERSIZE = (long) 20E6; //Maximaler Oversize pro Rohstoff
	
	/**
	 * 
	 */
	public NaturalRessourcesStorageItem()
	{
		super();
	}

	// /**
	// * @param data
	// */
	// public NaturalRessourcesStorageItem(Map data)
	// {
	// super(data);
	// }
	//
	/**
	 * @param data
	 * @param id
	 */
	public NaturalRessourcesStorageItem(Map data, ID id)
	{
		super(data, id);
	}

	/**
	 * 
	 * @return Menge des von diesem Rohstoffs, der auf allen Kolonien zusammen vorhanden ist
	 */
	public long getMenge()
	{
		int ret = 0;
		
		ret += getOversizeMenge().intValue();
		
		for(ID storage : getStorageList())
		{
			ret += Main.instance().getMOUDB().getKolonieDB().getKolonie(storage).computeMaterialInStorage(getID());
		}
		
		return ret;
	}
	
	public synchronized void takeMenge(long amount)
	{
		long rest = amount;
		long oversize = getOversizeMenge().intValue();
		
		if(oversize >= rest)
		{
			setOversizeMenge(getOversizeMenge().intValue()-rest);
			return;
		}
		else
		{
			setOversizeMenge(0);
			rest -= oversize;
		}
		
		for(ID storage : getStorageList())
		{
			rest += Main.instance().getMOUDB().getKolonieDB().getKolonie(storage).addMaterialToStorage(getID(), -rest);
			if(rest <= 0) return;
		}

		//Outsch didn't you check the stored amount before?
		return;
	}
	
	public synchronized void addMenge(long amount, boolean allowOversize)
	{
		if(amount < 0)
			takeMenge(amount);
		
		long rest = amount;
		
		for(ID storage : getStorageList())
		{
			rest -= Main.instance().getMOUDB().getKolonieDB().getKolonie(storage).addMaterialToStorage(getID(), rest);
			if(rest <= 0) return;
		}
		
		if(!allowOversize) return;
		setOversizeMenge(getOversizeMenge().longValue()+rest);
	}
	
	/* Beim abreißen eines Lagerhaus oder beim Verlust der Kolonie bleiben
	 * die Rohstoffe erhalten, um die Versorgung zu gewährleisten
	 */ 
	public synchronized Number getOversizeMenge()
	{
		return (Number) getAttribute(ATTR_OVERSIZE, ZERO_DOUBLE);
	}
	
	public synchronized void setOversizeMenge(Number menge)
	{
			long maxOversize = MAX_OVERSIZE - computeStorageSpace();
			if((menge == null)||(menge.longValue() == 0)||(maxOversize <= 0))
			{
				removeAttribute(ATTR_OVERSIZE, true);
				return;
			}
	
			if(menge.longValue() < maxOversize)
				setAttribute(ATTR_OVERSIZE, menge, true);
			else
				setAttribute(ATTR_OVERSIZE, maxOversize, true);
	}
	
	public long computeStorageSpace()
	{
		long ret = 0;
		
		for(ID storage : getStorageList())
		{
			ret += Main.instance().getMOUDB().getKolonieDB().getKolonie(storage).computeMaterialStorageSpace(getID());
		}
		
		return ret;
	}
	
	private Set<ID> getStorageList()
	{
		Set<ID> data = (Set<ID>) getAttribute(ATTR_STORAGE_LIST);
		if(data == null)
		{
			data = (Set<ID>) Collections.synchronizedSet(new HashSet());
			setAttribute(ATTR_STORAGE_LIST, data, false);
		}
		return data;
	}

	public void addToStorageList(ID storage)
	{
		getStorageList().add(storage);
	}
	
	public void removeFromStorageList(ID ressource)
	{
		getStorageList().remove(ressource);
	}

	public NaturalResource getNaturalResource()
	{
		return Main.instance().getMOUDB().getNaturalRessourceDescriptionDB().getNaturalResource(getID());
	}
	
	@Override
	public void removeAttribute(String name, boolean fireChangeEvent)
	{
		super.removeAttribute(name, fireChangeEvent);
	}

	/* Unused
	public Float getMarketPrice()
	{// TODO: Realen Marktpreis abfragen
		return new Float(1.0123456789);
	}
	*/

	/**
	 * Gibt den allzeit Hoch bei dem Marktpreis
	 * 
	 * @return
	 */
	/* Unused
	public Float getAllzeitHochpreis()
	{
		return new Float(1000);
	}
	*/
	/**
	 * Gibt den allzeit Tief bei dem Marktpreis
	 * 
	 * @return
	 */
	/* Unused
	public Float getAllzeitTiefpreis()
	{
		return new Float(0.0);
	}
	*/
}
