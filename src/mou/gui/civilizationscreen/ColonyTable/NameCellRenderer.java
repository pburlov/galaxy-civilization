/*
 * $Id: NameCellRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen.ColonyTable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import mou.core.colony.Colony;

public class NameCellRenderer extends JLabel
		implements TableCellRenderer
{

	public NameCellRenderer()
	{
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		setText(value.toString());
		Colony col = (Colony) (table.getModel().getValueAt(row, KolonieTableModel.COLONY_OBJ));
		if(col.isRebelled())
			setForeground(Color.RED);
		else
			setForeground(Color.BLACK);
		return this;
	}
}
