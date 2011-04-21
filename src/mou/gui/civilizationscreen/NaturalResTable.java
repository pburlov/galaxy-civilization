/*
 * $Id: NaturalResTable.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mou.Main;
import mou.core.res.natural.NaturalResource;
import mou.event.RowClickedEvent;
import mou.event.RowClickedEventListener;
import mou.gui.MOUNumberTableCellRenderer;
import org.jdesktop.swing.JXTable;

/**
 * @author pb
 */
public class NaturalResTable extends JPanel
{

	protected JXTable jTable;
	protected NaturalResourceTableModel tableModel = new NaturalResourceTableModel();
	private Vector rowEventListeners = new Vector();

	/**
	 * 
	 */
	public NaturalResTable()
	{
		jTable = new JXTable(tableModel);
		setLayout(new GridLayout(1, 1));
		JScrollPane scroll = new JScrollPane(jTable);
		add(scroll, BorderLayout.NORTH);
		// scroll.setPreferredSize(new Dimension(300,300));
		// scroll.getViewport().setPreferredSize(new Dimension(300,150));
		// scroll.getViewport().setMaximumSize(new Dimension(300,150));
		// scroll.getViewport().setSize(new Dimension(300,150));
		// scroll.setPreferredSize(new Dimension(300,300));
		jTable.setDefaultRenderer(Integer.class, new MOUNumberTableCellRenderer());
		jTable.setDefaultRenderer(Long.class, new MOUNumberTableCellRenderer());
		jTable.setDefaultRenderer(Double.class, new MOUNumberTableCellRenderer());
		jTable.setDefaultRenderer(Float.class, new MOUNumberTableCellRenderer());
		jTable.setDefaultRenderer(Number.class, new MOUNumberTableCellRenderer());
		jTable.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				int index = jTable.rowAtPoint(e.getPoint());
				if(index < 0) return;
				Object value = tableModel.getNaturalRessourceAtIndex(jTable.convertRowIndexToModel(index));
				if(value != null) dispatchRowClickedEvent(new RowClickedEvent(jTable, index, value));
				
				// :CHEAT: Rohstoffmenge vergrößern
				if(Main.isDebugMode())
				{
					if(e.getClickCount() >= 2)
						Main.instance().getMOUDB().getStorageDB().addMenge(((NaturalResource) value).getID(), (long) 20E6, true);
				}
			}
		});
	}

	public void addRowClickedEventListener(RowClickedEventListener listener)
	{
		rowEventListeners.add(listener);
	}

	protected void dispatchRowClickedEvent(RowClickedEvent event)
	{
		for(Iterator iter = rowEventListeners.iterator(); iter.hasNext();)
		{
			RowClickedEventListener listener = (RowClickedEventListener) iter.next();
			listener.rowClicked(event);
		}
	}

	// public void addActionListener(ActionListener listener)
	// {
	// _actionListeners.add(listener);
	// }
	//	
	// public void removeActionListener(ActionListener listener)
	// {
	// _actionListeners.remove(listener);
	// }
	public void addMouseListener(MouseListener l)
	{
		jTable.addMouseListener(l);
	}

	public int getSelectedRow()
	{
		return jTable.getSelectedRow();
	}

	public int[] getSelectedRows()
	{
		return jTable.getSelectedRows();
	}

	public NaturalResource getItemAtIndex(int index)
	{
		return tableModel.getNaturalRessourceAtIndex(index);
	}

	public int rowAtPoint(Point point)
	{
		return jTable.rowAtPoint(point);
	}
}
