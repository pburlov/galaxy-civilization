/*
 * $Id: MessagesTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.diplomacy.MessagesTable;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.gui.GUI;

/**
 * @author pbu
 */
public class MessagesTableModel extends AbstractTableModel
{

	static final public int DATA_OBJ = 0;
	static final public int TIME = 1;
	static final public int CIV = 2;
	static final public int TEXT = 3;
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();
	// private List<Civilization> listCivs = new ArrayList<Civilization>();
	// private DiplomacyServer diplServer = Main.instance().getNetSubsystem().getDiplomacyServer();
	private ArrayList<TextMessage> listData = new ArrayList<TextMessage>();

	/**
	 * 
	 */
	public MessagesTableModel()
	{
	}

	public void addMessage(TextMessage msg)
	{
		listData.add(0, msg);
		fireTableRowsInserted(0, 0);
	}

	/**
	 * @param index
	 *            Zeilenindex
	 * @return
	 */
	public TextMessage getMessageAtIndex(int index)
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
		TextMessage msg = getMessageAtIndex(rowIndex);
		if(msg == null) return null;
		switch(columnIndex)
		{
			case DATA_OBJ:
				return msg;
			case CIV:
				String civName = civDB.getCivName(CivilizationDB.createCivID(msg.getSource()));
				return civName;
			case TIME:
				return GUI.formatDate(msg.getTime());
			case TEXT:
				/*
				 * Liefert erste Zeile als Titel
				 */
				String[] lines = msg.getText().split("\n", 2);
				if(lines.length == 0) return "";
				return lines[0];
		}
		return null;
	}

	public void deleteIndex(int index)
	{
		listData.remove(index);
		fireTableRowsDeleted(index, index);
	}
}
