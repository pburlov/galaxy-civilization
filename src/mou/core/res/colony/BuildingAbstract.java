/*
 * $Id: BuildingAbstract.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony;

import java.util.Random;
import java.util.Set;
import mou.Main;
import mou.core.colony.Colony;
import mou.core.res.ResearchableResource;
import mou.gui.GUI;
import mou.storage.ser.ID;

public abstract class BuildingAbstract extends ResearchableResource
{

	/*
	 * Bestimmt wieviel Tonnen eine Größeneinheit hat
	 */
	static final protected double MASS_PER_SIZE = 1000000;
	static final protected double CREW_PER_SIZE = 100000;
	static final protected double BUILD_COST_FAKTOR = 5000;
	// static final protected double SUPPORT_COST_FAKTOR = 100;
	static final private String ATTR_LABOR_POWER_SUPPLY = "ATTR_LABOR_POWER_SUPPLY";
	// static final private String ATTR_ENERGY_SUPPLY = "ATTR_ENERGY_SUPPLY";
	static final private String ATTR_ACTIV_PERCENT = "ATTR_ACTIV_PERCENT";
	static final private String ATTR_BUILDED = "ATTR_BUILDED";
	static final private String ATTR_UTILIZATION = "ATTR_UTILIZATION";
	static final private String ATTR_COLONY = "ATTR_COLONY";

	protected boolean cleanup = false;  //Ob CleanUp-phase läuft
	
	public BuildingAbstract()
	{
		super();
	}

	/**
	 * @return true wenn diese Gebäude in einer Kolonie steht, false wenn dies nur ein Etnwurf ist
	 */
	public boolean isBuilded()
	{
		return ((Boolean) getAttribute(ATTR_BUILDED, Boolean.FALSE)).booleanValue();
	}

	/**
	 * @param val
	 */
	public void setBuilded(boolean val)
	{
		setAttribute(ATTR_BUILDED, new Boolean(val));
	}
	
	/* Erlaubt den Gebäuden vor dem löschen aufzuräumen */ 
	public void cleanUp()
	{
		cleanup = true;
	}
	
	@Override
	protected void generateMassFaktor(Random rnd, Set<ID> materials)
	{
	}
	
	@Override
	protected void generateCrewFaktor(Random rnd, Set<ID> materials)
	{
	}
	
	@Override
	protected void generateEnergyBalanceFaktor(Random rnd, Set<ID> materials)
	{
	}
	
	@Override
	protected void generateSupportCostFaktor(Random rnd, Set<ID> materials)
	{
	}

	// public BuildingAbstract(Map data)
	// {
	// super(data);
	// }
	//
	@Override
	public double computeNormalizedMass()
	{
		return MASS_PER_SIZE;
	}

	@Override
	public String getExtendenInfoForListCellRenderer()
	{
		if(isBuilded()) return "G: " + GUI.formatLong(getSize()) + "  A: " + GUI.formatSmartDouble(getKPD() * 100) + "%";
		return "Leistung: " + GUI.formatSmartDouble(computeCustomValue());
	}

	/**
	 * Gibt prozentueller Wert an, zu wieviel Prozent dieses Gebäude mit Arbeitskräften versogt ist.
	 * 
	 * @return Wert von 0 bis 1
	 */
	public double getLaborPowerSupply()
	{
		return ((Number) getAttribute(ATTR_LABOR_POWER_SUPPLY, ZERO_LONG)).doubleValue();
	}

	/**
	 * Gibt prozentueller Wert an, zu wieviel Prozent dieses Gebäude funktioniert.
	 * 
	 * @param val
	 *            Wert von 0 bis 1
	 */
	public void setLaborPowerSupply(double val)
	{
		setAttribute(ATTR_LABOR_POWER_SUPPLY, val);
	}

	/**
	 * Gibt die zugewiesene Anzhal der Arbeiter für dieses Gebäude
	 * 
	 * @return
	 */
	public double computeAssignedCrew()
	{
		return computeNeededCrew() * getKPD();
	}

	// /**
	// * Gibt prozentueller Wert an, zu wieviel Prozent dieses Gebäude funktioniert.
	// *
	// * @param val
	// * Wert von 0 bis 1
	// */
	// public void setEnergySupply(double val)
	// {
	// setAttribute(ATTR_ENERGY_SUPPLY, val);
	// }
	//
	// /**
	// * Gibt prozentueller Wert an, zu wieviel Prozent dieses Gebäude funktioniert. Dieser Wert ist
	// * abhängig von der Energieversorgung
	// *
	// * @return Wert von 0 bis 1
	// */
	// public double getEnergySupply()
	// {
	// return ((Number) getAttribute(ATTR_ENERGY_SUPPLY, ZERO_LONG)).doubleValue();
	// }
	//
	@Override
	public double computeEnergyBalance()
	{
		return DOUBLE_0;
	}
	
	@Override
	public double computeNormalizedEnergyBalance()
	{
		return DOUBLE_0;
	}

	/**
	 * Gibt prozentueller Wert an, zu wieviel Prozent dieses Gebäude funktioniert.
	 * 
	 * @return Wert von 0 bis 1
	 */
	public double getKPD()
	{
		/*
		 * Damit in Forschungsansicht alle Parameter korrekt gezeigt werden wird bei nicht gebauten
		 * Gebäuden immer KPD=1 zurückgegeben
		 */
		if(!isBuilded()) return 1;
		return Math.min(getLaborPowerSupply(), getActivFactor());
	}

	/**
	 * Liefert den, vom Benutzer eingestellen, Faktor zwischen 0 und 1. Dieser Faktor bestimmt zu
	 * wieviel Prozent dieses Gebäude aktiviert wird.
	 * 
	 * @return eine Zahl zwischen 0 und 1.
	 */
	public double getActivFactor()
	{
		return ((Number) getAttribute(ATTR_ACTIV_PERCENT, DOUBLE_1)).doubleValue();
	}

	/**
	 * Setzt den Faktor zwischen 0 und 1 mit welcher Leistung diese Gebäude funktionieren soll
	 * 
	 * @param val
	 */
	public void setActivFactor(double val)
	{
		setAttribute(ATTR_ACTIV_PERCENT, new Double(val));
	}
	
	/**
	 * Returns Array with, from user adjusted, utilization factors for the building
	 * 
	 * @return Array with values between 0 and 1.
	 */
	public double[] getUtilizationFactors()
	{
		double[] ret;
		ret = (double[])(getAttribute(ATTR_UTILIZATION, null));
		if(ret == null)
			ret = new double[0];
		
		return ret;
	}
	
	/**
	 * Sets the Utilization-Factors of the Building
	 * 
	 * @param factors Array with the Utilization-Factors
	 */
	public void setUtilizationFactors(double[] factors)
	{
		setAttribute(ATTR_UTILIZATION, factors);
	}

	/**
	 * Gives actually values of utilization for the building 
	 * 
	 * @return Array with values between 0 and 1.
	 */
	public double[] getUtilizationValues()
	{
		double[] ret = {getKPD()};
		return ret;
	}
	
	/**
	 * Maximal zu bauende Anzahl an Gebäuden,
	 * per Default durch max. Bevölkerung begrenzt
	 *
	 * @return Maximale größe als double (dann wird bis zum nächst größeren Wert gebaut,
	 * 			order -1, wenn größe unbegrenzt 
	 */
	public double getMaxSize()
	{
		return getColony().getMaxBevoelkerung()/computeNormalizedCrew();
	}
	
	public String getNoBuildMessage()
	{
		return "Gebäude hat maximale Größe erreicht";
	}
	
	/* Funktionen zur Ermittelung der Kolonie, auf der das Gebäude steht */
	public void setColonyID(ID id)
	{
		setAttribute(ATTR_COLONY, id);
	}
	
	public ID getColonyID()
	{
		return (ID) getAttribute(ATTR_COLONY);
	}
	
	public Colony getColony()
	{
		Colony ret = null;
		ID id = getColonyID();
		if(id != null)
			ret = Main.instance().getMOUDB().getKolonieDB().getKolonie(id);
		return ret;
	}

	// /**
	// * Gibt an ob diese Gebäude aktiviert ist
	// *
	// * @return
	// */
	// public boolean isActiv()
	// {
	// return ((Boolean) getAttribute(ATTR_ACTIVATED, Boolean.TRUE)).booleanValue();
	// }
	//
	// /**
	// * Gibt an ob diese Gebäude aktiviert ist
	// *
	// * @param val
	// */
	// public void setActiv(boolean val)
	// {
	// setAttribute(ATTR_ACTIVATED, new Boolean(val));
	// if(!val)
	// {
	// /*
	// * Beim Deaktivieren Werte auf 0 setzen
	// */
	// // setEnergySupply(0);
	// setLaborPowerSupply(0);
	// }
	// }
	public void computeColonyPoints(Colony col)
	{
		/*
		 * Unterhaltungskosten werden auf jeden Fall berechnet, egal ob Gebäude aktiv ist oder
		 * nicht.
		 */
		col.setSupportCost(col.getSupportCost() + computeSupportCost());
		if(getActivFactor() == 0) return;
		/*
		 * Zuerst Voraussetzungen überprüfen: Energieversorung und Arbeitskraft. Daraus wird der
		 * Leistungskoeffitien ausgerechnet.
		 */
		double kpdLaborPower = 0;
		double unemployed = col.getUnemployed();
		/*
		 * Anzahl der benötigten Leute wird in Relation zum vom Benutzer eingestellten Aktivfaktor
		 * ermittelt.
		 */
		double neededPeople = computeNeededCrew() * getActivFactor();
		if(neededPeople <= 0)
		{
			//Gebäude, die keine Arbeiter brauchen, sind immer voll besetzt
			kpdLaborPower = 1.0;
		}
		else if(unemployed >= neededPeople)
		{
			kpdLaborPower = 1.0;
			unemployed -= neededPeople;
		} else
		{
			kpdLaborPower = unemployed / neededPeople;
			unemployed = 0;
		}
		col.setUnemployed(unemployed);
		setLaborPowerSupply(kpdLaborPower);
		/*
		 * Jetzt Gebäudeabhängige Werte berechnen
		 */
		computeColonyPointsIntern(col);
	}

	@Override
	public double computeNormalizedSupportCost()
	{
		return DOUBLE_0;
	}

	@Override
	public double computeNormalizedCrew()
	{
		/*
		 * Personal festgesetzt
		 */
		return CREW_PER_SIZE;
		// return super.getNormalizedCrew().doubleValue() * CREW_PER_SIZE;
	}

	abstract protected void computeColonyPointsIntern(Colony col);

	/**
	 * Diese Methode soll UI-Objekte der abgeleiten Gebäuden liefern. Diese UI-Objekte werden dann
	 * im Koloniebereich zum Anzeigen und Steuern der einzelnen Koloniegebäude verwendet.
	 * 
	 * @return
	 */
	protected abstract BuildingUiAbstract getBuildingUiIntern();

	public BuildingUiAbstract getBuildingUI()
	{
		BuildingUiAbstract ret = getBuildingUiIntern();
		ret.refreshValues();
		return ret;
	}

	public String getHtmlFormattedInfo()
	{
		String ret = "<html>";
		if(isBuilded())
		{
			ret += "<b>Aktiv zu " + GUI.formatProzent(getKPD() * 100) + "</b><br>";
		}
		ret += "<b>Masse: </b>" + GUI.formatLong(computeMasse()) + "<br><b>Beschäftigungskapazität: </b>" + GUI.formatLong(computeNeededCrew());
		if(isBuilded())
			ret += "<br><b>Zugewiesene Arbeitskräfte: </b>" + GUI.formatLong(computeAssignedCrew());
		else
			ret += "<br><b>Bauaufwand: </b>" + GUI.formatLong(computeBuildCost());
		ret += "<br><b>Unterhaltskosten: </b>" + GUI.formatSmartDouble(computeSupportCost()) + "<br>" + getHtmlFormattedInfoIntern()
				+ "<br><b>=== Baustoffe ===</b>" + "<br>" + GUI.htmlFormatRessourceMenge(getNeededRes()) + "</html";
		return ret;
	}

	/**
	 * Abgeleitete Klassen liefert hiert ihre spezifische Informationen. Anführende und
	 * abschließende html-Tags sollen wegelasen werden.
	 * 
	 * @return
	 */
	abstract protected String getHtmlFormattedInfoIntern();
}
