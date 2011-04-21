/*
 * $Id: FremdeCivTabelle.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy.FremdeCivTable;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import mou.core.civilization.Civilization;
import org.jdesktop.swing.JXTable;
import org.jdesktop.swing.table.DefaultTableColumnModelExt;
import org.jdesktop.swing.table.TableColumnExt;

public class FremdeCivTabelle extends JPanel
{

	private JScrollPane scroll = new JScrollPane();
	private JXTable table;
	private FremdeCivTableModel tableModel = new FremdeCivTableModel();

	public FremdeCivTabelle()
	{
		table = new JXTable(tableModel);
		table.setCursor(new Cursor(Cursor.HAND_CURSOR));
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		scroll.getViewport().add(table);
		DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
		TableColumnExt column = new TableColumnExt(FremdeCivTableModel.NAME);
		column.setHeaderValue("Name");
		columnModel.addColumn(column);
		// column = new TableColumn(FremdeCivTableModel.POPULATION);
		// column.setHeaderValue("Bevölkerung");
		// columnModel.addColumn(column);
		// column.setCellRenderer(new MOUNumberTableCellRenderer());
		//		
		column = new TableColumnExt(FremdeCivTableModel.STATUS);
		column.setHeaderValue("Diplomatische Status");
		columnModel.addColumn(column);
		column = new TableColumnExt(FremdeCivTableModel.CONNECTION);
		column.setHeaderValue("Verbindung");
		columnModel.addColumn(column);
		table.setColumnModel(columnModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void addMouseListener(MouseListener l)
	{
		table.addMouseListener(l);
	}

	public Civilization getSelectedCivilization()
	{
		int index = table.getSelectedRow();
		if(index < 0) return null;
		Civilization civ = (Civilization) table.getModel().getValueAt(table.convertRowIndexToModel(index), FremdeCivTableModel.CIVILIZATION_OBJ);
		return civ;
	}

	public void deselectAll()
	{
		table.getSelectionModel().clearSelection();
	}
}