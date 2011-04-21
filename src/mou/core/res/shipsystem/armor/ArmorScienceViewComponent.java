/*
 * $Id: ArmorScienceViewComponent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem.armor;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

/**
 * @author pb
 */
public class ArmorScienceViewComponent extends DefaultResearchableScienceView
{

	private final JLabel labelStrengt = new JLabel();

	/**
	 * 
	 */
	public ArmorScienceViewComponent(Armor res)
	{
		super(res);
		addComponent("Schutzstärke:", labelStrengt, "");
		// removeComponent(ENERGIEBALANCE);
		labelStrengt.setText(GUI.formatDouble(res.computeGeamtschutz()));
	}
}
