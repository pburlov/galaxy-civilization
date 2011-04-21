/*
 * $Id: Ship.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.ship;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import mou.Main;
import mou.Universum;
import mou.core.civilization.CivDayReport;
import mou.core.civilization.CivYearReport;
import mou.core.civilization.CivilizationMember;
import mou.core.colony.Colony;
import mou.core.db.DBObjectImpl;
import mou.core.starmap.Positionable;
import mou.storage.ser.ID;

/**
 * Repräsentiert ein einzelne Schiff
 */
public class Ship extends DBObjectImpl
		implements Positionable, CivilizationMember
{

	static final private Double ZERO = new Double(0);
	static final private double STRUKT_REPARATUR_PRO_RUNDE = 0.01;
	static final public String ATTR_POSITION = "POSITION";
	static final private String ATTR_STRUKTUR = "STRUKTUR";
	static final private String ATTR_CURRENT_STRUKTUR = "CURRENT_STRUKTUR";
	static final private String ATTR_SPEED = "SPEED";
	static final private String ATTR_PANZER = "PANZER";
	static final private String ATTR_WAFFE = "WAFFE";
	static final private String ATTR_SHILD = "SHILD";
	static final private String ATTR_CREW = "CREW";
	static final private String ATTR_MASSE = "MASSE";
	static final private String ATTR_SETTLER = "SETTLER";
	static final private String ATTR_SUPPORT = "SUPPORT";
	static final private String ATTR_BUILD_COST = "BUILD_COST";
	static final private String ATTR_SHIP_CLASS_ID = "SHIP_CLASS_ID";
	static final private String ATTR_SHIP_CLASS_NAME = "SHIP_CLASS_NAME";

	// static final public String ATTR_FLYING = "ATTR_FLYING";
	// static final public String ATTR_CIV_ID = "ATTR_CIV_ID";
	/**
	 * 
	 */
	public Ship()
	{
		super(new HashMap());
	}

	public Ship(Map data)
	{
		super(data);
	}

	Ship(ShipClass sc, Point pos)
	{
		super(new HashMap());
		sc.computeWerte();
		setAttribute(DBObjectImpl.ATTR_ID, new ID(), false);// Neue Haupt-ID schon für das Schiff
		// setzen
		setShipClassID(sc.getID());
		setShipClassName(sc.getName());
		setCrew(sc.getCrew());
		setMasse(sc.getMasse());
		setArmor(sc.getPanzer());
		setSettler(sc.getSettler());
		setShild(sc.getSchild());
		setSpeed(sc.getHyperantrieb() / sc.getMasse());
		setStruktur(sc.getStruktur());
		setCurrentStruktur(sc.getStruktur());
		setSupportCost(sc.getSupportCost());
		setWeapon(sc.getWaffenstarke());
		setPosition(pos);
		setBuildcost(sc.getBuildCost());
	}

	public Ship(Ship prototyp)
	{
		super(prototyp.getObjectData());
		setAttribute(DBObjectImpl.ATTR_ID, new ID(), false);
	}

	public void resetID()
	{
		setAttribute(DBObjectImpl.ATTR_ID, new ID(), false);
	}

	public ID getShipClassID()
	{
		ID ret = (ID) getAttribute(ATTR_SHIP_CLASS_ID);
		if(ret == null)
		{
			/*
			 * Wenn ein Schiff ohne Schiffs-Klasse erzeugt wurde, dann kann diese ID null sein. Um
			 * dies zu vermeiden ist diese Sicherung eingebaut.
			 */
			ret = new ID();
			setShipClassID(ret);
		}
		return ret;
	}

	public void setShipClassID(ID id)
	{
		setAttribute(ATTR_SHIP_CLASS_ID, id, false);
	}

	public String getShipClassName()
	{
		return (String) getAttributLazy(ATTR_SHIP_CLASS_NAME, "SCHIFF");
	}

	public void setShipClassName(String name)
	{
		setAttribute(ATTR_SHIP_CLASS_NAME, name, false);
	}

	public double getBuildCost()
	{
		return ((Number) getAttributLazy(ATTR_BUILD_COST, ZERO)).doubleValue();
	}

	public void setBuildcost(double val)
	{
		setAttribute(ATTR_BUILD_COST, val, false);
	}

	public double getSpeed()
	{
		return ((Number) getAttributLazy(ATTR_SPEED, ZERO)).doubleValue();
	}

	public void setSpeed(double val)
	{
		setAttribute(ATTR_SPEED, val, false);
	}

	public double getCrew()
	{
		return ((Number) getAttributLazy(ATTR_CREW, ZERO)).doubleValue();
	}

	public void setCrew(double val)
	{
		setAttribute(ATTR_CREW, val, false);
	}

	public double getMasse()
	{
		return ((Number) getAttributLazy(ATTR_MASSE, ZERO)).doubleValue();
	}

	public void setMasse(double val)
	{
		setAttribute(ATTR_MASSE, val, false);
	}

	public double getArmor()
	{
		return ((Number) getAttributLazy(ATTR_PANZER, ZERO)).doubleValue();
	}

	public void setArmor(double val)
	{
		setAttribute(ATTR_PANZER, val, false);
	}

	public double getShild()
	{
		return ((Number) getAttributLazy(ATTR_SHILD, ZERO)).doubleValue();
	}

	public void setShild(double val)
	{
		setAttribute(ATTR_SHILD, val, false);
	}

	public double getSettler()
	{
		return ((Number) getAttributLazy(ATTR_SETTLER, ZERO)).doubleValue();
	}

	public void setSettler(double val)
	{
		setAttribute(ATTR_SETTLER, val, false);
	}

	public double getWeapon()
	{
		return ((Number) getAttributLazy(ATTR_WAFFE, ZERO)).doubleValue();
	}

	public void setWeapon(double val)
	{
		setAttribute(ATTR_WAFFE, val, false);
	}

	public double getSupportCost()
	{
		return ((Number) getAttributLazy(ATTR_SUPPORT, ZERO)).doubleValue();
	}

	public void setSupportCost(double val)
	{
		setAttribute(ATTR_SUPPORT, val, false);
	}

	// synchronized public ShipClass getShipClass()
	// {
	// if(shipClass != null) return shipClass;
	// Map data = (Map) getAttribute(ATTR_SHIPCLASS_DATA);
	// shipClass = new ShipClass(data);
	// shipClass.computeWerte();
	// return shipClass;
	// }
	//	
	/**
	 * Liefert schiffsspezifische Actionen
	 */
	public Collection<Action> getActions()
	{
		Collection<Action> ret = new ArrayList<Action>(1);
		final Point pos = getPosition();
		if(pos == null) return null;
		if(getSettler() > 0)
		{
			/*
			 * Wenn das Schiff koloniemodule besitzt dann entsprechenden Action-Object hinzufügen
			 */
			final Colony col = getMOUDB().getKolonieDB().getColonyAt(pos);
			Action act = null;
			if((col != null) && (col.getPopulation().longValue() < col.getMaxBevoelkerung()))
			{
				act = new AbstractAction("Kolonie erweitern")
				{

					public void actionPerformed(ActionEvent e)
					{
						col.extendsColony(getSettler());
						getMOUDB().getShipDB().deleteShip(getID());
					}
				};
			}
			if(col == null)
			{
				act = new AbstractAction("Kolonie gründen")
				{

					public void actionPerformed(ActionEvent e)
					{
						getMOUDB().getKolonieDB().createNewKolonie(pos, (int) getSettler());
						getMOUDB().getShipDB().deleteShip(getID());
					}
				};
			}
			if(act != null) ret.add(act);
		}
		return ret;
	}

	/**
	 * Liefert aktuelle Strukturwert des Schiffes. Wird bei Beschädigungen und Reparaturen gesetzt.
	 * 
	 * @return
	 */
	public Double getCurrentStruktur()
	{
		Double ret = (Double) getAttributLazy(ATTR_CURRENT_STRUKTUR, ZERO);
		return ret;
	}

	/**
	 * Setzt den aktuellen Strukturwert des Schiffes
	 * 
	 * @param val
	 */
	public void setCurrentStruktur(Double val)
	{
		if(val.doubleValue() > getStruktur()) val = getStruktur();
		/*
		 * DBEvent auslösen, damit im Flottenübersicht die Reparaturfortschritten sichtbar werden
		 */
		setAttribute(ATTR_CURRENT_STRUKTUR, val, true);
	}

	/**
	 * Liefert den Soll-Strukturwert. Wird beim Schiffsbau gesetzt.
	 * 
	 * @return
	 */
	public Double getStruktur()
	{
		Double ret = (Double) getAttributLazy(ATTR_STRUKTUR, ZERO);
		return ret;
	}

	/**
	 * Setzt den Soll-Strukturwert. Soll nur bei Schiffsbau aufgerufen werden
	 * 
	 * @param val
	 */
	public void setStruktur(Double val)
	{
		setAttribute(ATTR_STRUKTUR, val, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.Ship#getPosition()
	 */
	public Point getPosition()
	{
		return (Point) getAttribute(ATTR_POSITION);
	}

	/**
	 * Damit wird das Schiff verschrottet
	 */
	public void scrapShip()
	{
		getMOUDB().getShipDB().deleteShip(getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.Ship#setPoisiton(java.awt.Point)
	 */
	public void setPosition(Point newPosition)
	{
		if(newPosition != null) Main.instance().getMOUDB().getStarmapDB().markAsVisited(newPosition);
		setAttribute(ATTR_POSITION, newPosition, true);
	}

	/**
	 * Berechnen Starmap Koordinaten wo das Schiff sich derzeit befindet. Wenn das Schiff im Orbit
	 * ist, dann wird einfach Starsystem-Koorinate zurückgegeben. Wenn das Schiff im freien Flüg
	 * ist, dann wird eine ungefähre Position berechnnet.
	 * 
	 * @return
	 */
	public Point computeApproxMapPosition()
	{
		Point ret = getPosition();
		if(ret != null) return ret;// Das Schiff ist im Orbit
		ShipMovementOrder order = getMovementOrder();
		return order.computeCurrentApproxStarmapPoint();
	}

	/**
	 * Liefert aktuelle Glügbefehl, oder null
	 * 
	 * @return
	 */
	public ShipMovementOrder getMovementOrder()
	{
		ShipMovementOrder ret = getMOUDB().getShipMovementOrderDB().getOrderForShip(getID());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.Ship#getCivID()
	 */
	public ID getCivID()
	{
		return new ID(0l, getID().getConstantPart());
	}

	/**
	 * Prüft bo dieses Schiff eigener Zivilisation angehört
	 * 
	 * @return
	 */
	public boolean isOwn()
	{
		return Main.instance().getClientSerNumber().longValue() == getCivID().getConstantPart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.Positionable#getQuadrant()
	 */
	public Point getQuadrant()
	{
		return Universum.getQuadrantForPosition(getPosition());
	}

	public void doDailyWork(CivDayReport dayValues)
	{
		dayValues.setShipSupportCost(dayValues.getSupportCostShips() + getSupportCost());
		Point pos = getPosition();
		if(pos == null) return;
		if(getStruktur().doubleValue() != getCurrentStruktur().doubleValue())
		{
			Colony col = getMOUDB().getKolonieDB().getColonyAt(pos);
			if(col != null)
			{
				/*
				 * Schiffe die bei Kolonien stationiert sind werden Schaden mit % pro Tag
				 * automatisch repariert
				 */
				double strukt = getStruktur() * STRUKT_REPARATUR_PRO_RUNDE;
				setCurrentStruktur(new Double(getCurrentStruktur().doubleValue() + strukt));
			}
		}
	}

	public void doYearlyWork(CivYearReport yearValues)
	{
	}
}