/*
 * $Id: ActionArea.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui.starmapscreen.starmappanel;

import java.awt.Rectangle;

class ActionArea
{

	private Rectangle area;
	private MouseEventListener listener;

	public ActionArea(Rectangle rect, MouseEventListener listener)
	{
		area = rect;
		this.listener = listener;
	}

	public Rectangle getArea()
	{
		return area;
	}

	public MouseEventListener getListener()
	{
		return listener;
	}
}
