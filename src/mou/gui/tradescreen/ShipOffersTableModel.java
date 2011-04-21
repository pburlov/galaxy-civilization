/*
 * $Id: ShipOffersTableModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.tradescreen;

import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.core.trade.RemoteShipTradeOffer;

/**
 * @author pb
 */
public class ShipOffersTableModel extends AbstractTableModel
{

	static final public int SHIP_OFFER_OBJECT = 0;
	static final public int NAME = 1;
	static final public int HERSTELLER = 2;
	static final public int SPEED = 3;
	static final public int MASSE = 4;
	// static final public int STRUKTUR = 50;
	static final public int WEAPON = 5;
	static final public int PANZER = 6;
	static final public int SHILD = 7;
	static final public int SUPPORT = 8;
	static final public int CREW = 9;
	static final public int PRICE = 10;
	static final public int QUANTITY = 11;
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();
	// static final private Class[] CLASSES = {String.class,Point.class,Double.class};
	private List<RemoteShipTradeOffer> rowData;

	/**
	 * 
	 */
	public ShipOffersTableModel()
	{
		rowData = Collections.EMPTY_LIST;
	}

	/**
	 * 
	 */
	public ShipOffersTableModel(List<RemoteShipTradeOffer> data)
	{
		rowData = data;
	}

	public void setRowData(List<RemoteShipTradeOffer> data)
	{
		rowData = data;
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		if(rowData == null) return 0;
		return rowData.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return 12;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		RemoteShipTradeOffer offer = rowData.get(rowIndex);
		switch(columnIndex)
		{
			case SHIP_OFFER_OBJECT:
				return offer;
			case NAME:
				return offer.getName();// Name
			case HERSTELLER:
				return civDB.getCivName(CivilizationDB.createCivID(offer.getOfferID().getConstantPart()));
			case SPEED:
				return new Double(offer.getSpeed());// Geschwindigkeit
			case MASSE:
				return new Long((long) offer.getMasse());// Masse
				// case STRUKTUR:
				// return new Double(offer.getStruktur());//Struktur
			case CREW:
				return new Long((long) offer.getCrew());
			case PANZER:
				return new Long((long) offer.getPanzer());
			case SHILD:
				return new Long((long) offer.getShild());
			case WEAPON:
				return new Long((long) offer.getWeapon());
			case SUPPORT:
				return new Long((long) offer.getSupport());
			case PRICE:
				return new Long((long) offer.getPrice());
			case QUANTITY:
				return new Integer(offer.getQuantity());
		}
		return null;
	}
}