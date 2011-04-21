/*
 * $Id: ShipsystemAbstract.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res.shipsystem;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import mou.core.res.ResearchableResource;
import mou.core.res.ResourceMenge;
import mou.core.ship.ShipClass;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * Superklasse aller Schiffsystemen
 * 
 * @author pb
 */
abstract strictfp public class ShipsystemAbstract extends ResearchableResource
{

	static final private String STRUKTUR = "STRUKTUR";

	/**
	 * 
	 */
	public ShipsystemAbstract()
	{
		super();
	}

	@Override
	protected void generateMassFaktor(Random rnd, Set<ID> materials)
	{
	}
	
	@Override
	protected void generateCrewFaktor(Random rnd, Set<ID> materials)
	{
		/*
		 * Crewstärke wird ganz zufällig gewählt und ist nicht mit der Entwicklung des Systems
		 * gekoppelt
		 */
		setNormalizedCrewFaktor((rnd.nextDouble() * 0.25) + 0.05);
	}
	
	@Override
	protected void generateEnergyBalanceFaktor(Random rnd, Set<ID> materials)
	{
		/*
		 * Energieverbrauch wird exponentiell an die Forschungsstufe gekoppelt.
		 */
		setNormalizedEnergyBalanceFaktor(-(rnd.nextDouble() + 0.5));
	}
	

	@Override
	protected void generateSupportCostFaktor(Random rnd, Set<ID> materials)
	{
		/*
		 * Unterhaltskosten werden einfach als ein Bruchteil von dem Bauaufwand berechnet
		 * multipliziert mit einem zufälligem Wert. Somit steigen die Betriebskosten mit der
		 * Komplexität des Systems an.
		 */
		setNormalizedSupportCostFaktor(rnd.nextDouble() + 0.5); 
	}
	
	@Override
	public double computeNormalizedMass()
	{
		/*
		 * Eine Größeneinheit ist gleich 1 Tonne
		 */
		return 1;
	}

	@Override
	public String getExtendenInfoForListCellRenderer()
	{
		return "Leistung: " + GUI.formatDouble(computeCustomValue());
	}

	public String getHtmlFormattedInfo()
	{
		String ret = "<html><b>" + getName() + "</b>" + "<br><b>Crew: </b>" + GUI.formatSmartDouble(computeNeededCrew()) + "<br><b>Energie: </b>"
				+ GUI.formatSmartDouble(computeEnergyBalance()) + "<br><b>Baukosten: </b>" + GUI.formatSmartDouble(computeBuildCost())
				+ "<br><b>Unterhalstkosten: </b>" + GUI.formatSmartDouble(computeSupportCost()) + "<br>" + getHtmlFormattedInfoIntern()
				+ "<br><b>=== Baustoffe ===</b>" + "<br>" + GUI.htmlFormatRessourceMenge(getNeededRes()) + "</html>";
		return ret;
	}

	/**
	 * Methode soll eine in HTML formatierte Information über abgeleitete Eigenschften dieses System
	 * geben. Anfürende und abschlißende [html] Tags sollen hier weggelassen werden.
	 * 
	 * @return
	 */
	abstract public String getHtmlFormattedInfoIntern();

	/**
	 * Hier wird die eigentliche Wirkung des Systems auf die Eigenschaften des Schiffs berechnet.
	 * Die Subklassen müssen Methode computeShipPointsIntern implementieren.
	 * 
	 * @param ship
	 */
	public void computeShipPoints(ShipClass ship)
	{
		ship.setEnergie(ship.getEnergie() + computeEnergyBalance());
		ship.setCrew(ship.getCrew() + computeNeededCrew());
		ship.setSupportCost(ship.getSupportCost() + computeSupportCost());
		ship.setMasse(ship.getMasse() + computeMasse());
		ship.setBuildCost(ship.getBuildCost() + computeBuildCost());
		ship.setStruktur(ship.getStruktur() + getStruktur().doubleValue());
		/*
		 * Benötigte Baumateriaen hinzufügen
		 */
		Map<ID, Number> sm = ship.getNeededMaterials();
		for(ResourceMenge menge : getNeededRes())
		{
			Number val = sm.get(menge.getRessource().getID());
			if(val == null)
				val = menge.getMenge();
			else
				val = val.doubleValue() + menge.getMenge();
			sm.put(menge.getRessource().getID(), val);
		}
		computeShipPointsIntern(ship);
	}

	abstract protected void computeShipPointsIntern(ShipClass ship);
	
	@Override
	public double computeNormalizedCrew()
	{
		return getNormalizedCrewFaktor().doubleValue() * 1d;
	}
	
	@Override
	public double computeNormalizedEnergyBalance()
	{
		double ret;
		ret = getNormalizedEnergyBalanceFaktor().doubleValue();
		ret *= getQualityFaktor().doubleValue() * Math.pow(CUSTOM_VALUE_POW_BASE, getMaterialien().size());
		
		return ret;
	}
	
	@Override
	public double computeNormalizedSupportCost()
	{
		double ret;
		ret = getNormalizedSupportCostFaktor().doubleValue() * getQualityFaktor().doubleValue();
		ret *= Math.pow(CUSTOM_VALUE_POW_BASE, getMaterialien().size());
		ret *= computeNormalizedMass()/ 1E6;
		return ret;
	}
	
	public Number getStruktur()
	{
		Number ret = (Number) getAttribute(STRUKTUR);
		if(ret == null) ret = computeMasse();
		return ret;
	}

	public void setStruktur(Number val)
	{
		setAttribute(STRUKTUR, val);
	}

}
