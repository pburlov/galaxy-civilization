/*
 * $Id: ITradeOfferListener.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.trade;

/**
 * @author pb
 */
public interface ITradeOfferListener
{

	/**
	 * Mit dem Aufruf dieser Methode werden die Listeners benachrichtig, dass es Änderungen an dem
	 * Schiffsangebot gab.
	 */
	public void shipOfferAdded(LocalShipTradeOffer offer);

	public void shipOfferRemoved(LocalShipTradeOffer offer);

	public void shipOfferChanged(LocalShipTradeOffer offer);
}
