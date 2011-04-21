/*
 * $Id: BuildingList.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import mou.core.res.DefaultResearchableListCellRenderer;
import mou.core.res.colony.BuildingAbstract;
import mou.core.research.ResearchableDesign;

/**
 * Zeigt Liste mit Gebäuden. Daten werden als Parameter für die Methode showBuildings(..) mitgegeben
 * 
 * @author pbu
 */
public class BuildingList extends JPanel
{

	private JList listBuildings = new JList();

	public BuildingList()
	{
		setBorder(new TitledBorder("Koloniegebäude"));
		setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane();
		add(scroll, BorderLayout.CENTER);
		scroll.setViewportView(listBuildings);
		listBuildings.setCellRenderer(new DefaultResearchableListCellRenderer());
	}

	/**
	 * Zeigt eine Liste mit Gebäude- oder Schiffsdesigns-namen
	 * 
	 * @param buildings
	 *            Liste mit IDs von Gebäuden oder Schiffsdesigns
	 */
	public void showBuildings(Vector<ResearchableDesign<BuildingAbstract>> data)
	{
		listBuildings.setListData(data);
	}

	public JList getJList()
	{
		return listBuildings;
	}

	public ResearchableDesign<BuildingAbstract> getSelectedBuilding()
	{
		return (ResearchableDesign<BuildingAbstract>) listBuildings.getSelectedValue();
	}
}
