/*
 * $Id$
 * Created on 01.04.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.Silo;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;


/**
 * @author Dominik
 *
 */
public class SiloScienceView extends DefaultBuildingScienceView
{

	/**
	 * @param res
	 */
	public SiloScienceView(Silo res)
	{
		super(res);
		JLabel label = new JLabel();
		label.setText(GUI.formatLong(res.computeCapacity()/1E6)+"mio ");
		addComponent("Lagergröße: ", label,"T");
		label = new JLabel();
		label.setText(GUI.formatLong(res.computeCustomValue(2)));
		addComponent("Maximale Lagerdauer: ", label,"Tage");
	}
}
