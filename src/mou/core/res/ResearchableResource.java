/*
 * $Id: ResearchableResource.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.JComponent;
import mou.Main;
import mou.core.res.natural.NaturalResource;
import mou.storage.ser.ID;

/**
 * @author pb
 */
abstract public class ResearchableResource extends ResourceAbstract
{
	static final protected Double DOUBLE_0 = new Double(0);
	static final protected Double DOUBLE_1 = new Double(1);
	static final public double CUSTOM_VALUE_POW_BASE = 5;
	static final private int MAX_MATERIAL_SHARES = 1000;
	static final private String MATERIALIEN = "MATERIALIEN";
	static final private String ENERGIEVERBRAUCH = "ENERGIE";
	static final private String BAUKOSTEN = "BAUKOSTEN";
	static final private String CREW = "CREW";
	static final private String SUPPORT = "SUPPORT";
	// static final private String MASSE = "MASSE";
	static final private String SIZE = "SIZE";
	static final private String CUSTOM_FAKTORS = "CUSTOM_FAKTORS";
	static final private String QUALITY = "QUALITY";
	static final private String ATTR_DESC_ID = "ATTR_DESC_ID";

	public ResearchableResource()
	{
		this(new HashMap());
	}

	public ResearchableResource(Map data)
	{
		super(data);
	}

	/**
	 * Liefert kurze Info zur Leistungsdaten des Systems. Dieses Info wird in der ListCellRenderer
	 * verwendet.
	 * 
	 * @return
	 */
	abstract public String getExtendenInfoForListCellRenderer();

	/**
	 * Methode soll eine in HTML formatierte Informationüber dieses System geben.
	 * 
	 * @return
	 */
	abstract public String getHtmlFormattedInfo();

	/**
	 * Liefert die Größe des Objekts
	 * 
	 * @return
	 */
	public Number getSize()
	{
		return (Number) getAttribute(SIZE, ZERO_LONG);
	}

	public void setSize(Number size)
	{
		setAttribute(SIZE, size);
	}

	public double getNeededResearchPoints(Set<ID> materials)
	{
		return Math.pow(100, materials.size());
	}
	
	/**
	 * Methode ist dafür da um eine neues Forschungsergebnis zu produzieren
	 * 
	 * @param rnd
	 * @param materials
	 */
	public void generateAttributes(Random rnd, Set<ID> materials)
	{
		setQualityFaktor(Math.abs(rnd.nextGaussian()) + 1);
		generateMassFaktor(rnd, materials);
		generateMaterials(rnd, materials);
		generateCustomValueFaktors(rnd, materials);
		generateBuildCostFaktor(rnd, materials);
		generateCrewFaktor(rnd, materials);
		generateEnergyBalanceFaktor(rnd, materials);
		generateSupportCostFaktor(rnd, materials);
		
		setSize(1);
		// int rounds = materials.size();
		// for(;rounds > 0 && rnd.nextDouble() < 0.8d; rounds--)
		// {
		// custom = custom * 3d;
		// }
		// custom = custom * (rnd.nextDouble() + 1d);
	}
	
	abstract protected void generateMassFaktor(Random rnd, Set<ID> materials);
	
	protected void generateMaterials(Random rnd, Set<ID> materials)
	{
		int values[] = new int[materials.size()];
		double sum = 0;
		setMaterialien(new HashMap());
		/*
		 * Materialmengen generieren
		 */
		for(int i=0; i<materials.size(); i++)
		{
			values[i] = rnd.nextInt(MAX_MATERIAL_SHARES);
			sum += values[i];
		}
		
		for(int i=0; i<materials.size(); i++)
		{
			getMaterialien().put((ID) materials.toArray()[i], values[i]/sum * computeNormalizedMass());
		}

	}

	/* Hier können Gebäudespezifische Faktoren generiert und gespeichert werden */
	protected void generateCustomValueFaktors(Random rnd, Set<ID> materials)
	{
	}

	protected void generateBuildCostFaktor(Random rnd, Set<ID> materials)
	{
		setNormalizedBuildCostFaktor((rnd.nextDouble() + 0.5));
	}
	
	abstract protected void generateCrewFaktor(Random rnd, Set<ID> materials);

	abstract protected void generateEnergyBalanceFaktor(Random rnd, Set<ID> materials);
	
	abstract protected void generateSupportCostFaktor(Random rnd, Set<ID> materials);
	
	/* Gibt den wichtigsten Systemwert zurück */
	/*
	public Number getCustomValue()
	{
		return getCustomValues()[0];
	}
	*/
	
	/* Gibt auskunft über die Effektivität des Systems, je größer der Rückgabewert,
	 *  desto Leistungsstärker das Gebäude. Wird im Name des neuen Designs verwendet
	 */
	public int getCustomCounter()
	{
		return (int) computeCustomValue(1);
	}

	/**
	 * Dieser Wert kann zur Berechnung der Systemspezifischen Wirkeigenschaften des erforschten
	 * System herangezogen werden.
	 * 
	 * @param wert
	 *            von 0 bis Integer.MAX
	 */
	/*
	public void setCustomFaktor(Number wert)
	{
		setAttribute(CUSTOM_VALUE, wert);
	}
*/
	/* Gibt ein Array mit Faktoren, die zur berechnung des CustomValues verwendet werden können */ 
	public Number[] getCustomFaktors()
	{
		return (Number[]) getAttribute(CUSTOM_FAKTORS);
	}
	
	/*
	 *  Stellt die Systemeigenschaften ein,
	 *  auf Position 0 sollte der wichtigste Wert gespeichert sein
	 */
	public void setCustomFaktors(Number[] wert)
	{
		setAttribute(CUSTOM_FAKTORS, wert);
	}

	
	public double computeMasse()
	{
		return getSize().doubleValue() * computeNormalizedMass();
	}

	/**
	 * Liefert Masse pro Größeneinheit
	 * 
	 * @return
	 */
	abstract public double computeNormalizedMass();
	
	public Map<ID, Number> getMaterialien()
	{
		return (Map<ID, Number>) getAttribute(MATERIALIEN);
	}

	/* Ein Maß für die Güte des Gebäudes im Vergleich zu anderen der selben Stufe */ 
	public Number getQualityFaktor()
	{
		return (Double) getAttribute(QUALITY, DOUBLE_1);
	}
	
	public void setQualityFaktor(double quality)
	{
		setAttribute(QUALITY, quality);
	}
	
	public void setMaterialien(Map<ID, Number> werte)
	{
		setAttribute(MATERIALIEN, werte);
	}

	/* Ist kleiner 0, wenn Objekt Energie verbraucht, größer 0, wenn es sie produziert */
	public Number getNormalizedEnergyBalanceFaktor()
	{
		return (Double) getAttribute(ENERGIEVERBRAUCH, -DOUBLE_1);
	}

	public void setNormalizedEnergyBalanceFaktor(Double wert)
	{
		setAttribute(ENERGIEVERBRAUCH, wert);
	}

	/**
	 * Gibt Baukosten pro Mengeeinheit
	 * 
	 * @return
	 */
	public Number getNormalizedBuildCostFaktor()
	{
		return (Double) getAttribute(BAUKOSTEN, DOUBLE_1);
	}

	/**
	 * Setzt Baukosten pro Mengeeinheit
	 * 
	 * @param wert
	 */
	public void setNormalizedBuildCostFaktor(Double wert)
	{
		setAttribute(BAUKOSTEN, wert);
	}

	/**
	 * Liefert Masseabhängige Gesamtbaukosten des Objekts
	 * 
	 * @return
	 */
	public double computeBuildCost()
	{
		return computeNormalizedBuildCost() * getSize().doubleValue();
	}
	
	public double computeNormalizedBuildCost()
	{
		double ret;
		
		ret = getNormalizedBuildCostFaktor().doubleValue();
		ret *= getQualityFaktor().doubleValue() * Math.pow(CUSTOM_VALUE_POW_BASE, getMaterialien().size());
		ret *= computeNormalizedMass()/200d;
		return ret;
	}
	
	public double computeCustomValue()
	{
		return computeCustomValue(1);
	}
	
	public double computeCustomValue(int number)
	{
		/* 
		 * Per default immer Exponentielle Wachstumskurve, gekoppelt an QualityFaktor,
		 * CustomFaktor wird nicht verwendet, alle CustomValues sind gleich
		 */
		return getQualityFaktor().doubleValue() * Math.pow(CUSTOM_VALUE_POW_BASE, getMaterialien().size());
	}

	public Number getNormalizedCrewFaktor()
	{
		return (Double) getAttribute(CREW, DOUBLE_1);
	}

	public void setNormalizedCrewFaktor(Double wert)
	{
		setAttribute(CREW, wert);
	}

	public Number getNormalizedSupportCostFaktor()
	{
		return (Double) getAttribute(SUPPORT, DOUBLE_1);
	}

	public void setNormalizedSupportCostFaktor(Double wert)
	{
		setAttribute(SUPPORT, wert);
	}

	/**
	 * Liefert die benötigte Menge an Resourcen <br>
	 * für die bei der Initialisierung eingestellte Kombination<br>
	 * aus der Inputleistung und der verwendeten Modifikatoren
	 * 
	 * @return Collection mit ResourceMenge-Objekten
	 */
	public Collection<ResourceMenge> getNeededRes()
	{// Wird nich bei der Initialisierung sonder nur per Nachfrage berechnet
		return computeNeededResources(getSize().intValue());
	}

	public double computeSupportCost()
	{
		return computeNormalizedSupportCost() * getSize().doubleValue();
	}
	
	abstract public double computeNormalizedSupportCost();

	/**
	 * Liefert zum Bau benötigte Materialien
	 * 
	 * @return Collection mit ResourceMenge Objekten
	 */
	private List<ResourceMenge> computeNeededResources(int size)
	{
		ArrayList<ResourceMenge> ret = new ArrayList<ResourceMenge>();
		// Zuerst Prozentpunkten pro Masseeinheit der Modifikatoren ausrechnen
		Map<ID, Number> materialien = getMaterialien();
		if(materialien == null) return ret;

		for(Map.Entry<ID, Number> entry : materialien.entrySet())
		{
			NaturalResource res = Main.instance().getMOUDB().getNaturalRessourceDescriptionDB().getNaturalResource(entry.getKey());
			ret.add(new ResourceMenge(res, entry.getValue().doubleValue() * size));
		}
		Collections.sort(ret);
		return ret;
	}

	/**
	 * Berechnet Energie-Input/input des Schiffsystems/Gebäudes. Hängt von der Größe des Systems ab.
	 * 
	 * @return positive oder negative Wert
	 */
	public double computeEnergyBalance()
	{
		return getSize().doubleValue() * computeNormalizedEnergyBalance();
	}

	abstract public double computeNormalizedEnergyBalance();
	
	/**
	 * Berechnet die Anzahl des Wartungspersonals auf die gegebene Masse des Systems
	 * 
	 * @return
	 */
	public double computeNeededCrew()
	{
		return getSize().doubleValue() * computeNormalizedCrew();
	}
	
	abstract public double computeNormalizedCrew();

	public String getImagePath()
	{
		return "res/images/question.gif";
	}

	/**
	 * Hier soll ein Objekt zurückgegeben werden, das Systemeigenschaften im Forschungsbereich
	 * anzeigt
	 * 
	 * @return
	 */
	abstract public JComponent getScienceViewComponent();

	public String toString()
	{
		return getName();
	}

	public void setDesignID(ID id)
	{
		setAttribute(ATTR_DESC_ID,id);
	}
	
	public ID getDesignID()
	{
		return (ID) getAttribute(ATTR_DESC_ID);
	}

	/**
	 * Bestimmt,ob dieses System im Forschungsbereich erforscht werden kann. Dies ermöglicht das
	 * Definieren von Systemen mit festgesetzten Eigenschaften, die nicht weiter erforscht werden
	 * sollen.
	 * 
	 * @return
	 */
	public boolean isResearchAllowed()
	{
		return true;
	}
}
