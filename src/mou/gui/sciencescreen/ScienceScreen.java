/*
 * $Id: ScienceScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.sciencescreen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mou.Main;
import mou.Preferences;
import mou.core.research.ResearchableDesign;
import mou.gui.GUIScreen;

/**
 * Bildschirm zum erforschen neuen Schiffsystemen
 * 
 * @author pbu
 */
final public class ScienceScreen extends JPanel
		implements GUIScreen
{

	final protected ResearchedDesignsList listResearchedShipSystems = new ResearchedDesignsList(true);
	final protected ResearchedDesignsList listResearchedBuildings = new ResearchedDesignsList(false);
	final protected DesignPanel designPanel_1 = new DesignPanel();
	final protected DesignPanel designPanel_2 = new DesignPanel();
	final protected ForschungPanel forschungPanel = new ForschungPanel();

	public ScienceScreen()
	{
		// setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setLayout(new GridLayout(1, 3));
		forschungPanel.setAlignmentX(0.0f);
		Container panel = new JPanel();
		// panel.add(forschungPanel);
		add(forschungPanel, BorderLayout.WEST);
		// add(Box.createHorizontalGlue());
		panel = new JPanel(new GridLayout(2, 1));
		add(panel, BorderLayout.CENTER);
		Container panel2 = new JPanel(new GridLayout(1, 1));
		panel2.add(designPanel_1);
		panel.add(panel2);
		panel2 = new JPanel(new GridLayout(1, 1));
		panel2.add(designPanel_2);
		panel.add(panel2);
		// add(Box.createHorizontalGlue());
		JTabbedPane tabbet = new JTabbedPane();
		tabbet.setBorder(new TitledBorder("Forschungsergebnisse"));
		add(tabbet, BorderLayout.EAST);
		tabbet.add("Schiffssysteme", listResearchedShipSystems);
		tabbet.add("Koloniegebäude", listResearchedBuildings);
		listResearchedShipSystems.setAlignmentX(1.0f);
		forschungPanel.getJListForschungsergebnisse().addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent e)
			{
				if(e.getValueIsAdjusting() || forschungPanel.getJListForschungsergebnisse().getSelectedIndex() < 0) return;
				ResearchableDesign system = (ResearchableDesign) forschungPanel.getJListForschungsergebnisse().getSelectedValue();
				if(system == null) return;
				designPanel_1.removeAll();
				JComponent resUi = system.getResearchableResource().getScienceViewComponent();
				designPanel_1.add(resUi);
				designPanel_1.validate();
			}
		});
		listResearchedShipSystems.getJList().addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent e)
			{
				if(e.getValueIsAdjusting() || listResearchedShipSystems.getJList().getSelectedIndex() < 0) return;
				ResearchableDesign system = (ResearchableDesign) listResearchedShipSystems.getJList().getSelectedValue();
				if(system == null) return;
				designPanel_2.removeAll();
				JComponent resUi = system.getResearchableResource().getScienceViewComponent();
				designPanel_2.add(resUi);
				designPanel_2.validate();
			}
		});
		listResearchedBuildings.getJList().addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent e)
			{
				if(e.getValueIsAdjusting() || listResearchedBuildings.getJList().getSelectedIndex() < 0) return;
				ResearchableDesign system = (ResearchableDesign) listResearchedBuildings.getJList().getSelectedValue();
				if(system == null) return;
				designPanel_2.removeAll();
				JComponent resUi = system.getResearchableResource().getScienceViewComponent();
				designPanel_2.add(resUi);
				designPanel_2.validate();
			}
		});
		// :CHEAT: Forschung beenden
		forschungPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "Complete_research");
		forschungPanel.getActionMap().put("Complete_research", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(!Main.isDebugMode()) return;
				Main.instance().getMOUDB().getResearchDB().investResearchPoints(Main.instance().getMOUDB().getResearchDB().getNeededResearchPoints());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#restoreProperties(mou.Preferences)
	 */
	public void restoreProperties(Preferences prefs)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#saveProperties(mou.Preferences)
	 */
	public void saveProperties(Preferences prefs)
	{
	}
}
