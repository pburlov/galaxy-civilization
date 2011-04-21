/*
 * $Id: Colony.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.colony;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mou.Main;
import mou.core.civilization.CivDayReport;
import mou.core.civilization.CivYearReport;
import mou.core.civilization.Civilization;
import mou.core.civilization.CivilizationDB;
import mou.core.civilization.InfluenceFactor;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.db.AbstractDB;
import mou.core.db.DBObjectImpl;
import mou.core.res.ResearchableResource;
import mou.core.res.ResourceAbstract;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.ColonyCenter.ColonyCenter;
import mou.core.res.colony.MaterialStorage.MaterialStorage;
import mou.core.res.colony.Silo.Silo;
import mou.core.res.natural.NaturalResource;
import mou.core.research.ResearchableDesign;
import mou.core.ship.Ship;
import mou.core.ship.ShipDB;
import mou.core.ship.ShipGenerator;
import mou.core.starmap.Positionable;
import mou.core.starmap.StarSystem;
import mou.gui.GUI;
import mou.net.battle.ShipInfo;
import mou.net.battle.SpaceBattle;
import mou.net.battle.SpaceBattleResult;
import mou.storage.ser.ID;
import org.apache.commons.lang.SerializationUtils;

public class Colony extends DBObjectImpl
		implements Positionable
{

	// static final private double BASE_WORK_POINTS = 10;
	// static final private double BASE_MINING_POINTS = 10;
	// static final private double BASE_RESEARCH_POINTS = 10;
	// static final private double BASE_INCOME_POINTS = 10;
	static final public String ATTR_CIV_ID = "ATTR_CIV_ID";
	static final public String ATTR_STAR_POS = "ATTR_STAR_POS";
	static final public String ATTR_POPULATION = "ATTR_POPULATION";
	static final public String ATTR_UNEMPLOYMENT = "ATTR_UNEMPLOYMENT";
	static final public String ATTR_PRODUCTION = "ATTR_PRODUCTION";
	static final public String ATTR_INCOME_BRUTTO = "ATTR_INCOME_BRUTTO";
	static final public String ATTR_MINING = "ATTR_MINING";
	static final public String ATTR_FARMING = "ATTR_FARMING";
	static final public String ATTR_FOOD_BALANCE = "ATTR_FOOD_BALANCE";
	static final public String ATTR_POPULATION_GROW = "ATTR_POPULATION_GROW";
	static final public String ATTR_SCIENCE = "ATTR_SCIENCE";
	static final public String ATTR_MORAL = "ATTR_MORAL";
	static final public String ATTR_HABITAT_CAPACITY = "ATTR_HABITAT_CAPACITY";
	static final public String ATTR_SUPPORT_COST = "ATTR_SUPPORT_COST";
	static final public String ATTR_BUILDINGS = "ATTR_BUILDINGS";
	static final public String ATTR_BUILD_JOB = "ATTR_BUILD_JOB";
	static final public String ATTR_BUILD_QUEUE = "ATTR_BUILD_QUEUE";
	static final private String ATTR_MORAL_FACTORS = "ATTR_MORAL_FACTORS";
	static final private String ATTR_GROW_FACTORS = "ATTR_GROW_FACTORS";
	static final private String ATTR_IS_REBELLED = "ATTR_IS_REBELLED";
	static final private String ATTR_GROW_DAY_VALUES_SUM = "ATTR_GROW_DAY_VALUS_SUM";
	static final private String ATTR_GROW_DAY_VALUES_COUNT = "ATTR_GROW_DAY_VALUES_COUNT";
	private static final int ANFANGSBEVOELKERUNG = 100000;
	static final private double BASE_POPULATION_GROW = 0.04d;
	static final private double REBEL_POPULATION_GROW = -0.1d;
	private StarSystem star;
	private double bruttoProduction;
	private double bruttoMining;
	private double bruttoFarming;
	private double bruttoResearch;
	
	// /**
	// * Konstruktor für fremde Kolonien mit begrenzten Informationsgehalt
	// */
	// public Colony(long civId, Point pos, Integer pop)
	// {
	// super(new Hashtable());
	// setCivID(new ID(0,civId));
	// setPopulation(pop);
	// setStarPosition(pos);
	// }
	/**
	 * Leere Konstruktor für Instanzizierung per Refleektion
	 */
	public Colony()
	{
	}

	/**
	 * Konstruktor für neue eigene Kolonien
	 * 
	 * @param db
	 * @param civ
	 * @param starPosition
	 */
	Colony(AbstractDB db, ID civ, Point starPosition)
	{
		super(new Hashtable());
		setCivID(civ);
		setStarPosition(starPosition);
		setPopulation(ANFANGSBEVOELKERUNG);
		setAttribute(ATTR_BUILDINGS, new HashMap(), false);
		registerAsMaterialStorage();
		/* Beim gründen der Kolonie IdleBuildJob starten, um Meldung über leere Bauliste zu unterdrücken */
		getBuildQueue().addToBuildQueue(new IdleBuildJob());
	}

	/** 
	 * Erlaubt es aufräumarbeiten zu starten, wenn die Kolonie gelöscht wird
	 */
	public void cleanUp()
	{
		if(getNaturalResourcesSize() > 0)
			removeAsMaterialStorage();
	}
	
	/**
	 * Methode wird aufgerufen, wenn eine Kolonie per Kolonieschiff erweitert wird. Neben der
	 * Populationsvergößerung, wird auch der bestehende Koloniezentrum erweitert.
	 * 
	 * @param size
	 */
	public void extendsColony(double size)
	{
		addBuilding(new ResearchableDesign<BuildingAbstract>(new ColonyCenter(size), "Koloniezentrum"));
		setPopulation(getPopulation().doubleValue() + size);
	}

	/**
	 * Macht der NaturalRessourcesStorageDB bekannt, welche Rohstoffe in dieser Kolonie vorhanden sind 
	 *
	 */
	private void registerAsMaterialStorage()
	{
		NaturalRessourcesStorageDB storage = Main.instance().getMOUDB().getStorageDB();
		for(Iterator iter = getStarSystem().getNatRessources().iterator(); iter.hasNext();)
		{
			NaturalResource res = (NaturalResource) iter.next();
			storage.registerStorage(res.getID(), getID());
		}

	}
	
	/**
	 * Gibt der NaturalRessourcesStorageDB bekannt, daß in dieser Kolonie keine Rohstoffe mehr gelagert werden können
	 * noch vorhandene Rohstoffe werden auf andere Kolonien verteilt
	 */
	private void removeAsMaterialStorage()
	{
		NaturalRessourcesStorageDB storage = Main.instance().getMOUDB().getStorageDB();
		for(Iterator iter = getStarSystem().getNatRessources().iterator(); iter.hasNext();)
		{
			NaturalResource res = (NaturalResource) iter.next();
			storage.removeStorage(res.getID(), getID());
			storage.addMenge(res.getID(), computeMaterialInStorage(res.getID()), true);
		}
		
	}
	
	/**
	 * Hinzufügt neue Gebäude zu der Gebäudeliste. Wenn eine Gebäude des gleiche Bautyp schon
	 * exisitiert, dann wird alte Gebäude vergrößert. Vor der Hinzufügen wird der
	 * ResearchableDesign-Objekt geklont.
	 * 
	 * @param building
	 */
	public void addBuilding(ResearchableDesign<BuildingAbstract> building)
	{
		/*
		 * Sicherheitshalber Daten clonen
		 */
		Map clonedData = (Map) SerializationUtils.clone((Serializable) building.getObjectData());
		building = new ResearchableDesign<BuildingAbstract>(clonedData);
		building.getResearchableResource().setBuilded(true);
		ID designID = building.getID();
		building.getResearchableResource().setColonyID(getID());
		ResearchableDesign oldDesign = getBuilding(designID);

		if(oldDesign != null)
		{
			/*
			 * Alte Gebäude des gleichen Typs vergrößern
			 */
			BuildingAbstract oldBuilding = (BuildingAbstract) oldDesign.getResearchableResource();
			oldBuilding.setSize(oldBuilding.getSize().doubleValue() + building.getResearchableResource().getSize().doubleValue());
		} else
		{
			getBuildingsData().put(building.getID(), building.getObjectData());
		}
	}

	/**
	 * Ändert Göße der angegebenen Gebäude um die angegebene Anszahl der Stufen
	 * 
	 * @param building
	 * @param delta
	 */
	public void resizeBuilding(ResearchableDesign<BuildingAbstract> building, int delta)
	{
		int size = building.getResearchableResource().getSize().intValue() + delta;
		if(size <= 0)
			removeBuilding(building);
		else
		{
			building.getResearchableResource().setSize(size);
		}
	}

	public Collection<ResearchableDesign<BuildingAbstract>> getBuildings()
	{
		ArrayList<ResearchableDesign<BuildingAbstract>> ret = new ArrayList<ResearchableDesign<BuildingAbstract>>();
		for(Map data : getBuildingsData().values())
			ret.add(new ResearchableDesign<BuildingAbstract>(data));
		return ret;
	}

	public ResearchableDesign<BuildingAbstract> getBuilding(ID id)
	{
		Map data = getBuildingsData().get(id);
		if(data == null) return null;
		return new ResearchableDesign<BuildingAbstract>(data);
	}
	
	/**
	 * Liefert alle Gebäude mit der Design-ID id 
	 * 
	 * @param id Design-ID der geforderten Gebäude
	 * @return Liste der Gebäude vom Typ Design-ID
	 */
	public Collection<ResearchableDesign<BuildingAbstract>> getBuildingsFromType(ID id)
	{
		ArrayList<ResearchableDesign<BuildingAbstract>> ret = new ArrayList<ResearchableDesign<BuildingAbstract>>();
		for(ResearchableDesign<BuildingAbstract> building : getBuildings())
		{
			if(building.getResearchableResource().getID() == id)
				ret.add(building);		
		}

		return ret;
	}

	public void removeBuilding(ID id)
	{
		getBuilding(id).getResearchableResource().cleanUp();
		getBuildingsData().remove(id);
		/* Völlig leere Kolonien werden automatisch gelöscht */
		if((getBuildingsData().size() <= 0) && (getPopulation().intValue() < 1))
			Main.instance().getMOUDB().getKolonieDB().removeKolonie(getID());
	}

	public void removeBuilding(ResearchableDesign<BuildingAbstract> building)
	{
		removeBuilding(building.getID());
	}

	protected Map<ID, Map> getBuildingsData()
	{
		Map<ID, Map> data = (Map<ID, Map>) getAttribute(ATTR_BUILDINGS);
		if(data == null)
		{
			data = new HashMap<ID, Map>();
			setAttribute(ATTR_BUILDINGS, data, false);
		}
		return data;
	}

	public double getLivingSpace()
	{
		return ((Number) getAttribute(ATTR_HABITAT_CAPACITY, ZERO_DOUBLE)).doubleValue();
	}

	public void setLivingSpace(double val)
	{
		setAttribute(ATTR_HABITAT_CAPACITY, val, false);
	}

	/**
	 * @return Wert positiv oder negativ
	 */
	public double getMoral()
	{
		return ((Number) getAttribute(ATTR_MORAL, ZERO_DOUBLE)).doubleValue();
	}

	public void setMoral(double val)
	{
		setAttribute(ATTR_MORAL, val, false);
	}

	/**
	 * Gibt Anzahl der Arbeitslosen
	 * 
	 * @return
	 */
	public double getUnemployed()
	{
		return ((Number) getAttribute(ATTR_UNEMPLOYMENT, ZERO_DOUBLE)).doubleValue();
	}

	/**
	 * Setzt die Anzahl der Arbeitslosen
	 * 
	 * @param val
	 */
	public void setUnemployed(double val)
	{
		setAttribute(ATTR_UNEMPLOYMENT, val, false);
	}

	public double getSupportCost()
	{
		return ((Number) getAttribute(ATTR_SUPPORT_COST, ZERO_DOUBLE)).doubleValue();
	}

	public void setSupportCost(double val)
	{
		setAttribute(ATTR_SUPPORT_COST, val, false);
	}

	/**
	 * Ressourcenabbau
	 * 
	 * @return
	 */
	public double getMiningPoints()
	{
		return ((Number) getAttribute(ATTR_MINING, ZERO_DOUBLE)).doubleValue();
	}

	/**
	 * Ressourcenabbau
	 * 
	 * @param diggerPoints
	 */
	private void setMiningPoints(double diggerPoints)
	{
		setAttribute(ATTR_MINING, diggerPoints, false);
	}

	public void setPopulation(double pop)
	{
		/* Leere Kolonie ohne Gebäude löschen */
		if((pop < 1)&&(getBuildingsData().size() <= 0))
			Main.instance().getMOUDB().getKolonieDB().removeKolonie(getID());

		if(getStarSystem().getMaxPopulation() < pop)
		{
			pop = getStarSystem().getMaxPopulation();
		}
		setAttribute(ATTR_POPULATION, pop, true);
	}

	private void setStarPosition(Point pos)
	{
		setAttribute(ATTR_STAR_POS, pos, false);
	}

	private void setCivID(ID id)
	{
		setAttribute(ATTR_CIV_ID, id, true);
	}

	public boolean isMyKolonie()
	{
		return getCivID().equals(Main.instance().getMOUDB().getCivilizationDB().getMyCivilization().getID());
	}

	public ID getCivID()
	{
		return (ID) getAttribute(ATTR_CIV_ID);
	}

	// public Civilization getCivilization()
	// {
	// return
	// }
	public String getCivName()
	{
		Civilization civ = getMOUDB().getCivilizationDB().getCivilization(getCivID());
		if(civ == null) return "Unbekannt";
		return civ.getName();
	}

	public Number getPopulation()
	{
		return ((Number) getAttribute(ATTR_POPULATION));
	}

	public Point getPosition()
	{
		return (Point) getAttribute(ATTR_STAR_POS);
	}

	public long getMaxBevoelkerung()
	{
		return getStarSystem().getMaxPopulation();
	}

	private void resetColonyPoints()
	{
		synchronized(getLockObject())
		{
			setBruttoIncome(0);
			setProduction(0);
			setMiningPoints(0);
			setDayPopulationGrowRate(0);
			setSciencePoints(0);
			setLivingSpace(0);
			setMoral(0);
			setSupportCost(0);
			setUnemployed(getPopulation().doubleValue());
			setFarming(0);
			bruttoProduction = 0;
			bruttoMining = 0;
			bruttoFarming = 0;
			bruttoResearch = 0;
		}
	}

	/**
	 * Liefert den Bruttoeinkommen der Kolonie
	 * 
	 * @return
	 */
	public double getBruttoIncome()
	{
		return ((Number) getAttribute(ATTR_INCOME_BRUTTO, ZERO_DOUBLE)).doubleValue();
	}

	/**
	 * @param moneyPoints
	 */
	public void setBruttoIncome(double moneyPoints)
	{
		setAttribute(ATTR_INCOME_BRUTTO, moneyPoints, false);
	}

	/**
	 * Liefert den Nettoeinkommen der Kolonie abzüglich allen Kosten
	 * 
	 * @return
	 */
	public double getIncomeBalance()
	{
		return getBruttoIncome() - getSupportCost();
	}

	public String toString()
	{
		String ret = getName();
		if(isRebelled()) ret += " (Rebelliert)";
		return ret;
	}

	public String getName()
	{
		return getMOUDB().getStarmapDB().getStarSystemAt(getPosition()).toString();
	}

	/**
	 * @return Wert zwischen -1 und 1
	 */
	public double getDayPopulationGrowRate()
	{
		return ((Number) getAttribute(ATTR_POPULATION_GROW, ZERO_DOUBLE)).doubleValue();
	}

	/**
	 * @return Positive oder negative prozentuelle Wert
	 */
	public double getDayPopulationGrowPercent()
	{
		return 100 * getDayPopulationGrowRate();
	}

	/**
	 * @param bevoelkerungWachstum
	 */
	public void setDayPopulationGrowRate(double bevoelkerungWachstum)
	{
		setAttribute(ATTR_POPULATION_GROW, bevoelkerungWachstum, false);
	}

	public void fireColonyChangedEvent()
	{
		if(getDB() == null)return;
		((ColonyDB) getDB()).fireColonyChangedEvent(this);
	}

	public BuildQueue getBuildQueue()
	{
		LinkedHashMap<ID, Map> data = (LinkedHashMap<ID, Map>) getAttribute(ATTR_BUILD_QUEUE);
		if(data == null)
		{
			data = new LinkedHashMap<ID, Map>();
			setAttribute(ATTR_BUILD_QUEUE, data, false);
		}
		return new BuildQueue(data, this);
	}

	public StarSystem getStarSystem()
	{
		if(star == null)
		{
			star = Main.instance().getMOUDB().getStarmapDB().getStarSystemAt(getPosition());
		}
		return star;
	}
	
	public int getNaturalResourcesSize()
	{
		return getStarSystem().getNatRessources().size();
	}

	public boolean isRebelled()
	{
		// return true;
		return getAttribute(ATTR_IS_REBELLED) != null;
	}

	/**
	 * @param val
	 */
	private void setRebelled(boolean val)
	{
		if(val)
			setAttribute(ATTR_IS_REBELLED, val, true);
		else
			setAttribute(ATTR_IS_REBELLED, null, true);
	}

	/**
	 * Berechnet aus lokalen und globale Einflüssfaktore die Wahrscheinlichkeit einer Rebellion in
	 * der Kolonie. Es wird ein Wert > 0 zurückgegeben. Dies stellt eine Wahrscheinlichkeit einer
	 * Rebellion im galaktischen Jahr (1000 Tage) wobei 1 für 100% Prozent steht
	 * 
	 * @return
	 */
	public double computeRebelChance()
	{
		double ret = getMoral();
		if(ret > 0) return 0;
		return -ret;
	}

	public void beginRebelion()
	{
		if(isRebelled()) return;
		setRebelled(true);
		/*
		 * Verteidigungsschiffe generieren
		 */
		ShipGenerator gen = new ShipGenerator(getMOUDB().getResearchDB());
		double masse = getPopulation().doubleValue() * 0.1;
		Collection<Ship> ships = gen.generateShips(masse);
		ShipDB shipDB = getMOUDB().getRebelShipDB();
		for(Ship ship : ships)
		{
			shipDB.addNewShip(ship, getPosition());
		}
		/*
		 * Infodialog zuletzt anzeigen, damit der Infodialog den Prozessfluss nicht unterbricht, und
		 * Raumschlacht früehr anfängt als die rebellische Schiffe generiert werden
		 */
		Main.instance().getGUI().promtMessage("Rebellion", "Kolonie " + getName() + " hat eigene Unabhängigkeit erklärt", GUI.MSG_PRIORITY_POPUP,
				new Runnable()
				{

					public void run()
					{
						Main.instance().getGUI().centreStarmaponPosition(getPosition());
					}
				});
	}

	private void doRebelDailyWork()
	{
		/*
		 * Prüfen ob feindliche Schiffe im Orbit stationiert sind. Wenn ja, dann Weltraumschlacht
		 * beginnen
		 */
		List<Ship> govermentForces = getMOUDB().getShipDB().getShipsInStarsystem(getPosition());
		if(govermentForces.size() > 0)
		{
			computeBattle(govermentForces, getMOUDB().getRebelShipDB().getShipsInStarsystem(getPosition()));
			if(!isRebelled()) return;// Kolonie gefallen
		}
	}

	private void computeBattle(Collection<Ship> goodShips, Collection<Ship> evilShips)
	{
		List<ShipInfo> goverment = new ArrayList<ShipInfo>(goodShips.size());
		List<ShipInfo> rebel = new ArrayList<ShipInfo>(goodShips.size());
		for(Ship ship : goodShips)
			goverment.add(new ShipInfo(ship));
		for(Ship ship : evilShips)
			rebel.add(new ShipInfo(ship));
		SpaceBattle battle = new SpaceBattle(rebel, goverment, System.currentTimeMillis());
		battle.battle();
		List<ShipInfo> destroyedGov = battle.getDestroyedDefender();
		List<ShipInfo> destroyedRebel = battle.getDestroyedInvader();
		List<ShipInfo> damagedGov = battle.getDamagedDefender();
		List<ShipInfo> damagedRebel = battle.getDamagedInvader();
		SpaceBattleResult result = new SpaceBattleResult(destroyedRebel.size(), damagedRebel.size(), destroyedGov.size(), damagedGov.size(),
				(rebel.size() <= destroyedRebel.size()), getPopulation().intValue());
		Main.instance().getGUI().promtBattleResult(getPosition(), result, CivilizationDB.REBEL_ID);
		if(rebel.size() <= destroyedRebel.size()) setRebelled(false);
		/*
		 * Zerstörte Shiffe löschen und beschädigte Schiffe aktualisieren
		 */
		ShipDB shipDB = getMOUDB().getRebelShipDB();
		for(ShipInfo info : destroyedRebel)
			shipDB.deleteShip(info.getId());
		for(ShipInfo info : damagedRebel)
		{
			Ship ship = shipDB.getShip(info.getId());
			if(ship != null) ship.setStruktur(new Double(info.getStruktur()));
		}
		shipDB = getMOUDB().getShipDB();
		for(ShipInfo info : destroyedGov)
			shipDB.deleteShip(info.getId());
		for(ShipInfo info : damagedGov)
		{
			Ship ship = shipDB.getShip(info.getId());
			if(ship != null) ship.setStruktur(new Double(info.getStruktur()));
		}
	}

	/**
	 * @return ein Wert zwischen 0 un 1;
	 */
	public double computeUnemploymentFactor()
	{
		return getUnemployed() / (getPopulation().doubleValue() + 1);
	}

	public void doDailyWorkPhase1(CivDayReport dayValues)
	{
		double pop = getPopulation().doubleValue();
		
		/*
		 * Kolonieleistungsdaten berechnen
		 */
		resetColonyPoints();
		if(isRebelled())
		{
			setFoodBalance(-pop);
			dayValues.setFoodBalance(dayValues.getFoodBalance() - pop);

			doRebelDailyWork();
			return;
		}
		/*
		 * Alle Gebäude durchgehen und durch sie die Kolonieleistungsdaten verändern
		 */
		for(ResearchableDesign<BuildingAbstract> building : getBuildings())
			building.getResearchableResource().computeColonyPoints(this);
		/*
		 * Durch Gebäude erzeugte Werte mit dem Arbeitstag koppeln
		 */
		setProduction(bruttoProduction * dayValues.getWorkFaktor());
		setMiningPoints(bruttoMining * dayValues.getWorkFaktor());
		setFarming(bruttoFarming * dayValues.getWorkFaktor());
		setSciencePoints(bruttoResearch * dayValues.getWorkFaktor());
		/*
		 * Durch Gebäude erzeugte Kolonieleistungsdaten mit Standortfaktoren des Sternensystem
		 * koppeln
		 */
		StarSystem ss = getStarSystem();
		setProduction(getProduction() * ss.getProductionFaktor());
		setFarming(getFarming() * ss.getFarmingFaktor());
		setMiningPoints(getMiningPoints() * ss.getMiningFaktor());
		setSciencePoints(getSciencePoints() * ss.getScienceFaktor());

		// ### Natürlichen Ressourcen abbauen ###
		if(getMiningPoints() >= computeTotalFreeMaterialStorageSpace())
		{
			long mining = 0;
			/* Nicht mehr genug Lagerplatz, alle Lager komplett füllen */
			for(Iterator iter = getStarSystem().getNatRessources().iterator(); iter.hasNext();)
			{
				NaturalResource res = (NaturalResource) iter.next();
				mining += addMaterialToStorage(res.getID(), computeFreeMaterialStorageSpace(res.getID()));
			}
			
			/* MiningPoints sind lediglich von der größe mining */
			setMiningPoints(mining);
		}
		else
		{
			for(Iterator iter = getStarSystem().getNatRessources().iterator(); iter.hasNext();)
			{
				NaturalResource res = (NaturalResource) iter.next();
				double menge = (getMiningPoints() * computeFreeMaterialStorageSpace(res.getID())/computeTotalFreeMaterialStorageSpace());
				addMaterialToStorage(res.getID(), (long) menge);
			}
			
		}

		getBuildQueue().investProduction(getProduction());
		// dayValues
		// .setBruttoIncome(dayValues.getBruttoIncome()
		// + getBruttoIncome()
		// + ((getProduction() + getMiningPoints() + (getFarming() / 1000) + getSciencePoints())
		// * dayValues.getTaxRate()));
		dayValues.setColonySupportcost(dayValues.getSupportCostBuildings() + getSupportCost());
		dayValues.setPopulation(dayValues.getPopulation() + getPopulation().longValue());
		dayValues.setProduction(dayValues.getProduction() + getProduction());
		dayValues.setMining(dayValues.getMining() + getMiningPoints());
		dayValues.setSciencePoints(dayValues.getSciencePoints() + getSciencePoints());

		/*
		 * Lokale Lebensmittelversorgung berechnen
		 */
		double foodBalance = (getFarming()-pop);
		if(foodBalance >= 0)
		{
			/* Überschüsse global anbieten */
			dayValues.setFood(dayValues.getFood() + foodBalance);
		}
		
		setFoodBalance(foodBalance);
		dayValues.setFoodBalance(dayValues.getFoodBalance() + foodBalance);
		dayValues.setFarming(dayValues.getFarming() + getFarming());
	}

	public void doDailyWorkPhase2(CivDayReport dayValues)
	{
		double foodBalance = getFoodBalance();
		
		/* Bevölkerung bereits lokal versorgt */
		if(foodBalance>=0)
		{
			if(dayValues.getFoodBalance() >0)
			{
				/* Nahrungsüberschuß wird global nicht gebraucht, also ins Lager damit*/
				double stored = Math.min(foodBalance, dayValues.getFoodBalance());
				stored = addFoodToStorage(stored);
				setFoodBalance(getFoodBalance() - stored);
				dayValues.setFood(dayValues.getFood() - stored);
				dayValues.setFoodBalance(dayValues.getFoodBalance() - stored);
			}
		}
		/* Nahrungsimport erforderlich */
		else
		{
			/* Global genug Nahrung für alle Planeten da */
			if(dayValues.getFoodBalance() >=0)
			{
				dayValues.setFood(dayValues.getFood() + foodBalance);
				setFoodBalance(0);
			}
			/* Nicht genug Nahrung, irgend jemand muß hungern */
			else
			{
				/* Zuerst global Nahrung anfordern, fals noch vorhandern */
				double consumed = Math.min(-foodBalance, dayValues.getFood());
				double foodFromSilo = 0;

				foodBalance -= consumed;
				
				/* Fals immer noch Bedarf, aus Silos holen*/
				if(foodBalance < 0)
				{
					/* foodFromSilo is < 0 */
					foodFromSilo = addFoodToStorage(foodBalance);
					foodBalance -= foodFromSilo;
				}
				
				setFoodBalance(foodBalance);
				dayValues.setFood(dayValues.getFood() - consumed);
				
			}
		}
		
		
		generateDailyMoralFactors();
		setMoral(computeMoralFactor());
		generateDailyGrowFactors();
		setDayPopulationGrowRate(computePopulationGrowRate());
		addGrowDayValue(getDayPopulationGrowRate());
		double rebelChance = computeRebelChance();
		if(rebelChance > (Math.random() * 1000))
		{
			beginRebelion();
		}
	}

	private void addGrowDayValue(double val)
	{
		Number count = (Number) getAttributLazy(ATTR_GROW_DAY_VALUES_COUNT, Main.ZERO_NUMBER);
		Number sum = (Number) getAttribute(ATTR_GROW_DAY_VALUES_SUM, Main.ZERO_NUMBER);
		count = new Integer(count.intValue() + 1);
		sum = new Double(sum.doubleValue() + val);
		setAttribute(ATTR_GROW_DAY_VALUES_COUNT, count, false);
		setAttribute(ATTR_GROW_DAY_VALUES_SUM, sum, false);
	}

	/*
	 * Methode berechnet den mittleren Jahreswert und resettet die Werte fuer naechstes Jahr.
	 */
	private double computeAverageGrowRate()
	{
		/*
		 * Mittleren Wachstumswert aus im Laufe des Jahres gespeicherten Tageswerten ausrechnen
		 */
		int count = ((Number) getAttributLazy(ATTR_GROW_DAY_VALUES_COUNT, Main.ZERO_NUMBER)).intValue();
		double sum = ((Number) getAttribute(ATTR_GROW_DAY_VALUES_SUM, Main.ZERO_NUMBER)).doubleValue();
		if(count < 1) count = 1;
		/*
		 * Werte auf 0 setzen
		 */
		setAttribute(ATTR_GROW_DAY_VALUES_COUNT, Main.ZERO_NUMBER, false);
		setAttribute(ATTR_GROW_DAY_VALUES_SUM, Main.ZERO_NUMBER, false);
		return sum / (double) count;
	}

	/**
	 * Methode generiert aus lokalen Begebenheiten Moralfaktoren für diese Kolonie.
	 */
	private void generateDailyMoralFactors()
	{
		// if(isRebelled())return;
		Collection<InfluenceFactor> localFactors = getLocalMoralFactors();
		/*
		 * Alte Faktoren altern
		 */
		for(InfluenceFactor f : localFactors)
			f.setDuration(f.getDuration().longValue() - 1);
		/*
		 * Faktor aus Arbeitslosigkeit generieren
		 */
		double val = computeUnemploymentFactor();
		if(val > 0) addMoralFactor(new InfluenceFactor(1, -val, "Arbeitslosigkeit"));
		/*
		 * Faktor aus Nahrungsversorung generieren
		 */
		val = getFoodSupplyFactor();
		if(val < 1) addMoralFactor(new InfluenceFactor(1, val - 1, "Nahrungsmangel"));
	}

	/**
	 * @return Ein Wert < 0 >
	 */
	private double computeMoralFactor()
	{
		double ret = 0;
		for(InfluenceFactor f : getAllMoralFactors())
			ret += f.getValue().doubleValue();
		return ret;
	}

	/**
	 * Methode generiert lokale Faktoren für den Bevölkerungswachstum
	 */
	private void generateDailyGrowFactors()
	{
		double pop = getPopulation().doubleValue();
		Collection<InfluenceFactor> localFactors = getGrowFactors();

		/* Wachstum nur, wenn Bevölkerung vorhanden */
		if((pop <= 0)||(Double.isNaN(pop)))	return;
		/*
		 * Alte Faktoren altern
		 */
		for(InfluenceFactor f : localFactors)
			f.setDuration(f.getDuration().longValue() - 1);
		if(isRebelled())
		{
			addPopulationGrowFactor(new InfluenceFactor(1, REBEL_POPULATION_GROW, "Rebellion"));
		} else
		{
			double foodSupply = getFoodSupplyFactor();
			/*
			 * Faktor aus Nahrungsversorgung
			 */
			if(foodSupply < 1)
			{
				addPopulationGrowFactor(new InfluenceFactor(1, (foodSupply - 1), "Nahrungsmangel"));
			} else
			{
				addPopulationGrowFactor(new InfluenceFactor(1, BASE_POPULATION_GROW, "Basisrate"));
				addPopulationGrowFactor(new InfluenceFactor(1, getStarSystem().getPopulationGrowBonus(), "Planetbonus"));
				addPopulationGrowFactor(new InfluenceFactor(1, getMoral() / 10, "Zufriedenheit"));
				/*
				 * Faktor aus Wohnungssituation generieren
				 */
				double livingSpace = getLivingSpace();
				double val = ((livingSpace / pop) - 1) / 10;// Maximal -10%
				if(val < 0)
					addPopulationGrowFactor(new InfluenceFactor(1, val, "Wohnraummangel"));
				else
				{
					if(val > 0.01) val = 0.01;// Maximalen Wert nach oben begrenzen
					addPopulationGrowFactor(new InfluenceFactor(1, val, "Wohnraum"));
				}
			}
		}
	}

	public void doYearlyWork(CivYearReport yearValues)
	{
		// double sum = 0;
		double pop = getPopulation().doubleValue();
		pop = pop + pop * computeAverageGrowRate();
		setPopulation(pop);
	}

	private double computePopulationGrowRate()
	{
		double growRate = 0;
		for(InfluenceFactor f : getGrowFactors())
			growRate += f.getValue().doubleValue();
		return growRate;
	}

	public double getSciencePoints()
	{
		return ((Number) getAttribute(ATTR_SCIENCE, ZERO_DOUBLE)).doubleValue();
	}

	private void setSciencePoints(double sciencePoints)
	{
		setAttribute(ATTR_SCIENCE, sciencePoints, false);
	}

	public double getProduction()
	{
		return ((Number) getAttribute(ATTR_PRODUCTION, ZERO_DOUBLE)).doubleValue();
	}

	private void setProduction(double value)
	{
		setAttribute(ATTR_PRODUCTION, value, false);
	}

	public double getFarming()
	{
		return ((Number) getAttribute(ATTR_FARMING, ZERO_DOUBLE)).doubleValue();
	}

	private void setFarming(double value)
	{
		setAttribute(ATTR_FARMING, value, false);
	}

	/**
	 * Mitteilt zu welchen Teil diese kolonie mit Lebenesmittel versorgt ist
	 * 
	 * @return ein Wert zwischen 0 und 1
	 */
	public double getFoodSupplyFactor()
	{
		double pop = getPopulation().doubleValue();
		double foodBalance = getFoodBalance();
		
		/* In this case foodBalance should be 0, but return 1, just to be sure */
		if(pop <= 0) return 1;
		
		if(foodBalance >= 0) return 1;
		return (pop + foodBalance)/pop;
	}

	public double getFoodBalance()
	{
		return ((Number) getAttribute(ATTR_FOOD_BALANCE, ZERO_DOUBLE)).doubleValue();
	}
	
	public void setFoodBalance(double value)
	{
		setAttribute(ATTR_FOOD_BALANCE, value, false);		
	}
	
	/**
	 * Adds food to the planetary Silos
	 *  
	 * @param amount: Amount of food to put in the Silos
	 * @return amount of food actually added to the Silos
	 */
	public double addFoodToStorage(double amount)
	{
		double ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_SILO))
		{
			ret += ((Silo) building.getResearchableResource()).addFood(amount);
			amount -= ret;
			if (amount == 0)
				break;
		}
		
		return ret;
	}
	
	/**
	 * @return amount of food totally stored in this colony
	 */
	public double computeFoodInStorage()
	{
		double ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_SILO))
			ret += ((Silo) building.getResearchableResource()).getFood().doubleValue();
		
		return ret;
	}
	
	public double computeFreeFoodStorageSpace()
	{
		double ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_SILO))
			ret += ((Silo) building.getResearchableResource()).computeFreeSpace();
		
		return ret;
	}
	
	/* Wie langer reichen die Vorräte im Lager, wenn keine NAhrung mehr produziert wird */
	public double computeFoodStorageTime()
	{
		return computeFoodInStorage()/getPopulation().doubleValue();
	}

	/**
	 * Adds Material to the planetary Storage
	 *  
	 * @param amount: Amount of Material to put in the Silos
	 * @return amount of Material actually added to the Silos
	 */
	public long addMaterialToStorage(ID resource, long amount)
	{
		long ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
		{
			ret += ((MaterialStorage) building.getResearchableResource()).addMaterial(resource, amount);
			amount -= ret;
			if (amount == 0)
				return ret;
		}
		
		ResearchableDesign center = getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER);
		if(center != null)
			ret += ((ColonyCenter) center.getResearchableResource()).getMaterialStorage().addMaterial(resource, amount);
		
		return ret;
	}
	
	/**
	 * @return amount of Material totally stored in this colony
	 */
	public long computeMaterialInStorage(ID resource)
	{
		long ret = 0;
		
		ResearchableDesign<BuildingAbstract> center = getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER);
		if(center != null)
			ret += ((ColonyCenter) center.getResearchableResource()).getMaterialStorage().getMaterial(resource).doubleValue();

		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
			ret += ((MaterialStorage) building.getResearchableResource()).getMaterial(resource).doubleValue();
		
		return ret;
	}
	
	public long computeTotalMaterialInStorage()
	{
		long ret = 0;
		
		ResearchableDesign<BuildingAbstract> center = getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER);
		if(center != null)
			ret += ((ColonyCenter) center.getResearchableResource()).getMaterialStorage().computeTotalStored();

		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
			ret += ((MaterialStorage) building.getResearchableResource()).computeTotalStored();
		
		return ret;

	}
	
	public long computeFreeMaterialStorageSpace(ID id)
	{
		long ret = 0;

		ResearchableDesign<BuildingAbstract> center = getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER);
		if(center != null)
			ret += ((ColonyCenter) center.getResearchableResource()).getMaterialStorage().computeFreeSpace(id);

		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
			ret += ((MaterialStorage) building.getResearchableResource()).computeFreeSpace(id);
		
		return ret;
	}
	
	public long computeMaterialStorageSpace(ID id)
	{
		long ret = 0;

		ResearchableDesign<BuildingAbstract> center = getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER);
		if(center != null)
			ret += ((ColonyCenter) center.getResearchableResource()).getMaterialStorage().computeCapacity(id);

		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
			ret += ((MaterialStorage) building.getResearchableResource()).computeCapacity(id);
		
		return ret;

	}
	
	public long computeTotalFreeMaterialStorageSpace()
	{
		long ret = 0;
		
		ResearchableDesign center = getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER);
		if(center != null)
			ret += ((ColonyCenter) center.getResearchableResource()).getMaterialStorage().computeTotalFreeSpace();
		
		for(ResearchableDesign<BuildingAbstract> building : getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
			ret += ((MaterialStorage) building.getResearchableResource()).computeTotalFreeSpace();
		
		return ret;
		
	}
	
	/* Info über alle gelagerten Ressourcen als HTMLTable */
	public String getMaterialStoredHTMLInfo()
	{
		StringBuilder ret = new StringBuilder("<html><table>");
		
		for(Iterator iter = getStarSystem().getNatRessources().iterator(); iter.hasNext();)
		{
			NaturalResource res = (NaturalResource) iter.next();

			ret.append("<tr><td><b>");
			ret.append(res.getName());
			ret.append(": </b></td><td>");
			ret.append(GUI.formatSmartDouble(computeMaterialInStorage(res.getID())/1E6));
			ret.append("/");
			ret.append(GUI.formatSmartDouble(computeMaterialStorageSpace(res.getID())/1E6));
			ret.append(" mio T");
			ret.append("</td></tr>");
		}
		ret.append("</table></html>");

		return ret.toString();
	}
	
	public void addBruttoFarming(double bruttoFarming1)
	{
		this.bruttoFarming += bruttoFarming1;
	}

	public void addBruttoMining(double bruttoMining1)
	{
		this.bruttoMining += bruttoMining1;
	}

	public void addBruttoProduction(double bruttoProduction1)
	{
		this.bruttoProduction += bruttoProduction1;
	}

	public void addBruttoResearch(double bruttoResearch1)
	{
		this.bruttoResearch += bruttoResearch1;
	}

	public void addMoralFactor(InfluenceFactor factor)
	{
		getRawLocalMoralFactors().add(factor.getObjectData());
	}

	/**
	 * Liefert eine fusionierte Liste aus lokalen und globalen Moralfaktoren
	 * 
	 * @return
	 */
	public Collection<InfluenceFactor> getAllMoralFactors()
	{
		List<InfluenceFactor> ret = new ArrayList<InfluenceFactor>();
		ret.addAll(Main.instance().getMOUDB().getCivilizationDB().getMoralFactors());
		ret.addAll(getLocalMoralFactors());
		return ret;
	}

	/**
	 * Instanziiert InfluenceFactor Objekte mit Map Daten, und entfernt bei der Gelegenheit
	 * abgelaufen Faktoren
	 * 
	 * @return
	 */
	public Collection<InfluenceFactor> getLocalMoralFactors()
	{
		Collection<InfluenceFactor> ret = new ArrayList<InfluenceFactor>();
		for(Iterator<Map> iter = getRawLocalMoralFactors().iterator(); iter.hasNext();)
		{
			InfluenceFactor f = new InfluenceFactor(iter.next());
			if(f.getDuration().longValue() > 0)
				ret.add(f);
			else
				iter.remove();
		}
		return (ret);
	}

	@SuppressWarnings("unchecked")
	private Collection<Map> getRawLocalMoralFactors()
	{
		Collection<Map> ret = (Collection) getAttribute(ATTR_MORAL_FACTORS);
		if(ret == null)
		{
			ret = new ArrayList<Map>();
			setAttribute(ATTR_MORAL_FACTORS, ret, false);
		}
		return ret;
	}

	public void addPopulationGrowFactor(InfluenceFactor factor)
	{
		getRawPopulationGrowFactors().add(factor.getObjectData());
	}

	/**
	 * Instanziiert InfluenceFactor Objekte mit Map Daten, und entfernt bei der Gelegenheit
	 * abgelaufen Faktoren
	 * 
	 * @return
	 */
	public Collection<InfluenceFactor> getGrowFactors()
	{
		Collection<InfluenceFactor> ret = new ArrayList<InfluenceFactor>();
		for(Iterator<Map> iter = getRawPopulationGrowFactors().iterator(); iter.hasNext();)
		{
			InfluenceFactor f = new InfluenceFactor(iter.next());
			if(f.getDuration().longValue() > 0)
				ret.add(f);
			else
				iter.remove();
		}
		return (ret);
	}

	@SuppressWarnings("unchecked")
	private Collection<Map> getRawPopulationGrowFactors()
	{
		Collection<Map> ret = (Collection) getAttribute(ATTR_GROW_FACTORS);
		if(ret == null)
		{
			ret = new ArrayList<Map>();
			setAttribute(ATTR_GROW_FACTORS, ret, false);
		}
		return ret;
	}
}