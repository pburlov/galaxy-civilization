/*
 * $Id: ColonyModul.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.shipsystem.colonymodul;

import java.util.Random;
import java.util.Set;
import javax.swing.JComponent;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.ship.ShipClass;
import mou.storage.ser.ID;

/**
 * @author pb Im Unterschied zu anderen Schiffsystemen verbessert sich bei dem Koloniemodul nicht
 *         die Wirkleistung sondern die Baukosten und Energieverbrauch werden mit den besseren
 *         Forschungsegebnissen immer geringer. Sonst wäre es unsinnig mit einer Tonne Masse mehrere
 *         Hunderte Kolonisten unterzubringen.
 */
public class ColonyModul extends ShipsystemAbstract
{
	//TODO: Überarbeiten, neue Formel für Baukosten,...

	/**
	 * Feste Koloniestenzahl per Tonne Masse
	 */
	static final private double NORMALIZED_CREW = 0.2;

	public ColonyModul()
	{
		super();
	}

	public double computeKolonistenzahl()
	{
		return computeNeededCrew();
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new ColonyModulScienceView(this);
	}

	@Override
	public double computeNormalizedBuildCost()
	{
		double ret;
		
		ret = getNormalizedBuildCostFaktor().doubleValue();
		ret *= 1 / (getQualityFaktor().doubleValue()*Math.pow(CUSTOM_VALUE_POW_BASE, getMaterialien().size()));
		ret *= computeNormalizedMass()*200d;
		return ret;

	}
	
	@Override
	public double computeNormalizedMass()
	{
		return 500000d;
	}
	
	@Override
	protected void generateCrewFaktor(Random rnd, Set<ID> materials)
	{
	}
	
	@Override
	protected void generateEnergyBalanceFaktor(Random rnd, Set<ID> materials)
	{
	}

	// @Override
	// public Number getNormalizedEnergieverbrauch()
	// {
	// /*
	// *Bei einem Koloniemodul geht die Entwicklung in die Richtung
	// *weniger Baukosten und Energieverbrauch, und nicht mehr Kolonisten
	// *per Tonne Masse
	// */
	// return 1 / super.getNormalizedEnergieverbrauch().doubleValue();
	// }
	//
	@Override
	public double computeNormalizedCrew()
	{
		return NORMALIZED_CREW * computeNormalizedMass();
	}

	@Override
	public String getName()
	{
		return "Koloniemodul";
	}

	@Override
	public String getShortDescription()
	{
		return "<html>Ermöglicht Gründung neuer Kolonien und Erweiterung der alten.<br>" + "Das Koloniemodul samt des Trägerschiffs wird demontiert<br>"
				+ "und mit der gewonnen Materialien eine Kolonie aufgebaut</html>";
	}

	@Override
	protected void computeShipPointsIntern(ShipClass ship)
	{
		ship.setSettler(ship.getSettler() + computeKolonistenzahl());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.core.IDable#getID()
	 */
	public ID getID()
	{
		return ID_SHIPSYSTEM_COLONYMODUL;
	}

	@Override
	public String getHtmlFormattedInfoIntern()
	{
		return "";
		// return "<b>Kolonisten: </b>"+GUI.formatSmartDouble(computeKolonistenzahl());
	}

	public String getImagePath()
	{
		return "/res/images/koloniemodul.png";
	}

	@Override
	public double computeNormalizedEnergyBalance()
	{
		return -10*computeNormalizedMass();
	}
}
