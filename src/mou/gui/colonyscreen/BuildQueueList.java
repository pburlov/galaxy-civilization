/*
 * $Id: BuildQueueList.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import mou.core.colony.BuildJobAbstract;
import mou.core.colony.BuildQueue;
import mou.core.colony.Colony;

/**
 * @author pbu
 */
public class BuildQueueList extends JPanel
{

	private TitledBorder border = new TitledBorder("Bauaufträge");
	private JList listBuildings = new JList();
	private BuildQueue mQueue;
	private Colony mKolonie;
	private PopupMenu popupMenu = new PopupMenu();

	public BuildQueueList()
	{
		setBorder(border);
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane();
		add(scroll, BorderLayout.CENTER);
		scroll.getViewport().add(listBuildings);
		listBuildings.setAutoscrolls(true);
		listBuildings.setCellRenderer(new BuildingCellRenderer());
		listBuildings.setFixedCellWidth(200);
		
		listBuildings.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
		listBuildings.getActionMap().put("Delete", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				Object list[] = listBuildings.getSelectedValues();
				listBuildings.clearSelection();
				if(list == null || list.length == 0) return;
				for(Object job : list)
					mKolonie.getBuildQueue().removeFromBuildQueue(((BuildJobAbstract) job).getID());
			}
		});

		
		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent ev)
			{
				if(ev.getButton() != MouseEvent.BUTTON3) return;
				if(popupMenu.isVisible()) popupMenu.setVisible(false);
				popupMenu.show(listBuildings, ev.getPoint().x, ev.getPoint().y);
			}
		});
	}

	public void showBuildQueue(Colony kolonie)
	{
		int[] selection = listBuildings.getSelectedIndices();
		mKolonie = kolonie;
		mQueue = kolonie.getBuildQueue();
		listBuildings.setListData(mQueue.getJobList().toArray());
		listBuildings.setSelectedIndices(selection);
	}

	public JList getJList()
	{
		return listBuildings;
	}

	public BuildJobAbstract getSelecteBuildJob()
	{
		return (BuildJobAbstract) listBuildings.getSelectedValue();
	}
	
	// public Integer getSelectedValue()
	// {
	// return (Integer)mapBuildings.get(listBuildings.getSelectedValue());
	// }
	//	
	private class BuildingCellRenderer extends JPanel
			implements ListCellRenderer
	{

		// private ShipClassDB shipClassDB = Main.instance().getMOUDB().getShipClassDB();
		private JLabel label = new JLabel();

		public BuildingCellRenderer()
		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(label);
			add(Box.createHorizontalGlue());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			BuildJobAbstract item = (BuildJobAbstract) value;
			if(item == null)
			{
				label.setText("null");
				return this;// list.remove(index);
			}
			// mKolonie.computePoints();
			// if(item instanceof BuildingBuildQueueItem)
			// {//Gebäude anzeigen
			// int stufe = item.getValue().intValue();
			// long bauzeit = item.computeNeededWorkPoints() / (mKolonie.getWorkPoints()+1);
			// if(bauzeit < 1)bauzeit = 1;
			// setText("(Gebäude) "+item.getText() + " Stufe: "+stufe+
			// " Bauzeit: "+bauzeit +"Tage");
			// }
			long bauzeit = (long) (item.computeNeededWorkPoints() / (mKolonie.getProduction() + 1));
			if(bauzeit < 1) bauzeit = 1;
			label.setText(item.getName() + " Bauzeit: " + bauzeit + "Tage");
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
	 * @see java.awt.Component#addMouseListener(java.awt.event.MouseListener)
	 */
	public synchronized void addMouseListener(MouseListener l)
	{
		listBuildings.addMouseListener(l);
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
					Object list[] = listBuildings.getSelectedValues();
					listBuildings.clearSelection();
					if(list == null || list.length == 0) return;
					for(Object job : list)
						mKolonie.getBuildQueue().removeFromBuildQueue(((BuildJobAbstract) job).getID());
				}
			});
		}
	}

}
