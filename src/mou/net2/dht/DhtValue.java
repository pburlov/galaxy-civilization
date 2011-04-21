/*
 * $Id$
 * Created on May 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht;


public class DhtValue
{
	private byte[] valueHash;
	private int ttl;
	/**
	 * @param valueHash
	 * @param ttl
	 */
	public DhtValue(byte[] valueHash, int ttl)
	{
		super();
		this.valueHash = valueHash;
		this.ttl = ttl;
	}
	
	public int getTtl()
	{
		return ttl;
	}
	
	public byte[] getValueHash()
	{
		return valueHash;
	}
}
