/*
 * $Id: LocalShipOffersTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.tradescreen;

import mou.Main;
import mou.core.trade.ITradeOfferListener;
import mou.core.trade.LocalShipTradeOffer;
import mou.core.trade.TraderDB;

/**
 * @author pb
 */
public class LocalShipOffersTableModel extends ShipOffersTableModel
		implements ITradeOfferListener
{

	private TraderDB tradeDB = Main.instance().getMOUDB().getTraderDB();

	/**
	 * 
	 */
	public LocalShipOffersTableModel()
	{
		super(Main.instance().getMOUDB().getTraderDB().getShipOffers());
		tradeDB.addLocalTradeOfferListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.ITradeDBListener#shipOfferAdded(mou.db.ShipTradeOffer)
	 */
	public void shipOfferAdded(LocalShipTradeOffer offer)
	{
		setRowData(tradeDB.getShipOffers());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.ITradeDBListener#shipOfferRemoved(mou.db.ShipTradeOffer)
	 */
	public void shipOfferRemoved(LocalShipTradeOffer offer)
	{
		setRowData(tradeDB.getShipOffers());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.ITradeDBListener#shipOfferChanged(mou.db.ShipTradeOffer)
	 */
	public void shipOfferChanged(LocalShipTradeOffer offer)
	{
		setRowData(tradeDB.getShipOffers());
	}
}