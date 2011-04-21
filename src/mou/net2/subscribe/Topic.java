/*
 * $Id$
 * Created on May 6, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import mou.util.SerializationUtils;


/**
 * Klasse dient als Schluessel fuer Subscribe
 * @author paul
 */
public class Topic implements Externalizable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String domain;
	private Long key;
	
	/**
	 * 
	 */
	public Topic()
	{
		super();
	}

	/**
	 * @param domain
	 * @param key
	 */
	public Topic(String domain, Long key)
	{
		if(domain == null || key == null)throw new NullPointerException("null argument");
		this.domain = domain;
		this.key = key;
	}

	@Override
	public boolean equals(Object obj)
	{
		Topic t = (Topic)obj;
		return domain.equals(t.domain) && key.equals(t.key);
	}

	@Override
	public int hashCode()
	{
		return key.intValue();
	}
	
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeLong(key);
		SerializationUtils.writeString(domain, out);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		key = in.readLong();
		domain = SerializationUtils.readString(in);
	}

	
	public String getDomain()
	{
		return domain;
	}

	
	public Long getKey()
	{
		return key;
	}

	@Override
	public String toString()
	{
		return getDomain()+":"+getKey();
	}
}
