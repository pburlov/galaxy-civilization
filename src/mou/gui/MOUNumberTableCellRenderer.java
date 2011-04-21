/*
 * $Id: MOUNumberTableCellRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author pb
 */
public class MOUNumberTableCellRenderer extends DefaultTableCellRenderer
{

	/**
	 * 
	 */
	public MOUNumberTableCellRenderer()
	{
		super();
		setHorizontalAlignment(JLabel.RIGHT);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		try
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Number number = (Number) value;
			if(number.doubleValue() < 0)
				setForeground(Color.RED);
			else
				setForeground(Color.BLACK);
			if(value instanceof Double || value instanceof Float)
			{
				setText(GUI.formatDouble(((Number) value).doubleValue()));
			}
			if(value instanceof Long || value instanceof Integer)
			{
				setText(GUI.formatLong(((Number) value).longValue()));
			}
		} catch(Throwable th)
		{
			th.printStackTrace();
			setText("error");
		}
		return this;
	}
}
