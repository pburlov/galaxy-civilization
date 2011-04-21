/*
 * $Id$
 * Created on 07.05.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.ColonyCenter;

import mou.core.res.ResearchableResource;
import mou.core.res.colony.MaterialStorage.MaterialStorage;
import mou.storage.ser.ID;


/**
 * @author Dominik
 *
 */
public class ColonyCenterMaterialStorage extends MaterialStorage
{

	/**
	 * 
	 */
	public ColonyCenterMaterialStorage()
	{
		super();
	}
	
	@Override
	public long computeCapacity(ID id)
	{
		if(getActivFactor() == 0) return 0;
		long ret = ColonyCenter.NORMALIZED_STORAGE * getParent().getSize().longValue();
		
		ret = Math.min(ret, ColonyCenter.MAX_STORAGE);
		return ret;
	}
	
	@Override
	public long computeTotalCapacity()
	{
		if(getActivFactor() == 0) return 0;
		long ret = ColonyCenter.NORMALIZED_STORAGE * getParent().getSize().longValue();
		
		ret = Math.min(ret, ColonyCenter.MAX_STORAGE);
		ret *= getStorageData().size();
		
		return ret;
	}
	
	public ColonyCenter getParent()
	{
		return ((ColonyCenter) getColony().getBuilding(ResearchableResource.ID_BUILDING_COLONY_CENTER).getResearchableResource());
	}
	
	@Override
	public boolean isResearchAllowed()
	{
		return false;
	}
	
	@Override
	public ID getID()
	{
		return ID_BUILDING_COLONY_CENTER_MATERIAL_STORAGE;
	}
}
