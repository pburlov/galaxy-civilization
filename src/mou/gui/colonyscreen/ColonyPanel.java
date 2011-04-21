/*
 * $Id: ColonyPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import mou.core.colony.BuildJobAbstract;
import mou.core.colony.BuildQueue;
import mou.core.colony.BuildingBuildJob;
import mou.core.colony.Colony;
import mou.core.colony.ShipBuildJob;
import mou.core.res.colony.BuildingAbstract;
import mou.core.research.ResearchableDesign;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.gui.sciencescreen.ResearchedDesignsList;
import mou.gui.shipdesignscreen.ShipDesignList;
import org.jdesktop.swing.utils.WindowUtils;

/**
 * Component zur Administration einer Kolonie.
 * 
 * @author pbu
 */
public class ColonyPanel extends JPanel
{

	private Colony kolonie;
	private ColonyInfoPanel infoPanel = new ColonyInfoPanel();
	private BuildQueueList listBuildQueue = new BuildQueueList();
	private ShipDesignList listShipClasses = new ShipDesignList();
	private ResearchedDesignsList listResearchedBuidings = new ResearchedDesignsList(false);
	private BuildingList listBuildings = new BuildingList();
	private CurrentBuildPanel currentBuildPanel = new CurrentBuildPanel();
	private StarsystemInfoPanel starInfo = new StarsystemInfoPanel();
	private Timer refreshTimer;
	/**
	 * 
	 */
	public ColonyPanel()
	{
		setLayout(new GridLayout(1, 3));
		add(listBuildings);
		Container panelY = new Box(BoxLayout.Y_AXIS);
		add(panelY);
		panelY.add(infoPanel);
		panelY.add(starInfo);
		panelY = new Box(BoxLayout.Y_AXIS);
		add(panelY);
		JTabbedPane tabbed = new JTabbedPane();
		tabbed.setBorder(new TitledBorder("Bauobjekte"));
		panelY.add(tabbed);
		tabbed.add("Schiffe", listShipClasses);
		tabbed.add("Gebäude", listResearchedBuidings);
		panelY.add(currentBuildPanel);
		panelY.add(listBuildQueue);
		panelY.add(Box.createVerticalGlue());
		listShipClasses.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent event)
			{
				if(kolonie.isRebelled()) return;
				if(event.getButton() == MouseEvent.BUTTON1)
				{
					ShipClass ship = listShipClasses.getSelectedShipClass();
					if(ship == null) return;
					kolonie.getBuildQueue().addToBuildQueue(new ShipBuildJob(ship));
					showKolonie(kolonie);
				}
			}
		});
		/* Erweiter Popupmenü um Baumöglichkeit mit beliebiger Gebäudeanzahl */
		JMenuItem itemBuild = new JMenuItem("Bauen...");
		itemBuild.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent ev)
					{
						int buildSize = 0;
						
						ShipClass ship = listShipClasses.getSelectedShipClass();
						BuildQueue queue = kolonie.getBuildQueue();
						if(ship == null) return;
						
						BuildDialog dialog = new BuildDialog(WindowUtils.findJDialog(listShipClasses), ship);
						buildSize = dialog.showDialog();
						if(buildSize <= 0) return;
						for(int i=0; i<buildSize-1; i++)
							queue.addToBuildQueue(new ShipBuildJob(ship, false));
						queue.addToBuildQueue(new ShipBuildJob(ship, true));
						showKolonie(kolonie);
					}
				});
		listShipClasses.getPopupMenu().insert(itemBuild, 0);

		listResearchedBuidings.getJList().addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(kolonie.isRebelled()) return;
				if(e.getButton() == MouseEvent.BUTTON1)
				{
					ResearchableDesign<BuildingAbstract> des = (ResearchableDesign<BuildingAbstract>) listResearchedBuidings.getJList().getSelectedValue();
					if(des == null) return;
					des.getResearchableResource().setColonyID(getShowedColony().getID());
					kolonie.getBuildQueue().addToBuildQueue(new BuildingBuildJob(des));
					showKolonie(kolonie);
				}
			}
		});
		/* Erweiter Popupmenü um Baumöglichkeit mit beliebiger Gebäudeanzahl */
		itemBuild = new JMenuItem("Bauen...");
		itemBuild.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent ev)
					{
						int buildSize = 0;
						
						ResearchableDesign<BuildingAbstract> des = (ResearchableDesign<BuildingAbstract>) listResearchedBuidings.getJList().getSelectedValue();
						BuildQueue queue = kolonie.getBuildQueue();
						if(des == null) return;
						des.getResearchableResource().setColonyID(getShowedColony().getID());
						
						BuildDialog dialog = new BuildDialog(WindowUtils.findJDialog(listResearchedBuidings), des);
						buildSize = dialog.showDialog();
						if(buildSize <= 0) return;
						for(int i=0; i<buildSize-1; i++)
							queue.addToBuildQueue(new BuildingBuildJob(des, false));
						queue.addToBuildQueue(new BuildingBuildJob(des, true));
						showKolonie(kolonie);
					}
				});
		listResearchedBuidings.getPopupMenu().insert(itemBuild, 0);
		
		listBuildQueue.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent event)
			{
				if(kolonie.isRebelled()) return;
				if(event.getClickCount()<2)	return;
				if(event.getButton() == MouseEvent.BUTTON1)
				{
					BuildJobAbstract job = listBuildQueue.getSelecteBuildJob();
					listBuildQueue.getJList().clearSelection();
					if(job == null) return;
					kolonie.getBuildQueue().removeFromBuildQueue(job.getID());
					showKolonie(kolonie);
				}
			}
		});
		listBuildings.getJList().addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent event)
			{
				if(kolonie.isRebelled()) return;
				ResearchableDesign<BuildingAbstract> des = listBuildings.getSelectedBuilding();
				if(des == null) return;
				BuildingDialog dialog = new BuildingDialog(WindowUtils.findJDialog(listBuildings), kolonie, des);
				GUI.centreWindow(null, dialog);
				dialog.setVisible(true);
			}
		});
		refreshTimer = new Timer(1000, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				showKolonie(kolonie);
			}
		});
	}

	public void startRefreshTimer()
	{
		refreshTimer.start();
	}

	public void stopRefreshTimer()
	{
		refreshTimer.stop();
	}
	
	public void showKolonie(final Colony kol)
	{
		if(kol == null) return;
		kolonie = kol;
		listBuildQueue.showBuildQueue(kol);
		infoPanel.showColony(kol.getID());
		currentBuildPanel.showKolonie(kol);
		Vector<ResearchableDesign<BuildingAbstract>> buildings = new Vector<ResearchableDesign<BuildingAbstract>>(kol.getBuildings());
		/*
		 * Liste der gebauten Gebäude nach Gebäudetypen gruppieren
		 */
		Collections.sort(buildings, new Comparator<ResearchableDesign<BuildingAbstract>>()
		{

			public int compare(ResearchableDesign<BuildingAbstract> o1, ResearchableDesign<BuildingAbstract> o2)
			{
				return o1.getResearchableDescriptionID().compareTo(o2.getResearchableDescriptionID());
			}
		});
		listBuildings.showBuildings(buildings);
		starInfo.showStar(kolonie.getStarSystem());
	}

	public Colony getShowedColony()
	{
		return kolonie;
	}
}
