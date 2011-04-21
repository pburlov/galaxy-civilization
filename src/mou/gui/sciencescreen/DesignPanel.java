/*
 * $Id: DesignPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.sciencescreen;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import mou.core.research.ResearchableDesign;

public class DesignPanel extends JPanel
{

	private JPanel panelResEdit = new JPanel();// Panel zum plazieren der ressourcespezifieschen
												// UI-Elementen

	/**
	 * 
	 */
	public DesignPanel()
	{
		setLayout(new BorderLayout());
		add(panelResEdit, BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.CENTER);
	}

	public void showRessource(ResearchableDesign res)
	{
		panelResEdit.removeAll();
		JComponent resUi = res.getResearchableResource().getScienceViewComponent();
		panelResEdit.add(resUi, BorderLayout.NORTH);
		panelResEdit.validate();
	}
}
