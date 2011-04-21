/*
 * $Id$
 * Created on Apr 8, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;

public interface Connection
{

	public abstract DataInputStream getIn();

	public abstract DataOutputStream getOut();
	
	public abstract InetSocketAddress getTarget();
}