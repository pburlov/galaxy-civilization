/*
 * $Id$
 * Created on Mar 26, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.net.InetSocketAddress;


public interface SuccessListener
{
	public void success(Object message, InetSocketAddress target);
	public void failed(Object message, InetSocketAddress target, Throwable cause);
}
