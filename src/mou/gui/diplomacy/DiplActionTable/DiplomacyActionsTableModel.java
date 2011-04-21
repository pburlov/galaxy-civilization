/*
 * $Id: DiplomacyActionsTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.diplomacy.DiplActionTable;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.gui.GUI;
import mou.net.diplomacy.AbstractDiplomacyAction;

/**
 * @author pbu
 */
public class DiplomacyActionsTableModel extends AbstractTableModel
{

	static final public int DATA_OBJ = 0;
	static final public int RECEIVED_TIME = 10;
	static final public int VALID_BEFORE = 15;
	static final public int CIV = 20;
	static final public int NAME = 30;
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();
	// private List<Civilization> listCivs = new ArrayList<Civilization>();
	// private DiplomacyServer diplServer = Main.instance().getNetSubsystem().getDiplomacyServer();
	private ArrayList<AbstractDiplomacyAction> listData = new ArrayList<AbstractDiplomacyAction>();

	/**
	 * 
	 */
	public DiplomacyActionsTableModel()
	{
	}

	public AbstractDiplomacyAction getActionAtIndex(int index)
	{
		return listData.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 4;// Inklusive ID Spalte
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return listData.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		AbstractDiplomacyAction msg = getActionAtIndex(rowIndex);
		if(msg == null) return null;
		switch(columnIndex)
		{
			case DATA_OBJ:
				return msg;
			case CIV:
				String civName = civDB.getCivName(CivilizationDB.createCivID(msg.getSource()));
				return civName;
			case RECEIVED_TIME:
				return GUI.formatDate(msg.getReceivedTime());
			case VALID_BEFORE:
				return GUI.formatDate(msg.getValidBefor());
			case NAME:
				return msg.getName();
		}
		return null;
	}

	public void addDiplomacyAction(AbstractDiplomacyAction action)
	{
		listData.add(action);
		fireTableRowsInserted(listData.size() - 1, listData.size() - 1);
	}

	public void removeDiplomacyAction(AbstractDiplomacyAction action)
	{
		int index = listData.indexOf(action);
		listData.remove(index);
		fireTableRowsDeleted(index, index);
	}
}
