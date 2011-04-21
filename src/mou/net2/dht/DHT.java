/*
 * $Id$
 * Created on May 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht;

import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import mou.net2.dht.simple.WebserverDHT;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import burlov.collections.TimeOutHashtable;

/**
 * Klasse dient als implementationsunabhaengige Interface zu DHT-Funktionalitaet.
 * 
 * @author paul
 */
public abstract class DHT
{
	static private DHT instance;
	private byte[] secret;
	private TimeOutHashtable<ByteArrayKey, DhtValue> puttedValues = new TimeOutHashtable<ByteArrayKey, DhtValue>();
	private Timer refreshTimer = new Timer("DhtPutRefreshTimer",true);
	private Hashtable<byte[], TimerTask> putRefreshTasks = new Hashtable<byte[], TimerTask>();
	private Logger log ;
	
	protected DHT(byte[] secret, Logger log)
	{
		this.secret = secret;
		this.log = log;
	}
	
	static public DHT getInstance(byte[] secret, Logger log)
	{
		if(instance == null)
		{
			try
			{
//				instance = new OpenDhtOncRpcAdapter(secret, log);
				instance = new WebserverDHT(secret, log);
			} catch(Exception e)
			{
				throw new RuntimeException("DHT-Service is unreachable",e);
			}
		}
		return instance;
	}
	
	public void close()
	{
		refreshTimer.cancel();
	}
	
	abstract protected void putImpl(byte[] key, byte[] value, int ttl)throws RecoverableDhtException, UnrecoverableDhtException;
	abstract protected void removeImpl(byte[] key)throws RecoverableDhtException, UnrecoverableDhtException;
	abstract protected List<byte[]> getImpl(byte[] key, int maxValues)throws RecoverableDhtException, UnrecoverableDhtException;

	public void put(final byte[] key, final byte[] value, final int ttlSec, final boolean withRefresh)throws DhtException
	{
		log.fine("putting value. TTL="+ttlSec);
		RecoverableDhtException lastException = null;
		for(int i = 0; i < 3; i++)
		{
			try
			{
				putImpl(key, value, ttlSec);
				puttedValues.put(new ByteArrayKey(key), new DhtValue(sha1(value), ttlSec), ttlSec * 1000);
				
				TimerTask task = putRefreshTasks.remove(key);
				if(task != null)task.cancel();
				if(withRefresh)
				{
					task =	new TimerTask()
					{
						
						@Override
						public void run()
						{
							try
							{
								put(key, value, ttlSec, withRefresh);
							} catch(DhtException e)
							{
								e.printStackTrace();
							}
						}
					};
					putRefreshTasks.put(key, task);
					refreshTimer.schedule(task, (ttlSec * 1000) - (ttlSec * 100));
				}
				return;
			}catch(RecoverableDhtException e)
			{
				lastException = e;
				try
				{
					Thread.sleep(1000);
				} catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		throw lastException;
	}
	
	public void remove(byte[] key)throws DhtException
	{
		log.fine("remove from dht");
		RecoverableDhtException lastException = null;
		for(int i = 0; i < 3; i++)
		{
			try
			{
				removeImpl(key);
				puttedValues.remove(new ByteArrayKey(key));
				return;
			}catch(RecoverableDhtException e)
			{
				lastException = e;
				try
				{
					Thread.sleep(1000);
				} catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		throw lastException;
		
	}
	public List<byte[]> get(byte[] key, int maxValues)throws DhtException
	{
		log.fine("get");
		RecoverableDhtException lastException = null;
		for(int i = 0; i < 3; i++)
		{
			try
			{
				return getImpl(key, maxValues);
			}catch(RecoverableDhtException e)
			{
				lastException = e;
				try
				{
					Thread.sleep(1000);
				} catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		throw lastException;
	}

	protected DhtValue getPuttedValue(byte[] key)
	{
		return puttedValues.get(new ByteArrayKey(key));
	}
	
	protected byte[] getSecret()
	{
		return secret;
	}
	
	static public final byte[] sha1(byte[] data)
	{
		return DigestUtils.sha(data);
	}

	
	public Logger getLog()
	{
		return log;
	}
	
	private void testPut() throws DhtException
	{
		String val = RandomStringUtils.randomAlphanumeric(254);
		String key = RandomStringUtils.randomAlphanumeric(10);
		System.out.println("Put value size="+val.length()+" for key size="+key.length());
		put(key.getBytes(), val.getBytes(), 100, false);
		List<byte[]> ret = get(key.getBytes(), 10);
		System.out.println("Returned values: "+ret.size());
		if(!StringUtils.equals(val, new String(ret.get(0))))
		{
			throw new RuntimeException("Values not equal");
		}
		remove(key.getBytes());
	}
	
	static public void main(String[] args)
	{
		String secret = "secret";
		DHT dht = DHT.getInstance(secret.getBytes(), Logger.getAnonymousLogger());
		try
		{
			for(int i = 0; i < 10;i++)
			{
				long start = System.currentTimeMillis();
				dht.testPut();
				System.out.println("Time: "+(System.currentTimeMillis() - start));
//				Thread.sleep(500);
			}
		} catch(DhtException e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
