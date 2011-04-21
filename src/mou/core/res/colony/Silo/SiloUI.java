/*
 * $Id$
 * Created on 01.04.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.Silo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import mou.Main;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;


/**
 * @author Dominik
 *
 */
public class SiloUI extends DefaultBuildingUI<Silo>
{

	private JLabel storedLabel = new JLabel();
	private JLabel storageLabel = new JLabel();
	private JLabel storageTimeLabel = new JLabel();
	
	public SiloUI(final Silo building)
	{
		super(building);
		addSliderPanel(1, false, true);
		slider.setSliderLabel(0, "Vorrat: ");
		addField("Lagergröße: ", storageLabel);
		addField("gelagerte Nahrungsmittel: ", storedLabel);
		addField("maximale Lagerzeit: ", storageTimeLabel);
		
		// :CHEAT: Lagerhaus füllen
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "Cheat_Silo_Fill");
		getActionMap().put("Cheat_Silo_Fill", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(!Main.isDebugMode()) return;
				building.addFood(building.computeCapacity());
			}
		});

	}

	@Override
	protected void refreshValuesIntern(Silo building)
	{
		storageLabel.setText(GUI.formatLong(building.computeCapacity()/1E6)+"mio T");
		storedLabel.setText(GUI.formatSmartDouble(building.getFood().doubleValue()/1E6)+"mio T");
		storageTimeLabel.setText(GUI.formatLong(building.computeCustomValue(2))+"Tage");
	}
	
	protected boolean showSlider()
	{
		return false;
	}
	
	protected boolean showProgress()
	{
		return true;
	}
}
