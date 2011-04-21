/*
 * $Id$
 * Created on May 28, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.rpc;

import java.io.InputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.jboss.remoting.Client;
import org.jboss.remoting.InvokerLocator;

/**
 * Methoden dieser Klasse sind synchronisiert um die Methodenaufrufe
 * nur ueber eine TCP-Verbindung auszufuehren. Ausserdem wird die diconnect() 
 * Methode automatisch nach einer Idle-Zeitspanne aufgerufen. Somit wir das explizite
 * Aufrufen der disconnect() Methoden ubuerflussig, womit die bereits geoeffnete 
 * TCP-Verbindungen besser wiederverwendbar sind.
 * @author paul
 */
public class ClientExt extends Client
{
	static final private Timer timer = new Timer("ClientExt release timer",false);
	private long accessTime;
	private ClientExt instance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClientExt(InvokerLocator locator, final long idleTimeout) throws Exception
	{
		super(locator);
		instance = this;
		timer.schedule(new TimerTask()
		{
		
			@Override
			public void run()
			{
				synchronized(instance)
				{
					if((System.currentTimeMillis() - getAccessTime()) >= idleTimeout)
					{
						/*
						 * Verbindung wird schon langer unbenutzt
						 */
						instance.disconnect();
					}
				}
			}
		}, idleTimeout+300, idleTimeout / 4);
		setMaxNumberOfThreads(1);
	}

	synchronized public void resetIdleTime()
	{
		accessTime = System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.remoting.Client#invoke(java.io.InputStream, java.lang.Object)
	 */
	@Override
	synchronized public Object invoke(InputStream inputStream, Object param) throws Throwable
	{
		resetIdleTime();
		return super.invoke(inputStream, param);
	}

	/* (non-Javadoc)
	 * @see org.jboss.remoting.Client#invoke(java.lang.Object, java.util.Map)
	 */
	@Override
	synchronized public Object invoke(Object param, Map metadata) throws Throwable
	{
		resetIdleTime();
		return super.invoke(param, metadata);
	}

	/* (non-Javadoc)
	 * @see org.jboss.remoting.Client#invoke(java.lang.Object)
	 */
	@Override
	synchronized public Object invoke(Object param) throws Throwable
	{
		resetIdleTime();
		return super.invoke(param);
	}

	/* (non-Javadoc)
	 * @see org.jboss.remoting.Client#invokeOneway(java.lang.Object, java.util.Map, boolean)
	 */
	@Override
	synchronized public void invokeOneway(Object param, Map sendPayload, boolean clientSide) throws Throwable
	{
		resetIdleTime();
		super.invokeOneway(param, sendPayload, clientSide);
	}

	/* (non-Javadoc)
	 * @see org.jboss.remoting.Client#invokeOneway(java.lang.Object, java.util.Map)
	 */
	@Override
	synchronized public void invokeOneway(Object param, Map sendPayload) throws Throwable
	{
		resetIdleTime();
		super.invokeOneway(param, sendPayload);
	}

	/* (non-Javadoc)
	 * @see org.jboss.remoting.Client#invokeOneway(java.lang.Object)
	 */
	@Override
	synchronized public void invokeOneway(Object param) throws Throwable
	{
		resetIdleTime();
		super.invokeOneway(param);
	}

	
	@Override
	synchronized public void connect() throws Exception
	{
		super.connect();
	}

	@Override
	synchronized public void disconnect()
	{
		super.disconnect();
	}

	public long getAccessTime()
	{
		return accessTime;
	}
	
}
