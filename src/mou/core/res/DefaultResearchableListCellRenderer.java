/*
 * $Id: DefaultResearchableListCellRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25,
 * 2006 Copyright Paul Burlov 2001-2006
 */
package mou.core.res;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import mou.core.research.ResearchableDesign;

/**
 * @author PB
 */
public class DefaultResearchableListCellRenderer extends DefaultResearchableDesignUISmall
		implements ListCellRenderer
{

	/**
	 * 
	 */
	public DefaultResearchableListCellRenderer()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		ResearchableDesign des = (ResearchableDesign) value;
		this.showResearchableDesign(des);
		if(isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setEnabled(list.isEnabled());
		setOpaque(true);
		return this;
	}
}
