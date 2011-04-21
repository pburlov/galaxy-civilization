/*
 * $Id: CivilizationDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.civilization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mou.ClockEvent;
import mou.ClockListener;
import mou.Main;
import mou.core.colony.Colony;
import mou.core.colony.ColonyDB;
import mou.core.db.AbstractDB;
import mou.core.db.DBObjectImpl;
import mou.core.db.DBQueryResult;
import mou.gui.GUIConstants;
import mou.storage.ser.ID;
import burlov.collections.TimeOutHashtable;

public class CivilizationDB extends AbstractDB
		implements ClockListener
{

	static final private Class DB_DATA_OBJECT = Civilization.class;
	static private final String MY_CIVILIZATION_MONEY = "MY_CIVILIZATION_MONEY";
	static private final String MY_CIVILIZATION_NAME = "MY_CIVILIZATION_NAME";
	static private final String MY_CIVILIZATION_FOUNDATION_TIME = "MY_CIVILIZATION_FOUNDATION_TIME";
	static private final String MY_CIVILIZATION_TAX_RATE = "MY_CIVILIZATION_TAX_RATE";
	static private final String MY_CIVILIZATION_WORK_HOURS = "MY_CIVILIZATION_WORK_HOURS";
	static private final String MY_CIVILIZATION_REBEL_FAKTORS = "MY_CIVILIZATION_REBEL_FAKTORS";
	static private final String CIV_CAPITAL = "CIV_CAPITAL";
	static final public int MEIN = 0;
	static final public int NEUTRAL = 1;
	static final public int FEINDLICH = 2;
	static final public int ALIIERT = 3;
	static final public int UNBEKANNT = 4;
	static final public String STRING_MEIN = "meine";
	static final public String STRING_NEUTRAL = "neutral";
	static final public String STRING_FEINDLICH = "feindlich";
	static final public String STRING_ALIIERT = "alliiert";
	static final private long REMOTE_CIV_DATA_REQUEST_TIMEOUT = 1000 * 60 * 10;
	// static final private int MAX_HISTORY_LENGHT = 1000;
	static final public double MAX_TAX_RATE = .99;
	static final public double MAX_WORK_HOURS = 12;
	static final public double DEFAULT_WORK_HOURS = 8;
	static final public ID REBEL_ID = new ID(-1, 0);
	private List<CivilizationMember> listCivMembers = new ArrayList<CivilizationMember>();;
	private TimeOutHashtable<ID, ID> tableIDAnfragen = new TimeOutHashtable<ID, ID>(REMOTE_CIV_DATA_REQUEST_TIMEOUT);
	private CivDayReport civDayValues = new CivDayReport();

	public CivilizationDB(Hashtable<Object, Object> data)
	{
		super(data);
		Main.instance().getClockGenerator().addClockListener(this);
	}

	public void registerCivilizationMember(CivilizationMember member)
	{
		listCivMembers.add(member);
	}

	public void addRebelFactor(InfluenceFactor factor)
	{
		getRebelFactorsRaw().add(factor.getObjectData());
	}

	public Collection<InfluenceFactor> getMoralFactors()
	{
		Collection<InfluenceFactor> ret = new ArrayList<InfluenceFactor>();
		for(Iterator<Map> iter = getRebelFactorsRaw().iterator(); iter.hasNext();)
		{
			Map data = iter.next();
			InfluenceFactor factor = new InfluenceFactor(data);
			if(factor.getDuration().longValue() <= 0)
			{
				iter.remove();
			} else
				ret.add(factor);
		}
		return ret;
	}

	private Collection<Map> getRebelFactorsRaw()
	{
		Collection<Map> ret = (Collection) getSecondaryMapData().get(MY_CIVILIZATION_REBEL_FAKTORS);
		if(ret == null)
		{
			ret = new ArrayList<Map>();
			getSecondaryMapData().put(MY_CIVILIZATION_REBEL_FAKTORS, ret);
		}
		return ret;
	}

	public double getPopulation()
	{
		return civDayValues.getPopulation();
	}

	synchronized public Civilization getMyCivilization()
	{
		return getCivilization(getMyCivilizationID());
	}

	synchronized public Civilization createMyCivilization(String name)
	{
		this.getSecondaryMapData().put(MY_CIVILIZATION_NAME, name);
		this.getSecondaryMapData().put(MY_CIVILIZATION_FOUNDATION_TIME, Main.instance().getTime());
		return getMyCivilization();
	}

	static public ID createCivID(long clientSerNumber)
	{
		return new ID(0, clientSerNumber);
	}

	public CivDayReport getCivDayReport()
	{
		return civDayValues;
	}

	public Civilization addNewCiv(String name, long serial)
	{
		Civilization civ = new Civilization(this, name, serial);
		this.putData(civ, true);
		return civ;
	}

	/**
	 * Liefert ein Civilization Objekt, wenn Information ?ber die gefordete Civilization lokal
	 * gespeichert ist. Wenn keine Information lokal verf?gbar ist, dann wird null zur?ckgegeben und
	 * Anfrage ?ber diese Civilization ins Netz geschickt.
	 * 
	 * @param id
	 * @return
	 */
	public Civilization getCivilization(ID id)
	{
		if(id == null) return null;
		boolean requestName = false;
		Civilization ret = null;
		synchronized(getLockObject())
		{
			ret = (Civilization) getData(id);
			if(id.equals(getMyCivilizationID()))
			{// F?r eigene Civilization ein Civ-Objekt mit neuesten Daten f?llen
				String name = (String) getSecondaryMapData().get(MY_CIVILIZATION_NAME);
				if(name == null) return null;
				ret = new Civilization(null, name, id.getConstantPart());
				ret.setTimestamp();
				ret.setMoney(getMoney());
				ret.setFoundationTime((Long) getSecondaryMapData().get(MY_CIVILIZATION_FOUNDATION_TIME));
				ret.setKolonienAnzahl(new Integer(Main.instance().getMOUDB().getKolonieDB().getDBSize()));
				ret.setSchiffsanzahl(getMOUDB().getShipDB().getDBSize());
				ret.setBevolkerung(new Double(getPopulation()));
				return ret;
			}
			if(ret == null)
			{
				/*
				 * Vorsoglich einen Civilization Objekt erstellen um die NullpointerException zu
				 * vermeiden
				 */
				ret = addNewCiv(null, id.getConstantPart());
				requestName = true;
			}
		}
		/*
		 * Zivilisation ist noch unbekannt. Daten vom Netz anfordern. Dies muß außerhalb von
		 * synchronized Blocks stehen, sonst wenn 2 Zivilisationen zeitnah gegenseitig nach Namen
		 * fragen dann kommt ein Deadlock.
		 */
		if(requestName) requestCivData(id);
		return ret;
	}

	public Civilization getCivilization(long serNumber)
	{
		return getCivilization(createCivID(serNumber));
	}

	public void requestCivData(ID civ)
	{
		synchronized(tableIDAnfragen)
		{
			if(tableIDAnfragen.containsKey(civ)) return;
			tableIDAnfragen.put(civ, civ);
		}
		//TODO DiplomacyServer
//		DiplomacyServer server = Main.instance().getNetSubsystem().getDiplomacyServer();
//		if(server != null) server.requestCivName(new Long(civ.getConstantPart()));
	}

	public String getCivName(ID civID)
	{
		if(REBEL_ID.equals(civID)) return "Rebellen";
		Civilization civ = getCivilization(civID);
		if(civ == null) return "Unbekannt";
		return civ.getName();
	}

	/**
	 * Methode liefert den auktuellen Regierungssitz der Zivilisation.
	 * 
	 * @return
	 */
	public Colony getCapitalColony()
	{
		Colony ret = null;
		ID id = (ID) getSecondaryMapData().get(CIV_CAPITAL);
		if(id == null)
		{
			ret = suggestCapitalCandidate();
			/*
			 * Zivilisation hat keine Kolonien
			 */
			if(ret == null) return null;
			getSecondaryMapData().put(CIV_CAPITAL, ret.getID());
		} else
		{
			ret = (Colony) getMOUDB().getKolonieDB().getData(id);
			if(ret == null)
			{
				ret = suggestCapitalCandidate();
				getSecondaryMapData().put(CIV_CAPITAL, ret.getID());
			}
		}
		return ret;
	}

	/**
	 * Methode mach ein Vorschalg zum automatischen Auswahl des Regierungssitzes. Zur zeit wird
	 * einfach die bev?lkrerungsreichste Kolonie genommen
	 * 
	 * @return
	 */
	private Colony suggestCapitalCandidate()
	{
		ColonyDB colDB = getMOUDB().getKolonieDB();
		Colony ret = null;
		for(Colony col : colDB.getAlleKolonien().values())
		{
			if(ret == null) ret = col;
			if(ret.getPopulation().longValue() < col.getPopulation().longValue()) ret = col;
		}
		return ret;
	}

	/**
	 * Liefert ID eigener Zivilisation
	 * 
	 * @return
	 */
	public ID getMyCivilizationID()
	{
		return new ID(0l, Main.instance().getClientSerNumber().longValue());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "CivilizationDB";
	}

	/**
	 * Liefert Farbe in der Objekte dieser Zivilisation angezeigt werden sollen
	 * 
	 * @param civID
	 * @return
	 */
	public Color getGuiColorForCiv(ID civID)
	{
		Civilization c = getCivilization(civID);
		if(c == null) return GUIConstants.COLOR_NEUTRAL;
		if(c.getID().equals(getMyCivilizationID())) return GUIConstants.COLOR_MEIN;
		if(c.isEnemy()) return GUIConstants.COLOR_FEINDLICH;
		if(c.isAlly()) return GUIConstants.COLOR_ALLIIERT;
		return GUIConstants.COLOR_NEUTRAL;
	}

	/**
	 * Liefert textuelle Darstellung des diplomatischen Statuses
	 * 
	 * @param status
	 * @return
	 */
	public String getStringStatus(ID civ)
	{
		Civilization c = getCivilization(civ);
		if(c == null) return "Unbekannt";
		if(c.isEnemy()) return STRING_FEINDLICH;
		if(c.isAlly()) return STRING_ALIIERT;
		return STRING_NEUTRAL;
	}

	/**
	 * Liefert am St?ck die Civilization Objekte mit den gefordeten IDs
	 * 
	 * @param ids
	 * @return
	 */
	public DBQueryResult getCivs(Set ids)
	{
		return getDataWhere(DBObjectImpl.ATTR_ID, ids);
	}

	/**
	 * Liefert Menge der verf?gbaren Geldmittel
	 * 
	 * @return
	 */
	public double getMoney()
	{
		synchronized(getLockObject())
		{
			Number ret = (Number) getSecondaryMapData().get(MY_CIVILIZATION_MONEY);
			if(ret == null) ret = Main.ZERO_NUMBER;
			return ret.doubleValue();
		}
	}

	public void setMoney(double value)
	{
		synchronized(getLockObject())
		{
			getSecondaryMapData().put(MY_CIVILIZATION_MONEY, new Double(value));
		}
	}

	/**
	 * @return ein Wert > 0 und < 1
	 */
	public double getTaxRate()
	{
		synchronized(getLockObject())
		{
			Number ret = (Number) getSecondaryMapData().get(MY_CIVILIZATION_TAX_RATE);
			if(ret == null) ret = Main.ZERO_NUMBER;
			return ret.doubleValue();
		}
	}

	public void setTaxRate(double val)
	{
		if(val > MAX_TAX_RATE) val = MAX_TAX_RATE;
		if(val < 0) val = 0;
		synchronized(getLockObject())
		{
			getSecondaryMapData().put(MY_CIVILIZATION_TAX_RATE, new Double(val));
		}
	}

	public double getWorkHours()
	{
		synchronized(getLockObject())
		{
			Number ret = (Number) getSecondaryMapData().get(MY_CIVILIZATION_WORK_HOURS);
			if(ret == null)
			{
				setWorkHours(DEFAULT_WORK_HOURS);
				return DEFAULT_WORK_HOURS;
			}
			return ret.doubleValue();
		}
	}

	/**
	 * Berechnet ein Produktivitätsfaktor anhand der Arbeitstagsdauer
	 * 
	 * @return
	 */
	public double computeWorkFaktor()
	{
		return getWorkHours() / DEFAULT_WORK_HOURS;
	}

	public void setWorkHours(double val)
	{
		synchronized(getLockObject())
		{
			getSecondaryMapData().put(MY_CIVILIZATION_WORK_HOURS, new Double(val));
		}
	}

	/**
	 * Addiert eine Summe zu den vorhandenen Geldmitteln
	 * 
	 * @param value
	 *            Positive oder negative Wert
	 * @return
	 */
	public double addMoney(double value)
	{
		synchronized(getLockObject())
		{
			double newVal = getMoney() + value;
			Number ret = new Double(newVal);
			getSecondaryMapData().put(MY_CIVILIZATION_MONEY, ret);
			return ret.doubleValue();
		}
	}

	/**
	 * Liefert Set mit ID der bekannter Civilizationen die zu der eigener als freundlich eingestuft
	 * werden.
	 * 
	 * @return
	 */
	public Set<ID> getAlliiertCivs()
	{
		return getIDsWhere(Civilization.ATTR_STATUS_ALLY, new Boolean(true));
	}

	/**
	 * Liefert threadsicherre Set mit ID der bekannter Civilizationen die zu der eigener als
	 * feindlich eingestuft werden
	 * 
	 * @return
	 */
	public Set<ID> getFeindlichCivs()
	{
		return getIDsWhere(Civilization.ATTR_STATUS_ENEMY, new Boolean(true));
	}

	/**
	 * Liefert Collection mit allen lokal bekannten Civilizationen
	 * 
	 * @return Collection mit Civilization Objecten
	 * @see Civilization
	 */
	public Map<ID, Civilization> getAllCivs()
	{
		return getAllDBObjects();
	}

	/**
	 * Berechnet aus globalen Einflüssfaktoren die Aufstandswahrscheinlichkeit für die Zivilisation
	 * aus. Abgelaufen Einflüssfaktoren werden aus der Liste automatisch entfernt.
	 * 
	 * @return
	 */
	private double computeGlobalMoralFaktor()
	{
		double ret = 0;
		for(Iterator<InfluenceFactor> iter = getMoralFactors().iterator(); iter.hasNext();)
		{
			InfluenceFactor faktor = iter.next();
			ret += faktor.getValue().doubleValue();
		}
		return ret;
	}

	/**
	 * Methode geht alle RebelFactors durch und verringert ihre Lauzeit um 1. Abgelaufene Fyktoren
	 * werden dann utomatisch in der Methode getRebelFactors entfernt
	 */
	private void alterRebelFactors()
	{
		for(Iterator<InfluenceFactor> iter = getMoralFactors().iterator(); iter.hasNext();)
		{
			InfluenceFactor faktor = iter.next();
			long duration = faktor.getDuration().longValue() - 1;
			faktor.setDuration(duration);
		}
	}

	private void generateGlobalMoralFactors()
	{
		addRebelFactor(new InfluenceFactor(1, -getTaxRate(), "Steuern"));
		addRebelFactor(new InfluenceFactor(1, 1 - (getWorkHours() / DEFAULT_WORK_HOURS), "Arbeitstag"));
		double val = getMoney() / (getCivDayReport().getBSP() + 1);
		/*
		 * Unzufriedenheit durch Schulden nach unten begerenzen. Sonst kommt es zu einer
		 * Kettenreaktion: wenn ein Planet rebelliert verringert sich die BSP was zu schlechterer
		 * Moral führt und so zu nächsten Rebellion.
		 */
		if(val < -1) val = -1;
		if(val < 0) addRebelFactor(new InfluenceFactor(1, val, "Schulden"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.ClockListener#clockEvent(mou.ClockEvent)
	 */
	public void dailyEvent(ClockEvent event)
	{
		/*
		 * Einflüssfaktoren für Rebelleion ältern lassen
		 */
		alterRebelFactors();
		generateGlobalMoralFactors();
		civDayValues = new CivDayReport();
		civDayValues.setMoralFaktor(computeGlobalMoralFaktor());
		civDayValues.setTime(Main.instance().getTime());
		civDayValues.setWorkFaktor(computeWorkFaktor());
		civDayValues.setTaxRate(getTaxRate());
		for(CivilizationMember member : listCivMembers)
			member.doDailyWork(civDayValues);
		/*
		 * Folgende Abschnitt muss hier unten stehen, weil das Einkommen und Forschungetat dürfen
		 * esrt berechnet werden, wenn alle Kolonien und Schiffe ihren Beitrag gerechnet haben.
		 */
		// civDayValues.setSciencePoints(computeForschungspunkte());
		addMoney(civDayValues.computeNettoIncome());
	}

	public void yearlyEvent(ClockEvent event)
	{
		for(CivilizationMember member : listCivMembers)
			member.doYearlyWork(new CivYearReport());
	}
	// /**
	// * Berechnet anhand der eingestelten prozentuellen Forschungsetat und des Gesamteinkommens die
	// * verf?gbare Forschungspunkte
	// */
	// public double computeForschungspunkte()
	// {
	// double ret = (civDayValues.computeNettoIncome() / 100d) * getForschungEtat().doubleValue();
	// if(ret < 0) ret = 0;
	// return ret;
	// }
}