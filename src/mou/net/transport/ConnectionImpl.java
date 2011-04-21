/*
 * $Id$ Created on Apr 8, 2006 Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;

class ConnectionImpl
		implements Connection
{
	private ConnectionInputStream in;
	private ConnectionOutputStream out;
	private InetSocketAddress target;
	private ConnectionManager manager;
	private boolean closed = false;

	/**
	 * @param inputStream
	 * @param outputStream
	 */
	public ConnectionImpl(ConnectionManager manager, InetSocketAddress target, DataInputStream inputStream, DataOutputStream outputStream)
	{
		this.manager = manager;
		this.target = target;
		in = new ConnectionInputStream(inputStream);
		out = new ConnectionOutputStream(outputStream);
	}

	public ConnectionImpl()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.net.p2p.Connection#getIn()
	 */
	public ConnectionInputStream getIn()
	{
		return in;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.net.p2p.Connection#getOut()
	 */
	public ConnectionOutputStream getOut()
	{
		return out;
	}

	public void close()
	{
		if(closed)return;
		closed = true;
		try
		{
			in.close();
			out.close();
		} catch(Exception e)
		{
		}
		manager.closeConnection(this);
	}

	public InetSocketAddress getTarget()
	{
		return target;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		close();
	}
}
