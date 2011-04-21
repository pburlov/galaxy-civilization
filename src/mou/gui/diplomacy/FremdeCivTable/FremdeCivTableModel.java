/*
 * $Id: FremdeCivTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.diplomacy.FremdeCivTable;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import mou.Main;
import mou.core.civilization.Civilization;
import mou.core.civilization.CivilizationDB;

/**
 * @author pbu
 */
public class FremdeCivTableModel extends AbstractTableModel
{

	static final private int REFRESH_INTERVAL = 5000;
	static final public int CIVILIZATION_OBJ = 0;
	static final public int NAME = 1;
	static final public int POPULATION = 2;
	static final public int STATUS = 3;
	static final public int CONNECTION = 4;
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();
	private List<Civilization> listCivs = new ArrayList<Civilization>();
//	private DiplomacyServer diplServer = Main.instance().getNetSubsystem().getDiplomacyServer();

	/**
	 * 
	 */
	public FremdeCivTableModel()
	{
		new Timer(REFRESH_INTERVAL, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		}).start();
	}

	/**
	 * Methode erneuert die gesamte Tabelle
	 */
	private void refresh()
	{
		if(!EventQueue.isDispatchThread())
		{
			EventQueue.invokeLater(new Runnable()
			{

				public void run()
				{
					refresh();
				}
			});
			return;
		}
		listCivs = new ArrayList<Civilization>();
		//TODO
//		for(Civilization civ : civDB.getAllCivs().values())
//			if(diplServer.isCivOnline(civ.getID().getConstantPart())) listCivs.add(civ);
		fireTableDataChanged();
	}

	/**
	 * @param index
	 *            Zeilenindex
	 * @return
	 */
	public Civilization getKolonieAtIndex(int index)
	{
		return listCivs.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 8;// Inklusive ID Spalte
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return listCivs.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Civilization civ = getKolonieAtIndex(rowIndex);
		switch(columnIndex)
		{
			case CIVILIZATION_OBJ:
				return civ;
			case NAME:
				return civ.getName();
			case POPULATION:
				return civ.getBevolkerung();
			case STATUS:
				return civ.getCivStatusString();
			case CONNECTION:
				// TODO
//				if(diplServer.isCivOnline(civ.getID().getConstantPart())) return "Online";
				return "Offline";
		}
		return null;
	}
	// /* (non-Javadoc)
	// * @see mou.event.MOUEventListener#processMOUEvent(mou.event.MOUEvent)
	// */
	// public void processMOUEvent(MOUEvent event)
	// {
	// DBEvent ev = (DBEvent)event;
	// Colony kolonie;
	// switch(event.getTyp())
	// {
	// case DBEvent.ELEMENT_ADDED:
	// listKolonien.add(ev.getDBObjectID());
	// fireTableRowsInserted(listKolonien.size()-1,listKolonien.size()-1);
	// break;
	// case DBEvent.ELEMENT_REMOVED:
	// int index = listKolonien.indexOf(ev.getDBObjectID());
	// if(index < 0)return;
	// listKolonien.remove(index);
	// fireTableRowsDeleted(index,index);
	// break;
	// case DBEvent.ELEMENT_CHANGED:
	// index = listKolonien.indexOf(ev.getDBObjectID());
	// if(index < 0)return;
	// fireTableRowsUpdated(index,index);
	//				
	// }
	// }
}
