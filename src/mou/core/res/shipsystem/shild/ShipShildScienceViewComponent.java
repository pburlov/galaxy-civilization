/*
 * $Id: ShipShildScienceViewComponent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem.shild;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

/**
 * @author pb
 */
public class ShipShildScienceViewComponent extends DefaultResearchableScienceView
{

	private JLabel labelSchutz = new JLabel();

	/**
	 * 
	 */
	public ShipShildScienceViewComponent(ShipShild res)
	{
		super(res);
		addComponent("Schutz: ", labelSchutz, "Treffpunkte");
		labelSchutz.setText(GUI.formatDouble(res.computeSchutz()));
	}
}