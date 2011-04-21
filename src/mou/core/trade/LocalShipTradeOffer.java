/*
 * $Id: LocalShipTradeOffer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.trade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mou.Main;
import mou.core.IDable;
import mou.core.MapWrapper;
import mou.core.ship.Ship;
import mou.storage.ser.ID;
import org.apache.commons.lang.SerializationUtils;

/**
 * @author pb
 */
public class LocalShipTradeOffer extends MapWrapper
		implements IDable
{

	static final private String ID = "ID";
	static final private String QUANTITY = "QUANTITY";
	static final private String PRICE = "PRICE";
	static final private String SHIP_OBJECT_DATA = "SHIP_OBJECT_DATA";

	/**
	 * 
	 */
	public LocalShipTradeOffer(Ship ship, Number quantity, Number price)
	{
		super(new HashMap());
		setAttribute(LocalShipTradeOffer.ID, ship.getShipClassID());
		setQuantity(quantity);
		setPrice(price);
		setAttribute(SHIP_OBJECT_DATA, ship.getObjectData());
	}

	public LocalShipTradeOffer(Map data)
	{
		super(data);
	}

	public Ship getShip()
	{
		Map data = (Map) getAttribute(SHIP_OBJECT_DATA);
		Ship ship = new Ship((Map) SerializationUtils.clone((Serializable) data));
		ship.resetID();
		return ship;
	}

	/**
	 * Hier wird die ID des SchiffClasses zurückgegeben
	 */
	public ID getID()
	{
		ID ret = (ID) getAttribute(ID);
		if(ret == null)
		{
			ret = new ID();
			setAttribute(LocalShipTradeOffer.ID, ret);
		}
		return ret;
	}

	public Number getQuantity()
	{
		return (Number) getAttribute(QUANTITY, Main.ZERO_NUMBER);
	}

	public void setQuantity(Number quantity)
	{
		setAttribute(QUANTITY, quantity);
	}

	Number addQuantity(int quantity)
	{
		Number ret = getQuantity();
		int val = ret.intValue();
		val += quantity;
		if(val < 0) val = 0;
		ret = new Integer(val);
		setQuantity(ret);
		return ret;
	}

	public Number getPrice()
	{
		return (Number) getAttribute(PRICE, Main.ZERO_NUMBER);
	}

	public void setPrice(Number price)
	{
		setAttribute(PRICE, price);
	}

	/**
	 * Methode überschriben um die Objekte mit ihren IDs zu vergleichen
	 */
	public boolean equals(Object obj)
	{
		LocalShipTradeOffer offer = (LocalShipTradeOffer) obj;
		return getID().equals(offer.getID());
	}
}
