/*
 * $Id$
 * Created on Aug 1, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe.msg;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class PostMsg extends ScribeMsg
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object payload;

	
	public Object getPayload()
	{
		return payload;
	}

	
	public void setPayload(Object payload)
	{
		this.payload = payload;
	}


	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		super.readExternal(in);
		payload = in.readObject();
	}


	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		out.writeObject(payload);
	}
	
}
