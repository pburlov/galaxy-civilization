/*
 * $Id: NaturalResourceTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import mou.ClockEvent;
import mou.ClockListener;
import mou.Main;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.civilization.NaturalRessourcesStorageItem;
import mou.core.res.natural.NaturalResource;

public class NaturalResourceTableModel extends AbstractTableModel
		implements ClockListener
{

	private NaturalRessourcesStorageDB naturalResDB = Main.instance().getMOUDB().getStorageDB();
	// private NaturalResourceTableModel self;
	private List listData;

	public NaturalResourceTableModel()
	{
		// self = this;
		initModel();
		Main.instance().getClockGenerator().addClockListener(this);
		// naturalResDB.addDBEventListener(this,null);
	}

	private void initModel()
	{
		listData = new ArrayList(naturalResDB.getAllRessources().values());
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	synchronized public int getRowCount()
	{
		return listData.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	synchronized public Object getValueAt(int rowIndex, int columnIndex)
	{
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) listData.get(rowIndex);
		switch(columnIndex)
		{
			case 0:
				return item.getNaturalResource().getName();
			case 1:
				return item.getMenge();
		}
		return null;
	}

	public Class getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return String.class;
			case 1:
				return Integer.class;
		}
		return String.class;
	}

	synchronized public NaturalResource getNaturalRessourceAtIndex(int index)
	{
		NaturalRessourcesStorageItem item = (NaturalRessourcesStorageItem) listData.get(index);
		return item.getNaturalResource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column)
	{
		switch(column)
		{
			case 0:
				return "Name";
			case 1:
				return "Lagerbestand";
		}
		return "Undefiniert";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.ClockListener#clockEvent(mou.ClockEvent)
	 */
	public void dailyEvent(ClockEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				initModel();
			}
		});
	}

	public void yearlyEvent(ClockEvent event)
	{
	}
}
