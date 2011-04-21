/*
 * $Id: Hyperdrive.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res.shipsystem.engine;

import javax.swing.JComponent;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class Hyperdrive extends ShipsystemAbstract
{

	static final private double INITIAL_HYPER = 1;
	static final private double FACT_A = 40;
	static final private double FACT_B = 20;
	/**
	 * 
	 */
	public Hyperdrive()
	{
		super();
	}

	public String getImagePath()
	{
		return "/res/images/hyperantrieb.png";
	}

	public JComponent getScienceViewComponent()
	{
		return new HyperdriveScienceViewComponent(this);
	}

	@Override
	public String getName()
	{
		return "Hyperantrieb";
	}

	@Override
	public String getShortDescription()
	{
		return "Ermöglicht interstellaren Reisen";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IDable#getID()
	 */
	public ID getID()
	{
		return ID_SHIPSYSTEM_HYPERDRIVE;
	}

	protected void computeShipPointsIntern(ShipClass ship)
	{
		ship.setHyperantrieb(ship.getHyperantrieb() + computeHyper());
	}

	/**
	 * Berechnet Hyperantriebst?rke
	 * 
	 * @return
	 */
	public double computeHyper()
	{
		return computeCustomValue(1) * getSize().doubleValue();
	}
	
	@Override
	public double computeCustomValue(int number)
	{
		double stufe = getMaterialien().size();
		double ret = INITIAL_HYPER * (Math.log(stufe) /(1+ FACT_A* Math.exp(-stufe))*FACT_B+1);
		ret *= getQualityFaktor().doubleValue()/4;
		
		return ret;
	}

	@Override
	public int getCustomCounter()
	{
		return (int) (computeCustomValue(1)*100);
	}
	
	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "<b>Leistung: </b>" + GUI.formatSmartDouble(computeHyper()) + " Lj/Tags";
	}
}
