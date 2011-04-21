/*
 * $Id$
 * Created on May 28, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.rpc;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import mou.net2.PeerHandle;
import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;


/**
 * @author paul
 */
abstract public class MarshalerAbstract<O> implements Marshaller, UnMarshaller
{
	private ClassLoader classLoader;

	public void write(Object dataObject, OutputStream output) throws IOException
	{
		write(dataObject, new DataOutputStream(output));
	}

	public Marshaller cloneMarshaller() throws CloneNotSupportedException
	{
		return getNewInstance();
	}

	public Object read(InputStream inputStream, Map metadata) throws IOException, ClassNotFoundException
	{
		return read(new DataInputStream(inputStream));
	}

	public UnMarshaller cloneUnMarshaller() throws CloneNotSupportedException
	{
		return getNewInstance();
	}
	
	
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
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
	
	abstract protected MarshalerAbstract getNewInstance();
	abstract protected void write(O object, DataOutput out)throws IOException;
	abstract protected O read(DataInput in)throws IOException;
}
