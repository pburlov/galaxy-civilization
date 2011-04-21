/*
 * $Id$
 * Created on Jul 16, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.dht;


/**
 * Klasse signalisiert Fehler die durch Wiederholung der DHT-Operationen behoben
 * werden koennen
 * @author paul
 */
public class RecoverableDhtException extends DhtException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public RecoverableDhtException()
	{
		super();
	}

	/**
	 * @param s
	 * @param cause
	 */
	public RecoverableDhtException(String s, Throwable cause)
	{
		super(s, cause);
	}

	/**
	 * @param s
	 */
	public RecoverableDhtException(String s)
	{
		super(s);
	}
	
}
