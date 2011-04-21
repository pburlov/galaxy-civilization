/*
 * $Id: Navpoint.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.starmap;

import java.awt.Point;
import mou.gui.GUI;

/**
 * @author pb
 */
public class Navpoint extends Point
{

	private String comment = "";

	/**
	 * @param p
	 */
	public Navpoint(Point p, String comment)
	{
		super(p);
		if(comment == null) comment = "";
		if(comment.length() > 30) comment = comment.substring(0, 30);
		this.comment = comment;
	}

	/**
	 * @param x
	 * @param y
	 */
	public Navpoint(int x, int y)
	{
		super(x, y);
	}

	public String getComment()
	{
		return comment;
	}

	public String toString()
	{
		return GUI.formatPoint(this) + " " + getComment();
	}
}
