/*
 * $Id: ColonyDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.colony;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mou.Main;
import mou.core.civilization.CivDayReport;
import mou.core.civilization.CivYearReport;
import mou.core.civilization.CivilizationDB;
import mou.core.civilization.CivilizationMember;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.ColonyCenter.ColonyCenter;
import mou.core.research.ResearchableDesign;
import mou.core.starmap.PositionableDB;
import mou.gui.colonyscreen.ColonyDialog;
import mou.storage.ser.ID;

/**
 * H?lt und verwaltet Kolonie-Objekten
 */
public class ColonyDB extends PositionableDB
		implements CivilizationMember
{

	static final public int STANDARD_COLONY_SIZE = 1000000;
	static final private Class DB_DATA_OBJECT = Colony.class;
	private List<ColonyDBListener> listeners = new ArrayList<ColonyDBListener>();
	private int rebelledColonies = 0;

	public ColonyDB(Hashtable data)
	{
		super(data);
		Main.instance().getMOUDB().getCivilizationDB().registerCivilizationMember(this);
	}

	public void addColonyDBListener(ColonyDBListener listener)
	{
		listeners.add(listener);
	}

	void fireColonyChangedEvent(Colony col)
	{
		for(ColonyDBListener listener : listeners)
			listener.colonyChanged(col);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "KolonieDB";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		return DB_DATA_OBJECT;
	}

	public Colony createNewKolonie(Point position, double colonySize)
	{
		Main.instance().getMOUDB().getStarmapDB().markAsVisited(position);
		Colony kol = new Colony(this, Main.instance().getMOUDB().getCivilizationDB().getMyCivilization().getID(), position);
		kol.setPopulation(colonySize);
		this.putData(kol, true);
		ColonyCenter center = new ColonyCenter(colonySize);
		center.setColonyID(kol.getID());
		kol.addBuilding(new ResearchableDesign<BuildingAbstract>(center, "Koloniezentrum"));
		return kol;
	}

	/**
	 * Threadsichere Liste aller Kolonien
	 * 
	 * @return Key: ID; Value: Kolonie Objekt
	 */
	public Map<ID, Colony> getAlleKolonien()
	{
		return getAllDBObjects();
	}

	/**
	 * @param id
	 * @return
	 */
	public Colony getKolonie(ID id)
	{
		Colony ret = (Colony) getData(id);
		// if(ret != null)ret.computePoints();
		return ret;
	}

	/**
	 * Methode rechnet die Gesamtbev?lkerung aller Kolonien aus der DB
	 * 
	 * @return
	 */
	public long computePopulation()
	{
		long ret = 0;
		for(Iterator iter = getAlleKolonien().values().iterator(); iter.hasNext();)
		{
			ret += ((Colony) iter.next()).getPopulation().longValue();
		}
		return ret;
	}

	/**
	 * Liste mit Kolonie Objekten
	 * 
	 * @return
	 */
	public List getColoniesInSystem(Point position)
	{
		return getDataWhere(Colony.ATTR_STAR_POS, position).getList();
	}

	public Colony getColonyAt(Point pos)
	{
		List kols = getColoniesInSystem(pos);
		if(kols.isEmpty())
			return null;
		else
			return (Colony) kols.get(0);
	}

	public void addColony(Colony kol)
	{
		putData(kol);
	}

	public void removeKolonie(ID id)
	{
		removeKolonie(id, true);
	}
	
	public void removeKolonie(ID id, boolean allowCleanUp)
	{
		if(id == null) return;
		Colony kolonie = getKolonie(id);
		ColonyDialog dialog = Main.instance().getGUI().getColonyDialog(); 
		
		/* ColonyDialog schließen */
		if((dialog != null) && (dialog.getShowedColony() != null) &&
				(dialog.getShowedColony().getID() == id))
		{
			dialog.setVisible(false);
		}
		/* RebellenSchiffe löschen */
		if(kolonie.isRebelled())
			Main.instance().getMOUDB().getRebelShipDB().deleteShipsInStarsystem(kolonie.getPosition());
		if(allowCleanUp)
			kolonie.cleanUp();
		removeData(id);
	}

	/*
	 * Setzt alle Kolonien zurück, die Anzahl der Leute in der Kolonie bleibt erhalten,
	 * alle Gebäude werden gelöscht und ein ausreichend großes Koloniegebäude erstellt
	 */
	public void resetAllColonies()
	{
		//long pop = 0;
		for(Iterator iter = getAlleKolonien().values().iterator(); iter.hasNext();)
		{
			//pop = ((Colony) iter.next()).getPopulation().longValue();
			resetColony((Colony) iter.next());
		}
	}
	
	public void resetColony(Colony kol)
	{
		if(kol == null) return;
		Point pos = kol.getPosition();
		double pop = kol.getPopulation().doubleValue();
		/* Kein CleanUp, da Kolonie wieder erzeugt wird */
		removeKolonie(kol.getID(), false);
		if(pop > 0)
			createNewKolonie(pos, pop);
	}
	
	/**
	 * Entfernt alle Kolonien einer Zivilisation in einem Sternensystem
	 * 
	 * @param pos
	 * @param civ
	 */
	public void removeColonyData(Point pos, long civ)
	{
		synchronized(getLockObject())
		{
			ID civID = CivilizationDB.createCivID(civ);
			Iterator iter = getColoniesInSystem(pos).iterator();
			while(iter.hasNext())
			{
				Colony kol = (Colony) iter.next();
				if(kol.getCivID().equals(civID))
				{
					removeKolonie(kol.getID());
					continue;
				}
			}
		}
	}

	public int getRebelledColoniesCount()
	{
		return rebelledColonies;
	}

	public int getColoniesCount()
	{
		return this.getDBSize();
	}

	/**
	 * L?scht alle Kolonien aller Zivilisationen an einer Position
	 * 
	 * @param pos
	 */
	public void deleteColonies(Point pos)
	{
		synchronized(getLockObject())
		{
			Iterator iter = getColoniesInSystem(pos).iterator();
			while(iter.hasNext())
			{
				Colony kol = (Colony) iter.next();
				removeKolonie(kol.getID());
			}
		}
	}

	public void doDailyWork(CivDayReport dayValues)
	{
		rebelledColonies = 0;
		for(Colony col : getAlleKolonien().values())
		{
			col.doDailyWorkPhase1(dayValues);
		}
		for(Colony col : getAlleKolonien().values())
		{
			col.doDailyWorkPhase2(dayValues);
			if(col.isRebelled()) rebelledColonies++;
		}
	}

	public void doYearlyWork(CivYearReport yearValues)
	{
		for(Colony col : getAlleKolonien().values())
			col.doYearlyWork(yearValues);
	}
}