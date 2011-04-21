/*
 * $Id: Armor.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.shipsystem.armor;

import java.util.Random;
import java.util.Set;
import javax.swing.JComponent;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class Armor extends ShipsystemAbstract
{

	/**
	 * 
	 */
	public Armor()
	{
		super();
	}

	public String getImagePath()
	{
		return "/res/images/panzerung.png";
	}

	public JComponent getScienceViewComponent()
	{
		return new ArmorScienceViewComponent(this);
	}

	public double computeNeededCrew()
	{
		return 0;
	}
	
	@Override
	public double computeNormalizedEnergyBalance()
	{
		return DOUBLE_0;
	}
	
	@Override
	public double computeNormalizedSupportCost()
	{
		return 0;
	}

	@Override
	public String getName()
	{
		return "Panzerung";
	}

	@Override
	public String getShortDescription()
	{
		return "<html>Schutzschicht aus Materie.<br>" + "Panzerung braucht keine Energie und keine Wartung.<br> "
				+ "Kann aber nur nach dem Kampfende repariert werden" + "</html>";
	}

	public ID getID()
	{
		return ID_SHIPSYSTEM_ARMOR;
	}

	@Override
	protected void generateEnergyBalanceFaktor(Random rnd, Set<ID> materials)
	{
	}
	
	@Override
	protected void generateSupportCostFaktor(Random rnd, Set<ID> materials)
	{
	}

	@Override
	protected void computeShipPointsIntern(ShipClass ship)
	{
		ship.setPanzer(ship.getPanzer() + computeGeamtschutz());
	}

	public double computeGeamtschutz()
	{
		return computeCustomValue(1) * getSize().doubleValue();
	}

	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "<b>Schutz: </b>" + GUI.formatSmartDouble(computeGeamtschutz());
	}
}
