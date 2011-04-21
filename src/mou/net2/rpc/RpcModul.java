/*
 * $Id$ Created on May 28, 2006 Copyright Paul Burlov 2001-2006
 */
package mou.net2.rpc;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.remoting.InvokerLocator;
import org.jboss.remoting.transport.Connector;
import mou.Main;
import mou.Modul;
import mou.Subsystem;

public class RpcModul extends Modul
{

	static final private String RPC_WIRE_PROTOCOL = "socket";
	static final private long IDLE_TIMEOUT = 1000 * 60 * 5;
	static final private Map<String, String> PARAMS = new HashMap<String, String>();
	static
	{
		PARAMS.put("clientMaxPoolSize", "1");
		PARAMS.put("socketTimeout", "15000");
	}
	private HashMap<InetSocketAddress, ClientExt> clients = new HashMap<InetSocketAddress, ClientExt>();
	private Connector connector;
	private AtomicBoolean stopped = new AtomicBoolean(false);

	public RpcModul(Subsystem parent)
	{
		super(parent);
	}

	public void registerApplication(RpcApplication appl) throws RpcException
	{
		if(connector == null) throw new IllegalStateException("Connector object ist not initialized");
		// MarshalFactory.addMarshaller(appl.getApplicationName(), appl.getMarshaler(),
		// appl.getMarshaler());
		try
		{
			connector.addInvocationHandler(appl.getApplicationName(), appl);
		} catch(Exception e)
		{
			throw new RpcException(e);
		}
	}

	public Object invoke(InetSocketAddress target, String application, Object arg) throws RpcException
	{
		if(stopped.get()) throw new IllegalStateException("RpcModul is already stopped");
		try
		{
			target = checkForLocalAddress(target);
			getLogger().info("invoke() Target: "+target+" App: "+application);
			ClientExt client = getClient(target);
			client.setSubsystem(application);
			return client.invoke(arg);
		} catch(Throwable e)
		{
			throw new RpcException(e);
		}
	}

	/**
	 * @param target
	 * @param application
	 * @param arg
	 * @param blocking true wenn bis zur Ausfuherungs ende gewartet werden soll. False fuer fire und forget.
	 * @throws RpcException
	 */
	public void invokeOneway(InetSocketAddress target, String application, Object arg, boolean blocking)throws RpcException
	{
		if(stopped.get()) throw new IllegalStateException("RpcModul is already stopped");
		try
		{
			target = checkForLocalAddress(target);
			getLogger().info("invokeOneway() Target: "+target+" App: "+application);
			ClientExt client = getClient(target);
			client.setSubsystem(application);
			client.invokeOneway(arg,null,!blocking);
		} catch(Throwable e)
		{
			throw new RpcException(e);
		}
	}
	
	private ClientExt getClient(InetSocketAddress target) throws Exception
	{
		ClientExt client = null;
		synchronized(clients)
		{
			client = clients.get(target);
			if(client == null)
			{
				client = new ClientExt(createInvokeLocator(target), IDLE_TIMEOUT);
			}
			clients.put(target, client);
		}
		client.connect();
		return client;
	}

	@Override
	public String getModulName()
	{
		return "RPC Modul";
	}

	@Override
	protected void shutdownIntern()
	{
		if(!stopped.compareAndSet(false, true)) return;
		connector.stop();
		synchronized(clients)
		{
			for(ClientExt client : clients.values())
			{
				client.disconnect();
			}
			clients.clear();
		}
	}

	@Override
	protected File getPreferencesFile()
	{
		return null;
	}

	@Override
	protected void startModulIntern() throws Exception
	{
		/*
		 * TimerTask zum Entfernen der disconnected Clients
		 */
		Main.instance().getGlobalTimer().schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				synchronized(clients)
				{
					for(Iterator<ClientExt> iter = clients.values().iterator(); iter.hasNext();)
					{
						ClientExt client = iter.next();
						if(!client.isConnected()) iter.remove();
					}
				}
			}
		}, 10000, 10000);
		/*
		 * Rpc-Server starten
		 */
		int port = Main.instance().getNetSubsystem().getPort();
		InvokerLocator locator = new InvokerLocator(RPC_WIRE_PROTOCOL + "://localhost:" + port);
		connector = new Connector(locator);
		connector.create();
		connector.start(true);
	}

	/**
	 * Methode tauscht externe Adresse des lokalen Servers auf lokale Adresse.
	 * Somit gehen Aufrufe des lokalen Servers nicht in der Schleife ueber DSL-Router. 
	 * @param adr
	 * @return
	 * @throws UnknownHostException
	 */
	private InetSocketAddress checkForLocalAddress(InetSocketAddress adr) throws UnknownHostException
	{
		if(Main.instance().getNetSubsystem().getExtAddress().getAddress().equals(adr.getAddress()))
		{
			return new InetSocketAddress("localhost",adr.getPort());
		}
		return adr;
	}
	
	static private InvokerLocator createInvokeLocator(InetSocketAddress target)
	{
		return new InvokerLocator(RPC_WIRE_PROTOCOL, target.getAddress().getHostAddress(), target.getPort(), null, PARAMS);
	}
}
