/*
 * $Id$
 * Created on May 6, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import mou.net2.PeerHandle;


public class SerializationUtils
{

	protected SerializationUtils()
	{
		super();
	}
	
	static final public void writeString(String str, DataOutput out) throws IOException
	{
		if(str == null)
		{
			out.writeShort(0);
			return;
		}
		out.writeShort(str.length());
		out.write(str.getBytes());
	}

	static final public String readString(DataInput in) throws IOException
	{
		int lenght = in.readUnsignedShort();
		if(lenght < 0) throw new IOException("Length of string < 0");
		byte[] buf = new byte[lenght];
		in.readFully(buf);
		return new String(buf);
	}

	static final public void writePeerHandle(PeerHandle handle, DataOutput dOut) throws IOException
	{
		dOut.writeLong(handle.getId().longValue());
		dOut.write(handle.getInetSocketAddr().getAddress().getAddress());
		dOut.writeInt(handle.getInetSocketAddr().getPort());
	}

	static final public PeerHandle readPeerHandle(DataInput dataIn) throws IOException
	{
		long peerId = dataIn.readLong();
		byte[] byteAdr = new byte[4];
		dataIn.readFully(byteAdr);
		int port = dataIn.readInt();
		InetAddress adr = InetAddress.getByAddress(byteAdr);
		return new PeerHandle(peerId, new InetSocketAddress(adr, port));
	}
}
