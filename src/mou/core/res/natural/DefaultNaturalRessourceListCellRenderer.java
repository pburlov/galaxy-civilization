/*
 * $Id: DefaultNaturalRessourceListCellRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar
 * 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou.core.res.natural;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import mou.Main;
import mou.core.civilization.NaturalRessourceDescriptionDB;
import mou.core.res.DefaultResourceUISmall;
import mou.storage.ser.ID;

/**
 * @author pbu
 */
final public class DefaultNaturalRessourceListCellRenderer extends DefaultResourceUISmall
		implements ListCellRenderer
{

	private NaturalRessourceDescriptionDB resDB = Main.instance().getMOUDB().getNaturalRessourceDescriptionDB();

	/**
	 * 
	 */
	public DefaultNaturalRessourceListCellRenderer()
	{
		super();
	}

	public DefaultNaturalRessourceListCellRenderer(boolean opaque)
	{
		super(opaque);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		NaturalResource res = null;
		if(value instanceof ID)
		{
			res = resDB.getNaturalResource((ID) value);
		} else
			res = (NaturalResource) value;
		showResource(res);
		if(isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setEnabled(list.isEnabled());
		// setOpaque(false);
		return this;
		// try
		// {
		// RessourceDescription res = (RessourceDescription)value;
		// mRenderer.setText(res.getName());
		// mRenderer.setIcon(res.getIcon());
		// }catch(ClassCastException e)
		// {
		// mRenderer.setText(value.toString());
		// mRenderer.setIcon(null);
		// }
		// if(isSelected)
		// {
		// mRenderer.setBackground(list.getSelectionBackground());
		// mRenderer.setForeground(list.getSelectionForeground());
		// }else
		// {
		// mRenderer.setBackground(list.getBackground());
		// mRenderer.setForeground(list.getForeground());
		// }
		// mRenderer.setFont(list.getFont());
		// mRenderer.setEnabled(list.isEnabled());
		// mRenderer.setOpaque(true);
		// return mRenderer;
	}
}
