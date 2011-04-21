/*
 * $Id: ShipShild.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.shipsystem.shild;

import javax.swing.JComponent;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class ShipShild extends ShipsystemAbstract
{

	/**
	 * 
	 */
	public ShipShild()
	{
		super();
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/shild.png";
	}

	public double computeSchutz()
	{
		return computeCustomValue(1) * getSize().doubleValue();
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new ShipShildScienceViewComponent(this);
	}

	@Override
	public String getName()
	{
		return "Schutzschild";
	}

	@Override
	public String getShortDescription()
	{
		return "<html>Regenerativer Energieschutzschild<br>" + "Wird während des Kampfes ständig regeneriert<br> " + "</html>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IDable#getID()
	 */
	public ID getID()
	{
		return ID_SHIPSYSTEM_SHILD;
	}

	@Override
	protected void computeShipPointsIntern(ShipClass ship)
	{
		ship.setSchild(ship.getSchild() + computeSchutz());
	}

	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "<b>Schutz: </b>" + GUI.formatSmartDouble(computeSchutz());
	}
}
