/*
 * $Id: ColonyCenter.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony.ColonyCenter;

import java.util.Map;
import javax.swing.JComponent;
import mou.core.colony.Colony;
import mou.core.res.colony.BuildingAbstract;
import mou.core.res.colony.BuildingUiAbstract;
import mou.core.res.colony.MaterialStorage.MaterialStorage;
import mou.core.research.ResearchableDesign;
import mou.gui.GUI;
import mou.storage.ser.ID;

public class ColonyCenter extends BuildingAbstract
{

	/*
	 * Alle Kolonisten werden zum Betrieb des Koloniezentrums benötigt
	 */
	static final private Double NORMALIZED_CREW = new Double(1.0);
	/*
	 * koloniezentrum bietet Platz für 200% Crewgröße des Kolonieschiffes
	 */
	static final private Double NORMALIZED_LIVING_SPACE = new Double(2);
	static final private Double NORMALIZED_SUPPORT_COST = new Double(0);
	static final private Double NORMALIZED_PRODUCTION = new Double(0.0005);
	static final private Double NORMALIZED_SCIENCE = new Double(0.0005);
	static final private Double NORMALIZED_MINING = new Double(0.0005);
	static final private Double NORMALIZED_FARMING = new Double(2);
	static final private Double NORMALIZED_INCOME = new Double(0.0);

	// Größe des Lagerplatzes pro Rohstoff + Maximale Größe
	static final public long NORMALIZED_STORAGE = 25;
	static final public long MAX_STORAGE = (long) 25E6;
	static final private String ATTR_STORAGE = "ATTR_STORAGE";

	public ColonyCenter()
	{
		super();
		setSize(1);
		createMaterialStorage();
	}

	public ColonyCenter(double size)
	{
		super();
		setSize(size);
		createMaterialStorage();
	}
	
	@Override
	public void setColonyID(ID id)
	{
		super.setColonyID(id);
		getMaterialStorage().setColonyID(id);
	}
	
	@Override
	public void cleanUp()
	{
		super.cleanUp();
		getMaterialStorage().cleanUp();
	}
	
	private void createMaterialStorage()
	{
		ResearchableDesign design = new ResearchableDesign<BuildingAbstract>(new ColonyCenterMaterialStorage(), "Koloniezentrum: Lagerhaus");
		setAttribute(ATTR_STORAGE, design.getObjectData());
		
	}
	
	public MaterialStorage getMaterialStorage()
	{
		Map data = (Map) getAttribute(ATTR_STORAGE);
		if(data == null) return null;
		ResearchableDesign design = new ResearchableDesign<BuildingAbstract>(data);

		return (MaterialStorage) design.getResearchableResource(); 
	}

	@Override
	public double computeNormalizedMass()
	{
		return 10d;
	}

	@Override
	protected void computeColonyPointsIntern(Colony col)
	{
		col.addBruttoProduction(getProduction());
		col.addBruttoMining(getMining());
		col.addBruttoResearch(getScience());
		// col.setBruttoIncome(col.getBruttoIncome() + getIncome());
		col.setLivingSpace(col.getLivingSpace() + getLivingSpace());
		col.addBruttoFarming(getFarming());
	}

	@Override
	public JComponent getScienceViewComponent()
	{
		return new ColonyCenterScienceView(this);
	}

	@Override
	public String getName()
	{
		return "Koloniezentrum";
	}

	@Override
	public String getShortDescription()
	{
		return "<html>Erste Koloniegebäude. Wird bei der Koloniegründung<br>" + "automatisch aus dem zerlegten Kolonieschiff gebaut.<html>";
	}

	public ID getID()
	{
		return ID_BUILDING_COLONY_CENTER;
	}
	
	protected @Override
	BuildingUiAbstract getBuildingUiIntern()
	{
		return new ColonyCenterUI(this);
	}

	@Override
	public boolean isResearchAllowed()
	{
		/*
		 * ColonyCenter wird nicht erforscht. Seine Eigenschaften sind festgelegt.
		 */
		return false;
	}

	@Override
	public double computeNormalizedCrew()
	{
		return NORMALIZED_CREW;
	}

	@Override
	public double computeNormalizedSupportCost()
	{
		return NORMALIZED_SUPPORT_COST;
	}

	public double getProduction()
	{
		return NORMALIZED_PRODUCTION * getSize().doubleValue() * getKPD();
	}

	public double getScience()
	{
		return NORMALIZED_SCIENCE * getSize().doubleValue() * getKPD();
	}

	public double getMining()
	{
		if(getColony().getNaturalResourcesSize() <= 0) return 0;
		return NORMALIZED_MINING * getSize().doubleValue() * getKPD();
	}

	public double getLivingSpace()
	{
		return NORMALIZED_LIVING_SPACE * getSize().doubleValue();
	}

	public double getIncome()
	{
		return NORMALIZED_INCOME * getSize().doubleValue() * getKPD();
	}

	public double getFarming()
	{
		return NORMALIZED_FARMING * getSize().doubleValue() * getKPD();
	}

	@Override
	protected String getHtmlFormattedInfoIntern()
	{
		String ret = "<b>Wohnraum: </b>" + GUI.formatLong(getLivingSpace()) + "<br><b>Nahrungsproduktion: </b>" + GUI.formatLong(getFarming())
				+ "<br><b>Produktion: </b>" + GUI.formatLong(getProduction()) + "<br><b>Bergbau: </b>" + GUI.formatLong(getMining()) + "<br><b>Forschung: </b>"
				+ GUI.formatLong(getScience()) + "<br><b>Einkommen: </b>" + GUI.formatLong(getIncome());
		return ret;
	}

	@Override
	public String getImagePath()
	{
		return "/res/images/koloniemodul.png";
	}
}
