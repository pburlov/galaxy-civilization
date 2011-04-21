/*
 * $Id: GUIScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui;

import mou.Preferences;

public interface GUIScreen
{

	public void saveProperties(Preferences prefs);

	public void restoreProperties(Preferences prefs);
}