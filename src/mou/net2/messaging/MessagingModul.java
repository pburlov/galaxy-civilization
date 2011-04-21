/*
 * $Id$
 * Created on May 28, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.messaging;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;
import org.apache.commons.collections15.multimap.MultiHashMap;
import mou.Main;
import mou.Modul;
import mou.Subsystem;
import mou.net2.rpc.MarshalerAbstract;

/**
 * Modul ist fuer das Versenden und empfangen der Nachrichtenpackete verantwortlich
 * @author paul
 */
public class MessagingModul extends Modul
{
	static final private int MAX_PACKET_SIZE = 45000;
	private MultiHashMap<String, MessageReceiver> nameToReceiver = new MultiHashMap<String, MessageReceiver>();
	private DatagramSocket socket;
	private Thread receiverThread;
	
	public MessagingModul(Subsystem parent)
	{
		super(parent);
	}

	public void addMessageListener(String name, MessageReceiver listener)
	{
		synchronized(nameToReceiver)
		{
			nameToReceiver.put(name, listener);
		}
	}
	
	protected void packetReceived(DatagramPacket packet) throws IOException
	{
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.getData(),0,packet.getLength()));
		/*
		 * Receivername lesen
		 */
		String name = MarshalerAbstract.readString(in);
		int length = in.readUnsignedShort();
		if(length > MAX_PACKET_SIZE)throw new IOException("Invalid payload length");
		byte[] buf = new byte[length];
		in.readFully(buf);
		synchronized(nameToReceiver)
		{
			Collection<MessageReceiver> receivers = nameToReceiver.getCollection(name);
			if(receivers == null)return;
			for(MessageReceiver receiver : receivers)
			{
				receiver.messageReceived((InetSocketAddress)packet.getSocketAddress(), buf);
			}
		}
	}
	@Override
	public String getModulName()
	{
		return "Messaging Subsystem";
	}

	@Override
	protected void shutdownIntern()
	{
		receiverThread.interrupt();
		socket.close();
	}

	@Override
	protected File getPreferencesFile()	
	{
		return null;
	}

	@Override
	protected void startModulIntern() throws Exception
	{
		socket = new DatagramSocket(Main.instance().getNetSubsystem().getPort());
		receiverThread = new Thread()
		{
		
			@Override
			public void run()
			{
				byte[] buf = new byte[MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				while(!isInterrupted())
				{
					try
					{
						socket.receive(packet);
						packetReceived(packet);
					} catch(Throwable e)
					{
						if(!isInterrupted())logException(e);
					}
				}
			}
		};
		receiverThread.setDaemon(false);
		receiverThread.setName(getModulName()+" : "+"UDP-Receiver");
		receiverThread.start();
	}
}
