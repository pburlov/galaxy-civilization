/*
 * $Id$ Created on Jun 3, 2006 Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.management.MBeanServer;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import mou.Main;
import mou.Modul;
import mou.Subsystem;
import mou.net2.NetSubsystem;
import mou.net2.PeerHandle;
import mou.net2.dht.DhtException;
import mou.net2.rpc.RpcApplication;
import mou.net2.rpc.RpcException;
import mou.net2.subscribe.msg.PostMsg;
import mou.net2.subscribe.msg.SubscribeMsg;
import mou.net2.subscribe.msg.UnsubscribeMsg;

public class SubscribeApplication extends Modul
		implements RpcApplication
{

	static private final long REREGISTER_INTERVAL = 1000 * 60 * 5;
	static private final int DHT_TTL = (int) (REREGISTER_INTERVAL / 1000) + 60;
	private SubscribeData globalSubscriberData = new SubscribeData(Main.instance().getGlobalTimer(), REREGISTER_INTERVAL * 2);
	private Hashtable<Topic, TopicListener> subscribedTopics = new Hashtable<Topic, TopicListener>();
	private Hashtable<Topic, TimerTask> refreshTasks = new Hashtable<Topic, TimerTask>();
	private Hashtable<Topic, Object> postetMessages = new Hashtable<Topic, Object>();
	private NetSubsystem netSubsystem;

	public SubscribeApplication(Subsystem parent)
	{
		super(parent);
	}

	/**
	 * Methode returned sofort ohne zu blockieren
	 * @param topic
	 * @param listener
	 * @throws DhtException
	 * @throws RpcException
	 */
	public void joinTopic(final Topic topic, TopicListener listener)
	{
		if(!Main.isOnlineMode())
		{
			return;
		}
		getLogger().info("Join Topic: "+topic);
		if(subscribedTopics.put(topic, listener) != null)
		{
			/*
			 * Subscription besteht bereits
			 */
			return;
		}
		Main.instance().getExecutorService().execute(new Runnable()
		{
		
			public void run()
			{
				registerForTopic(topic);
			}
		});
	}

	/**
	 * Methode returned sofort ohne zu blockieren
	 */
	public void leaveTopic(final Topic topic)
	{
		if(!Main.isOnlineMode())
		{
			return;
		}
		getLogger().info("Leave Topic "+topic);
		if(!subscribedTopics.containsKey(topic)) return;
		subscribedTopics.remove(topic);
		postetMessages.remove(topic);
		TimerTask task = refreshTasks.remove(topic);
		if(task != null) task.cancel();
		/*
		 * Zuerst eigene Adresse aus DHT entfernen
		 */
		Main.instance().getExecutorService().execute(new Runnable()
		{

			public void run()
			{
				try
				{
					netSubsystem.getDhtModul().getDht().remove(computeKeyForTopic(topic));
				} catch(DhtException e)
				{
					getLogger().log(Level.WARNING, "DHT remove operation failed.", e);
				}
			}
		});
		final UnsubscribeMsg msg = new UnsubscribeMsg();
		msg.setHandle(netSubsystem.getLocalHandle());
		msg.setTopic(topic);
		for(PeerHandle target : globalSubscriberData.getSubscribers(topic))
		{
			Main.instance().getExecutorService().execute(new UnsubscribeRunnable(msg, target));
		}
	}

	public List<PeerHandle> getSubscribers(Topic topic)
	{
		List<PeerHandle> ret = globalSubscriberData.getSubscribers(topic);
		getLogger().fine("getSubscribers: found "+ret.size()+" subscribers for topic"+topic);
		return ret;
	}
	
	public void postMessage(Topic topic, Object payload)
	{
		if(!Main.isOnlineMode())
		{
			return;
		}
		if(!subscribedTopics.containsKey(topic))return;
		postetMessages.put(topic, payload);
		PostMsg msg = new PostMsg();
		msg.setPayload(payload);
		msg.setHandle(netSubsystem.getLocalHandle());
		msg.setTopic(topic);
		for(PeerHandle target : globalSubscriberData.getSubscribers(topic))
		{
			PostRunnable run = new PostRunnable(target, msg);
			Main.instance().getExecutorService().execute(run);
		}
	}
	/**
	 * Methode liefert in DHT gespeicherte InetSocketAddresses fuer einen Topic
	 * 
	 * @param topic
	 * @return
	 * @throws DhtException
	 */
	private List<PeerHandle> getSubscribersFromDHT(Topic topic) throws DhtException
	{
		List<byte[]> data = Main.instance().getNetSubsystem().getDhtModul().getDht().get(this.computeKeyForTopic(topic), 100);
		List<PeerHandle> ret = new ArrayList<PeerHandle>(data.size());
		/*
		 * Versuche aus gewonnenen Daten gueltige Internet-Adresse zu erzeugen
		 */
		for(byte[] d : data)
		{
			if(d == null || d.length < 4) continue;
			try
			{
				PeerHandle handle = new PeerHandle();
				handle.readExternal(new ObjectInputStream(new ByteArrayInputStream(d)));
				ret.add(handle);
			} catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
		return ret;
	}

	private void registerForTopic(final Topic topic)
	{
		TimerTask task = refreshTasks.get(topic);
		if(task != null) task.cancel();
		try
		{
			/*
			 * Zuerst eigene Adresse in DHT putten
			 */
			registerInDHT(topic);
			/*
			 * Startgruppe aus DHT geben lassen
			 */
			List<PeerHandle> group = getSubscribersFromDHT(topic);
			registerInGroup(topic, group);
		} catch(Exception e)
		{
			Main.instance().severeErrorOccured(e, "DHT nicht ansprechbar. <br>" + "Bitte pruefen Sie ob eine Internetverbindung besteht", true);
		}
		task = new TimerTask()
		{

			@Override
			public void run()
			{
				Main.instance().getExecutorService().execute(new Runnable()
				{

					public void run()
					{
						registerForTopic(topic);
					}
				});
			}
		};
		/*
		 * Timer zur Erfirschung der Registrierung starten
		 */
		Main.instance().getGlobalTimer().schedule(task, REREGISTER_INTERVAL);
	}

	/**
	 * Methode versucht lokalen Peer bei den anderen Peers fuer bestimmten Topic zu registrieren.
	 * Dabei wird mit multiplen Threads gearbeitet um die Anfragen zu parallelisieren.
	 * 
	 * @param topic
	 * @return Liste der Peers bei denen die Registrierung erfolgreich verlief
	 */
	private void registerInGroup(final Topic topic, Collection<PeerHandle> peers)
	{
		SubscribeMsg msg = new SubscribeMsg();
		msg.setTopic(topic);
		msg.setHandle(netSubsystem.getLocalHandle());
		for(PeerHandle handle : peers)
		{
			if(netSubsystem.getLocalHandle().equals(handle)) continue;
			ReregisterRunnable run = new ReregisterRunnable(handle, msg);
			Main.instance().getExecutorService().execute(run);
		}
	}

	private void registerInDHT(Topic topic) throws DhtException
	{
		byte[] key = computeKeyForTopic(topic);
		ByteArrayOutputStream out = new ByteArrayOutputStream(20);
		try
		{
			ObjectOutputStream oout = new ObjectOutputStream(out);
			Main.instance().getNetSubsystem().getLocalHandle().writeExternal(oout);
			oout.close();
		} catch(IOException e)
		{// Sollte nie vorkommen
			e.printStackTrace();
		}
		Main.instance().getNetSubsystem().getDhtModul().getDht().put(key, out.toByteArray(), DHT_TTL, false);
	}

	/**
	 * Wird vom Rpc-System aufgerufen wenn auf einem entferntem Rechener invoke(..) Methode
	 * aufgerufen wird.
	 */
	public Object invoke(InvocationRequest invocation) throws Throwable
	{
		if(!Main.isOnlineMode())
		{
			return null;
		}
		Object param = invocation.getParameter();
		if(param instanceof SubscribeMsg) 
		{
			SubscribeMsg msg = (SubscribeMsg)invocation.getParameter();
			getLogger().info("New subscriber for topic "+msg.getTopic()+" Peer "+msg.getHandle());
			return invokeSubscribeMsg(msg); 
		}
		if(param instanceof UnsubscribeMsg)
		{
			UnsubscribeMsg msg = (UnsubscribeMsg) invocation.getParameter();
			getLogger().info("Unsubscribe for topic "+msg.getTopic()+" from peer "+msg.getHandle());
			globalSubscriberData.removeSubscriber(msg.getTopic(), msg.getHandle());
			return null;
		}
		if(param instanceof PostMsg)
		{
			PostMsg msg = (PostMsg)param;
			return invokePostMsg(msg);
		}
		return null;
	}

	private Object invokePostMsg(PostMsg msg)
	{
		TopicListener listener = subscribedTopics.get(msg.getTopic());
		if(listener == null)return Boolean.FALSE;
		listener.receiveMessage(msg.getHandle(), msg.getTopic(), msg.getPayload());
		return Boolean.TRUE;
	}
	
	private Object invokeSubscribeMsg(final SubscribeMsg msg)
	{
		if(!subscribedTopics.containsKey(msg.getTopic())) return Boolean.FALSE;
		globalSubscriberData.addSubscriber(msg.getTopic(), msg.getHandle());
		if(!msg.getHandle().equals(netSubsystem.getLocalHandle()))
		{
			/*
			 * Topic Listener benachrichtigen
			 */
			Main.instance().getExecutorService().execute(new Runnable()
			{
	
				public void run()
				{
					TopicListener listener = subscribedTopics.get(msg.getTopic());
					if(listener != null) listener.peerJoined(msg.getHandle(), msg.getTopic());
				}
			});
			/*
			 * Zuletzt gepostete Nachricht an neuen Subscriber als Antwort schicken
			 */
			Object payload = postetMessages.get(msg.getTopic());
			if(payload != null)
			{
				PostMsg postMsg = new PostMsg();
				postMsg.setHandle(netSubsystem.getLocalHandle());
				postMsg.setPayload(payload);
				postMsg.setTopic(msg.getTopic());
				return postMsg;
			}
		}
		return Boolean.TRUE;
	}

	private Object sendPostMsg(Topic topic, PeerHandle target, Object payload) throws RpcException
	{
		PostMsg msg = new PostMsg();
		msg.setHandle(netSubsystem.getLocalHandle());
		msg.setPayload(payload);
		msg.setTopic(topic);
		return netSubsystem.getRpcModul().invoke(target.getInetSocketAddr(), getApplicationName(), msg);
	}
	
	private byte[] computeKeyForTopic(Topic topic)
	{
		return DigestUtils.sha("GalaxyCivilization" + topic.getDomain() + topic.getKey().toString());
	}

	@Override
	public String getModulName()
	{
		return getClass().getSimpleName();
	}

	@Override
	protected void shutdownIntern()
	{
	}

	@Override
	protected File getPreferencesFile()
	{
		return null;
	}

	@Override
	protected void startModulIntern() throws Exception
	{
		netSubsystem = Main.instance().getNetSubsystem();
		netSubsystem.getRpcModul().registerApplication(this);
	}

	public String getApplicationName()
	{
		return getModulName();
	}

	public void setMBeanServer(MBeanServer server)
	{
	}

	public void setInvoker(ServerInvoker invoker)
	{
	}

	public void addListener(InvokerCallbackHandler callbackHandler)
	{
	}

	public void removeListener(InvokerCallbackHandler callbackHandler)
	{
	}

	class UnsubscribeRunnable
			implements Runnable
	{

		private UnsubscribeMsg msg;
		private PeerHandle target;

		/**
		 * @param msg
		 * @param target
		 */
		public UnsubscribeRunnable(UnsubscribeMsg msg, PeerHandle target)
		{
			super();
			this.msg = msg;
			this.target = target;
		}

		public void run()
		{
			try
			{
				netSubsystem.getRpcModul().invoke(target.getInetSocketAddr(), getApplicationName(), msg);
			} catch(RpcException e)
			{
				globalSubscriberData.removeSubscriber(msg.getTopic(), target);
				getLogger().info("Invoke unsubscribe failed. Peer :" + target + " Cause: " + e.getLocalizedMessage());
			}
		}
	}

	class PostRunnable implements Runnable
	{
		private PeerHandle target;
		private PostMsg msg;
		
		/**
		 * @param target
		 * @param msg
		 */
		public PostRunnable(PeerHandle target, PostMsg msg)
		{
			super();
			this.target = target;
			this.msg = msg;
		}


		public void run()
		{
			try
			{
				Boolean ret = (Boolean)netSubsystem.getRpcModul().invoke(target.getInetSocketAddr(), getApplicationName(),msg);
				if(ret == null)return;
				if(!ret.booleanValue())
				{
					globalSubscriberData.removeSubscriber(msg.getTopic(), target);
				}
			}catch(RpcException e)
			{
				globalSubscriberData.removeSubscriber(msg.getTopic(), target);
				getLogger().info("Invoke subscribe failed. Peer: " + target + " Cause: " + e.getLocalizedMessage());
			}
		}
		
	}
	class ReregisterRunnable
			implements Runnable
	{

		private PeerHandle handle;
		private SubscribeMsg msg;

		/**
		 * @param handle
		 */
		public ReregisterRunnable(PeerHandle handle, SubscribeMsg message)
		{
			super();
			this.handle = handle;
			msg = message;
		}

		public void run()
		{
			try
			{
				Object ret =  netSubsystem.getRpcModul().invoke(handle.getInetSocketAddr(), getApplicationName(), msg);
				if(ret == null)return;
				boolean positive = false;
				if(ret instanceof Boolean)
				{
					if(!((Boolean)ret).booleanValue())
					{
						globalSubscriberData.removeSubscriber(msg.getTopic(), handle);
					}
					else
					{
						if(globalSubscriberData.addSubscriber(msg.getTopic(), handle))
						{
							TopicListener listener = subscribedTopics.get(msg.getTopic());
							if(listener != null) listener.peerJoined(handle, msg.getTopic());
						}
						positive = true;
					}
				}
				if(ret instanceof PostMsg)
				{
					PostMsg pm = (PostMsg)ret;
					if(globalSubscriberData.addSubscriber(msg.getTopic(), handle))
					{
						TopicListener listener = subscribedTopics.get(msg.getTopic());
						if(listener != null) 
						{
							listener.peerJoined(handle, msg.getTopic());
							listener.receiveMessage(handle, pm.getTopic(), pm.getPayload());
						}
					}
					positive = true;
				}
				/*
				 * Falls lokal schon eine Nachricht fuer den gerade subscribed Topic gepostet wurde,
				 * dann gleich diese Nachricht dem anderen Peer zukommen lassen 
				 */
				if(positive)
				{
					Object payload = postetMessages.get(msg.getTopic());
					if(payload != null) sendPostMsg(msg.getTopic(), handle, payload);
				}

			} catch(RpcException e)
			{
				globalSubscriberData.removeSubscriber(msg.getTopic(), handle);
				getLogger().info("Invoke subscribe failed. Peer: " + handle + " Cause: " + e.getLocalizedMessage());
			}
		}
	}
}
