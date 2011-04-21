/*
 * $Id: Universum.java 10 2006-03-25 09:35:17Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import mou.core.starmap.StarSystem;
import burlov.util.CRC32Ex;

/**
 * Generiert Sternendaten abhängig von der Position des Sterns. Und so modeliert ein unendliches
 * Raum voller Sterne
 */
public strictfp class Universum
{

	static final public int QUADRANT_SIZE = 100;// Kantenlänge des Quandrantes in LJ
	static final public int GALAXY_RADIUS = 2000;
	static final private int MAX_STERNE = 70;// Maximal Anzahl der Sterne pro Quadrant
	/*
	 * Bestimmt den Areal in welchen Sterne als Startpositionen für neue Zivilisationen ausgewählt
	 * werden. Der Wert bedeutet Anzahl der Quadranten in jeder Richtung vom Punkt [0:0] aus.
	 */
	static final private int RANDOM_STAR_POSITION_AREA = 1;
	// private static final int MIN_STERNE = 5;//Minimale Anzahl der Sterne pro Quadrant
	// private Hashtable mapPointToQuadrant = new Hashtable(1000000);
	// private Hashtable mapPointToStaticStarSystem = new Hashtable(0);
	// private MessageDigest digest;
	// private Adler32 adler32 = new Adler32();
	private CRC32Ex crc32 = new CRC32Ex();
	// final private byte[] buf = new byte[8];
	private Random rnd = new Random();

	public Universum()
	{
		/*
		 * try { digest = MessageDigest.getInstance("SHA1"); }catch(Exception e) {
		 * Main.instance().getMOUDB().getLogger().log(Level.SEVERE, "Kann MessageDigest nicht
		 * initialisieren: "+ e.getLocalizedMessage(),e); }
		 */
	}

	/**
	 * Generiert jedes Mal die Sterne im Kartenabschnitt neu.
	 */
	final public List<StarSystem> getStarsInArea(Point upperLeft, Point downRight)
	{
		List<Point> points = getPointsInArea(upperLeft, downRight);
		List<StarSystem> ret = new ArrayList<StarSystem>(points.size());
		for(Point point : points)
		{
			ret.add(generateStar(point));
		}
		return ret;
	}

	/**
	 * Liefert eine zufällige Starmap Koordinate mit einem Stern
	 * 
	 * @return
	 */
	synchronized public Point getRandomStarPosition()
	{
		List<Point> points = getPointsInArea(new Point(-QUADRANT_SIZE * RANDOM_STAR_POSITION_AREA, QUADRANT_SIZE * RANDOM_STAR_POSITION_AREA), new Point(
				QUADRANT_SIZE * RANDOM_STAR_POSITION_AREA, -QUADRANT_SIZE * RANDOM_STAR_POSITION_AREA));
		rnd.setSeed(System.currentTimeMillis());
		int index = rnd.nextInt(points.size());
		return points.get(index);
	}

	/**
	 * Generiert StaticStarSystem Object für eine Position. ACHTUNG! Es wird auf jeden Fall ein Star
	 * generiert, obwohl die von der MEthode getStarsInArea(..) gelieferte Liste an dieser Position
	 * keinen Stern enthalten kann.
	 */
	synchronized final public StarSystem generateStar(Point pos)
	{
		StarSystem star = null;// getCachedStar(pos);
		if(star != null) return star;
		long seed = generateSeed(pos.x, pos.y);// generateSeed_SHA1(x, y);
		star = mou.core.starmap.StarSystem.generateStarSystem(pos.x, pos.y, seed);
		// setCachedStar(pos, star);
		return star;
	}

	// private void setCachedStar(Point pos, StaticStarSystem star)
	// {
	// SoftReference ref = new SoftReference(star);
	// mapPointToStaticStarSystem.put(pos, ref);
	// }
	//	
	// final private StaticStarSystem getCachedStar(Point pos)
	// {
	// SoftReference ref = (SoftReference)mapPointToStaticStarSystem.get(pos);
	// if(ref == null)return null;
	// StaticStarSystem star = (StaticStarSystem)ref.get();
	// return star;
	// }
	//	
	/**
	 * Liefert eine Liste mit Point-Objecten, die Sterne in einem Area repräsentieren
	 * 
	 * @param upperLeft
	 * @param downRight
	 * @return
	 */
	synchronized final public List<Point> getPointsInArea(Point upperLeft, Point downRight)
	{
		ArrayList<Point> ret = new ArrayList<Point>(100);
		for(Point quadrant : getQuadrantsForArea(upperLeft, downRight))
		{
			Set<Point> starPoints = getStarPointsInQuadrant(quadrant);
			for(Point star : starPoints)
			{
				if(upperLeft.x <= star.x && downRight.x > star.x && upperLeft.y >= star.y && downRight.y < star.y) ret.add(star);
			}
		}
		return ret;
	}

	/**
	 * Methode generiert Set mit Positionen der Sternen in einem Quadrant.
	 * 
	 * @param quadrant
	 * @return
	 */
	synchronized public Set<Point> getStarPointsInQuadrant(Point quadrant)
	{
		// SoftReference ref = (SoftReference)mapPointToQuadrant.get(quadrant);
		HashMap<Point, Point> cachedQuadrant = null;
		// if(ref != null)cachedQuadrant = (HashMap)ref.get();
		// if(cachedQuadrant == null)
		// {//Kein Quadrant im Cache
		rnd.setSeed(generateSeed(quadrant.x, quadrant.y));
		int sterne = computeNumberOfStars(quadrant);// rnd.nextInt(MAX_STERNE -
													// MIN_STERNE)+MAX_STERNE;
		cachedQuadrant = new HashMap<Point, Point>(MAX_STERNE);
		for(; sterne > 0; sterne--)
		{
			Point point = new Point(rnd.nextInt(QUADRANT_SIZE) + quadrant.x, quadrant.y - rnd.nextInt(QUADRANT_SIZE));
			cachedQuadrant.put(point, null);
		}
		// ref = new SoftReference(cachedQuadrant);
		// mapPointToQuadrant.put(quadrant, ref);
		// }
		return cachedQuadrant.keySet();
	}

	private int computeNumberOfStars(Point pos)
	{
		int number = (int) (MAX_STERNE - ((Point2D.distance(0, 0, pos.getX(), pos.getY()) / GALAXY_RADIUS) * MAX_STERNE));
		if(number < 0) number = 0;
		return number;
	}

	final private long generateSeed(int x, int y)
	{
		// return generateSeed_Adler32(x,y);
		return generateSeed_CRC32(x, y);
		// return generateSeed_SHA1(x,y);
	}

	/**
	 * Generiert Seed für den Random Generator aus Kartekoordinaten mit Hilfe von CRC32 Checksum.
	 * Gute Kombination aus der Wertstreuung und der Geschwindigkeit
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	final private long generateSeed_CRC32(int x, int y)
	{
		crc32.reset();
		crc32.updateInt(x);
		crc32.updateInt(y);
		return crc32.getValue();
	}

	/**
	 * Generiert Seed für den Random Generator aus Kartekoordinaten mit Hilfe von Adler32 Checksum.
	 * Sehr schnell aber die Werstreuung ist nicht akzeptabel. (regelmäsige Sternenkonstellationen
	 * werden gebildet)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	/*
	 * final private long generateSeed_Adler32(int x, int y) { adler32.reset();
	 * adler32.update(copyToArray(x,y)); return adler32.getValue(); }
	 */
	/*
	 * final private byte[] copyToArray(int x, int y) { for(int i = 0; i < 4; i++) { buf[i] =
	 * ((byte)(x & 0xFF)); x = x >> 8; } for(int i = 4; i < 8; i++) { buf[i] = ((byte)(y & 0xFF)); y =
	 * y >> 8; } return buf; }
	 */
	/**
	 * Generiert Seed aus Kartenkoordinaten mit Hilfe von SHA-1 Message Digest. Sehr zuverlässig
	 * (Wertstreuung) aber auch sehr langsam.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	/*
	 * final private long generateSeed_SHA1(int x, int y) { digest.reset(); digest.update((byte)(x &
	 * 0xFF)); x = x >> 8; digest.update((byte)(x & 0xFF)); x = x >> 8; digest.update((byte)(x &
	 * 0xFF)); x = x >> 8; digest.update((byte)(x & 0xFF)); digest.update((byte)(y & 0xFF)); y = y >>
	 * 8; digest.update((byte)(y & 0xFF)); y = y >> 8; digest.update((byte)(y & 0xFF)); y = y >> 8;
	 * digest.update((byte)(y & 0xFF)); byte[] result = digest.digest(); long seed = result[0]; seed =
	 * seed << 8; seed = seed + result[1]; seed = seed << 8; seed = seed + result[2]; seed = seed <<
	 * 8; seed = seed + result[3]; seed = seed << 8; seed = seed + result[4]; seed = seed << 8;
	 * seed = seed + result[5]; seed = seed << 8; seed = seed + result[6]; seed = seed << 8; seed =
	 * seed + result[7]; return seed; }
	 */
	static public Point getQuadrantForPosition(Point pos)
	{
		return getQuadrantForPosition(pos, 1);
	}

	/**
	 * Quadranten werden durch ihre linke obere Ecke addressiert
	 * 
	 * @param pos
	 *            beliebige Koordinate auf der Sternenkarte
	 * @param multiplikator
	 *            bestimmt Große des zu beachtende Quadrantes. Dabei wird die QUADRANT_SIZE mit dem
	 *            Multiplikator multipliziert, und so wird die Kantenlänge des Quadrantes ermittelt
	 * @return Koordinaten des Quadrantes wo sich dieser Punkt befindet
	 */
	static public Point getQuadrantForPosition(Point pos, int multiplikator)
	{
		if(multiplikator < 1) multiplikator = 1;
		int kante = QUADRANT_SIZE * multiplikator;
		int pX = pos.x;
		int pY = pos.y;
		// Damit die Quadrantgrenzen im X-Minusbereich genauso
		// werden wie im X-Plusbereich
		if(pX < 0) pX++;
		int qX = (int) (pX / kante);
		if(pos.x < 0) qX--;
		qX = qX * kante;// reale Koordinaten ausrechnen
		// ###### Y-Koordinate ausrechnen #######
		// Damit die Quadrantgrenzen im Y-Plusbereich genauso
		// werden wie im Y-Minusbereich
		if(pY > 0) pY--;
		int qY = (int) (pY / kante);
		if(pos.y > 0) qY++;
		qY = qY * kante;
		return new Point(qX, qY);
	}

	static public List<Point> getQuadrantsForArea(Point leftUpper, Point rightDown)
	{
		return getQuadrantsForArea(leftUpper, rightDown, 1);
	}

	/**
	 * Liefert Liste mit Point-Objekten, die einzelne Quadranten addressieren die sich innerhalb der
	 * gegebenen Koordinaten befinden
	 * 
	 * @param leftUpper
	 * @param rightDown
	 * @param multiplikator
	 *            bestimmt die Große der Quadranten. QUADRANT_SIZE wird mit dem Multiplikator
	 *            multipliziert, und so die Kantenlänge des Quadrantes berechnet.
	 * @return List mit Point-Objekten
	 */
	static public List<Point> getQuadrantsForArea(Point leftUpper, Point rightDown, int multiplikator)
	{
		if(multiplikator < 1) multiplikator = 1;
		Point start = getQuadrantForPosition(leftUpper, multiplikator);
		Point end = getQuadrantForPosition(rightDown, multiplikator);
		List<Point> ret = new ArrayList<Point>();
		int kante = QUADRANT_SIZE * multiplikator;
		for(int x = start.x; x <= end.x; x = x + kante)
		{
			for(int y = start.y; y >= end.y; y = y - kante)
			{
				ret.add(new Point(x, y));
			}
		}
		return ret;
	}
}