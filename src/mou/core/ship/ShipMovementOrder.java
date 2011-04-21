/*
 * $Id: ShipMovementOrder.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.ship;

import java.awt.Point;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import mou.Main;
import mou.core.db.DBObjectImpl;
import burlov.util.GeomUtil;

/**
 * @author pb
 */
public class ShipMovementOrder extends DBObjectImpl
{

	static final private String ATTR_SHIP_IDS = "ATTR_SHIP_IDS";
	// static final private String ATTR_FLEET_ID = "ATTR_FLEET_ID";
	// static final private String ATTR_ROUTE = "ATTR_ROUTE";
	static final private String ATTR_SPEED = "ATTR_SPEED";
	static final private String ATTR_START = "ATTR_START";
	static final private String ATTR_TARGET = "ATTR_TARGET";
	static final private String ATTR_START_TIME = "ATTR_START_TIME";

	/**
	 * 
	 */
	public ShipMovementOrder()
	{
		super();
	}

	public ShipMovementOrder(List ships, Point start, Point target, Double speed)
	{
		super(new Hashtable());
		Set shipIDs = new HashSet();
		for(Iterator iter = ships.iterator(); iter.hasNext();)
		{
			shipIDs.add(((Ship) iter.next()).getID());
		}
		setAttribute(ATTR_SHIP_IDS, shipIDs, false);
		// setAttribute(ATTR_ROUTE, route, false);
		setAttribute(ATTR_SPEED, speed, false);
		setAttribute(ATTR_START, start, false);
		setAttribute(ATTR_TARGET, target, false);
	}

	public Point getTarget()
	{
		return (Point) getAttribute(ATTR_TARGET);
	}

	public Point getStart()
	{
		return (Point) getAttribute(ATTR_START);
	}

	// public List getRoute()
	// {
	// return (List) getAttribute(ATTR_ROUTE);
	// }
	//
	/**
	 * ID des Schiffen das mit diesem MovementOrder bewegt werden soll
	 * 
	 * @return ID des Schiffes oder null.
	 */
	public Set getShipIDs()
	{
		return (Set) getAttribute(ATTR_SHIP_IDS);
	}

	public Double getSpeed()
	{
		return (Double) getAttribute(ATTR_SPEED);
	}

	// /**
	// * MEthode berechnet anhand der Geschwindigkeit und der verstrichener Zeit
	// * die aktuelle Position der Schiffen
	// * @param currentTime
	// * @return
	// */
	// public Point computeCurrentPos(long currentTime)
	// {
	// long deltaTime = currentTime - getCreateTime().longValue();
	// Point start = getStart();
	// Point target = getTarget();
	// if(deltaTime <= 0)return start;
	// double geflogen = getSpeed().doubleValue() * deltaTime;
	// double distanz = getStart().distance(getTarget());
	// if(distanz <= geflogen)return target;
	// double k = geflogen / (distanz - geflogen);
	// double x = (start.getX() + (k * target.getX())) / (1 + k);
	// double y = (start.getY() + (k * target.getY())) / (1 + k);
	// return new Point((int)Math.round(x), (int)Math.round(y));
	// }
	//	
	/**
	 * true wenn Flügziel erreicht wurde
	 */
	public boolean isArrived()
	{
		long deltaTime = getAppliedTime();
		return computeOverallFlyingTime() <= (double) deltaTime;
	}

	public Long getStartTime()
	{
		return (Long) getAttribute(ATTR_START_TIME);
	}

	public void setStartTime(Long time)
	{
		setAttribute(ATTR_START_TIME, time, false);
	}

	/**
	 * Wie lange der Flüg schon dauert (in Tagen)
	 * 
	 * @return
	 */
	public long getAppliedTime()
	{
		Long createTime = getStartTime();
		if(createTime == null) return 0;
		return Main.instance().getTime() - createTime.longValue();
	}

	/**
	 * Berechnet gesamtdaer des Flüges in Tagen
	 * 
	 * @return
	 */
	public double computeOverallFlyingTime()
	{
		double speed = getSpeed().doubleValue();
		if(speed <= 0) return Integer.MAX_VALUE;
		return getRouteLength() / speed;
	}

	public double getRouteLength()
	{
		return getStart().distance(getTarget());
	}

	/**
	 * Berechnet geflogene Distanz
	 * 
	 * @return
	 */
	public double computeFlyedDistance()
	{
		if(isArrived()) return getRouteLength();
		return getAppliedTime() * getSpeed().doubleValue();
	}

	/**
	 * Berechnet grobe Position auf Sternenkarte des auktuellen Standpunktes
	 * 
	 * @return
	 */
	public Point computeCurrentApproxStarmapPoint()
	{
		if(isArrived()) return getTarget();
		Point pos = GeomUtil.computePosOnLine(getStart(), getTarget(), computeFlyedDistance());
		return pos;
	}
}