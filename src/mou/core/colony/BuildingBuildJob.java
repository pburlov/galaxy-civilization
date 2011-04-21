/*
 * $Id: BuildingBuildJob.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.colony;

import java.util.Map;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.res.ResourceMenge;
import mou.core.res.colony.BuildingAbstract;
import mou.core.research.ResearchableDesign;

public class BuildingBuildJob extends BuildJobAbstract
{

	static final private String ATTR_BUILDING_DATA = "ATTR_BUILDING_DATA";
	private ResearchableDesign<BuildingAbstract> design;

	public BuildingBuildJob(Map data)
	{
		super(data);
	}

	public BuildingBuildJob(ResearchableDesign<BuildingAbstract> design)
	{
		this(design, true);
	}

	public BuildingBuildJob(ResearchableDesign<BuildingAbstract> design, boolean showMessage)
	{
		super(TYP_BUILDING, showMessage);
		setAttribute(ATTR_BUILDING_DATA, design.getObjectData());
		this.design = design;
	}
	
	public ResearchableDesign<BuildingAbstract> getBuilding()
	{
		if(design != null) return design;
		return new ResearchableDesign<BuildingAbstract>((Map) getAttribute(ATTR_BUILDING_DATA));
	}

	@Override
	public String getName()
	{
		return getBuilding().getName();
	}

	@Override
	public double computeNeededWorkPoints()
	{
		return getBuilding().computeWorkCost();
	}

	@Override
	public BuildAllowed startBuild(Colony colony)
	{
		BuildingAbstract building = (BuildingAbstract) getBuilding().getResearchableResource();
		building.setColonyID(colony.getID());
		
		/* Wenn Gebäude schon auf Planeten vorhanden, maximale Baugröße beachten */
		ResearchableDesign colonyDesign = colony.getBuilding(getBuilding().getID());
		int colonyBuildingSize = 0;
		int maxSize = (int) Math.ceil(building.getMaxSize());
		if(colonyDesign != null)
		{
			colonyBuildingSize = colonyDesign.getResearchableResource().getSize().intValue();
		}
		if(maxSize != -1)
		{
			maxSize -= colonyBuildingSize; 
		}
		if(maxSize <= 0)
			return new BuildAllowed(false,building.getNoBuildMessage());
		if(building.getSize().intValue()> maxSize)
			building.setSize(maxSize);

		/* Auf genügen Rohstoffe prüfen */
		for(ResourceMenge menge : building.getNeededRes())
		{
			if(getMOUDB().getStorageDB().getMenge(menge.getRessource().getID()).doubleValue() < menge.getMenge()) { return new BuildAllowed(false,
					"Unzureichende Menge von " + menge.getRessource().getName()); }
		}
		
		/*
		 * ### Benötigte Ressource abziehen ###
		 */
		for(ResourceMenge menge : building.getNeededRes())
		{
			getMOUDB().getStorageDB().takeMenge(menge.getRessource().getID(), (int) menge.getMenge());
		}
		return new BuildAllowed(true, "");
	}

	@Override
	public void cancelBuild(Colony colony)
	{
		NaturalRessourcesStorageDB storage = getMOUDB().getStorageDB();
		long putBack = 0;
		/*
		 * Abgezogene Materialien zurückgeben
		 */
		for(ResourceMenge menge : getBuilding().getResearchableResource().getNeededRes())
		{
			putBack = (long) (menge.getMenge()*Math.min(MAX_PUT_BACK, 1 - getProgress()));
			storage.addMenge(menge.getRessource().getID(), putBack , false);
		}
	}

	@Override
	public void completeBuild(Colony colony)
	{
		colony.addBuilding(getBuilding());
	}
}
