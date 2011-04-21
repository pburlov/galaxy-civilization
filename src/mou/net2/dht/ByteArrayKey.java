/*
 * $Id$
 * Created on May 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht;

import java.util.Arrays;


public class ByteArrayKey
{
	private byte[] key;
	/**
	 * @param key
	 */
	public ByteArrayKey(byte[] key)
	{
		super();
		this.key = key;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return Arrays.equals(key, ((ByteArrayKey)obj).key);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(key);
	}

	public byte[] getKey()
	{
		return key;
	}
}
