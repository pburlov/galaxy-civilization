/*
 * $Id$
 * Created on Apr 8, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionManager
{
	static final public int CONNECT_TIMEOUT = 30000;
	private boolean shutdown = false;
	private Map<InetSocketAddress, Socket> adrToSocket = new Hashtable<InetSocketAddress, Socket>();
	private Map<InetSocketAddress, ReentrantLock> adrToLock = new HashMap<InetSocketAddress, ReentrantLock>();
	
	private ConnectionImpl openConnection(InetSocketAddress target)throws IOException
	{
		ReentrantLock lock = null;
		synchronized(adrToLock)
		{
			lock = adrToLock.get(target);
			if(lock == null)lock = new ReentrantLock();
			adrToLock.put(target, lock);
		}
		lock.lock();
		try
		{
			Socket socket = adrToSocket.get(target);
			if(socket == null)
			{
				socket = connectNewSocket(target);
			}
			else if(!socket.isConnected() || socket.isInputShutdown() || socket.isOutputShutdown())
			{
				socket = connectNewSocket(target);
			}
			adrToSocket.put(target, socket);
			return new ConnectionImpl(this,target,new DataInputStream( socket.getInputStream()),
					new DataOutputStream(socket.getOutputStream()));
		}catch(IOException e)
		{
			synchronized(adrToLock)
			{
				adrToLock.remove(target);
			}
			lock.unlock();
			throw e;
		}
	}
	
	void closeConnection(ConnectionImpl con)
	{
		ReentrantLock lock = null;
		synchronized(adrToLock)
		{
			lock = adrToLock.get(con.getTarget());
			if(lock == null)return;
		}
		if(lock.isHeldByCurrentThread()) lock.unlock();
	}
	
	private Socket connectNewSocket(InetSocketAddress target)throws IOException
	{
		Socket socket = new Socket();
		socket.setSoTimeout(CONNECT_TIMEOUT);
		socket.setTcpNoDelay(true);
		socket.connect(target, CONNECT_TIMEOUT);
		adrToSocket.put(target, socket);
		return socket;
	}
	/**
	 * Wenn eine TCP-Verbindung zu dem Targethost bereits geoeffnet ist und nicht woanders
	 * benutzt wird, dann wird diese Verbindung dem ConnectionReceiver gegeben. Wenn verbindung 
	 * gerade benutz wird dann wird sie erst nach der Freigabe weitergegeben. Wenn noch ueberhaupt
	 * keine Verbindung zu dem Host exisiert, dann wird versucht eine aufzubauen. 
	 * ACHTUNG! Auf jedem Fall sicherstellen, dass ein Thread diese Methode nicht rekursiv aufruft also
	 * aus dem ConnectionReceiver heraus. 
	 */
	public void requestConnectionAndWait(InetSocketAddress target, ConnectionReceiver receiver)
	{
		if(shutdown)
		{
			receiver.requestFailed(target, new Exception("ConnectionManager is down"));
			return;
		}
		ConnectionImpl con = null;
		try
		{
			con = openConnection(target);
		} catch(IOException e1)
		{
			receiver.requestFailed(target, e1);
			return;
		}
		try
		{
			receiver.receiveConnection(con);
		} catch(Throwable e)
		{
		}
		con.close();
//		SocketEntry entry = null;
//		synchronized(adrToSocket)
//		{
//			entry = adrToSocket.get(target);
//			if(entry == null)
//			{
//				entry = new SocketEntry(new Socket());
//				adrToSocket.put(target, entry);
//			}
//			entry.getReceivers().add(receiver);
//			if(entry.isInUse())
//			{
//				/*
//				 * socket ist unfrei also Receiver hinzufuegen und returnen.
//				 * Der andere Thread wird den Receiver benachrichtigen
//				 */
//				return;
//			}
//			entry.setInUse(true);
//		}
//		while(true)
//		{
//			if(!entry.getSocket().isConnected())
//			{
//				/*
//				 * Frischer Socket, das noch verbunden werden muss
//				 */
//				try
//				{
//					entry.getSocket().connect(target, CONNECT_TIMEOUT);
//				} catch(IOException e)
//				{
//					/*
//					 * Socket wurde von der anderer Seite geschlossen
//					 */
//					synchronized(adrToSocket)
//					{
//						adrToSocket.remove(target);
//					}
//					for(ConnectionReceiver rec : entry.getReceivers())
//					{
//						/*
//						 *alle uebrige Receivers benachrichtigen 
//						 */
//						rec.requestFailed(target, e);
//					}
//					return;
//				}
//			}
//			if(entry.getSocket().isClosed())
//			{
//				/*
//				 * Socket wurde von der anderer Seite geschlossen
//				 */
//				synchronized(adrToSocket)
//				{
//					adrToSocket.remove(target);
//				}
//				for(ConnectionReceiver rec : entry.getReceivers())
//				{
//					/*
//					 *alle uebrige Receivers benachrichtigen 
//					 */
//					rec.requestFailed(target, new IOException("Connection broken"));
//				}
//				return;
//			}
//			/*
//			 * Socket ist frei und verbunden.
//			 */
//			try
//			{
//				ConnectionReceiver rec = null;
//				synchronized(adrToSocket)
//				{
//					rec = entry.getReceivers().poll();
//					if(rec == null)
//					{
//						entry.setInUse(false);
//						/*
//						 * Hier ist die Arbeit fuer diesen Thread zu ende. Fuer weitere
//						 * Verbindungsanfragen werden die anfragende Threads arbeiten
//						 */
//						return;
//					}
//					
//				}
//				entry.getSocket().setSoTimeout(NetSubsystem.SOCKET_READ_TIMEOUT);
//				ConnectionImpl con = new ConnectionImpl(target, 
//						new DataInputStream(entry.getSocket().getInputStream()), 
//						new DataOutputStream(entry.getSocket().getOutputStream()));
//				rec.receiveConnection(con);
//				/*
//				 * Danach sicherheitshalber OutputStream fluschen und
//				 * Connection als geschlossen markieren damit 2 Threads 
//				 * nicht unerlaubterweise gleichzeitig ins Stream schreiben.
//				 */
//				con.getOut().flush();
//				con.close();
//				
//			}catch(Exception e)
//			{
//				synchronized(adrToSocket)
//				{
//					adrToSocket.remove(target);
//				}
//				try
//				{
//					entry.getSocket().close();
//				} catch(IOException unwichtig)
//				{
//				}
//				for(ConnectionReceiver rec : entry.getReceivers())
//				{
//					/*
//					 *alle uebrige Receivers benachrichtigen 
//					 */
//					rec.requestFailed(target, e);
//				}
//				return;
//			}
//		}
	}
	
	public boolean hasOpenedConnection(InetSocketAddress host)
	{
		Socket socket = adrToSocket.get(host);
		if(socket == null || socket.isClosed())return false;
		return true;
	}

	public void shutdown()
	{
		shutdown = true;
		synchronized(adrToSocket)
		{
			for(Socket entry : adrToSocket.values())
			{
				try
				{
					entry.close();
				} catch(IOException e)
				{
				}
			}
		}
	}
	
//	class SocketEntry
//	{
//		private Socket socket;
//		private boolean inUse = false;
//		private Queue<ConnectionReceiver> receivers = new LinkedList<ConnectionReceiver>();
//		/**
//		 * @param socket
//		 */
//		public SocketEntry(Socket socket)
//		{
//			super();
//			this.socket = socket;
//		}
//		
//		public boolean isInUse()
//		{
//			return inUse;
//		}
//		
//		public void setInUse(boolean inUse)
//		{
//			this.inUse = inUse;
//		}
//		
//		public Socket getSocket()
//		{
//			return socket;
//		}
//		
//		public Queue<ConnectionReceiver> getReceivers()
//		{
//			return receivers;
//		}
//		
//	}
	
}
