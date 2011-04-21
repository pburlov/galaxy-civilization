/*
 * $Id$
 * Created on Jun 4, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe.msg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import mou.net2.PeerHandle;
import mou.net2.rpc.MarshalerAbstract;
import mou.net2.subscribe.Topic;


public class ScribeMsg implements Externalizable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Topic topic;
	private PeerHandle handle;
	
	public ScribeMsg()
	{
		super();
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		topic.writeExternal(out);
		MarshalerAbstract.writePeerHandle(handle, out);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		topic = new Topic();
		topic.readExternal(in);
		handle = MarshalerAbstract.readPeerHandle(in);
	}

	public PeerHandle getHandle()
	{
		return handle;
	}
	
	public void setHandle(PeerHandle handle)
	{
		this.handle = handle;
	}

	public Topic getTopic()
	{
		return topic;
	}
	
	public void setTopic(Topic topic)
	{
		this.topic = topic;
	}
	
}
