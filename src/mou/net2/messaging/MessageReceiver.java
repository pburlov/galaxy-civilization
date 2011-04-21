/*
 * $Id$
 * Created on May 28, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.messaging;

import java.io.IOException;
import java.net.InetSocketAddress;


public interface MessageReceiver
{
	public void messageReceived(InetSocketAddress source, byte[] msg)throws IOException;
}
