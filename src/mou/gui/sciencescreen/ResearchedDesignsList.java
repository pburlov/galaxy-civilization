/*
 * $Id: ResearchedDesignsList.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.sciencescreen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import mou.Main;
import mou.core.res.DefaultResearchableListCellRenderer;
import mou.core.res.ResearchableResource;
import mou.core.research.ResearchDBLstener;
import mou.core.research.ResearchableDesign;

/**
 * @author pbu
 */
public class ResearchedDesignsList extends JPanel
{

	private JScrollPane scrollPane = new JScrollPane();
	private JList listRessourcen = new JList();
	private PopupMenu popupMenu = new PopupMenu();
	private boolean showShipSystems = true;

	public ResearchedDesignsList(boolean showShipSystems)
	{
		this.showShipSystems = showShipSystems;
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(listRessourcen);
		// setBorder(new TitledBorder("Schiffsysteme"));
		listRessourcen.setCellRenderer(new DefaultResearchableListCellRenderer());
		listRessourcen.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent ev)
			{
				if(ev.getButton() != MouseEvent.BUTTON3) return;
				listRessourcen.setSelectedIndex(listRessourcen.locationToIndex(ev.getPoint()));
				if(popupMenu.isVisible()) popupMenu.setVisible(false);
				popupMenu.show(listRessourcen, ev.getPoint().x, ev.getPoint().y);
			}
		});
		listRessourcen.setFixedCellHeight(35);
		listRessourcen.setFixedCellWidth(200);
		Main.instance().getMOUDB().getResearchDB().addResearchDBListener(new ResearchDBLstener()
		{

			public void researchResultRemoved(ResearchableDesign<? extends ResearchableResource> des)
			{
				refreshList();
			}

			public void researchResultAdded(ResearchableDesign<? extends ResearchableResource> des)
			{
				refreshList();
			}
		});
		// new Timer(1000, new ActionListener()
		// {
		//
		// public void actionPerformed(ActionEvent e)
		// {
		// refreshList();
		// }
		// }).start();
		refreshList();
	}

	public void refreshList()
	{
		Collection artRes = null;
		if(isShowShipsystems())
			artRes = Main.instance().getMOUDB().getResearchDB().getResearchedShipsystems();
		else
			artRes = Main.instance().getMOUDB().getResearchDB().getResearchedBuildings();
		int selectedIndex = listRessourcen.getSelectedIndex();
		listRessourcen.setListData(artRes.toArray());
		if(selectedIndex >= 0 && artRes.size() > selectedIndex) listRessourcen.setSelectedIndex(selectedIndex);
		return;
	}

	public boolean isShowShipsystems()
	{
		return showShipSystems;
	}

	public void setShowShipsystems(boolean showShipsystems)
	{
		this.showShipSystems = showShipsystems;
	}

	// public void addListSelectionListener(ListSelectionListener listener)
	// {
	// listRessourcen.addListSelectionListener(listener);
	// }
	public JList getJList()
	{
		return listRessourcen;
	}
	
	public JPopupMenu getPopupMenu()
	{
		return popupMenu;
	}

	public ResearchableDesign getSelection()
	{
		return (ResearchableDesign) listRessourcen.getSelectedValue();
	}
	
	private void deleteSelection()
	{
		ResearchableDesign resDes = getSelection();
		if(resDes == null) return;
		Main.instance().getMOUDB().getResearchDB().deleteResearchableDesign(resDes.getID());
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
					int option = JOptionPane.showConfirmDialog(popupMenu,"Wollen Sie das Design: " + getSelection().getName() + " wirklich löschen?", "Achtung!",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(option != JOptionPane.OK_OPTION) return;

					deleteSelection();
				}
			});
		}
	}
}