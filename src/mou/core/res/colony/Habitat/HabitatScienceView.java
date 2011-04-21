/*
 * $Id: HabitatScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res.colony.Habitat;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;

public class HabitatScienceView extends DefaultBuildingScienceView
{

	public HabitatScienceView(Habitat res)
	{
		super(res);
		addComponent("Wohnraum für:", new JLabel(GUI.formatSmartDouble(res.computeLivingSpace())), "Personen");
	}
}
