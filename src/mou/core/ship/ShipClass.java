/*
 * $Id: ShipClass.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.ship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mou.core.db.AbstractDB;
import mou.core.db.DBObjectImpl;
import mou.core.res.ResourceMenge;
import mou.core.res.shipsystem.ShipsystemAbstract;
import mou.core.res.shipsystem.weapon.Weapon;
import mou.core.research.ResearchableDesign;
import mou.storage.ser.ID;
import org.apache.commons.lang.SerializationUtils;

/**
 * Enthält eine Beschreibung des Schiffsklasses samt allen Eigenschaften und Ausrüstungen. Einzelne
 * Schiffen werden dann zu diesen Schiffsklassen zugeordnet.
 */
public strictfp class ShipClass extends DBObjectImpl
{

	// static final private float FAKTOR_RUMPFMASSE = 0.1f;
	// static final private float FAKTOR_PANZERDICKE = 0.1f; //Faktor =1 ein
	// Meter Dicke, 0.5
	// halbe Meter, usw
	// static final private float FAKTOR_ARBEITSKOSTEN = 10;//Pro Tonne
	// Rumpfmasse
	static final private String ATTR_NAME = "ATTR_NAME";
	static final public String ATTR_SYSTEMS = "ATTR_SYSTEMS";
	static final public int MAX_CREW_SKILL = 10;// Maximal Erfahrungsstufe
	// static final public String ATTR_ZUSTAND = "ATTR_ZUSTAND";
	// static final private String ATTR_FINAL = "FINAL";//True wenn das Schiff
	// schon gebaut wurde
	private double weapon;
	private double hyperAntrieb;
	private double energy;
	private double armor;
	private double shild;
	private double crew;
	private double lebenserhaltung;
	private double masse;
	private double struktur;
	private double settler;
	private double support;
	private double buildCost;
	private Map<ID, Number> neededMaterials = new HashMap<ID, Number>();

	/**
	 * 
	 */
	public ShipClass()
	{
		super();
		initWithData(new HashMap());
		setAttribute(ATTR_SYSTEMS, new ArrayList(), false);
		setAttribute(ATTR_NAME, "Schiff", false);
	}

	public ShipClass(Map data)
	{
		initWithData(data);
		computeWerte();
	}

	/**
	 * Methode prüft ob ein Schiff nach diesem BAuplan gebaut werden kann/darf oder nicht.
	 * 
	 * @return
	 */
	public BuildAllowed canBuild()
	{
		computeWerte();
		if(getEnergie() < 0) { return new BuildAllowed(false, "Zu schwache Energieversorgung!"); }
		if(getLebenserhaltung() < getCrew()) { return new BuildAllowed(false, "Zu schwache Lebenserhaltungssystem"); }
		return new BuildAllowed(true, "");
	}

	public void setStruktur(double struktur)
	{
		this.struktur = struktur;
	}

	public double getStruktur()
	{
		return struktur;
	}

	public double getSupportCost()
	{
		return support;
	}

	public void setSupportCost(double support)
	{
		this.support = support;
	}

	/**
	 * Erzeugt neuen Schiff mit den Daten von anderem Schiff. ShipID wird auf null gesetzt.
	 * 
	 * @param prototyp
	 */
	public ShipClass(ShipClass prototyp)
	{
		super();
		initWithData((Map) SerializationUtils.clone((Serializable) prototyp.getObjectData()));
		setAttribute(ATTR_ID, new ID(), false);
		computeWerte();
	}

	/**
	 * Hinzufügt ein Schiffsystem wenn möglich. <br>
	 * Wenn das Schiff noch in Projektphase ist und die maximale Nutzlast <br>
	 * kleiner als Masse der Systemen, dann wird die maximale Nutzlast angepasst.
	 * 
	 * @param system
	 */
	public void addShipSystem(ResearchableDesign system)
	{
		synchronized(getLockObject())
		{
			List systems = getSystemsIntern();
			systems.add(SerializationUtils.clone((Serializable) system.getObjectData()));
			computeWerte();
		}
	}

	public void removeShipSystem(ResearchableDesign system)
	{
		synchronized(getLockObject())
		{
			List systems = (List) getAttribute(ATTR_SYSTEMS);
			systems.remove(system.getObjectData());
		}
	}

	/**
	 * Liefert Liste (ResearchableDesign Objekte) mit installierten Waffen
	 * 
	 * @return
	 */
	public List getWeapons()
	{
		List ret = new ArrayList();
		synchronized(getLockObject())
		{
			for(Iterator iter = getSystems().iterator(); iter.hasNext();)
			{
				ResearchableDesign des = (ResearchableDesign) iter.next();
				if(des.getResearchableResource() instanceof Weapon) ret.add(des);
			}
			return ret;
		}
	}

	/**
	 * Liefert eine Liste mit installierten Schiffssytemen
	 * 
	 * @return Iteratorsichere Liste mit ShipSystemDesign-Objecten
	 */
	synchronized public List<ResearchableDesign> getSystems()
	{
		List<ResearchableDesign> listSystems = new ArrayList<ResearchableDesign>();
		for(int i = 0; i < getShipSystemCount(); i++)
		{
			ResearchableDesign des = getShipSystem(i);
			if(des != null) listSystems.add(des);
		}
		return listSystems;
	}

	/**
	 * Liefert native Liste mit Daten der Systemen
	 * 
	 * @return
	 */
	private List<Map> getSystemsIntern()
	{
		return (List<Map>) getAttribute(ATTR_SYSTEMS);
	}

	public ResearchableDesign getShipSystem(int index)
	{
		synchronized(getLockObject())
		{
			List list = getSystemsIntern();
			if(list == null || list.size() <= index) { return null; }
			ResearchableDesign ret = new ResearchableDesign((Map) list.get(index));
			return ret;
		}
	}

	public int getShipSystemCount()
	{
		List list = getSystemsIntern();
		if(list == null) return 0;
		return list.size();
	}

	public String getName()
	{
		String ret = (String) getAttribute(ATTR_NAME);
		if(ret == null) ret = "Unbekannt";
		return ret;
	}

	public void setName(String name)
	{
		this.setAttribute(ATTR_NAME, name, false);
	}

	public double getEnergie()
	{
		return energy;
	}

	/**
	 * Methode berechnet aus der Antriebsleistung und der Masse des Schiffes die maximale Reichweite
	 * pro Sprung
	 */
	public double getSpeed()
	{
		return hyperAntrieb / getMasse();
	}

	public void computeWerte()
	{
		weapon = 0;
		hyperAntrieb = 0;
		energy = 0;
		armor = 0;
		shild = 0;
		crew = 1;
		struktur = 0;
		lebenserhaltung = 0;
		masse = 0;
		buildCost = 0;
		settler = 0;
		energy = 0;
		support = 0;
		neededMaterials = new HashMap<ID, Number>();
		// #### Jetzt dürfen die installierte Schiffsysteme die
		// Schiffseigenschaften verändern #####
		Iterator iter = getSystems().iterator();
		while(iter.hasNext())
		{
			ResearchableDesign shipDesign = (ResearchableDesign) iter.next();
			((ShipsystemAbstract) shipDesign.getResearchableResource()).computeShipPoints(this);
		}
	}

	/**
	 * MEthode berechnet Summe der Baukosten der installierten Systeme
	 * 
	 * @return
	 */
	public double getBuildCost()
	{
		return buildCost;
	}

	public void setBuildCost(double buildCost)
	{
		this.buildCost = buildCost;
	}

	/**
	 * Liefet vorsortierte Liste mit für die Bau dieses Schiffes benötigten Materialien
	 * 
	 * @return Liste mit RessourceMenge-Objecten
	 */
	public List<ResourceMenge> getNeededRessources()
	{
		ArrayList<ResourceMenge> ret = new ArrayList<ResourceMenge>();
		for(Map.Entry<ID, Number> entry : getNeededMaterials().entrySet())
		{
			ret.add(new ResourceMenge(entry.getKey(), entry.getValue().doubleValue()));
		}
		Collections.sort(ret);
		return ret;
	}

	/**
	 * @return Returns the antriebstaerke.
	 */
	public double getHyperantrieb()
	{
		return hyperAntrieb;
	}

	/**
	 * @param antriebstaerke
	 *            The antriebstaerke to set.
	 */
	public void setHyperantrieb(double antriebstaerke)
	{
		this.hyperAntrieb = antriebstaerke;
	}

	public double getPanzer()
	{
		return armor;
	}

	public void setPanzer(double panzer)
	{
		this.armor = panzer;
	}

	/**
	 * @return Returns the waffenstarke_bean.
	 */
	public double getWaffenstarke()
	{
		return weapon;
	}

	/**
	 * @param waffenstarke_bean
	 *            The waffenstarke_bean to set.
	 */
	public void setWaffenstarke(double waffenstarke)
	{
		this.weapon = waffenstarke;
	}

	/**
	 * @param energie
	 *            The energie to set.
	 */
	public void setEnergie(double energie)
	{
		this.energy = energie;
	}

	public double getCrew()
	{
		return crew;
	}

	public void setCrew(double crew)
	{
		this.crew = crew;
	}

	public double getSettler()
	{
		return settler;
	}

	public void setSettler(double settler)
	{
		this.settler = settler;
	}

	public double getLebenserhaltung()
	{
		return lebenserhaltung;
	}

	public void setLebenserhaltung(double lebenserhaltung)
	{
		this.lebenserhaltung = lebenserhaltung;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.DBObjectImpl#insertInDB(mou.db.AbstractDB)
	 */
	protected void insertInDB(AbstractDB db)
	{
		insertInDB(db, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.DBObjectImpl#insertInDB(mou.db.AbstractDB, boolean)
	 */
	public void insertInDB(AbstractDB db, boolean fireEvent)
	{
		// if(getID() != null)return;
		// initWithData(new Hashtable(getObjectData()));
		if(getID() == null) setAttribute(ATTR_ID, new ID(), false);
		// setMaxNutzlast(computeSystemsMasse());//Maximale Nutzlast für alle Zeiten
		// festlegen
		super.insertInDB(db, fireEvent);
	}

	public double getSchild()
	{
		return shild;
	}

	/**
	 * @param schild_energie
	 *            The schild_energie to set.
	 */
	public void setSchild(double schild)
	{
		this.shild = schild;
	}

	/**
	 * Kleine Hilfsklasse für die Methode canBuild();
	 * 
	 * @author pb
	 */
	public class BuildAllowed
	{

		private boolean allowed;
		private String comment;

		public BuildAllowed(boolean allow, String comment)
		{
			allowed = allow;
			this.comment = comment;
		}

		/**
		 * Gibt an ob das Schiff gebaut werden darf
		 * 
		 * @return
		 */
		public boolean isAllowed()
		{
			return allowed;
		}

		/**
		 * Liefert Kommentar falls das Bau nicht erlaubt ist
		 * 
		 * @return
		 */
		public String getComment()
		{
			return comment;
		}
	}

	public double getMasse()
	{
		return masse;
	}

	public void setMasse(double masse)
	{
		this.masse = masse;
	}

	public Map<ID, Number> getNeededMaterials()
	{
		return neededMaterials;
	}
}