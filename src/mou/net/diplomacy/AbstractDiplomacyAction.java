/*
 * $Id: AbstractDiplomacyAction.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net.diplomacy;

import mou.Main;

/**
 * @author pb
 */
public abstract class AbstractDiplomacyAction
{

	static final public int PRIORITY_URGENT = Integer.MAX_VALUE;
	static final public int PRIORITY_NORMAL = 0;
	private String name;
	private String comment;
	private Long source;
	private int priority;
	private long validBefor;
	private long receivedTime;

	/**
	 * @param name
	 * @param source
	 * @param priority
	 */
	public AbstractDiplomacyAction(String name, String comment, Long source, long validBefor, int priority)
	{
		super();
		this.name = name;
		this.comment = comment;
		this.source = source;
		this.priority = priority;
		this.validBefor = validBefor;
		receivedTime = Main.instance().getTime();
	}

	public long getReceivedTime()
	{
		return receivedTime;
	}

	public long getValidBefor()
	{
		return validBefor;
	}

	public String getName()
	{
		return name;
	}

	public int getPriority()
	{
		return priority;
	}

	public Long getSource()
	{
		return source;
	}

	public String getComment()
	{
		return comment;
	}

	/**
	 * Methode wird vom GUI aufgerufen wenn der Spieler diesen Angebot akzeptiert. Methode Starten
	 * einen neuen Thread um den SwingThread abzukoppeln.
	 */
	public void accept()
	{
		new Thread(new Runnable()
		{

			public void run()
			{
				acceptImpl();
			}
		}, "Thread für accept() Methode der " + getName() + " Action").start();
	}

	/**
	 * Methode wird vom GUI aufgerufen wenn der Spieler diesen Angebot ablehnt. Methode Starten
	 * einen neuen Thread um den SwingThread abzukoppeln.
	 */
	public void reject()
	{
		new Thread(new Runnable()
		{

			public void run()
			{
				rejectImpl();
			}
		}, "Thread für reject() Methode der " + getName() + " Action").start();
	}

	abstract protected void acceptImpl();

	/**
	 */
	abstract protected void rejectImpl();
}
