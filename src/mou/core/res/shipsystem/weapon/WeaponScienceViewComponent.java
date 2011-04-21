/*
 * $Id: WeaponScienceViewComponent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem.weapon;

import javax.swing.JLabel;
import mou.core.res.DefaultResearchableScienceView;
import mou.gui.GUI;

/**
 * @author pb
 */
public class WeaponScienceViewComponent extends DefaultResearchableScienceView
{

	private JLabel labelSchaden = new JLabel();

	/**
	 * 
	 */
	public WeaponScienceViewComponent(Weapon res)
	{
		super(res);
		addComponent("Stärke:", labelSchaden, "Punkte");
		labelSchaden.setText(GUI.formatDouble(res.computeWaffenstaerke()));
	}
}
