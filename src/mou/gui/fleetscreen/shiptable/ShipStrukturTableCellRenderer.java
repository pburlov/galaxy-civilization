/*
 * $Id: ShipStrukturTableCellRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.fleetscreen.shiptable;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import mou.core.ship.Ship;
import mou.gui.StatusBalken;

/**
 * @author pb
 */
public class ShipStrukturTableCellRenderer extends StatusBalken
		implements TableCellRenderer
{

	// private StarmapDB starDB = Main.instance().getMOUDB().getStarmapDB();
	/**
	 * 
	 */
	public ShipStrukturTableCellRenderer()
	{
		super(0, 1);
		this.setBorderColor(null);
		// setBorder(new BevelBorder(BevelBorder.LOWERED));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Ship ship = (Ship) value;
		if(value == null) return this;
		setMax(ship.getStruktur().intValue());
		setValue(ship.getCurrentStruktur().intValue());
		return this;
	}
}