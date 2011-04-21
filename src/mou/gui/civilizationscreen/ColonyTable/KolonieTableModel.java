/*
 * $Id: KolonieTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen.ColonyTable;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import mou.Main;
import mou.core.colony.BuildJobAbstract;
import mou.core.colony.Colony;
import mou.core.colony.ColonyDB;

/**
 * @author pbu
 */
public class KolonieTableModel extends AbstractTableModel
{

	static final public int COLONY_OBJ = 0;
	static final public int NAME = 1;
	static final public int POPULATION = 2;
	static final public int GROW = 3;
	static final public int UNEMPLOYEMENT = 4;
	static final public int PRODUCTION = 5;
	static final public int MINING = 6;
	static final public int SCIENCE = 7;
	static final public int INCOME = 8;
	static final public int FARMING = 9;
	static final public int BUILD_JOB = 10;
	private ColonyDB kolonieDB = Main.instance().getMOUDB().getKolonieDB();
	// Primäre Datenliste, enthält ID-Objecte zu Kolonien
	private List listKolonien = new ArrayList();

	/**
	 * 
	 */
	public KolonieTableModel()
	{
		new Timer(1000, new ActionListener()
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
		listKolonien = new ArrayList(kolonieDB.getAlleKolonien().values());
		// Iterator iter = kolonieDB.getAlleKolonien().values().iterator();
		// while(iter.hasNext())
		// {
		// Colony kol = (Colony)iter.next();
		// listKolonien.add(kol.getID());
		// }
		fireTableDataChanged();
	}

	/**
	 * @param index
	 *            Zeilenindex
	 * @return
	 */
	public Colony getKolonieAtIndex(int index)
	{
		return (Colony) listKolonien.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 10;// Inklusive ID Spalte
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return listKolonien.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Colony kolonie = getKolonieAtIndex(rowIndex);
		// kolonie.computePoints();
		switch(columnIndex)
		{
			case COLONY_OBJ:
				return kolonie;
			case NAME:
				return kolonie.getName();
			case GROW:
				return new Float(kolonie.getDayPopulationGrowPercent());// Wachstum
			case INCOME:
				return new Long((long) kolonie.getIncomeBalance());
			case BUILD_JOB:
				BuildJobAbstract job = kolonie.getBuildQueue().getCurrentBuildJob();// Bauauftrag
				if(job == null)
					return "";
				else
					return job.toString();
			case MINING:
				return new Long((long) kolonie.getMiningPoints());
			case PRODUCTION:
				return new Long((long) kolonie.getProduction());
			case POPULATION:
				return kolonie.getPopulation().longValue();
			case SCIENCE:
				return new Long((long) kolonie.getSciencePoints());
			case FARMING:
				return new Long((long) kolonie.getFarming());
			case UNEMPLOYEMENT:
				return new Float((float) (kolonie.computeUnemploymentFactor() * 100));
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
