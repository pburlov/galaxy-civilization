/*
 * $Id: ShipOffersTable.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.tradescreen;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import mou.core.trade.RemoteShipTradeOffer;
import mou.gui.MOUNumberTableCellRenderer;
import org.jdesktop.swing.JXTable;
import org.jdesktop.swing.table.DefaultTableColumnModelExt;
import org.jdesktop.swing.table.TableColumnExt;

/**
 * @author pb
 */
public class ShipOffersTable extends JPanel
{

	protected JXTable jTable;
	// protected ShipOffersTableModel tableModel = new ShipOffersTableModel();
	protected BuyDialog buyDialog = new BuyDialog();
	private DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
	private ShipOffersTableModel tableModel;

	public ShipOffersTable(ShipOffersTableModel tableModel)
	{
		this.tableModel = tableModel;
		jTable = new JXTable(tableModel);
		MOUNumberTableCellRenderer renderer = new MOUNumberTableCellRenderer();
		TableColumnExt column = new TableColumnExt(ShipOffersTableModel.NAME);
		column.setHeaderValue("Modell");
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.HERSTELLER);
		column.setHeaderValue("Hersteller");
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.MASSE);
		column.setHeaderValue("Masse");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.SPEED);
		column.setHeaderValue("Speed (Lj/Tag)");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.WEAPON);
		column.setHeaderValue("Waffenstärke");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.PANZER);
		column.setHeaderValue("Panzerung");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.SHILD);
		column.setHeaderValue("Schilde");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.SUPPORT);
		column.setHeaderValue("Support Cr./Jahr");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.CREW);
		column.setHeaderValue("Crew");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.PRICE);
		column.setHeaderValue("Preis");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipOffersTableModel.QUANTITY);
		column.setHeaderValue("Anzahl");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		jTable.setColumnModel(columnModel);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane();
		add(scroll, BorderLayout.CENTER);
		scroll.setViewportView(jTable);
	}

	public void addMouseListener(MouseListener l)
	{
		jTable.addMouseListener(l);
	}

	public void showData(List<RemoteShipTradeOffer> data)
	{
		tableModel.setRowData(data);
	}

	// public void setTableModel(TableModel model)
	// {
	// jTable.setModel(model);
	// jTable.setColumnModel(columnModel);
	// }
	public RemoteShipTradeOffer getSelectedOffer()
	{
		int index = jTable.getSelectedRow();
		if(index < 0) return null;
		return (RemoteShipTradeOffer) jTable.getModel().getValueAt(jTable.convertRowIndexToModel(index), ShipOffersTableModel.SHIP_OFFER_OBJECT);
	}
}
