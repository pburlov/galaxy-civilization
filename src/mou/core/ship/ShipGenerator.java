/*
 * $Id: ShipGenerator.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.research.ResearchDB;
import mou.core.research.ResearchableDesign;
import mou.storage.ser.ID;

/**
 * Klasse dien zur Generierung der Schiffe für zufällige spielinterne Ereignisse (Rebellion,
 * Invasion, usw.)
 */
public class ShipGenerator
{

	private ResearchDB researchDB;
	private double strengIndex = 0;

	public ShipGenerator(ResearchDB db)
	{
		super();
		researchDB = db;
		strengIndex = computeShipResearchStrengIndex();
	}

	/**
	 * Generiert zufällige Anzahl der Schiffen, deren Gesamtmasse dem gewünschten Wert angenährt
	 * wird.
	 * 
	 * @param gesamtmasse
	 * @return
	 */
	public Collection<Ship> generateShips(double gesamtmasse)
	{
		ArrayList<Ship> ret = new ArrayList<Ship>();
		int count = (int) (Math.random() * 10) + 1;
		double masse = gesamtmasse / count;
		for(int i = count; i > 0; i--)
		{
			ret.add(generateShip(masse));
		}
		return ret;
	}

	/**
	 * Methode generiert ein Schiff anhang der aktuellen Forschungsergebnissen einer Zivilisation.
	 * 
	 * @param masse
	 * @param db
	 * @return
	 */
	public Ship generateShip(double masse)
	{
		double val = strengIndex * masse;
		Ship ship = new Ship();
		ship.setCrew(1000);
		ship.setShipClassName("Generated ship");
		ship.setSpeed(5);
		ship.setMasse(masse);
		ship.setStruktur(masse);
		ship.setArmor(val / 4);
		ship.setShild(val / 4);
		ship.setWeapon(val / 4);
		ship.setBuildcost(masse);
		ship.setShipClassID(new ID());
		return ship;
	}

	/**
	 * Methode berechnet ein Mittelwert der Stärke besten erforschten Schiffsystemen
	 * 
	 * @return
	 */
	private double computeShipResearchStrengIndex()
	{
		Map<ID, Number> bestValues = new HashMap<ID, Number>();
		List<ResearchableDesign<ShipsystemAbstract>> systems = researchDB.getResearchedShipsystems();
		/*
		 * Zuerst beste Werte für einzige Schiffsystemtypen rausfinden
		 */
		for(ResearchableDesign<ShipsystemAbstract> system : systems)
		{
			ShipsystemAbstract sa = system.getResearchableResource();
			Number val = bestValues.get(sa.getID());
			Number valnew = system.getResearchableResource().computeCustomValue(1);
			if(val == null)
				bestValues.put(sa.getID(), valnew);
			else
			{
				if(val.doubleValue() < valnew.doubleValue()) bestValues.put(sa.getID(), valnew);
			}
		}
		double ret = 0;
		/*
		 * Ermittelte Bestwerte addieren und durch Teilung mit der Anzahl der Werte den Mittelwert
		 * finden.
		 */
		for(Number val : bestValues.values())
		{
			ret += val.doubleValue();
		}
		return (ret / bestValues.size()) / 2;
	}
}
