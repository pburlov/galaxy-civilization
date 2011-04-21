/*
 * $Id: RemoteShipTradeOffer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.trade;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mou.core.ship.Ship;
import mou.storage.ser.ID;
import mou.util.SerializationUtils;

/**
 * @author pb
 */
public class RemoteShipTradeOffer
{

	private long idVariablePart;
	private long sellerCiv;// Id des Verkäfers
	private long idConstantPart;// Id der Herstellercivilisation
	private int quantity;
	private float masse;
	private float panzer;
	private float shild;
	private float weapon;
	private float crew;
	private float speed;
	private float support;
	private float price;
	private String name;
	// transient private ID shipClassID;
	transient private ID offerID;

	public RemoteShipTradeOffer(LocalShipTradeOffer localOffer)
	{
		idVariablePart = localOffer.getID().getVariablePart();
		idConstantPart = localOffer.getID().getConstantPart();
		quantity = localOffer.getQuantity().intValue();
		price = localOffer.getPrice().floatValue();
		Ship ship = localOffer.getShip();
		masse = (float) ship.getMasse();
		panzer = (float) ship.getArmor();
		shild = (float) ship.getShild();
		weapon = (float) ship.getWeapon();
		crew = (float) ship.getCrew();
		speed = (float) ship.getSpeed();
		support = (float) ship.getSupportCost();
		name = ship.getShipClassName();
	}

	public ID getOfferID()
	{
		if(offerID == null) offerID = new ID(idVariablePart, idConstantPart);
		return offerID;
	}

	// public ID getShipClassID()
	// {
	// if(shipClassID == null)shipClassID = new ID(id,civId);
	// return shipClassID;
	// }
	/**
	 * 
	 */
	protected RemoteShipTradeOffer()
	{
		super();
	}

	public long getSellerCiv()
	{
		return sellerCiv;
	}

	public float getCrew()
	{
		return crew;
	}

	public float getMasse()
	{
		return masse;
	}

	public String getName()
	{
		return name;
	}

	public float getPanzer()
	{
		return panzer;
	}

	public float getPrice()
	{
		return price;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public float getShild()
	{
		return shild;
	}

	public float getSpeed()
	{
		return speed;
	}

	public float getSupport()
	{
		return support;
	}

	public float getWeapon()
	{
		return weapon;
	}

	/**
	 * Serializiert alle interne Variable
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void serialize(DataOutput out) throws IOException
	{
		out.writeLong(idConstantPart);
		out.writeLong(idVariablePart);
		out.writeInt(quantity);
		out.writeFloat(masse);
		out.writeFloat(panzer);
		out.writeFloat(shild);
		out.writeFloat(weapon);
		out.writeFloat(crew);
		out.writeFloat(speed);
		out.writeFloat(support);
		out.writeFloat(price);
		SerializationUtils.writeString(name,out);
	}

	/**
	 * Deserialiisert alle interne Variablen
	 * 
	 * @param in
	 * @throws IOException
	 */
	private void deserialize(DataInput in) throws IOException
	{
		idConstantPart = in.readLong();
		idVariablePart = in.readLong();
		quantity = in.readInt();
		masse = in.readFloat();
		panzer = in.readFloat();
		shild = in.readFloat();
		weapon = in.readFloat();
		crew = in.readFloat();
		speed = in.readFloat();
		support = in.readFloat();
		price = in.readFloat();
		name = SerializationUtils.readString(in);
	}

	static public void serializeTradeOffers(DataOutput out, long sellerCiv, Collection<RemoteShipTradeOffer> offers) throws IOException
	{
		out.writeLong(sellerCiv);
		out.writeInt(offers.size());
		for(RemoteShipTradeOffer offer : offers)
			offer.serialize(out);
	}

	static public List<RemoteShipTradeOffer> deserializeTradeOffers(DataInput in) throws IOException
	{
		long sellerCiv = in.readLong();
		int size = in.readInt();
		ArrayList<RemoteShipTradeOffer> ret = new ArrayList<RemoteShipTradeOffer>(size);
		for(int i = 0; i < size; i++)
		{
			RemoteShipTradeOffer offer = new RemoteShipTradeOffer();
			offer.deserialize(in);
			offer.sellerCiv = sellerCiv;
			ret.add(offer);
		}
		return ret;
	}
}
