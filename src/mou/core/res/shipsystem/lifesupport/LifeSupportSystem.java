/*
 * $Id: LifeSupportSystem.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.shipsystem.lifesupport;

import javax.swing.JComponent;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class LifeSupportSystem extends ShipsystemAbstract
{

	static final private double INITIAL_LIVE_SUPPORT = 1;
	static final private double FACT_A = 40;
	static final private double FACT_B = 20;
	/**
	 * 
	 */
	public LifeSupportSystem()
	{
		super();
	}

	public String getImagePath()
	{
		return "/res/images/lebenserhaltung.png";
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new LifeSupportScienceViewComponent(this);
	}

	@Override
	public double computeCustomValue(int number)
	{
		double stufe = getMaterialien().size();
		double ret = INITIAL_LIVE_SUPPORT * (Math.log(stufe) /(1+ FACT_A* Math.exp(-stufe))*FACT_B+1);
		ret *= getQualityFaktor().doubleValue()/4;
		
		return ret;
	}
	
	@Override
	public int getCustomCounter()
	{
		return (int) (computeCustomValue(1)*100);
	}
	
	public double computeLebenserhaltung()
	{
		return computeCustomValue(1) * getSize().doubleValue();
	}

	@Override
	public String getName()
	{
		return "Lebenserhaltung";
	}

	@Override
	public String getShortDescription()
	{
		return "Versorgt die Schiffsbesatzung mit Luft, Wasser und Nahrung";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.IDable#getID()
	 */
	public ID getID()
	{
		return ID_SHIPSYSTEM_LIFE_SUPPORT;
	}

	protected void computeShipPointsIntern(ShipClass ship)
	{
		ship.setLebenserhaltung(ship.getLebenserhaltung() + computeLebenserhaltung());
	}

	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "<b>Lebenserhaltung für: </b>" + GUI.formatSmartDouble(computeLebenserhaltung()) + " Person";
	}
}
