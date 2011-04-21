/*
 * $Id: ITextMessageReceiver.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.net.diplomacy;

/**
 * @author pb
 */
public interface ITextMessageReceiver
{

	public void receiveMessage(Long sender, String msg);
}
