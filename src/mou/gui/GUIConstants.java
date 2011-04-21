/*
 * $Id: GUIConstants.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

public class GUIConstants
{

	static final public int FONT_SIZE = 18;
	static final public Font FONT_DEFAULT = new Font("Serif", Font.BOLD, FONT_SIZE);
	static public Color COLOR_MEIN = Color.GREEN;
	static final public Color COLOR_UNERFORSCHT = Color.GRAY;
	static final public Color COLOR_ALLIIERT = Color.BLUE;
	static final public Color COLOR_NEUTRAL = Color.YELLOW;
	static final public Color COLOR_FEINDLICH = Color.RED;
	static private final String CURSOR_SELECT_TARGET_RED_NAME = "CURSOR_SELECT_TARGET_RED";
	static private final String CURSOR_SELECT_TARGET_GREEN_NAME = "CURSOR_SELECT_TARGET_GREEN";
	static private final String CURSOR_SELECT_TARGET_GREY_NAME = "CURSOR_SELECT_TARGET_GREY";
	static public final Cursor CURSOR_SELECT_TARGET_RED;
	static public final Cursor CURSOR_SELECT_TARGET_GREEN;
	static public final Cursor CURSOR_SELECT_TARGET_GREY;
	static
	{
		try
		{
			// FONT_DEFAULT = Font.createFont(
			// Font.TRUETYPE_FONT, new FileInputStream("res/font.ttf"));
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		CURSOR_SELECT_TARGET_RED = Toolkit.getDefaultToolkit()
				.createCustomCursor(new ImageIcon(GUIConstants.class.getResource("/res/images/cursor_target_rot.png")).getImage(), new Point(16, 16),
						CURSOR_SELECT_TARGET_RED_NAME);
		CURSOR_SELECT_TARGET_GREEN = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(GUI.loadImage("/res/images/cursor_target_gruen.png")).getImage(), new Point(16, 16), CURSOR_SELECT_TARGET_GREEN_NAME);
		CURSOR_SELECT_TARGET_GREY = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(GUI.loadImage("/res/images/cursor_target_grau.png")).getImage(), new Point(16, 16), CURSOR_SELECT_TARGET_GREY_NAME);
	}

	protected GUIConstants()
	{
	}
}