/*
 * $Id$
 * Created on Apr 9, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;


public interface IConnectorListener
{
	/**
	 * Wenn eine Exception geworfen wird, dann wird der Socket geschlossen
	 * @param socket
	 * @throws IOException
	 */
	public void connectionAccepted(Socket socket)throws IOException;
	public void datagramPacketReceived(DatagramPacket packet);
}
