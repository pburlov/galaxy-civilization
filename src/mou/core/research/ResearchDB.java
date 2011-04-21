/*
 * $Id: ResearchDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import mou.Main;
import mou.core.MapWrapper;
import mou.core.civilization.CivDayReport;
import mou.core.civilization.CivYearReport;
import mou.core.civilization.CivilizationDB;
import mou.core.civilization.CivilizationMember;
import mou.core.res.ResearchableResource;
import mou.core.res.ResourceAbstract;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.ColonyCenter.ColonyCenter;
import mou.core.res.colony.ColonyCenter.ColonyCenterMaterialStorage;
import mou.core.res.colony.Farm.Farm;
import mou.core.res.colony.Habitat.Habitat;
import mou.core.res.colony.MaterialStorage.MaterialStorage;
import mou.core.res.colony.ResearchCenter.ResearchCenter;
import mou.core.res.colony.Silo.Silo;
import mou.core.res.colony.factory.Factory;
import mou.core.res.colony.mine.Mine;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.res.shipsystem.armor.Armor;
import mou.core.res.shipsystem.colonymodul.ColonyModul;
import mou.core.res.shipsystem.energy.EnergyGenerator;
import mou.core.res.shipsystem.engine.Hyperdrive;
import mou.core.res.shipsystem.lifesupport.LifeSupportSystem;
import mou.core.res.shipsystem.shild.ShipShild;
import mou.core.res.shipsystem.weapon.Weapon;
import mou.gui.GUI;
import mou.gui.MainFrame;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class ResearchDB extends MapWrapper
		implements CivilizationMember
{

	// Anzahl der vorläufigen Forschungsergebnissen
	static final private int RESEARCH_RESULTS_SIZE = 20;
	static final private String RESEARCH_RUNNING = "RESEARCH_RUNNING";
	static final private String RESEARCH_MATERIALS = "RESEARCH_MATERIALS";
	static final private String RESEARCHABLE_ID = "RESEARCHABLE_ID";
	// static final private String NEEDED_RESEARCH_POINTS = "NEEDED_RESEARCH_POINTS";
	static final private String INVESTED_RESEARCH_POINTS = "INVESTED_RESEARCH_POINTS";
	static final private String RESEARCH_RESULTS = "RESEARCH_RESULTS";
	static final private String SAVED_DESIGNS = "SAVED_DESIGNS";
	static final private Map<ID, Class> mapResearchableClasses = new HashMap<ID, Class>();
	static
	{
		// ######## Mögliche Schiffsysteme initialisieren #########
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_ENERGYGENERATOR, EnergyGenerator.class);
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_HYPERDRIVE, Hyperdrive.class);
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_WEAPON, Weapon.class);
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_ARMOR, Armor.class);
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_SHILD, ShipShild.class);
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_LIFE_SUPPORT, LifeSupportSystem.class);
		mapResearchableClasses.put(ResourceAbstract.ID_SHIPSYSTEM_COLONYMODUL, ColonyModul.class);

		// ######## Mögliche Gebäude initialisieren #########		
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_COLONY_CENTER, ColonyCenter.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_COLONY_CENTER_MATERIAL_STORAGE, ColonyCenterMaterialStorage.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_HARVESTER, Mine.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_FACTORY, Factory.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_RESEARCH_CENTER, ResearchCenter.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_HABITAT, Habitat.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_FARM, Farm.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_SILO, Silo.class);
		mapResearchableClasses.put(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE, MaterialStorage.class);
	}
	private List<ResearchDBLstener> researchListeners = new ArrayList<ResearchDBLstener>();

	/**
	 * @param data
	 */
	public ResearchDB(Map data)
	{
		super(data);
		Main.instance().getMOUDB().getCivilizationDB().registerCivilizationMember(this);
		// Main.instance().getClockGenerator().addClockListener(new ClockListener()
		// {
		//		
		// public void yearlyEvent(ClockEvent event)
		// {
		// MOUDB moudb = Main.instance().getMOUDB();
		// if(moudb.getCivilizationDB() == null
		// || moudb.getCivilizationDB().getCivDayValues() == null) return;
		// investResearchPoints(moudb.getCivilizationDB().getCivDayValues().getSciencePoints());
		// }
		//		
		// public void dailyEvent(ClockEvent event)
		// {
		// }
		// });
	}

	public void addResearchDBListener(ResearchDBLstener listener)
	{
		researchListeners.add(listener);
	}

	protected void fireResearchResultAddedEvent(ResearchableDesign<? extends ResearchableResource> des)
	{
		for(ResearchDBLstener listener : researchListeners)
			listener.researchResultAdded(des);
	}

	protected void fireResearchResultRemovedEvent(ResearchableDesign<? extends ResearchableResource> des)
	{
		for(ResearchDBLstener listener : researchListeners)
			listener.researchResultRemoved(des);
	}

	public String getDBName()
	{
		return "ResearchDB";
	}

	synchronized public Double getInvestedResearchPoints()
	{
		Double ret = (Double) getAttribute(INVESTED_RESEARCH_POINTS);
		if(ret == null) ret = new Double(0);
		return ret;
	}

	synchronized protected void setInvestedResearchPoints(Double value)
	{
		setAttribute(INVESTED_RESEARCH_POINTS, value);
	}

	/**
	 * Wenn ein Forschungsauftrag gerade läuft, dann wird val zu den bereits investierten
	 * Forschungspunkten addiert.
	 * 
	 * @param val
	 */
	synchronized public void investResearchPoints(double val)
	{
		if(!isResearchRunning()) return;
		double invested = getInvestedResearchPoints().doubleValue();
		setInvestedResearchPoints(new Double(invested + val));
		if(getFortschritt() >= 1.0)
		{
			setResearchRunning(false);
			generateResearchResults();
			Main.instance().getGUI().promtMessage("Forschung abgeschlossen", "", GUI.MSG_PRIORITY_URGENT, new Runnable()
			{

				public void run()
				{
					Main.instance().getGUI().getMainFrame().selectScreen(MainFrame.SCREEN_SCIENCE);
				}
			});
		}
	}

	protected void generateResearchResults()
	{
		/*
		 * Seed generieren aus IDs der Materialien und ID des erforschten Systems
		 */
		long seed = getResearchMaterials().size();
		for(ID id : getResearchMaterials())
		{
			seed = seed ^ id.getVariablePart();
		}
		seed ^= getCurrentResearchableID().getVariablePart();
		/*
		 * Damit werden Forschungsergebnisse für alle Zivlisationen anders asufallen
		 */
		seed ^= getCurrentResearchableID().getConstantPart();
		Random rnd = new Random(seed);
		for(int i = 0; i < RESEARCH_RESULTS_SIZE; i++)
		{
			ResearchableResource res = getCurrentResearchObject();
			res.generateAttributes(rnd, getResearchMaterials());
			addNewResearchResult(res, res.getName() + "-" + res.getCustomCounter());
		}
	}

	/**
	 * Methode hinzügt neuen Forschungsrgebnisszu zu den vorläufigen Egebnissen
	 * 
	 * @param res
	 */
	protected void addNewResearchResult(ResearchableResource res, String name)
	{
		Collection<Map> maps = (Collection<Map>) getAttribute(RESEARCH_RESULTS);
		if(maps == null)
		{
			maps = new ArrayList<Map>();
			setAttribute(RESEARCH_RESULTS, maps);
		}
		maps.add(new ResearchableDesign<ResearchableResource>(res, name).getObjectData());
	}

	public double getFortschritt()
	{
		// if(!isResearchRunning())return 0.0;
		double invested = getInvestedResearchPoints().doubleValue();
		double needed = getNeededResearchPoints();
		if(invested >= needed || needed <= 0.0) return 1.0;
		return invested / needed;
	}

	public double getNeededResearchPoints()
	{
		//Jede ResearchableRessource kann eigene Forschungsdauer haben
		if(getCurrentResearchObject()!=null)
			return getCurrentResearchObject().getNeededResearchPoints(getResearchMaterials());
		return 0d;
	}

	// synchronized protected void setNeededResearchPoints(Double value)
	// {
	// setAttribute(NEEDED_RESEARCH_POINTS, value);
	// }
	//
	synchronized public Set<ID> getResearchMaterials()
	{
		Set<ID> ret = (Set<ID>) getAttribute(RESEARCH_MATERIALS);
		if(ret == null)
		{
			ret = new HashSet<ID>();
			setResearchMaterials(ret);
		}
		return ret;
	}

	synchronized protected void setResearchMaterials(Set<ID> materials)
	{
		setAttribute(RESEARCH_MATERIALS, materials);
	}

	synchronized public boolean isResearchRunning()
	{
		return getAttribute(RESEARCH_RUNNING) != null;
	}

	synchronized public void setResearchRunning(boolean running)
	{
		if(running)
			setAttribute(RESEARCH_RUNNING, "true");
		else
			removeAttribute(RESEARCH_RUNNING);
	}

	public void startNewResearch(ResearchableResource target)
	{
		setResearchRunning(false);
		setInvestedResearchPoints(0.0);
		// setNeededResearchPoints(Math.pow(10, getResearchMaterials().size()));
		setCurrentResearchableID(target.getID());
		// Vorherige forschungsergebnisse löschen
		setResearchedObjects(new ArrayList<ResearchableDesign>());
		setResearchRunning(true);
	}

	/**
	 * Liefert ID des gerade erfoschenden Objektes
	 * 
	 * @return
	 */
	synchronized public ID getCurrentResearchableID()
	{
		return (ID) getAttribute(RESEARCHABLE_ID);
	}

	synchronized public void setCurrentResearchableID(ID id)
	{
		if(!isResearchRunning())
			setAttribute(RESEARCHABLE_ID, id);
	}

	public ResearchableResource getCurrentResearchObject()
	{
		return getResearchableResource(getCurrentResearchableID());
	}

	/**
	 * Liefert sortierte Liste der noch nicht endgültig abgespeicherten Forschungsergenisse
	 * 
	 * @return
	 */
	synchronized public Collection<ResearchableDesign> getResearchResults()
	{
		Collection<ResearchableDesign> ret = new TreeSet<ResearchableDesign>(new Comparator<ResearchableDesign>()
		{

			public int compare(ResearchableDesign o1, ResearchableDesign o2)
			{
				/*
				 * Objekte umgekehrt vergleichen Um größere werte nach oben zu platzieren
				 */
				return o2.getResearchableResource().getCustomCounter() - o1.getResearchableResource().getCustomCounter();
			}
		});
		// Collection<ResearchableDesign> ret = new ArrayList<ResearchableDesign>();
		Collection<Map> maps = (Collection<Map>) getAttribute(RESEARCH_RESULTS);
		if(maps == null) return ret;
		for(Map map : maps)
			ret.add(new ResearchableDesign(map));
		return ret;
	}

	/**
	 * Speichert Forschungsergebnisse zum späteren Auswahl duch den Spieler
	 * 
	 * @param objects
	 */
	synchronized protected void setResearchedObjects(Collection<ResearchableDesign> objects)
	{
		Collection<Map> maps = new ArrayList<Map>();
		for(ResearchableDesign des : objects)
			maps.add(des.getObjectData());
		setAttribute(RESEARCH_RESULTS, maps);
	}

	/**
	 * @return Sortierte Liste mit Objekten die erforscht werden können
	 */
	public List<ResearchableResource> getResearchTargets()
	{
		List<ResearchableResource> ret = new ArrayList<ResearchableResource>(mapResearchableClasses.size());
		/*
		 * Zuerst Liste mit erforschbaren Schiffsystemen erstellen und sortieren
		 */
		List<ResearchableResource> shipSystems = new ArrayList<ResearchableResource>(mapResearchableClasses.size());
		for(ID id : mapResearchableClasses.keySet())
		{
			ResearchableResource res = getResearchableResource(id);
			if(res.isResearchAllowed() && res instanceof ShipsystemAbstract) shipSystems.add(res);
		}
		Collections.sort(shipSystems, new Comparator<ResearchableResource>()
		{

			public int compare(ResearchableResource o1, ResearchableResource o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		ret.addAll(shipSystems);
		/*
		 * Jetzt Liste mit erforschbaren Koloniegebäude erstellen und sortieren
		 */
		List<ResearchableResource> buildings = new ArrayList<ResearchableResource>(mapResearchableClasses.size());
		for(ID id : mapResearchableClasses.keySet())
		{
			ResearchableResource res = getResearchableResource(id);
			if(res.isResearchAllowed() && res instanceof BuildingAbstract) buildings.add(res);
		}
		Collections.sort(buildings, new Comparator<ResearchableResource>()
		{

			public int compare(ResearchableResource o1, ResearchableResource o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		ret.addAll(buildings);
		return ret;
	}

	public ResearchableResource getResearchableResource(ID id)
	{
		if(id == null) return null;
		Class cl = (Class) mapResearchableClasses.get(id);
		if(cl == null)
			Main.instance().severeErrorOccured(new IllegalStateException(), "Researchable Object mit der ID: " + id + " wurde nicht gefunden", true);
		ResearchableResource ret = null;
		try
		{
			ret = (ResearchableResource) cl.newInstance();
		} catch(Exception e)
		{
			Main.instance().severeErrorOccured(e, "Fehler bei der Instanziierung eines ResearchableReource-Objektes", true);
		}
		return ret;
	}

	private Map<ID, Map> getDesignsMap()
	{
		Map<ID, Map> data = (Map) getAttribute(SAVED_DESIGNS);
		if(data == null)
		{
			data = new Hashtable();
			setAttribute(SAVED_DESIGNS, data);
		}
		return data;
	}

	public void saveResearchableDesign(ResearchableDesign design)
	{
		getDesignsMap().put(design.getID(), design.getObjectData());
		fireResearchResultAddedEvent(design);
	}

	/**
	 * @return Vorsortierte Liste mit permanent gespeicherten ShipSystem Objekten
	 */
	public List<ResearchableDesign<ShipsystemAbstract>> getResearchedShipsystems()
	{
		List<ResearchableDesign<ShipsystemAbstract>> ret = new ArrayList();
		for(Map data : getDesignsMap().values())
		{
			/*
			 * Gebäude ausfiltern
			 */
			ResearchableDesign des = new ResearchableDesign(data);
			if(des.getResearchableResource() instanceof ShipsystemAbstract) ret.add(des);
		}
		/*
		 * Liste nach Namen sortieren
		 */
		Collections.sort(ret, new Comparator<ResearchableDesign<ShipsystemAbstract>>()
		{

			public int compare(ResearchableDesign<ShipsystemAbstract> o1, ResearchableDesign<ShipsystemAbstract> o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		return ret;
	}

	/**
	 * @return Vorsortierte Liste mit permanent gespeicherten Building Objekten
	 */
	public List<ResearchableDesign<BuildingAbstract>> getResearchedBuildings()
	{
		List<ResearchableDesign<BuildingAbstract>> ret = new ArrayList();
		for(Map data : getDesignsMap().values())
		{
			/*
			 * Gebäude ausfiltern
			 */
			ResearchableDesign des = new ResearchableDesign(data);
			if(des.getResearchableResource() instanceof BuildingAbstract) ret.add(des);
		}
		/*
		 * Liste nach Namen sortieren
		 */
		Collections.sort(ret, new Comparator<ResearchableDesign<BuildingAbstract>>()
		{

			public int compare(ResearchableDesign<BuildingAbstract> o1, ResearchableDesign<BuildingAbstract> o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		return ret;
	}

	public ResearchableDesign<? extends ResearchableResource> getResearchedDesign(ID id)
	{
		Map data = getDesignsMap().get(id);
		if(data == null) return null;
		return new ResearchableDesign<ResearchableResource>(data);
	}

	public void deleteResearchableDesign(ID id)
	{
		ResearchableDesign<? extends ResearchableResource> des = getResearchedDesign(id);
		getDesignsMap().remove(id);
		if(des != null) fireResearchResultRemovedEvent(des);
	}

	public void doDailyWork(CivDayReport dayValues)
	{
		investResearchPoints(dayValues.getSciencePoints());
	}

	public void doYearlyWork(CivYearReport yearValues)
	{
	}
}
