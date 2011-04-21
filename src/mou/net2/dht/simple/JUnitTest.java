/*
 * $Id$
 * Created on Jul 16, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht.simple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import junit.framework.TestCase;
import mou.net2.PeerHandle;
import mou.net2.dht.DhtException;


public class JUnitTest extends TestCase
{
	private WebserverDHT dht;
	private Random rnd = new Random();

	public void setUp() throws Exception
	{
		byte[] secret = new byte[10];
		rnd.nextBytes(secret);
		dht = new WebserverDHT(secret, Logger.getAnonymousLogger());
	}
	
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		dht.close();
	}

	public void testTTL() throws DhtException, InterruptedException
	{
		byte[] key = new byte[20];
		rnd.nextBytes(key);
		byte[] value = new byte[200];
		rnd.nextBytes(value);
		dht.put(key, value, 5, false);
		
		List<byte[]> ret = dht.get(key, 10);
		assertEquals(1,ret.size());
		assertTrue(Arrays.equals(value, ret.get(0)));
		
		Thread.sleep(7000);
		
		ret = dht.get(key, 10);
		assertEquals(0,ret.size());
	}

	public void testAutorefresh() throws DhtException, InterruptedException
	{
		byte[] key = new byte[20];
		rnd.nextBytes(key);
		byte[] value = new byte[200];
		rnd.nextBytes(value);
		dht.put(key, value, 5, true);
		
		List<byte[]> ret = dht.get(key, 10);
		assertEquals(1,ret.size());
		assertTrue(Arrays.equals(value, ret.get(0)));
		
		Thread.sleep(7000);
		
		ret = dht.get(key, 10);
		assertTrue(ret.size() > 0);
		assertTrue(Arrays.equals(value, ret.get(0)));
	}
	public void testPutGetRemove() throws IOException
	{
		byte[] key = new byte[20];
		rnd.nextBytes(key);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bout);
		PeerHandle handle = new PeerHandle(rnd.nextLong(), new InetSocketAddress(InetAddress.getLocalHost(), 1234));
		handle.writeExternal(out);
		out.close();
		byte[] value = bout.toByteArray();
		dht.put(key, value, 10, false);
		
		List<byte[]> ret = dht.get(key, 10);
		assertEquals(1,ret.size());
		assertTrue(Arrays.equals(value, ret.get(0)));
		
		dht.remove(key);
		ret= dht.get(key, 10);
		assertEquals(0,ret.size());
	}
	
	public void testTooLongData()
	{
		long start = System.currentTimeMillis();
		byte[] key = new byte[WebserverDHT.MAX_KEY_SIZE + 1];
		rnd.nextBytes(key);
		byte[] value = new byte[WebserverDHT.MAX_VALUE_SIZE];
		rnd.nextBytes(value);
		try
		{
			dht.put(key, value, 10, false);
			fail();
		} catch(DhtException e)
		{
			System.out.println("Its ok! "+e.getLocalizedMessage());
		}

		key = new byte[WebserverDHT.MAX_KEY_SIZE];
		rnd.nextBytes(key);
		value = new byte[WebserverDHT.MAX_VALUE_SIZE + 1];
		rnd.nextBytes(value);
		try
		{
			dht.put(key, value, 10, false);
			fail();
		} catch(DhtException e)
		{
			System.out.println("Its ok! "+e.getLocalizedMessage());
		}
		System.out.println("Time: "+(System.currentTimeMillis() - start));
}
}
