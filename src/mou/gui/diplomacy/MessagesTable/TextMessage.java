/*
 * $Id: TextMessage.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy.MessagesTable;

/**
 * @author pb
 */
public class TextMessage
{

	private String text;
	private Long source;
	private Long time;

	/**
	 * @param text
	 * @param source
	 * @param time
	 */
	public TextMessage(String text, Long source, long time)
	{
		super();
		this.text = text;
		this.source = source;
		this.time = time;
	}

	public Long getSource()
	{
		return source;
	}

	public String getText()
	{
		return text;
	}

	public Long getTime()
	{
		return time;
	}
}
