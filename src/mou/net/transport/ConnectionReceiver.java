/*
 * $Id$
 * Created on Apr 8, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.net.InetSocketAddress;


public interface ConnectionReceiver
{
	/**
	 * Wenn die Methode returned wird, dann wird auch der Connection-Objekt
	 * invalid, damit die TCP-Verbindung fuer andere frei wird.
	 * Wenn eine Exception geworfen wird, dann wird die physikalische TCP-Verbindung
	 * geschlossen.
	 * @param con
	 */
	public void receiveConnection(Connection con);
	
	/**
	 *  Wenn Verbundungsaufbau fehlgeschlagen ist.
	 * @param target
	 * @param cause
	 */
	public void requestFailed(InetSocketAddress target, Exception cause);
}
