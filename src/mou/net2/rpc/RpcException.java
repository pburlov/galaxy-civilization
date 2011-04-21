/*
 * $Id$
 * Created on Jun 29, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.rpc;


public class RpcException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcException()
	{
		super();
	}

	public RpcException(String message)
	{
		super(message);
	}

	public RpcException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RpcException(Throwable cause)
	{
		super(cause);
	}
}
