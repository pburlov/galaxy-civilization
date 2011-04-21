/*
 * $Id$
 * Created on 04.05.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.MaterialStorage;

import javax.swing.JLabel;
import mou.core.res.colony.DefaultBuildingScienceView;
import mou.gui.GUI;


/**
 * @author Dominik
 *
 */
public class MaterialStorageScienceView extends DefaultBuildingScienceView
{

	/**
	 * @param res
	 */
	public MaterialStorageScienceView(MaterialStorage res)
	{
		super(res);
		JLabel label = new JLabel();
		label.setText(GUI.formatSmartDouble(res.computeTotalCapacity()/1E6)+"mio ");
		addComponent("Lagergröße: ", label,"T");
		label = new JLabel();
		label.setText(GUI.formatLong(res.computeCustomValue(2)));
		addComponent("Maximale Gebäudegröße: ", label, "");
	}
}
