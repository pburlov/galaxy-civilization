/*
 * $Id: StarmapDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.starmap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import mou.Universum;
import mou.core.db.AbstractDB;

/**
 * Speicher dynamische Informationen zu der Sternenkarte. Z.B Systemnamen, Resourcenmenge auf den
 * Planeten
 */
public class StarmapDB extends AbstractDB
{

	static final private String ATTR_NAVPOINTS = "ATTR_NAVPOINTS";
	private static final Universum universum = new Universum();

	// Hier werden nur DynamicStarStystem abgespeichert, bei denen etwas verändert wurde. Z.B Name
	// private Hashtable starmap = new Hashtable(); // Key: StarSystemID, Value: DynamicStarSystem
	public StarmapDB(Hashtable data)
	{
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBName()
	 */
	public String getDBName()
	{
		return "StarmapDB";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.AbstractDB#getDBObiectImplClass()
	 */
	protected Class getDBObiectImplClass()
	{
		return null;
	}

	/**
	 * Liefert die Liste mit StarSystem-Objekten, die sich in dem spezifizierten Ausschnitt der
	 * Sternenkarte befinden
	 */
	protected List getStaticStarsSystemsInPolygon(Point leftUpper, Point rightDown)
	{
		return universum.getStarsInArea(leftUpper, rightDown);
	}

	/**
	 * Liefert eine zufällige Starmap Koordinate mit einem Stern
	 * 
	 * @return
	 */
	public Point getRandomStarPosition()
	{
		return universum.getRandomStarPosition();
	}

	/**
	 * Liefert Liste mit StarSystem-Objecten die sich in diesem Kartenausschnitt befinden.
	 * 
	 * @param leftUpper
	 * @param rightDown
	 * @return
	 */
	public List getStarSystemsInPolygon(Point leftUpper, Point rightDown)
	{
		// List ret = new ArrayList();
		// Iterator iter = universum.getStarsInArea(leftUpper, rightDown).iterator();
		// while(iter.hasNext())
		// {
		// StarSystem star = (StarSystem)iter.next();
		// ret.add(star);
		// }
		// return ret;
		return universum.getStarsInArea(leftUpper, rightDown);
	}

	/**
	 * Liefert StarSystem-Object, das Stern bei dieser Position beschreibt
	 * 
	 * @param position
	 * @return Starsystem oder null wenn keine Stern bei dieser Position
	 */
	public StarSystem getStarSystemAt(Point position)
	{
		StarSystem ss = universum.generateStar(position);
		if(ss == null) throw new IllegalStateException("Kein Stern an dieser Stelle");
		String name = getStarsystemName(position);
		if(name != null) ss.setName(name);
		return ss;
	}

	public String getStarsystemName(Point pos)
	{
		String name = (String) getSecondaryMapData().get(pos);
		return name;
	}

	public void setStarsystemName(Point pos, String name)
	{
		getSecondaryMapData().put(pos, name);
	}

	public boolean isStarsystemVisited(Point pos)
	{
		return getStarsystemName(pos) != null;
	}

	public void markAsVisited(Point pos)
	{
		if(getStarsystemName(pos) != null) return;
		setStarsystemName(pos, getStarSystemAt(pos).getName());
	}

	public List<Navpoint> getNavpoints()
	{
		Map<Point, String> data = (Map<Point, String>) getSecondaryMapData().get(ATTR_NAVPOINTS);
		if(data == null) return Collections.EMPTY_LIST;
		List<Navpoint> ret = new ArrayList<Navpoint>(data.size());
		for(Map.Entry<Point, String> entry : data.entrySet())
			ret.add(new Navpoint(entry.getKey(), entry.getValue()));
		return ret;
	}

	public void deleteNavpoint(Point pos)
	{
		Map<Point, String> data = (Map<Point, String>) getSecondaryMapData().get(ATTR_NAVPOINTS);
		if(data == null) return;
		data.remove(pos);
	}

	public void addNavpoint(Point pos, String comment)
	{
		Map<Point, String> data = (Map<Point, String>) getSecondaryMapData().get(ATTR_NAVPOINTS);
		if(data == null)
		{
			data = new HashMap<Point, String>();
			getSecondaryMapData().put(ATTR_NAVPOINTS, data);
		}
		data.put(pos, comment);
	}
}