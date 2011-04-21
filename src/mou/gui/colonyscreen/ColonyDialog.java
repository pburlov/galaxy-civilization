/*
 * $Id: ColonyDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import mou.Main;
import mou.core.colony.Colony;

/**
 * Maske zur detailierten Anzeuge und Administration einer Kolonie
 * 
 * @author pbu
 */
public class ColonyDialog extends JDialog
{

	private ColonyPanel colonyPanel = new ColonyPanel();

	public ColonyDialog(Frame parent)
	{
		super(parent, true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(colonyPanel, BorderLayout.CENTER);
		setAlwaysOnTop(isAlwaysOnTop());
		colonyPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		colonyPanel.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		// :CHEAT: Bevölkerung vergrösern
		colonyPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK),
				"Cheat_population");
		colonyPanel.getActionMap().put("Cheat_population", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(!Main.isDebugMode()) return;
				Colony kol = colonyPanel.getShowedColony();
				if(kol == null) return;
				String res = JOptionPane.showInputDialog(null, "Bevölkerung: ", kol.getPopulation().intValue());
				try
				{
					kol.setPopulation(Integer.parseInt(res));
				} catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		// :CHEAT: Rebellion auslösen
		colonyPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), "Rebellion");
		colonyPanel.getActionMap().put("Rebellion", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(!Main.isDebugMode()) return;
				Colony kol = colonyPanel.getShowedColony();
				if(kol == null) return;
				kol.beginRebelion();
			}
		});
		
		addWindowListener(new WindowListener()
				{
					public void windowOpened(WindowEvent arg0)
					{
						colonyPanel.startRefreshTimer();
					}
					public void windowClosing(WindowEvent arg0)
					{
						colonyPanel.stopRefreshTimer();
					}
					public void windowClosed(WindowEvent arg0)
					{}
					public void windowIconified(WindowEvent arg0)
					{}
					public void windowDeiconified(WindowEvent arg0)
					{}
					public void windowActivated(WindowEvent arg0)
					{
						colonyPanel.startRefreshTimer();
					}
					public void windowDeactivated(WindowEvent arg0)
					{}		
				});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#restoreProperties(mou.Preferences)
	 */
	public void showKolonie(Colony kol)
	{
		colonyPanel.showKolonie(kol);
	}
	
	public Colony getShowedColony()
	{
		return colonyPanel.getShowedColony();
	}

}