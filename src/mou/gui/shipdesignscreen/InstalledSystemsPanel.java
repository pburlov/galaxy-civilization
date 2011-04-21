/*
 * $Id: InstalledSystemsPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.shipdesignscreen;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import mou.core.research.ResearchableDesign;
import mou.core.ship.ShipClass;
import mou.gui.MOUNumberTableCellRenderer;
import mou.gui.NumberCellEditor;

/**
 * Zeig auf einem Schiff installierten Systeme. Dazu wird eine JTable verwendet.
 * 
 * @author pb
 */
public class InstalledSystemsPanel extends JPanel
{

	private JTable systemTable = new JTable(new ShipSystemTableModel(null));
	private ShipClass showedShip;
	private ShipChangedListener shipListener;
	// private boolean edit = false;
	private TablePopupMenu popup = new TablePopupMenu();

	/**
	 * 
	 */
	public InstalledSystemsPanel()
	{
		super();
		JScrollPane scroll = new JScrollPane();
		setLayout(new GridLayout(1, 1));
		// add(systemTable);
		add(scroll);
		scroll.setViewportView(systemTable);
		systemTable.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				// popup.hide();
				if(e.getButton() == MouseEvent.BUTTON3)
				{
					int row = systemTable.rowAtPoint(e.getPoint());
					if(row < 0) return;
					systemTable.getSelectionModel().setSelectionInterval(row, row);
					popup.show(systemTable, e.getX(), e.getY());
				}
			}
		});
		systemTable.setShowVerticalLines(false);
		systemTable.setDefaultEditor(Number.class, new NumberCellEditor());
		systemTable.setDefaultRenderer(Number.class, new MOUNumberTableCellRenderer());
		// systemTable.setDefaultRenderer(Integer.class,new NumberCellEditor());
	}

	/**
	 * Zeigt Schiff in ReadOnly Modus
	 * 
	 * @param ship
	 */
	public void showShip(ShipClass ship)
	{
		showedShip = ship;
		// edit = false;
		systemTable.setModel(new ShipSystemTableModel(ship));
		systemTable.getColumnModel().getColumn(1).setCellRenderer(new NumberCellEditor());
	}

	/**
	 * Zeigt Schiffsysteme im Editiermodus. Schiffsysteme kann man entfernen und die
	 * Ausgangsleistung verändern.
	 * 
	 * @param ship
	 * @param onlyRemove
	 *            wenn true dann kann man keine Ausgangsleistung ändern
	 * @param listener
	 */
	public void editShip(ShipClass ship, ShipChangedListener listener)
	{
		shipListener = listener;
		showedShip = ship;
		// edit = true;
		systemTable.setModel(new ShipSystemTableModel(ship));
		systemTable.getColumnModel().getColumn(1).setCellRenderer(new NumberCellEditor());
	}

	// private void addShipChangedListener(ShipChangedListener listener)
	// {
	// shipListeners.add(listener);
	// }
	//	
	// private void fireShipSystemRemovedEvent(ShipSystemDesign system)
	// {
	// Enumeration en = shipListeners.elements();
	// while(en.hasMoreElements())
	// {
	// ShipChangedListener listener = (ShipChangedListener)en.nextElement();
	// listener.shipsystemRemoved(system);
	// }
	// }
	//	
	// private void fireShipSystemChangedEvent(ShipSystemDesign system)
	// {
	// Enumeration en = shipListeners.elements();
	// while(en.hasMoreElements())
	// {
	// ShipChangedListener listener = (ShipChangedListener)en.nextElement();
	// listener.shipsystemChanged(system);
	// }
	// }
	//	
	private class TablePopupMenu extends JPopupMenu
	{

		private JMenuItem itemDelete = new JMenuItem("Entfernen");

		public TablePopupMenu()
		{
			add(itemDelete);
			itemDelete.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					ResearchableDesign system = (ResearchableDesign) showedShip.getSystems().get(systemTable.getSelectedRow());
					showedShip.removeShipSystem(system);
					systemTable.setModel(new ShipSystemTableModel(showedShip));
					shipListener.shipsystemChanged(system);
				}
			});
		}
	}

	private class ShipSystemTableModel extends AbstractTableModel
	{

		final private String[] COLUMNS = { "Schiffsystem", "Masse", "Energie", "Baukosten", "Unterhaltskosten", "Personal"};
		final private Class[] CLASSES = { String.class, Number.class, Number.class, Number.class, Number.class, Number.class};
		private ShipClass ship;

		public ShipSystemTableModel(ShipClass ship)
		{
			this.ship = ship;
		}

		// public void removeShipSystem(ShipSystemDesign system)
		// {
		// ship.removeShipSystem(system);
		// fire
		// }
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount()
		{
			if(ship == null) return 0;
			return ship.getSystems().size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			ResearchableDesign system = (ResearchableDesign) ship.getSystems().get(rowIndex);
			if(system == null)
			{// Daten inkonsistent
				fireTableDataChanged();
				return null;
			}
			switch(columnIndex)
			{
				case 0: // Name
					return system.getName();
				case 1: // Masse
					return new Integer((int) system.getResearchableResource().computeMasse());
				case 2: // Energie
					return new Double(system.computeEnergy());
				case 3: // Herstellungskosten
					return new Double(system.computeWorkCost());
				case 4: // Unterhaltungskosten
					return new Double(system.computeSupportCost());
				case 5: // Besatzung
					return new Double(system.computeCrew());
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int columnIndex)
		{
			return CLASSES[columnIndex];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return (columnIndex == 1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			switch(columnIndex)
			{
				case 1:// Masse
					ResearchableDesign system = ((ResearchableDesign) ship.getSystems().get(rowIndex));
					int size = (int) (((Integer) aValue).intValue()/system.getResearchableResource().computeNormalizedMass()+ .5);
					if(size == 0)
					{
						/*
						 * Auskommentiert, sonst kommt IndexOutOfBoundsException
						 */
						// ship.removeShipSystem(system);
					} else
						system.getResearchableResource().setSize((Number) size);
					ship.computeWerte();
					shipListener.shipsystemChanged((ResearchableDesign) ship.getSystems().get(rowIndex));
					break;
			}
			
			fireTableDataChanged();
		}
	}
}
