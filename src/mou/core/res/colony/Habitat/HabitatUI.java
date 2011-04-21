/*
 * $Id: HabitatUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.colony.Habitat;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;

public class HabitatUI extends DefaultBuildingUI<Habitat>
{

	private JLabel labelLivingSpyce = new JLabel();

	public HabitatUI(Habitat b)
	{
		super(b);
		addSliderPanel(1);
		addField("Wohnraum:", labelLivingSpyce);
	}

	@Override
	protected void refreshValuesIntern(Habitat b)
	{
		labelLivingSpyce.setText(GUI.formatSmartDouble(b.computeLivingSpace()));
	}
}
