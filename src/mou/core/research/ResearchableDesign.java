/*
 * $Id: ResearchableDesign.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.research;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import mou.Main;
import mou.core.IDable;
import mou.core.MapWrapper;
import mou.core.res.ResearchableResource;
import mou.storage.ser.ID;

/**
 * Repräsentiert ein "erforschtes" Schiffsystem oder Gebäude. Dient lediglich als Zwischenschicht,
 * das nötige Klasseninstanz des erforschten System initialisiert
 * 
 * @author pbu
 */
public class ResearchableDesign<O extends ResearchableResource> extends MapWrapper
		implements IDable
{

	static final private String ATTR_DESC_ID = "DESC_ID";
	static final private String ATTR_ID = "ID";
	static final private String RESEARCHABLE_DATA = "RESEARCHABLE_DATA";
	static final private String NAME = "NAME";
	private O researchableResource;

	/**
	 * 
	 */
	public ResearchableDesign(Map data)
	{
		super(data);
	}

	public ResearchableDesign(O res, String name)
	{
		super(new HashMap());
		/*
		 * Wenn diese ResearchableResource erforschbar ist, dann muss eine eindeutige ID generiert
		 * werden weil es mehrere Designs eines Resourcen-Typs geben kann. Wenn Resource nicht
		 * erforschbar dann wird als Design-ID die ResearchableResource-ID genommen, weil dann nur
		 * ein möglicher Design existieren kann.
		 */
		if(res.isResearchAllowed())
			setAttribute(ATTR_ID, new ID());
		else
			setAttribute(ATTR_ID, res.getID());
		setAttribute(ATTR_DESC_ID, res.getID());
		setAttribute(RESEARCHABLE_DATA, res.getObjectData());
		setName(name);
	}

	private Map getResearchableData()
	{
		Map ret = (Map) getAttribute(RESEARCHABLE_DATA);
		return ret;
	}

	public double computeWorkCost()
	{
		return getResearchableResource().computeBuildCost();
	}

	public double computeSupportCost()
	{
		return getResearchableResource().computeSupportCost();
	}

	public double computeEnergy()
	{
		return getResearchableResource().computeEnergyBalance();
	}

	public double computeCrew()
	{
		return getResearchableResource().computeNeededCrew();
	}

	/**
	 * @return Collection mit ResourceMenge Objekten
	 */
	public Collection computeNeededResources()
	{
		return getResearchableResource().getNeededRes();
	}

	public String toString()
	{
		return getName();
	}

	public ID getResearchableDescriptionID()
	{
		return (ID) getAttribute(ATTR_DESC_ID);
	}

	public O getResearchableResource()
	{
		if(researchableResource == null)
		{
			O ss = (O) Main.instance().getMOUDB().getResearchDB().getResearchableResource(getResearchableDescriptionID());
			ss.initWithData(getResearchableData());
			ss.setDesignID(getID());
			researchableResource = ss;
		}
		return researchableResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IRessourceDescription#getDescriptionShort()
	 */
	public String getDescriptionShort()
	{
		return getResearchableResource().getShortDescription();
	}

	/**
	 * Liefert kurze Zusatzinformation, die mit hilfe von ListCellRenderer mitangezeigt werden soll.
	 * 
	 * @return
	 */
	public String getExtendenInfoForListCellRenderer()
	{
		return getResearchableResource().getExtendenInfoForListCellRenderer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IRessourceDescription#getIcon()
	 */
	public ImageIcon getIcon()
	{
		return getResearchableResource().getIcon();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IRessourceDescription#getName()
	 */
	public String getName()
	{
		String ret = (String) getAttribute(NAME);
		if(ret == null) ret = getResearchableResource().getName();
		return ret;
	}

	/**
	 * Setzt Name für diesen Design
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		setAttribute(NAME, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.IDable#getID()
	 */
	public ID getID()
	{
		return (ID) getAttribute(ATTR_ID);
	}
}
