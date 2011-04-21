/*
 * $Id: LifeSupportScienceViewComponent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem.lifesupport;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

/**
 * @author pb
 */
public class LifeSupportScienceViewComponent extends DefaultResearchableScienceView
{

	private JLabel labelSupport = new JLabel();

	/**
	 * 
	 */
	public LifeSupportScienceViewComponent(LifeSupportSystem res)
	{
		super(res);
		addComponent("Lebenserhaltung für: ", labelSupport, " Mann");
		labelSupport.setText(GUI.formatDouble(res.computeLebenserhaltung()));
	}
}