/*
* Copyright © 2001,2010 by Paul Burlov. All Rights Reserved.
* Created 28.11.2010
*/
package mou.net2.dht.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mou.net2.dht.DHT;
import mou.net2.dht.RecoverableDhtException;
import mou.net2.dht.UnrecoverableDhtException;

public class LocalDHT extends DHT
{
	private Map<String, Object> map = new HashMap<String, Object>();
	public LocalDHT()
	{
		super(null, null);
	}

	@Override
	protected void putImpl(byte[] key, byte[] value, int ttl) throws RecoverableDhtException, UnrecoverableDhtException
	{
//		map.put(Arrays.toString(key), value);
	}

	@Override
	protected void removeImpl(byte[] key) throws RecoverableDhtException, UnrecoverableDhtException
	{
//		map.remove(Arrays.toString(key));
	}

	@Override
	protected List<byte[]> getImpl(byte[] key, int maxValues) throws RecoverableDhtException, UnrecoverableDhtException
	{
		return new ArrayList<byte[]>();
	}

}
