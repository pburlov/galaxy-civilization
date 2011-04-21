/*
 * $Id: ListSelectionGroup.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Ähnlich wie RadioButtonGroup überwacht ListSelectionGroup die selectionen in den registirerten
 * JList Objekten und lässt nur eine JList mit selektierten Einträgen zu.
 * 
 * @author PB
 */
public class ListSelectionGroup
{

	private Vector list = new Vector();

	/**
	 * 
	 */
	public ListSelectionGroup()
	{
	}

	public void addJList(JList jList)
	{
		list.add(jList);
		jList.addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent ev)
			{
				if(ev.getValueIsAdjusting()) return;
				if(((JList) ev.getSource()).getSelectedIndex() < 0) return;
				JList source = (JList) ev.getSource();
				Enumeration en = list.elements();
				while(en.hasMoreElements())
				{
					JList element = (JList) en.nextElement();
					if(source != element)
					{
						element.clearSelection();
					}
				}
			}
		});
	}
}
