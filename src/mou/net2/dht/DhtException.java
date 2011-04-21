/*
 * $Id$
 * Created on Jun 27, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht;

import java.io.IOException;


public class DhtException extends IOException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DhtException()
	{
		super();
	}

	public DhtException(String s)
	{
		super(s);
	}
	
	public DhtException(String s, Throwable cause)
	{
		this.initCause(cause);
	}
}
