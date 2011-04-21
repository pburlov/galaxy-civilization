/*
 * $Id: HyperdriveScienceViewComponent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem.engine;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

/**
 * @author pb
 */
public class HyperdriveScienceViewComponent extends DefaultResearchableScienceView
{

	private JLabel labelHyper = new JLabel();

	/**
	 * 
	 */
	public HyperdriveScienceViewComponent(Hyperdrive res)
	{
		super(res);
		addComponent("Leistung Lj/Tag:", labelHyper, "");
		labelHyper.setText(GUI.formatDouble(res.computeHyper()));
	}
}
