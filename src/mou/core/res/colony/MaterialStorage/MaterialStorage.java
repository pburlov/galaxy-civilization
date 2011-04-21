/*
 * $Id$
 * Created on 03.05.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.MaterialStorage;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import mou.Main;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.civilization.NaturalRessourcesStorageItem;
import mou.core.colony.Colony;
import mou.core.res.ResourceAbstract;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.core.res.natural.NaturalResource;
import mou.core.research.ResearchableDesign;
import mou.gui.GUI;
import mou.storage.ser.ID;


/**
 * @author Dominik
 *
 */
public class MaterialStorage extends BuildingAbstract
{
	protected NaturalRessourcesStorageDB storage = Main.instance().getMOUDB().getStorageDB();
	
	static final private double INITIAL_STORAGE_SIZE = 200000;
	static final private long INITIAL_MAX_SIZE = 10;
	static final private double FACT_A = 3;
	static final protected String ATTR_STORAGE="ATTR_STORAGE";
	
	public MaterialStorage()
	{
		super();
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
	}

	@Override
	protected BuildingUiAbstract getBuildingUiIntern()
	{
		return new MaterialStorageUI(this);
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		return "<b>Lagergröße: </b>" + GUI.formatSmartDouble(computeTotalCapacity()/1E6)+"mio T";
	}
	
	/* Info über alle gelagerten Ressourcen als HTMLTable */
	public String getMaterialStoredHTMLInfo()
	{
		StringBuilder ret = new StringBuilder("<html><table>");
		
		for(ID id : getStorageData().keySet())
		{
			ret.append("<tr><td><b>");
			ret.append(Main.instance().getMOUDB().getNaturalRessourceDescriptionDB().getNaturalResource(id).getName());
			ret.append(": </b></td><td>");
			ret.append(GUI.formatSmartDouble(getMaterial(id).doubleValue()/1E6));
			ret.append("/");
			ret.append(GUI.formatSmartDouble(computeCapacity(id)/1E6));
			ret.append(" mio T");
			ret.append("</td></tr>");
		}
		ret.append("</table></html>");

		return ret.toString();
	}


	@Override
	public JComponent getScienceViewComponent()
	{
		return new MaterialStorageScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Lagerhalle";
	}

	@Override
	public String getShortDescription()
	{
		return "Ein Lager für Rohstoffe";
	}

	public ID getID()
	{
		return ID_BUILDING_MATERIAL_STORAGE;
	}
	
	@Override
	public String getImagePath()
	{
		return "/res/images/lager.png";
	}
	
	@Override
	public void cleanUp()
	{
		super.cleanUp();
		long rest;
		
		for(ID id : getStorageData().keySet())
		{
			rest = getMaterial(id).longValue();
			/* Rohstoffe auf andere Lagerhäuser der Kolonie verteilen */
			rest -= getColony().addMaterialToStorage(id, rest);
			/* Rest als Oversize lagern */
			overSize(id, rest);
		}
	}

	//Inizialisierung des Speicherobjektes
	@Override
	public void setColonyID(ID id)
	{
		super.setColonyID(id);
		int resSize = getColony().getNaturalResourcesSize();
		double[] utilizationFaktors = new double[resSize];
		
		Map<ID, Number> data = Collections.synchronizedMap(new TreeMap<ID, Number>());
		setAttribute(ATTR_STORAGE, data);
		
		int i=0;
		for(NaturalResource res : getColony().getStarSystem().getNatRessources())
		{
			data.put(res.getID(), 0);
			utilizationFaktors[i] = 1d/resSize;
			i++;
		}
		
		setUtilizationFactors(utilizationFaktors);
		
	}
	
	@Override
	public double computeCustomValue(int number)
	{
		double stufe = getMaterialien().size();
		double ret;
		
		switch (number)
		{
			/* Erster Wert: Lagergröße */
			case 1:
				ret = INITIAL_STORAGE_SIZE * (1+ FACT_A *Math.log(stufe));
				ret *= getQualityFaktor().doubleValue()/4;
				return ret;
			/* Zweiter Wert: Maximale Lagergröße */
			case 2:
				ret = Math.ceil(INITIAL_MAX_SIZE *  Math.pow(1.5, stufe-1));
 				return ret;
		}
		
		/* Unknown, use default value */
		return computeCustomValue();
	}
	
	@Override
	public int getCustomCounter()
	{
		return (int) (computeCustomValue(1)/10000);
	}
	
	/* Liefert die Zahl aller anderen gebauten Lagerhallen */
	private int getOtherStorageBuildingCount()
	{
		int ret = 0;
		
		for(ResearchableDesign<BuildingAbstract> building : getColony().getBuildingsFromType(ResourceAbstract.ID_BUILDING_MATERIAL_STORAGE))
		{
			if(getDesignID().equals(building.getID()))
				continue;
			
			ret += building.getResearchableResource().getSize().intValue();
		}
		
		return ret;
	}
	
	@Override
	public double getMaxSize()
	{
		if(getColony().getNaturalResourcesSize() <= 0)	return 0;	//Kolonie hat kein Bergbau
		return (long) computeCustomValue(2) - getOtherStorageBuildingCount();
	}
	
	@Override
	public String getNoBuildMessage()
	{
		if(getColony().getNaturalResourcesSize() <= 0)
			return "Keine Rohstoffe zum Abbau vorhanden";
		return super.getNoBuildMessage();
	}
	
	public long getNormalizedStorage()
	{
		return (long) Math.floor(computeCustomValue(1));
	}
	
	public long computeTotalCapacity()
	{
		return getNormalizedStorage()*getSize().longValue();
	}
	
	public long computeTotalStored()
	{
		long ret = 0;
		
		for(ID id : getStorageData().keySet())
			ret += getMaterial(id).longValue();
		
		return ret;
	}
	
	public Map<ID, Number> getStorageData()
	{
		// Wird bei Initialisierung des Gebäudes mit setColonyID erzeugt und nicht mehr gelöscht
		return (Map<ID, Number>) getAttribute(ATTR_STORAGE);
	}
	
	/**
	 * @return # of stored Materials with ID id
	 */
	public Number getMaterial(ID id)
	{
		if(!getStorageData().containsKey(id))
			return 0;
		
		Number ret = getStorageData().get(id);
		long storageSize = computeCapacity(id);
		long rest = ret.longValue()-storageSize;
		
		if(ret.longValue() > storageSize)
		{
			setMaterial(id, storageSize);
			/* Material auf andere Lagerhäuser verteilen */
			rest -= getColony().addMaterialToStorage(id, rest);
			overSize(id, rest);
			
			ret = storageSize;
		}
			
		return ret;
	}
	
	/**
	 * Behandelt Rohstoffe, die nicht mehr ins Lager passen (zum Beispiel durch größenanpassung)
	 * Es werden nur so viele Rohstoffe auf andere Planeten übertragen, daß eine Grundversorgung mit
	 * MAX_OVERSIZE sichergestellt ist
	 * 
	 * @param id
	 * @param amount
	 */
	private void overSize(ID id, long amount)
	{
		long maxOversize = NaturalRessourcesStorageItem.MAX_OVERSIZE - computeCapacity(id);
		
		if(maxOversize <= 0) return;
		if(amount >= maxOversize) amount = maxOversize;
		
		storage.addMenge(id, amount, true);
	}

	/**
	 * 
	 * @param amount amount of Material to add to or take from the storage
	 * @return amount of Material actually added to or taken from the storage
	 */
	public long addMaterial(ID id, long amount)
	{
		if(!getStorageData().containsKey(id))
			return 0;
		if(cleanup)	return 0;
		
		long ret = 0;
		
		/* oversize ins Lagerhaus importieren */		
		synchronized(storage.getLockObject())
		{
			long oversize = storage.getOversizeMenge(id);

			if(oversize <= computeFreeSpace(id))
			{
				/* Oversize wird vollständig ins Lager, danach eventuell noch freier Platz */
				setMaterial(id, getMaterial(id).longValue()+ oversize);
				storage.setOversizeMenge(id, 0);
			}else
			{
				/* Lager wird vollständig mit oversize gefüllt, es wird nichts eingelagert */
				storage.setOversizeMenge(id, storage.getOversizeMenge(id)- computeFreeSpace(id));
				setMaterial(id, computeCapacity(id));
				return 0;
			}
		}
		
		if(amount == 0) return 0;
		
		/* hinzufügen oder abziehen? */
		if(amount > 0)
		{
			/* Lagerplatz überprüfen */
			if(amount <= computeFreeSpace(id))
			{
				ret = amount;
				setMaterial(id, getMaterial(id).longValue()+ amount);
			}else
			{
				ret = computeFreeSpace(id);
				setMaterial(id, computeCapacity(id));
			}
		}else
		{
			/* Nahrungsvorrat prüfen */
			if(-amount <= getMaterial(id).longValue())
			{
				ret = amount;
				setMaterial(id, getMaterial(id).longValue()+ amount);
			}else
			{
				/* Minus, weil Menge entnommen wird! */
				ret = -getMaterial(id).longValue();
				setMaterial(id, 0);
			}
		}
		
		return ret;
	}

	/* Use add to add or remove food from storage */
	protected void setMaterial(ID id, Number amount)
	{
		//getStorageData().put(id, amount);
		if(amount == null || amount.longValue() < 0)
			//Auf keinen Fall entfernen wegen Sortierung
			getStorageData().put(id, 0);
		else if(amount.longValue() <= computeCapacity(id))
			getStorageData().put(id, amount);
		else
			getStorageData().put(id, computeCapacity(id));
	}
	
	public long computeCapacity(ID id)
	{
		if(cleanup) return 0;
		if(!getStorageData().containsKey(id)) return 0;
		
		int i=0;
		for(ID iter : getStorageData().keySet())
		{
			if(iter.equals(id))
				break;
			i++;
		}
		return (int) Math.floor(getUtilizationFactors()[i] * computeTotalCapacity());
	}
	
	public long computeFreeSpace(ID id)
	{
		if(!getStorageData().containsKey(id))
			return 0;
		return computeCapacity(id) - getMaterial(id).longValue();
	}

	public long computeTotalFreeSpace()
	{
		return computeTotalCapacity() - computeTotalStored();
	}
	
	/**
	 * Lagerhaus braucht keine Arbeiter
	 */
	@Override
	public double computeNormalizedCrew()
	{
		return DOUBLE_0;
	}

	@Override
	public double[] getUtilizationValues()
	{
		double[] ret = new double[getStorageData().size()];
		double capacity;
		
		int i=0;
		for(ID id : getStorageData().keySet())
		{
			capacity = computeCapacity(id);
			if(capacity > 0)
				ret[i] = getMaterial(id).doubleValue()/capacity;
			else
				ret[i] = 1;
			i++;			
		}
		
		return ret;
	}

}
