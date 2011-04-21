/*
 * $Id: BuildJobAbstract.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.colony;

import java.util.Hashtable;
import java.util.Map;
import mou.Main;
import mou.core.IDable;
import mou.core.MOUDB;
import mou.core.MapWrapper;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pbu
 */
abstract public class BuildJobAbstract extends MapWrapper
		implements IDable
{

	static public final String ATTR_ID = "id";
	static public final String ATTR_TYP = "typ";
	static final private String ATTR_COMPLETED = "ATTR_COMPLETED";
	static final private String ATTR_INVESTED_PRODUCTION = "ATTR_INVESTED_PRODUCTION";
	static public final Integer TYP_BUILDING = new Integer(1);
	static public final Integer TYP_SHIP = new Integer(2);
	static public final Integer TYP_IDLE = new Integer(3);
	static final protected double BUY_PRICE_FAKTOR = 10;
	static protected final String ATTR_BUILD_MESSAGE = "ATTR_BUILD_MESSAGE";
	//	Beim Abbruch des Jobs werden bis zu MAX_PUT_BACK der Ressourcen zurückgegeben
	static final protected double MAX_PUT_BACK = 0.75;

	/**
	 * Name für die Darstellung in GUI
	 * 
	 * @return
	 */
	abstract public String getName();

	abstract public double computeNeededWorkPoints();

	/**
	 * Wird aufgerufen wenn die Bau gestartet wird
	 * 
	 * @param colony
	 * @return
	 */
	abstract public BuildAllowed startBuild(Colony colony);

	/**
	 * Wird aufgerufen wenn die Bau gecancelt wurde
	 * 
	 * @param colony
	 */
	abstract public void cancelBuild(Colony colony);

	/**
	 * Wird aufgerufen, wenn die Bau fertiggestellt werden soll
	 * 
	 * @param colony
	 */
	abstract public void completeBuild(Colony colony);

	/**
	 * Gibt an, ob bei Abschluß des Baus eine Nachricht verschickt werden soll 
	 * 
	 * @return
	 */
	public boolean showMessageWhenCompleted()
	{
		return ((Boolean) getAttribute(ATTR_BUILD_MESSAGE, true)).booleanValue();
	}
	
	public void showMessageWhenCompleted(boolean showMessage)
	{
		setAttribute(ATTR_BUILD_MESSAGE, showMessage);
	}
	
	
	/**
	 * Liefert den Preis zu dem dieser Bauauftrag sofort beendet werden kann.
	 * 
	 * @return
	 */
	public double getBuyPrice()
	{
		double delta = computeNeededWorkPoints() - getInvestedProduction();
		if(delta < 0) return 0;
		return delta * BUY_PRICE_FAKTOR;
	}

	/**
	 * Berechnet den normalen Bauprozess.
	 * 
	 * @param production
	 */
	public void proceedBuild(Colony colony, double production)
	{
		setInvestedProduction(getInvestedProduction() + production);
		if(getProgress() >= 1.0)
		{
			completeBuild(colony);
			setCompleted();
		}
	}

	public double getInvestedProduction()
	{
		return ((Number) getAttribute(ATTR_INVESTED_PRODUCTION, Main.ZERO_NUMBER)).doubleValue();
	}

	/**
	 * Liefert den Baufortschritt als Zahl zwischen 0 und 1
	 * 
	 * @return
	 */
	public double getProgress()
	{
		return getInvestedProduction() / computeNeededWorkPoints();
	}

	public void setInvestedProduction(double val)
	{
		setAttribute(ATTR_INVESTED_PRODUCTION, val);
	}

	public boolean isCompleted()
	{
		return getAttribute(ATTR_COMPLETED) != null;
	}

	public void setCompleted()
	{
		setAttribute(ATTR_COMPLETED, new Boolean(true));
	}

	/**
	 * Factory Methode. Konstruiert eine von BuildQueueItemAbstract abgeleitete Klasse anhand von
	 * ATTR_TYP-Attributes aus der mitgegebener Map
	 * 
	 * @param data
	 * @return
	 */
	static public BuildJobAbstract constructBuildQueueItem(Map data)
	{
		Integer typ = (Integer) data.get(ATTR_TYP);
		if(typ == null)
			throw new IllegalStateException("Falsche initialisierungsdaten für ein BuildQueueItemAbstract Object.\n"
					+ " Kann den Attribut ATTR_TYP nicht finden.");
		if(TYP_BUILDING.equals(typ)) { return new BuildingBuildJob(data); }
		if(TYP_SHIP.equals(typ)) { return new ShipBuildJob(data); }
		if(TYP_IDLE.equals(typ)) { return new IdleBuildJob(data); }
		throw new IllegalStateException("Unbekannte Typ von BuildQueueItemAbstract-Klasse");
	}

	public BuildJobAbstract(Map data)
	{
		super(data);
	}

	public BuildJobAbstract(Integer typ)
	{
		this(typ, true);
	}
	
	public BuildJobAbstract(Integer typ, boolean showMessage)
	{
		super(new Hashtable());
		setAttribute(ATTR_ID, new ID());
		setAttribute(ATTR_TYP, typ);

		showMessageWhenCompleted(showMessage);
	}

	public Integer getTyp()
	{
		return (Integer) getAttribute(ATTR_TYP);
	}

	public ID getID()
	{
		return (ID) getAttribute(ATTR_ID);
	}

	public MOUDB getMOUDB()
	{
		return Main.instance().getMOUDB();
	}

	@Override
	public boolean equals(Object obj)
	{
		return getID().equals(((BuildJobAbstract) obj).getID());
	}

	public class BuildAllowed
	{

		private boolean allowed;
		private String comment;

		public BuildAllowed(boolean allowed, String comment)
		{
			this.allowed = allowed;
			this.comment = comment;
		}

		public boolean isAllowed()
		{
			return allowed;
		}

		public String getComment()
		{
			return comment;
		}
	}

	@Override
	public String toString()
	{
		return getName() + " " + GUI.formatProzent(getProgress() * 100);
	}
}
