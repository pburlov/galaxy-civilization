/*
 * $Id: ShipDesignScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.shipdesignscreen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mou.Preferences;
import mou.core.research.ResearchableDesign;
import mou.core.ship.ShipClass;
import mou.gui.GUIScreen;
import mou.gui.sciencescreen.ResearchedDesignsList;

/**
 * @author pbu
 */
public class ShipDesignScreen extends JPanel
		implements GUIScreen
{

	private ShipDesignList designList = new ShipDesignList();
	private ResearchedDesignsList komponentList = new ResearchedDesignsList(true);
	private ShipDesigner shipDesigner = new ShipDesigner();

	public ShipDesignScreen()
	{
		setLayout(new BorderLayout());
		add(designList, BorderLayout.WEST);
		add(komponentList, BorderLayout.EAST);
		add(shipDesigner, BorderLayout.CENTER);
		designList.setMinimumSize(new Dimension(300, 200));
		komponentList.setMinimumSize(new Dimension(300, 200));
		komponentList.getJList().addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent event)
			{
				if(event.getButton() != MouseEvent.BUTTON1) return;
				shipDesigner.addSystem((ResearchableDesign) komponentList.getJList().getSelectedValue());
			}
		});
		designList.addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent ev)
			{
				if(ev.getValueIsAdjusting() || designList.getSelectedValue() == null) return;
				ShipClass ship = (ShipClass) designList.getSelectedValue();
				// ship.initWithData(((ShipClass)designList.getSelectedValue()).getObjectData());
				shipDesigner.showShipClass(ship);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#saveProperties(mou.Preferences)
	 */
	public void saveProperties(Preferences prefs)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#restoreProperties(mou.Preferences)
	 */
	public void restoreProperties(Preferences prefs)
	{
	}
}
