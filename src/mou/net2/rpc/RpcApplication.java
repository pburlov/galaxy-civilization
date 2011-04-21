/*
 * $Id$
 * Created on Jun 2, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.rpc;

import org.jboss.remoting.ServerInvocationHandler;


public interface RpcApplication extends ServerInvocationHandler
{
//	abstract public MarshalerAbstract getMarshaler();
	abstract public String getApplicationName();
}
