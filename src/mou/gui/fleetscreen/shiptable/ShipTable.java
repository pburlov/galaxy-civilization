/*
 * $Id: ShipTable.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui.fleetscreen.shiptable;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import mou.core.ship.Ship;
import mou.gui.MOUNumberTableCellRenderer;
import org.jdesktop.swing.JXTable;
import org.jdesktop.swing.table.DefaultTableColumnModelExt;
import org.jdesktop.swing.table.TableColumnExt;

/**
 * @author pb
 */
public class ShipTable extends JPanel
{

	private JXTable jTable;
	private ShipTableModel tableModel = new ShipTableModel();
	private JPopupMenu popup;

	public ShipTable(boolean fullDescription)
	{
		/*
		 * Hier wurde vorerst keine JSortableTable genommen, weil sie beim neusortieren die
		 * selektierte Einträge nicht merkt. So wird der auswahl bei jeder kleinste Änderung
		 * komplett verworfen :-(
		 */
		jTable = new JXTable(tableModel);
		jTable.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				if(popup == null) return;
				if(!SwingUtilities.isRightMouseButton(e)) return;
				int clickedRow = jTable.rowAtPoint(e.getPoint());
				if(!jTable.getSelectionModel().isSelectedIndex(clickedRow))
				{
					/*
					 * Angecklickte Zeile ist nicht selektiert. Also evt. vorhandene alte Selektion
					 * entfernen und diese Zeile selektieren
					 */
					jTable.getSelectionModel().setSelectionInterval(clickedRow, clickedRow);
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		MOUNumberTableCellRenderer renderer = new MOUNumberTableCellRenderer();
		DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
		TableColumnExt column = new TableColumnExt(ShipTableModel.SHIP_OBJECT);
		column.setHeaderValue("Object");
		columnModel.addColumn(column);
		column.setVisible(false);
		column = new TableColumnExt(ShipTableModel.NAME);
		column.setHeaderValue("Modell");
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipTableModel.POSITION);
		column.setHeaderValue("Position");
		// column.setCellRenderer(new PositionTableCellRenderer());
		columnModel.addColumn(column);
		column.setVisible(fullDescription);
		column = new TableColumnExt(ShipTableModel.MASSE);
		column.setHeaderValue("Masse");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipTableModel.SPEED);
		column.setHeaderValue("Speed (Lj/Tag)");
		column.setCellRenderer(renderer);
		columnModel.addColumn(column);
		column = new TableColumnExt(ShipTableModel.STRUKTUR);
		column.setHeaderValue("Zustand");
		column.setCellRenderer(new ShipStrukturTableCellRenderer());
		columnModel.addColumn(column);
		if(fullDescription)
		{// Erweiterte Anzeige mit mehrere Spalten
			column = new TableColumnExt(ShipTableModel.WEAPON);
			column.setHeaderValue("Waffenstärke");
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);
			column = new TableColumnExt(ShipTableModel.PANZER);
			column.setHeaderValue("Panzerung");
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);
			column = new TableColumnExt(ShipTableModel.SHILD);
			column.setHeaderValue("Schilde");
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);
			column = new TableColumnExt(ShipTableModel.SUPPORT);
			column.setHeaderValue("Unterhaltskosten");
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);
			column = new TableColumnExt(ShipTableModel.CREW);
			column.setHeaderValue("Crew");
			column.setCellRenderer(renderer);
			columnModel.addColumn(column);
		}
		jTable.setColumnModel(columnModel);
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane();
		add(scroll, BorderLayout.CENTER);
		scroll.setViewportView(jTable);
		// jTable.setComponentPopupMenu(popup);
	}

	// public void addMouseListener(MouseListener l)
	// {
	// jTable.addMouseListener(l);
	// }
	public void setComponentPopupMenu(JPopupMenu popup)
	{
		this.popup = popup;
	}

	/**
	 * Zeigt Schiffe bei der angegebener Position. Wenn Position ist null, dann werden alle eigene
	 * Schiffe gezeigt
	 * 
	 * @param pos
	 */
	public void showShipsAtPosition(Point pos)
	{
		// List ships = null;
		// if(pos != null) ships = Main.instance().getMOUDB().getShipDB().getShipsInStarsystem(pos);
		// else
		// {
		// Map map = Main.instance().getMOUDB().getShipDB().getAllShips();
		// ships = new ArrayList(map.values());
		// }
		// showShips(ships);
		// // invalidate();
		tableModel.showShipsAt(pos);
	}

	// public void showShips(List ships)
	// {
	// tableModel.showShips(ships);
	// }
	//	
	/**
	 * @return Liste mit Ship selektierten Objekten
	 */
	public List<Ship> getSelectedShips()
	{
		int rows[] = jTable.getSelectedRows();
		ArrayList<Ship> ret = new ArrayList<Ship>(rows.length);
		for(int i = 0; i < rows.length; i++)
		{
			int index = jTable.convertRowIndexToModel(rows[i]);
			ret.add(tableModel.getShipAtRow(index));
			// ret.add((Ship)jTable.getModel().getValueAt(rows[i], ShipTableModel.SHIP_OBJECT));
		}
		return ret;
	}
	// /**
	// * Selectiert gegeben Schiffe
	// * @param ships Liste mit Ship Objekte
	// */
	// public void selectShips(Collection ships)
	// {
	// jTable.clearSelection();
	// for(Iterator iter = tableModel.getIndexesForShips(ships).iterator();iter.hasNext();)
	// {
	// jTable.changeSelection(((Integer)iter.next()).intValue(), -1, true, false);
	// }
	// }
}
