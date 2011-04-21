/*
 * $Id: BuildingDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.core.research.ResearchableDesign;

public class BuildingDialog extends JDialog
{
	private BuildingUiAbstract buildingUI = null;
	// private BuildingDialog self;
	
	public BuildingDialog(Dialog parent, final Colony col, final ResearchableDesign<BuildingAbstract> building) throws HeadlessException
	{
		super(parent, true);
		// self = this;
		setTitle(building.getName());
		buildingUI = building.getResearchableResource().getBuildingUI();
		setResizable(false);
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		getContentPane().add(pane);
		pane.setLayout(new BorderLayout());
		pane.add(buildingUI, BorderLayout.CENTER);
		buildingUI.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		buildingUI.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		JPanel panel = new JPanel();
		pane.add(panel, BorderLayout.SOUTH);
		JButton button = new JButton("Geb‰ude abreiﬂen");
		panel.add(button);
		button.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				// if(JOptionPane.showConfirmDialog(self,
				// "Diese Geb‰ude wirklich abreiﬂen?", "Best‰tigung",
				// JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)return;
				DiscardBuildingDialog dlg = new DiscardBuildingDialog();
				int quantity = dlg.showDialog(building.getResearchableResource().getSize().intValue(), building.getName());
				if(quantity < 1) return;
				col.resizeBuilding(building, -quantity);
				/*
				 * Geb‰ude nicht mehr vorhanden, also Dialog schliessen
				 */
				buildingUI.stopRefreshTimer();
				dispose();
			}
		});
		
		addWindowListener(new WindowListener()
				{
					public void windowOpened(WindowEvent arg0)
					{
						buildingUI.startRefreshTimer();
					}
					public void windowClosing(WindowEvent arg0)
					{
						buildingUI.stopRefreshTimer();
					}
					public void windowClosed(WindowEvent arg0)
					{}
					public void windowIconified(WindowEvent arg0)
					{}
					public void windowDeiconified(WindowEvent arg0)
					{}
					public void windowActivated(WindowEvent arg0)
					{}
					public void windowDeactivated(WindowEvent arg0)
					{}		
				});
		
		pack();
	}
}
