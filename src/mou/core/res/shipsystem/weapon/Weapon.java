/*
 * $Id: Weapon.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.shipsystem.weapon;

import javax.swing.JComponent;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class Weapon extends ShipsystemAbstract
{

	/**
	 * 
	 */
	public Weapon()
	{
		super();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/waffe.png";
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new WeaponScienceViewComponent(this);
	}

	@Override
	protected void computeShipPointsIntern(ShipClass ship)
	{
		ship.setWaffenstarke(ship.getWaffenstarke() + computeWaffenstaerke());
	}

	/**
	 * @return
	 */
	public double computeWaffenstaerke()
	{
		return computeCustomValue(1) * getSize().doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IDable#getID()
	 */
	public ID getID()
	{
		return ID_SHIPSYSTEM_WEAPON;
	}

	@Override
	public String getName()
	{
		return "Schiffswaffe";
	}

	@Override
	public String getShortDescription()
	{
		return "Verursacht Schäden an feindlichen Schiffen";
	}

	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "<b>Stärke: </b>" + GUI.formatSmartDouble(computeWaffenstaerke());
	}
}
