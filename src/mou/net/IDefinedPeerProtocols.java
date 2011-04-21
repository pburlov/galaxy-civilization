/*
 * $Id$
 * Created on Apr 22, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net;


public interface IDefinedPeerProtocols
{
	static final public byte JOIN_PROTOCOL = 0;
	static final public byte BROADCAST_PEER_HANDLE_PROTOCOL = 1;
	static final public byte PEER_EXIT_PROTOCOL = 2;
	static final public byte ROUTING_PROTOCOL = 3;
	static final public byte SEND_DATA_PROTOCOL = 4;
	static final public byte ROUTER_PROTOCOL = 9;
}
