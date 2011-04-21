/*
 * $Id$
 * Created on Jul 15, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import mou.net2.UrlCommunicator;
import mou.net2.dht.DHT;
import mou.net2.dht.DhtException;
import mou.net2.dht.RecoverableDhtException;
import mou.net2.dht.UnrecoverableDhtException;


/**
 * @author paul
 */
public class WebserverDHT extends DHT
{
	static final public int MAX_KEY_SIZE = 125;
	static final public int MAX_SECRET_SIZE = 125;
	static final public int MAX_VALUE_SIZE = 32000; 
	static final private String SERVER_ADDRESS = "http://galaxy-civilization.de/system/hashtable";
	private UrlCommunicator getCommunicator;
	private UrlCommunicator putCommunicator;
	private UrlCommunicator deleteCommunicator;
	private Hex codec = new Hex();
	/**
	 * @param secret
	 * @param log
	 */
	public WebserverDHT(byte[] secret, Logger log)
	{
		super(secret, log);
		if(secret.length > MAX_SECRET_SIZE)throw new RuntimeException("DHT-Secret is too long");
		getCommunicator = new UrlCommunicator(SERVER_ADDRESS+"/get.php");
		putCommunicator = new UrlCommunicator(SERVER_ADDRESS+"/put.php");
		deleteCommunicator = new UrlCommunicator(SERVER_ADDRESS+"/delete.php");
	}

	/* (non-Javadoc)
	 * @see mou.net2.dht.DHT#close()
	 */
	@Override
	public void close()
	{
		super.close();
	}

	/* (non-Javadoc)
	 * @see mou.net2.dht.DHT#getImpl(byte[], int)
	 */
	@Override
	protected List<byte[]> getImpl(byte[] key, int maxValues) throws UnrecoverableDhtException,RecoverableDhtException
	{
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("key", encodeParam(key));
		params.put("limit", Integer.toString(maxValues));
		synchronized(getCommunicator)
		{
			try
			{
				getCommunicator.sendCommand(params, false);
				
				String error = getCommunicator.getError();
				if(StringUtils.isNotBlank(error))throw new UnrecoverableDhtException(error);
			} catch(IOException e)
			{
				throw new UnrecoverableDhtException("Error executing http query",e);
			}
			List<byte[]> ret = new ArrayList<byte[]>(getCommunicator.getReplyValues().size());
			for(String val : getCommunicator.getReplyValues())ret.add(decodeParam(val));
			return ret;
		}
	}

	/* (non-Javadoc)
	 * @see mou.net2.dht.DHT#putImpl(byte[], byte[], int)
	 */
	@Override
	protected void putImpl(byte[] key, byte[] value, int ttl) throws UnrecoverableDhtException,RecoverableDhtException
	{
		if(key.length > MAX_KEY_SIZE)throw new UnrecoverableDhtException("DHT-Key ist too long");
		if(value.length > MAX_VALUE_SIZE)throw new UnrecoverableDhtException("DHT-Value is too long");
		Map<String, String> params = new HashMap<String, String>(4);
		params.put("key", encodeParam(key));
		params.put("value", encodeParam(value));
		params.put("secret", encodeParam(getSecret()));
		params.put("ttl", Integer.toString(ttl));
		synchronized(putCommunicator)
		{
			try
			{
				putCommunicator.sendCommand(params, false);
				String error = putCommunicator.getError();
				if(StringUtils.isNotBlank(error))throw new DhtException(error);
			} catch(IOException e)
			{
				throw new UnrecoverableDhtException("Error executing http query",e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see mou.net2.dht.DHT#removeImpl(byte[])
	 */
	@Override
	protected void removeImpl(byte[] key) throws UnrecoverableDhtException,RecoverableDhtException
	{
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("key", encodeParam(key));
		params.put("secret", encodeParam(getSecret()));
		synchronized(deleteCommunicator)
		{
			try
			{
				deleteCommunicator.sendCommand(params, false);
				String error = deleteCommunicator.getError();
				if(StringUtils.isNotBlank(error))throw new DhtException(error);
			} catch(IOException e)
			{
				throw new UnrecoverableDhtException("Error executing http query",e);
			}
		}
	}
	
	 private String encodeParam(byte[] data)
	{
		synchronized(codec)
		{
			return new String(codec.encode(data));
		}
	}
	 
	 private byte[] decodeParam(String data)
	 {
		 synchronized(codec)
		{
			try
			{
				return codec.decode(data.getBytes());
			} catch(DecoderException e)
			{
				e.printStackTrace();
				return new byte[0];
			}
		}
	 }
}
