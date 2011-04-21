/*
 * $Id: ShipDesignList.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.shipdesignscreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionListener;
import mou.Main;
import mou.core.db.DBEventListener;
import mou.core.db.DBObjectImpl;
import mou.core.db.ObjectChangedEvent;
import mou.core.ship.ShipClass;

/**
 * Zeig eine Liste mit fertigen Schiffskonstruktionen
 * 
 * @author pbu
 */
public class ShipDesignList extends JPanel
		implements DBEventListener
{

	private JScrollPane scrollPane = new JScrollPane();
	private JList jList = new JList();
	private PopupMenu popupMenu = new PopupMenu();

	public ShipDesignList()
	{
		setLayout(new BorderLayout());
		// setBorder(new TitledBorder("Schiffsdesigns"));
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(jList);
		jList.setCellRenderer(new CellRenderer());
		// jList.setFixedCellHeight(35);
		jList.setFixedCellWidth(200);
		jList.setListData(new Vector(Main.instance().getMOUDB().getShipClassDB().getAllShipClasses().values()));
		Main.instance().getMOUDB().getShipClassDB().addDBEventListener(this);
		jList.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent ev)
			{
				if(ev.getButton() != MouseEvent.BUTTON3) return;
				jList.setSelectedIndex(jList.locationToIndex(ev.getPoint()));
				if(popupMenu.isVisible()) popupMenu.setVisible(false);
				popupMenu.show(jList, ev.getPoint().x, ev.getPoint().y);
			}
		});
	}

	/**
	 * @param listener
	 */
	public void addListSelectionListener(ListSelectionListener listener)
	{
		jList.addListSelectionListener(listener);
	}

	/**
	 * @return
	 */
	public Object getSelectedValue()
	{
		return jList.getSelectedValue();
	}

	public ShipClass getSelectedShipClass()
	{
		return (ShipClass) getSelectedValue();
	}

	private void deleteSelection()
	{
		ShipClass resDes = (ShipClass) jList.getSelectedValue();
		if(resDes == null) return;
		Main.instance().getMOUDB().getShipClassDB().deleteShipClass(resDes.getID());
	}

	public JPopupMenu getPopupMenu()
	{
		return popupMenu;
	}
	
	private class PopupMenu extends JPopupMenu
	{

		private JMenuItem itemDelete = new JMenuItem("Löschen");

		PopupMenu()
		{
			add(itemDelete);
			itemDelete.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent ev)
				{
					deleteSelection();
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
	 */
	public synchronized void addMouseListener(MouseListener l)
	{
		jList.addMouseListener(l);
	}

	private class CellRenderer extends JLabel
			implements ListCellRenderer
	{

		/*
		 * Klasse rendert einzelnen Zellen in der Liste
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			ShipClass ship = (ShipClass) value;
			ship.computeWerte();
			setText(ship.getName());
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
			setOpaque(true);
			return this;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.DBEventListener#objectRemoved(mou.core.DBObjectImpl)
	 */
	public void objectRemoved(DBObjectImpl obj)
	{
		jList.setListData(new Vector(Main.instance().getMOUDB().getShipClassDB().getAllShipClasses().values()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.DBEventListener#objectAdded(mou.core.DBObjectImpl)
	 */
	public void objectAdded(DBObjectImpl obj)
	{
		jList.setListData(new Vector(Main.instance().getMOUDB().getShipClassDB().getAllShipClasses().values()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.DBEventListener#objectChanged(mou.core.ObjectChangedEvent)
	 */
	public void objectChanged(ObjectChangedEvent event)
	{
	}
}
