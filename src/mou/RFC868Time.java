/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Klasse dient dazu die SNTP-Zeit abzufragen
 * 
 * @author PB
 */
public class RFC868Time
{

	static final private String[] SERVERS = { "ptbtime1.ptb.de", "ptbtime2.ptb.de", "time-a.nist.gov", "time-b.nist.gov"};// Liste
																															// der
																															// Zeitserver
																															// im
																															// Internet
	static final int ATTEMP_NUMBER = 3; // 3 Mal versuchen zu konnekten
	static final int SOCKET_TIMEOUT = 1000;
	static final int TIME_PORT = 37;
	private DatagramSocket mSocket;

	/**
	 * 
	 */
	public RFC868Time() throws IOException
	{
		mSocket = new DatagramSocket();
		mSocket.setSoTimeout(SOCKET_TIMEOUT);
	}

	/**
	 * Liefert die Netz-Zeit
	 * 
	 * @return long[0] Lokale Zeit in Millisekunden, long[1] dazugehorige Netzzeit in Millisekunden
	 *         seit 00:00 1. Januar 1900
	 * @throws IOException
	 */
	public long[] getTime() throws IOException
	{
		for(int a = 0; a < ATTEMP_NUMBER; a++)
		{
			for(int i = 0; i < SERVERS.length; i++)
			{
				byte[] buf = new byte[8];
				DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(SERVERS[i]), TIME_PORT);
				long sendTime = 0;
				try
				{
					mSocket.send(packet);
					sendTime = System.currentTimeMillis();
					mSocket.receive(packet);
				} catch(IOException e)
				{// Timeout, nächste Adresse versuchen
					continue;
				}
				long time = 0;
				int wert = toUnsignedInt(packet.getData()[0]);
				time = time | wert;
				time = time << 8;
				wert = toUnsignedInt(packet.getData()[1]);
				time = time | wert;
				time = time << 8;
				wert = toUnsignedInt(packet.getData()[2]);
				time = time | wert;
				time = time << 8;
				wert = toUnsignedInt(packet.getData()[3]);
				time = time | wert;
				long[] ret = new long[2];
				ret[0] = sendTime;// Lokale Zeit in Millisekunden
				ret[1] = time * 1000; // und dazugehörige Netz-Zeit.
				return ret;
			}
		}
		throw new IOException("Kann keiner der Zeitserver erreichen.");
	}

	private static int toUnsignedInt(byte value)
	{
		return (value & 0x7F) + (value < 0 ? 128 : 0);
	}
}
