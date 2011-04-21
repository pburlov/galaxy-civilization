/*
 * $Id: MessagesTable.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy.MessagesTable;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import mou.gui.diplomacy.ViewMessageDialog;

public class MessagesTable extends JPanel
{

	private JScrollPane scroll = new JScrollPane();
	private JTable table;
	private MessagesTableModel tableModel = new MessagesTableModel();
	private ViewMessageDialog viewDialog = new ViewMessageDialog();

	public MessagesTable()
	{
		table = new JTable(tableModel);
		table.setCursor(new Cursor(Cursor.HAND_CURSOR));
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		scroll.getViewport().add(table);
		table.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent ev)
			{
				if(ev.getButton() == MouseEvent.BUTTON1)
				{
					int index = table.rowAtPoint(ev.getPoint());
					if(index < 0) return;
					if(viewDialog.showDialog((TextMessage) table.getModel().getValueAt(index, MessagesTableModel.DATA_OBJ))) tableModel.deleteIndex(index);
				}
			}
		});
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		TableColumn column = new TableColumn(MessagesTableModel.TIME);
		column.setHeaderValue("Empfangen");
		columnModel.addColumn(column);
		column.setPreferredWidth(50);
		column = new TableColumn(MessagesTableModel.CIV);
		column.setHeaderValue("Absender");
		columnModel.addColumn(column);
		column.setPreferredWidth(100);
		column = new TableColumn(MessagesTableModel.TEXT);
		column.setHeaderValue("");
		columnModel.addColumn(column);
		table.setColumnModel(columnModel);
	}

	public void addMessage(TextMessage msg)
	{
		tableModel.addMessage(msg);
		// int row = tableModel.getRowCount();
	}
}