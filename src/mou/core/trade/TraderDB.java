/*
 * $Id: TraderDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.trade;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.core.db.AbstractDB;
import mou.core.ship.Ship;
import mou.core.ship.ShipDB;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * Dieser Datenbank speichert die Handelanweisungen f?r nat?rliche Ressourcen
 * 
 * @author pb
 */
public class TraderDB extends AbstractDB
{

	/*
	 * Faktor wird mit der Herstellungskosten multipliziert. Daraus resultiert der Verkaufspreis.
	 * Der Verk?fer kriegt aber nur Herstellungskosten zur?ck. Der Unterschied sind die
	 * Provisionskosten.
	 */
	static final private double BUY_PRICE_FAKTOR = 1.1;
	static final private String LOCAL_SHIP_OFFERS = "LOCAL_SHIP_OFFERS";
	static final private String SELL_ON_ALLY_ONLY = "SELL_ON_ALLY_ONLY";
	private final Vector<ITradeOfferListener> localTradeOfferListeners = new Vector<ITradeOfferListener>();
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();

	/**
	 * @param data
	 */
	public TraderDB(Map data)
	{
		super(data);
	}

	public void addLocalTradeOfferListener(ITradeOfferListener listener)
	{
		localTradeOfferListeners.add(listener);
	}

	private void fireLocalOfferChangedEvent(LocalShipTradeOffer offer)
	{
		for(Iterator iter = localTradeOfferListeners.iterator(); iter.hasNext();)
		{
			ITradeOfferListener listener = (ITradeOfferListener) iter.next();
			listener.shipOfferChanged(offer);
		}
	}

	private void fireLocalOfferAddedEvent(LocalShipTradeOffer offer)
	{
		for(Iterator iter = localTradeOfferListeners.iterator(); iter.hasNext();)
		{
			ITradeOfferListener listener = (ITradeOfferListener) iter.next();
			listener.shipOfferAdded(offer);
		}
	}

	private void fireLocalOfferRemovedEvent(LocalShipTradeOffer offer)
	{
		for(Iterator<ITradeOfferListener> iter = localTradeOfferListeners.iterator(); iter.hasNext();)
		{
			ITradeOfferListener listener = iter.next();
			listener.shipOfferRemoved(offer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "TradeDB";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		// keine Extraklasse
		return String.class;
	}

	private Map<ID, Map> getShipOffersData()
	{
		Map<ID, Map> ret = (Map<ID, Map>) getSecondaryMapData().get(LOCAL_SHIP_OFFERS);
		if(ret == null)
		{
			ret = new HashMap<ID, Map>();
			getSecondaryMapData().put(LOCAL_SHIP_OFFERS, ret);
		}
		return ret;
	}

	public LocalShipTradeOffer getShipOfferForShipClass(ID shipClassID)
	{
		return getShipOffer(shipClassID);
	}

	public List<RemoteShipTradeOffer> getShipOffers()
	{
		List<LocalShipTradeOffer> localOffers = getLocalShipTradeOffers();
		ArrayList<RemoteShipTradeOffer> ret = new ArrayList<RemoteShipTradeOffer>(localOffers.size());
		for(LocalShipTradeOffer offer : localOffers)
			ret.add(new RemoteShipTradeOffer(offer));
		return ret;
	}

	private List<LocalShipTradeOffer> getLocalShipTradeOffers()
	{
		synchronized(getLockObject())
		{
			Map<ID, Map> offers = getShipOffersData();
			List<LocalShipTradeOffer> ret = new ArrayList<LocalShipTradeOffer>(offers.size());
			for(Iterator<Map> iter = offers.values().iterator(); iter.hasNext();)
			{
				Map data = iter.next();
				LocalShipTradeOffer offer = new LocalShipTradeOffer(data);
				if(offer.getQuantity().intValue() <= 0)
				{
					iter.remove();
				} else
					ret.add(offer);
			}
			return ret;
		}
	}

	/**
	 * Methode wird aufgerufen um eigene Schiffe zu verkaufen
	 * 
	 * @param ships
	 *            Collection mit Ship-Objekten
	 */
	public void sellShips(List<Ship> ships)
	{
		Set<ID> changedOffers = null;
		Set<ID> addedOffers = null;
		synchronized(getLockObject())
		{
			for(Ship ship : ships)
			{
				LocalShipTradeOffer offer = getShipOfferForShipClass(ship.getShipClassID());
				if(offer == null)
				{// Noch kein Eintrag f?r diese Schiffsklasse
					offer = new LocalShipTradeOffer(ship, new Integer(1), new Double(ship.getBuildCost() * BUY_PRICE_FAKTOR));
					getShipOffersData().put(offer.getID(), offer.getObjectData());
					if(addedOffers == null) addedOffers = new HashSet<ID>();
					addedOffers.add(ship.getShipClassID());
				} else
				{
					offer.addQuantity(1);
					if(changedOffers == null) changedOffers = new HashSet<ID>();
					changedOffers.add(ship.getShipClassID());
				}
				getMOUDB().getShipDB().deleteShip(ship.getID());
			}
		}
		if(changedOffers != null)
		{
			for(Iterator iter = changedOffers.iterator(); iter.hasNext();)
			{
				ID id = (ID) iter.next();
				LocalShipTradeOffer offer = getShipOffer(id);
				if(offer != null) fireLocalOfferChangedEvent(offer);
			}
		}
		if(addedOffers != null)
		{
			for(Iterator iter = addedOffers.iterator(); iter.hasNext();)
			{
				ID id = (ID) iter.next();
				LocalShipTradeOffer offer = getShipOffer(id);
				if(offer != null) fireLocalOfferAddedEvent(offer);
			}
		}
		// /*
		// * Verkaufserl?s gutschreiben
		// */
		// double summe = estimateSellPrice(ships);
		// getMOUDB().getCivilizationDB().addMoney(summe);
		// /*
		// * Schiffe l?schen
		// */
		// for(Ship ship : ships)
		// {
		// getMOUDB().getShipDB().deleteShip(ship.getID());
		// }
		// return summe;
	}

	/**
	 * Methode berechnet den voraussichtlichen Verkaufserl?s von den Schiffen
	 * 
	 * @param ships
	 * @return
	 */
	public double estimateSellPrice(List<Ship> ships)
	{
		double ret = 0;
		for(Ship s : ships)
		{
			ret += s.getBuildCost();
		}
		return ret * BUY_PRICE_FAKTOR;
	}

	public LocalShipTradeOffer getShipOffer(ID offerID)
	{
		Map data = getShipOffersData().get(offerID);
		LocalShipTradeOffer ret = null;
		if(data != null) ret = new LocalShipTradeOffer(data);
		return ret;
	}

	/**
	 * Methode wird vom TradeServer aufgerufen, wenn eine fremde Zivilisation lokale Schiffe kaufen
	 * will
	 * 
	 * @param offerId
	 * @param anzahl
	 * @return Anzahl der tats?chlich gekauften Schiffe
	 */
	public int buyShips(ID offerId, int anzahl, ID buyer)
	{
		synchronized(getLockObject())
		{
			LocalShipTradeOffer offer = getShipOffer(offerId);
			if(offer == null) return 0;
			if(offer.getQuantity().intValue() < anzahl) anzahl = offer.getQuantity().intValue();
			int val = offer.addQuantity(-anzahl).intValue();
			if(val > 0) fireLocalOfferChangedEvent(offer);
			if(val <= 0)
			{
				getShipOffersData().remove(offer.getID());
				fireLocalOfferRemovedEvent(offer);
			}
			/*
			 * Den Verkaufserl?s gutschreiben
			 */
			double gewinn = offer.getShip().getBuildCost() * anzahl;
			civDB.addMoney(gewinn);
			Main.instance().getGUI().promtMessage("Schiffe verkauft",
					"Zivilisation \"" + civDB.getCivName(buyer) + "\" hat " + anzahl + " Schiff(e) von dir gekauft. Erlös: " + GUI.formatSmartDouble(gewinn),
					GUI.MSG_PRIORITY_NORMAL);
			return anzahl;
		}
	}

	/**
	 * Methode wird aufgerufen um Schiffe aus lokalen Angebot selbst zurück zu kaufen.
	 * 
	 * @param offerId
	 * @param anzahl
	 */
	public void buyShipsLocal(ID offerId, int anzahl)
	{
		synchronized(getLockObject())
		{
			ShipDB shipDB = getMOUDB().getShipDB();
			final Point pos = shipDB.getDefaultFleetCollectorPoint();
			LocalShipTradeOffer offer = getShipOfferForShipClass(offerId);
			anzahl = buyShips(offerId, anzahl, civDB.getMyCivilizationID());
			civDB.addMoney(-offer.getPrice().doubleValue() * anzahl);
			/*
			 * Man kann nur soviel kaufen, wieviel tats?chlich zur Kaufzeit verf?gbar
			 */
			for(int i = 0; i < anzahl; i++)
			{
				shipDB.addNewShip(offer.getShip(), pos);
			}
			Main.instance().getGUI().promtMessage("Kaufen", anzahl + " Schiff(e) gekauft und bei der Position " + GUI.formatPoint(pos) + " stationiert",
					GUI.MSG_PRIORITY_NORMAL, new Runnable()
					{

						public void run()
						{
							Main.instance().getGUI().centreStarmaponPosition(pos);
						}
					});
		}
	}

	/**
	 * Dieses Wert entscheidet ob Schiffe nur an alliierte Zivs verkauft werden sollen
	 * 
	 * @param value
	 */
	public void setSellOnAllyOnly(boolean value)
	{
		getSecondaryMapData().put(SELL_ON_ALLY_ONLY, new Boolean(value));
	}

	/**
	 * Dieses Wert entscheidet ob Schiffe nur an alliierte Zivs verkauft werden sollen
	 * 
	 * @param value
	 */
	public boolean isSellOnAllyOnly()
	{
		boolean ret = false;
		Boolean val = (Boolean) getSecondaryMapData().get(SELL_ON_ALLY_ONLY);
		if(val != null) ret = val.booleanValue();
		return ret;
	}

	/**
	 * @param buyer
	 * @return true wenn Verkauf an diese Zivilisation erlaubt ist
	 */
	public boolean isSellAllowed(ID buyer)
	{
		/*
		 * An Feinde wird siwieso nicht verkauft
		 */
		if(civDB.getFeindlichCivs().contains(buyer)) return false;
		/*
		 * 
		 */
		if(isSellOnAllyOnly() && !civDB.getAlliiertCivs().contains(buyer)) return false;
		return true;
	}
}
