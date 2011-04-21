/*
 * $Id: PeerHandle.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.net2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author pb
 */
public class PeerHandle
		implements Comparable, Externalizable
{

	static final private long serialVersionUID = 1;
	private Long id;
	private InetSocketAddress inetSocketAddr;

	/**
	 * Leere Konstruktor nur fuer Desrialiserung
	 */
	public PeerHandle()
	{
	}
	/**
	 * 
	 */
	public PeerHandle(long id, InetSocketAddress adr)
	{
		this.id = id;
		inetSocketAddr = adr;
	}

	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeLong(id.longValue());
		out.write(inetSocketAddr.getAddress().getAddress());
		out.writeInt(inetSocketAddr.getPort());
	}

	public void readExternal(ObjectInput dataIn) throws IOException, ClassNotFoundException
	{
		id = dataIn.readLong();
		byte[] byteAdr = new byte[4];
		dataIn.readFully(byteAdr);
		int port = dataIn.readInt();
		inetSocketAddr = new InetSocketAddress(InetAddress.getByAddress(byteAdr),port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		long id2 = ((PeerHandle) o).getId();
		if(id > id2) return 1;
		if(id < id2) return -1;
		return 0;
	}

	public boolean equals(Object arg0)
	{
		if(arg0 == null) return false;
		return getId().longValue() == ((PeerHandle) arg0).getId().longValue();
	}

	public int hashCode()
	{
		return (int) id.longValue();
	}

	public Long getId()
	{
		return id;
	}

	public InetSocketAddress getInetSocketAddr()
	{
		return inetSocketAddr;
	}

	/**
	 * MEthode berechnet Strecke mit Vorzeiche zu dem anderen Peer
	 * 
	 * @param p2
	 * @return
	 */
	public long getUnsignedDistance(PeerHandle p2)
	{
		return Math.abs(getSignedDistance(this, p2));
	}

	/**
	 * Methode berechnet Strecke mit Vorzeichen zwischen zwei Peers nach der Formel: p2 - p1
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	static public long getSignedDistance(PeerHandle p1, PeerHandle p2)
	{
		return p2.getId() - (p1.getId());
	}

	public String toString()
	{
		// return id.toString();
		return Long.toString(id) + " at " + inetSocketAddr.getHostName() + ":" + inetSocketAddr.getPort();
	}

}
