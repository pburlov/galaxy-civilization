/*
 * $Id$
 * Created on May 6, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe;

import mou.net2.PeerHandle;


public interface TopicListener
{
	/**
	 * Methode wird aufgerufen wenn ein entfernte Peer offline gegangen ist
	 * oder nicht mehr Subscriber ist
	 * @param handle
	 * @param topic
	 */
	abstract public void peerJoined(PeerHandle handle, Topic topic);
	abstract public void receiveMessage(PeerHandle source, Topic topic, Object msg);
}
