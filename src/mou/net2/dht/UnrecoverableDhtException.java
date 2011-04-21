/*
 * $Id$
 * Created on Jul 16, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht;

/**
 * Klasse signalisiert Fehler die nicht durch wiederholtes Ausfuehren der DHT-Operation
 * behoben werden kann.
 * @author paul
 */
public class UnrecoverableDhtException extends DhtException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param s
	 * @param cause
	 */
	public UnrecoverableDhtException(String s, Throwable cause)
	{
		super(s, cause);
	}

	/**
	 * @param s
	 */
	public UnrecoverableDhtException(String s)
	{
		super(s);
	}

	public UnrecoverableDhtException()
	{
	}
}
