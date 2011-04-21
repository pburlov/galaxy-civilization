/*
 * $Id$
 * Created on 27.04.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import mou.Main;
import mou.core.res.colony.BuildingAbstract;
import mou.core.research.ResearchableDesign;
import mou.core.ship.ShipClass;
import mou.gui.GUI;


/**
 * @author Dominik
 *
 */
public class BuildDialog extends JDialog
{
	/* Um ein einfrieren des Programms beim hinzufügen zu vieler Gebäude zu verhindern
	 * nch oben hin begrenzen. Dies reduziert nicht die max. zu bauende Anzahl durch Mehrfachaufrufe
	 */
	private Integer MAX_VALUE = 1000;
	
	private JButton buttonBuild = new JButton("Bauen");
	private JButton buttonCancel = new JButton("Abbrechen");
	private JSpinner spinnerNumber;
	private int maxSize = 0;
	private String jobName;
	private boolean ok=false;
	
	BuildDialog(Dialog parent, ResearchableDesign<BuildingAbstract> des)
	{
		super(parent, true);
		
		BuildingAbstract colonyBuilding = null;
		BuildingAbstract building = (BuildingAbstract) des.getResearchableResource();
		ResearchableDesign colonyDes = building.getColony().getBuilding(des.getID());
		if(colonyDes!=null)
			colonyBuilding = (BuildingAbstract) colonyDes.getResearchableResource();
		jobName = des.getName();
		/* Berechen maximale noch zu bauende Anzahl */
		maxSize = (int) Math.ceil(building.getMaxSize());
		if((colonyBuilding!=null)&&(maxSize!=-1))
			maxSize-= colonyBuilding.getSize().intValue();
		if((maxSize == -1)||(maxSize > MAX_VALUE))
			maxSize = MAX_VALUE;
		else if(maxSize < 0)
			maxSize = 0;
		
		createDialog();
	}

	BuildDialog(Dialog parent, ShipClass ship)
	{
		super(parent, true);
		
		maxSize = MAX_VALUE;
		jobName = ship.getName();
		
		createDialog();
	}
	
	private void createDialog()
	{
		setTitle("Bauen: " + jobName);
		setResizable(false);
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		add(pane);
		pane.add(new JLabel("Zu bauende Anzahl:"), BorderLayout.PAGE_START);

		spinnerNumber = new JSpinner(new SpinnerNumberModel(0, 0, maxSize, 1));
		pane.add(spinnerNumber, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		pane.add(panel, BorderLayout.PAGE_END);
		panel.add(buttonBuild, BorderLayout.LINE_START);
		panel.add(buttonCancel, BorderLayout.LINE_END);
		
		buttonBuild.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok = true;
				setVisible(false);
			}
		});
		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		spinnerNumber.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		spinnerNumber.getActionMap().put("Escape", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		spinnerNumber.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		spinnerNumber.getActionMap().put("Enter", new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						ok = true;
						setVisible(false);
					}
				});

	}
		
	public int showDialog()
	{
		pack();
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
		if(ok)
		{
			ok = false;
			return ((Integer) spinnerNumber.getValue()).intValue();
		} else
			return 0;

	}
}
