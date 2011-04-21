/*
 * $Id: DiplomacyActionsTable.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.diplomacy.DiplActionTable;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import mou.Main;
import mou.net.diplomacy.AbstractDiplomacyAction;

public class DiplomacyActionsTable extends JPanel
{

	private JScrollPane scroll = new JScrollPane();
	private JTable table;
	private DiplomacyActionsTableModel tableModel = new DiplomacyActionsTableModel();
	private DiplActionViewDialog viewDialog = new DiplActionViewDialog();

	public DiplomacyActionsTable()
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
					AbstractDiplomacyAction action = (AbstractDiplomacyAction) table.getModel().getValueAt(index, DiplomacyActionsTableModel.DATA_OBJ);
					if(action.getValidBefor() < Main.instance().getTime())
					{
						/*
						 * Angebot ist abgelaufen
						 */
						int option = JOptionPane.showConfirmDialog(table, "Dieses Angebot ist abgelaufen. Entfernen?", "Angebot abgelaufen",
								JOptionPane.YES_NO_OPTION);
						if(option == JOptionPane.YES_OPTION) tableModel.removeDiplomacyAction(action);
						return;
					}
					if(viewDialog.showOffer(action)) tableModel.removeDiplomacyAction(action);
				}
			}
		});
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		TableColumn column = new TableColumn(DiplomacyActionsTableModel.RECEIVED_TIME);
		column.setHeaderValue("Empfangen");
		columnModel.addColumn(column);
		column.setPreferredWidth(50);
		column = new TableColumn(DiplomacyActionsTableModel.VALID_BEFORE);
		column.setHeaderValue("Gültig bis");
		columnModel.addColumn(column);
		column.setPreferredWidth(100);
		column = new TableColumn(DiplomacyActionsTableModel.CIV);
		column.setHeaderValue("Absender");
		columnModel.addColumn(column);
		column.setPreferredWidth(100);
		column = new TableColumn(DiplomacyActionsTableModel.NAME);
		column.setHeaderValue("");
		columnModel.addColumn(column);
		table.setColumnModel(columnModel);
	}

	public void addDiplomacyAction(AbstractDiplomacyAction action)
	{
		tableModel.addDiplomacyAction(action);
	}
}