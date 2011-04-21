/*
 * $Id$
 * Created on Apr 8, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ConnectionOutputStream extends DataOutputStream
{
	private boolean closed = false;
	
	public ConnectionOutputStream(OutputStream out)
	{
		super(out);
	}

	@Override
	public void close() throws IOException
	{
		flush();
		closed = true;
	}
	
	private void checkClosed()throws IOException
	{
		if(closed)throw new IOException("Stream is closed");
	}

	@Override
	public void flush() throws IOException
	{
		checkClosed();
		super.flush();
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException
	{
		checkClosed();
		super.write(b, off, len);
	}

	@Override
	public synchronized void write(int b) throws IOException
	{
		checkClosed();
		super.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		checkClosed();
		super.write(b);
	}
}
