/*
 * $Id: ResourceAbstract.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res;

import java.util.Hashtable;
import java.util.Map;
import javax.swing.ImageIcon;
import mou.core.IDable;
import mou.core.MapWrapper;
import mou.gui.GUI;
import mou.storage.ser.ID;
import org.apache.commons.lang.StringUtils;

/**
 * Superklasse aller Ressourcen
 * 
 * @author pb
 */
abstract public class ResourceAbstract extends MapWrapper
		implements IDable, Comparable<Object>
{

	static final public Number ZERO_LONG = new Long(0);
	/*
	 * Um den Überblick um alle IDs zu behalten, werden sie alle hier in der Basisklasse definiert
	 */
	static final public ID ID_BUILDING_COLONY_CENTER = new ID(2000, 0);// Basiseinrichtung für die
																		// industrielle Produktion
	static final public ID ID_BUILDING_COLONY_CENTER_MATERIAL_STORAGE = new ID(2000, 1); //Materiallager für ColonyCenter
	static final public ID ID_BUILDING_HARVESTER = new ID(2001, 0);// Baut Resourcen ab
	static final public ID ID_BUILDING_FACTORY = new ID(2002, 0);
	static final public ID ID_BUILDING_RESEARCH_CENTER = new ID(2003, 0);
	static final public ID ID_BUILDING_HABITAT = new ID(2004, 0);
	static final public ID ID_BUILDING_FARM = new ID(2005, 0);
	static final public ID ID_BUILDING_SILO = new ID(2006, 0); //Getreidesilo, lagert Lebensmittel
	static final public ID ID_BUILDING_MATERIAL_STORAGE = new ID(2007, 0); //MaterialLager
	
	static final public ID ID_SHIPSYSTEM_ENERGYGENERATOR = new ID(1200, 0);
	static final public ID ID_SHIPSYSTEM_HYPERDRIVE = new ID(1201, 0);
	static final public ID ID_SHIPSYSTEM_WEAPON = new ID(1203, 0);
	static final public ID ID_SHIPSYSTEM_ARMOR = new ID(1205, 0);
	static final public ID ID_SHIPSYSTEM_SHILD = new ID(1206, 0);
	static final public ID ID_SHIPSYSTEM_LIFE_SUPPORT = new ID(1207, 0);
	static final public ID ID_SHIPSYSTEM_ZIELSYSTEM = new ID(1208, 0);
	static final public ID ID_SHIPSYSTEM_ANTIZIELSYSTEM = new ID(1209, 0);
	static final public ID ID_SHIPSYSTEM_COLONYMODUL = new ID(1210, 0);
	/**
	 * Speichert bereits geladene Image-Date für die Ressource-Icons Key: Image path (String);
	 * Value: ImageIcon
	 */
	static final private Hashtable imageCache = new Hashtable(100);
	static private DefaultResourceUISmall uiSmall = new DefaultResourceUISmall();

	/**
	 * 
	 */
	public ResourceAbstract()
	{
		super();
	}

	public ResourceAbstract(Map data)
	{
		super(data);
	}

	// /**
	// * Hilfsmethode zum Laden einer Image von der Festplatte
	// * @param path
	// * @return
	// */
	// protected Image loadImage(String path)
	// {
	// Image ret = null;
	// try
	// {
	// ImageIO.setUseCache(false);
	// ret = ImageIO.read(new File(path));
	// }
	// catch (IOException e)
	// {
	// // Main.instance().logThrowable("Fehler beim Lader der Image: "+path,e);
	// Main.instance().severeErrorOccured(e,"Fehler beim Laden der Image: "+path,true);
	// }
	// return ret;
	// }
	public ImageIcon getIcon()
	{
		String path = getImagePath();
		if(StringUtils.isEmpty(path)) return new ImageIcon();
		ImageIcon image = (ImageIcon) imageCache.get(path);
		if(image == null)
		{
			// URL url = GUI.loadImage(path);
			image = new ImageIcon();
			image.setImage(GUI.loadImage(path));
			imageCache.put(path, image);
		}
		return image;
	}

	/**
	 * Liefert RessourceUIAbstract-Objekt um diese Ressource in Kurzform grafisch darzustellen
	 * 
	 * @return
	 */
	static public ResourceUI getResourceUISmall()
	{
		return uiSmall;
	}

	// /**
	// * Liefert ein grafische Element zur Kurzdarstllung dieser Resource.
	// * Normaleweise ein JLabel mit dem Ressourcenname und Icon
	// * @return
	// */
	// abstract public JComponent getUIComponentSmall();
	//	
	/**
	 * @return
	 */
	abstract public String getImagePath();

	abstract public String getName();

	abstract public String getShortDescription();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ResourceAbstract)) return false;
		return getID().equals(((ResourceAbstract) obj).getID());
	}

	public int compareTo(Object o)
	{
		return toString().compareToIgnoreCase(o.toString());
	}
}
