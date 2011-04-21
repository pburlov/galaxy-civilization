/*
 * $Id: ComponentResizerGroup.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JComponent;

/**
 * Klasse bring alle Componente in dieser Gruppe auf eine Größe.<br>
 * Es werden die Dimensionen von dem größtem Component genommen.<br>
 * Zur Zeit werden die Größen nur zum Zeitpunkt des Einfügens zu der Gruppe beinflüsst.
 * 
 * @author pb
 */
public class ComponentResizerGroup
{

	private Dimension maxSize = new Dimension(0, 0);
	private Vector components = new Vector();

	/**
	 * 
	 */
	public ComponentResizerGroup()
	{
	}

	public void add(JComponent comp)
	{
		// maxSize = comp.getPreferredSize();
		Dimension size = comp.getPreferredSize();
		if(size.height > maxSize.height || size.width > maxSize.width)
		{
			maxSize = size;
		}
		components.add(comp);
		unisizeComponents();
	}

	private void unisizeComponents()
	{
		Enumeration en = components.elements();
		while(en.hasMoreElements())
		{
			JComponent com = (JComponent) en.nextElement();
			com.setSize(maxSize);
			com.setMaximumSize(maxSize);
			com.setMinimumSize(maxSize);
			com.setPreferredSize(maxSize);
		}
	}
}
