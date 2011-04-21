/*
 * $Id: CivilizationScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import mou.Preferences;
import mou.gui.GUIScreen;
import mou.gui.civilizationscreen.ColonyTable.KolonieTabelle;

/**
 * Klasse zur Darstellung und Verwaltung der eigene Civilization samt Kolonien
 */
public class CivilizationScreen extends JPanel
		implements GUIScreen
{

	private KolonieTabelle kolonieTabelle = new KolonieTabelle();
	private JPanel panelKolonien = new JPanel();
	private CivManagerPanel panelManager = new CivManagerPanel();

	public CivilizationScreen()
	{
		this.setLayout(new BorderLayout());
		panelKolonien.setBorder(new TitledBorder("Kolonien"));
		panelKolonien.setLayout(new BorderLayout());
		panelKolonien.add(kolonieTabelle, BorderLayout.CENTER);
		this.add(panelKolonien, BorderLayout.CENTER);
		add(panelManager, BorderLayout.NORTH);
	}

	public void restoreProperties(Preferences prefs)
	{
	}

	public void saveProperties(Preferences prefs)
	{
	}
}