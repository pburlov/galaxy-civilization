/*
 * $Id$
 * Created on Apr 8, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ConnectionInputStream extends DataInputStream
{
	private boolean closed = false;
	
	public ConnectionInputStream(InputStream in)
	{
		super(in);
	}
	private void checkClosed()throws IOException
	{
		if(closed)throw new IOException("Stream is closed");
	}
	@Override
	public int available() throws IOException
	{
		checkClosed();
		return super.available();
	}
	@Override
	public void close() throws IOException
	{
		closed = true;
	}
	public int read() throws IOException
	{
		checkClosed();
		return super.read();
	}
	@Override
	public synchronized void reset() throws IOException
	{
		checkClosed();
		super.reset();
	}
	@Override
	public long skip(long n) throws IOException
	{
		checkClosed();
		return super.skip(n);
	}

	public boolean isClosed()
	{
		return closed;
	}
}
