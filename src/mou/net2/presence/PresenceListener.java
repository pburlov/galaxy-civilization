/*
 * $Id$
 * Created on May 28, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.presence;

import mou.net2.PeerHandle;


public interface PresenceListener
{
	public void peerIsOnline(PeerHandle handle);
	public void peerIsOffline(PeerHandle handle);
}
