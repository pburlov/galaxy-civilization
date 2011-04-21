/*
 * $Id: StarSystem.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.starmap;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import mou.Main;
import mou.Universum;
import mou.core.colony.Colony;
import mou.core.colony.ForeignColonyPersistent;
import mou.core.res.natural.NaturalResource;
import mou.gui.GUI;

/**
 * Repräsentationsklasse eines einzelnes Sternensystems Enthält nur statische Daten über Sternsystem
 * ohne dynamischen Komponenten. Diese Klasse ist nicht serialisierbar, weil Objekte dieser Klasse
 * immer neu generiert werden.
 */
public strictfp class StarSystem
{

	/*
	 * Klasse-O-Sterne werden auch häufig als "blaue Riesen" bezeichnet, da sie sehr massenreich
	 * sind und blau leuchten. Sterne dieses Typs weisen vor allem sehr starke Absorptionslinien von
	 * hoch ionisierten Heliumatomen auf (Bei frühen Klasse-O-Sternen kommt das ionisierte He III
	 * vor, spätere weisen in dunklen/hellen Linien auch bereits das neutrale Helium d.h. He II
	 * auf.). Zudem kommen in relativ schwacher Form Linien von N II, Si II oder O II hinzu.
	 * Strahlungsfarbe: über ultraviolett, tiefblau bis blauweiß Temperatur: 25000-50000K (sehr
	 * heiss) Masse: 10 - 40 Msonne Beispiele: Theta Orionis
	 */
	public final static String STAR_TYP_O = "O";
	public final static int MAX_PLANETS_STAR_TYP_O = 20;
	/*
	 * Klasse B: Klasse-B-Sterne der Hauptlinie sind massenreich und strahlen weiß. Ihre
	 * Absorptionslinien sind auf Helium, Wasserstoff und ionsiertem Sauerstoff zurückzuführen. Die
	 * Balmer-Linien des Wasserstoffs (H I) sind stärker ausgeprägt, dafür beginnen die Heliumlinien
	 * (He I) langsam zu schwinden, womit auch die Effektivtemperatur sinkt (Die stärkste Ausprägung
	 * finden die Linien des elementaren Heliums in der Klasse B2, danach sinken sie.).
	 * Strahlungsfarbe: noch leicht bläulich, vorwiegend weiss Temperatur: 10000-25000K (heiss)
	 * Masse: 3 - 10 Msonne Beispiele: Epsilon Orionis
	 */
	public final static String STAR_TYP_B = "B";
	public final static int MAX_PLANETS_STAR_TYP_B = 15;
	/*
	 * Klasse A: Bei den hellgelben Klasse-A-Sternen (etwa mittlerer Masse) erreicht die Reaktion
	 * von Wasserstoff ihr Maximum (höchste Intensität bei A2/A3-Sternen). Schwache Linien von
	 * ionisierten Metallen wie etwa Ca II (ionisiertes Calcium) oder Fe II (ionisiertes Eisen)
	 * tauchen allmählich auf. He I-Linien sind nur noch sehr schwach erkennbar und gehen weiter
	 * zurück. Strahlungsfarbe: weissgelb bzw. hellgelb Temperatur: 7600 - 10000K (heiss) Masse: 2 -
	 * 3 Msonne Beispiele: Alpha Sirius
	 */
	public final static String STAR_TYP_A = "A";
	public final static int MAX_PLANETS_STAR_TYP_A = 10;
	/*
	 * Klasse F: Bei Klasse-F-Sternen sind die Wasserstofflinien (der Balmer-Serie) weiter
	 * abgeschwächt, dafür sind die Absorptionslinien von ionisiertem Calicum und anderen (jetzt
	 * auch neutralen) Metallen weiter ausgeprägt. Die Temperaturen sind noch höher als bei Sol,
	 * entsprechend ist auch die Leuchtfarbe ein helleres Gelb. Strahlungsfarbe: hellgelb bis gelb
	 * Temperatur: 6000 - 7600K (etwas heisser als Sol) Masse: 1-2 Msonne Beispiele: Prokyon; Delta
	 * Aquilae
	 */
	public final static String STAR_TYP_F = "F";
	public final static int MAX_PLANETS_STAR_TYP_F = 8;
	/*
	 * Klasse G: In Klasse-G-Sternen (wie Sol) gibt es eine Vielzahl von neutralen Metallen, unter
	 * denen vor allem Eisen und das zweifach ionisierte Calcium dominieren. Im Allgemeinen gehen
	 * die Linien der ionisierten Atome bzw. Metalle zurück und jene der neutralen werden stärker.
	 * Die Wasserstofflinien sind nur noch schwach präsent. Strahlungsfarbe: hellgelb bis gelb
	 * Temperatur: 5100 - 6000K (Temperaturen von Sol) Masse: 0.8 - 1 Msonne Beispiele: Sol
	 */
	public final static String STAR_TYP_G = "G";
	public final static int MAX_PLANETS_STAR_TYP_G = 6;
	/*
	 * Klasse K: Klasse-K-Sterne sind in der Regel etwas kleiner und kühler als Sol. Die
	 * Metall-Linien herrschen weiterhin vor, allerdings gibt es nun auch Banden von Titanoxid und
	 * diverser einfacher Moleküle (CH, CN..). Die bisher starken Ca I-Linien weichen Ca II, während
	 * die Wasserstofflinien noch weiter dahinschwinden. Strahlungsfarbe: gelb - rötlich Temperatur:
	 * 3600 - 5100K (kühler als Sol) Masse: 0.5 - 0.8 Msonne Beispiele: Alpha Bootis, Aldebaran,
	 * Arktur
	 */
	public final static String STAR_TYP_K = "K";
	public final static int MAX_PLANETS_STAR_TYP_K = 3;
	/*
	 * Klasse M: Bei Sternen der Klasse M ("rote Riesen") sind sehr starke Linien von Metallen,
	 * Titanoxid (TiO) und diversen neutralen Atomen zu erkennen. Die Anzahl der Moleküle ist weit
	 * höher als bei Klasse-K-Sternen. Der obere Bereich des Spektrums (ultraviolett / violett) ist
	 * kaum noch erkennbar. Strahlungsfarbe: rötlich - dunkelrot Temperatur: 2700 - 3600K (kühl)
	 * Masse: 0.02 - 0.5 Msonne Beispiele: Beteigeuze; Alpha Orionis
	 */
	public final static String STAR_TYP_M = "M";
	public final static int MAX_PLANETS_STAR_TYP_M = 1;
	/*
	 * Klasse L: Bei den seltenen Klasse-L-Sternen (auch "braune Zwerge" genannt) sind die
	 * eigentlichen Kernreaktionen bereits soweit ausgebrannt, dass der Stern nicht mehr in der Lage
	 * ist, die für Sterne typische Leuchtkraft / Hitze zu erzeugen. Lediglich die im Vergleich zu
	 * Planeten große Masse und verschiedene Restbestände an ausgebrannten Atomen geben über den
	 * einstigen Stern Aufschluss. Strahlungsfarbe: rot - braun Temperatur: unter 2700 K (sehr kühl)
	 * Masse: 0.001 - 0.02 Msonne Beispiele: 2MASS J1146+2230
	 */
	public final static String STAR_TYP_L = "L";
	public final static int MAX_PLANETS_STAR_TYP_L = 0;
	/*
	 * Sterne der Nebenlinie Zwerge
	 */
	public final static String STAR_TYP_ZWERG_0 = "Z0";// Blau
	public final static String STAR_TYP_ZWERG_1 = "Z1";// Weiss
	public final static String STAR_TYP_ZWERG_2 = "Z2";// Gelb
	public final static String STAR_TYP_ZWERG_3 = "Z3";// Rot
	public final static int MAX_PLANETS_STAR_TYP_Z = 2;
	/*
	 * Sterne der Nebenlinie roten Riesen
	 */
	public final static String STAR_TYP_RIESE_0 = "R0";// Superriese
	public final static int MAX_PLANETS_STAR_TYP_R0 = 20;
	public final static String STAR_TYP_RIESE_1 = "R1";// Mittlere Riese
	public final static int MAX_PLANETS_STAR_TYP_R1 = 15;
	public final static String STAR_TYP_RIESE_2 = "R2";// Riese
	public final static int MAX_PLANETS_STAR_TYP_R2 = 10;
	private final static int STAR_GEWICHTUNG_HAUPTLINIE = 80; // Prozent aller Sterne der
	// Hauptlinie
	private final static int STAR_GEWICHTUNG_ZWERGE = 10;// Prozent aller Zwergen allen
	// Spektralklassen
	private final static int STAR_GEWICHTUNG_ROTE_RIESEN = 10;// Prozent roten Riesen allen
	// Groessen
	static final private int MAX_POPULATION_PER_PLANET = 10000000;
	static final private int MIN_POPULATION = 1000000;
	private static final Random _random = new Random();
	static final private double MAX_PRODUCTION_DELTA = 0.5d;
	static final private double MAX_MINING_DELTA = 0.5d;
	static final private double MAX_SCIENCE_DELTA = 0.5d;
	static final private double MAX_FARMING_DELTA = 0.5d;
	static final private double MAX_POPULATION_GROW_BONUS = 0.04;
	private String starclass = STAR_TYP_G;
	private List<NaturalResource> natRessources;// Liste mit NaturalResource Objekten
	private int planetcount = 0;
	private Point position;
	private String name;
	private double productionFaktor;
	private double miningFaktor;
	private double scienceFaktor;
	private double farmingFaktor;
	private double populationGrowBonus;
	private boolean systemPropertiesGenerated = false;
	private long mSeed;// wird zum on-demand Generierung Starsystemeigenschaften verwendet

	StarSystem(Point pos)
	{
		position = pos;
	}

	public Point getPosition()
	{
		return position;
	}

	public Point getQuadrant()
	{
		return Universum.getQuadrantForPosition(getPosition());
	}

	public void setName(String newName)
	{
		name = newName;
	}

	public long getMaxPopulation()
	{
		return getPlanetcount() * MAX_POPULATION_PER_PLANET + MIN_POPULATION;
	}

	/**
	 * @return Die generierte Name des Sterns
	 */
	public String getName()
	{
		String nm = Main.instance().getMOUDB().getStarmapDB().getStarsystemName(getPosition());
		if(nm == null) return name;
		return nm;
	}

	/**
	 * Generiert komplett ein neues Sternensystem an der gegebener Position
	 */
	synchronized static public StarSystem generateStarSystem(final int x, final int y, final long seed)
	{
		_random.setSeed(seed);
		// StarSystemID id = new StarSystemID(x,y);
		StarSystem ss = new StarSystem(new Point(x, y));
		int wert = _random.nextInt(100);
		wert = wert - STAR_GEWICHTUNG_HAUPTLINIE;
		if(wert <= 0)
			ss.generateHauptlinie(_random);
		else
		{
			wert = wert - STAR_GEWICHTUNG_ROTE_RIESEN;
			if(wert <= 0)
				ss.generateRoteRiese(_random);
			else
			{
				wert = wert - STAR_GEWICHTUNG_ZWERGE;
				if(wert <= 0)
					ss.generateZwerge(_random);
				else
					ss.generateHauptlinie(_random); // als Default-Wert
			}
		}
		ss.mSeed = seed;
		ss.setName((ss.getStarClass()));
		return ss;
	}

	/**
	 * Generiert detailierte Systemeigenschaften "on demand"
	 * 
	 * @param seed
	 */
	final private void generateStarSystemProperties(final long seed)
	{
		/*
		 * Um die Random-Objekt Erzeugung für jeden Stern zu vermeiden wird ein statischer Objekt
		 * genommen
		 */
		synchronized(_random)
		{
			_random.setSeed(seed);
			natRessources = Main.instance().getMOUDB().getNaturalRessourceDescriptionDB().generateRessource(seed);
			productionFaktor = computeBonusFaktor(_random, MAX_PRODUCTION_DELTA);
			miningFaktor = computeBonusFaktor(_random, MAX_MINING_DELTA);
			scienceFaktor = computeBonusFaktor(_random, MAX_SCIENCE_DELTA);
			farmingFaktor = computeBonusFaktor(_random, MAX_FARMING_DELTA);
			populationGrowBonus = _random.nextDouble() * MAX_POPULATION_GROW_BONUS;
			if(_random.nextBoolean()) populationGrowBonus = -populationGrowBonus;
			systemPropertiesGenerated = true;
		}
	}

	final private double computeBonusFaktor(final Random rnd, final double maxValue)
	{
		double ret = rnd.nextDouble() * maxValue;
		if(rnd.nextBoolean())
			return 1 + ret;// Faktor bewirkt Vergrößerung da > 1.0
		else
			return 1 - ret;// Faktor bewirkt Verkleinerung da < 1.0
	}

	private void generateZwerge(Random rnd)
	{
		switch(rnd.nextInt(4))
		{
			case 0:
				starclass = STAR_TYP_ZWERG_0;
				break;
			case 1:
				starclass = STAR_TYP_ZWERG_1;
				break;
			case 2:
				starclass = STAR_TYP_ZWERG_2;
				break;
			case 3:
				starclass = STAR_TYP_ZWERG_3;
				break;
		}
		planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_Z); // Für alle Zwerge gleiche
		// Planetenanzahl
	}

	/**
	 * Generiert einer der drei großen roten Riesen
	 */
	private void generateRoteRiese(Random rnd)
	{
		final int BIG = 30; // 10-30 Msonne
		final int NORMAL = 30;// 3-7 Msonne
		final int LITTLE = 30;// 0.8-3 Msonne
		int wert = rnd.nextInt(100) + 1;
		wert = wert - BIG;
		if(wert <= 0)
		{
			starclass = STAR_TYP_RIESE_0;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_R0 + 1);
			return;
		}
		wert = wert - NORMAL;
		if(wert <= 0)
		{
			starclass = STAR_TYP_RIESE_1;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_R1 + 1);
			return;
		}
		wert = wert - LITTLE;
		if(wert <= 0)
		{
			starclass = STAR_TYP_RIESE_2;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_R2 + 1);
			return;
		}
	}

	/**
	 * Generiert Sterne der Hauptlinie Gewichtung der Sternenklassen:
	 */
	private void generateHauptlinie(Random rnd)
	{
		// ##### Koefizienten der Klassenverteilung ######
		final int O = 3;
		final int B = 10;
		final int A = 17;
		final int F = 21;
		final int G = 21;
		final int K = 17;
		final int M = 10;
		final int L = 1;
		// ########################################
		int wert = rnd.nextInt(100) + 1;
		wert = wert - L;// L
		if(wert <= 0)
		{
			starclass = STAR_TYP_L;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_L + 1);
			return;
		}
		wert = wert - O;// O
		if(wert <= 0)
		{
			starclass = STAR_TYP_O;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_O + 1);
			return;
		}
		wert = wert - B;//
		if(wert <= 0)
		{
			starclass = STAR_TYP_B;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_B + 1);
			return;
		}
		wert = wert - A;// L
		if(wert <= 0)
		{
			starclass = STAR_TYP_A;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_A + 1);
			return;
		}
		wert = wert - F;// L
		if(wert <= 0)
		{
			starclass = STAR_TYP_F;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_F + 1);
			return;
		}
		wert = wert - G;// L
		if(wert <= 0)
		{
			starclass = STAR_TYP_G;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_G + 1);
			return;
		}
		wert = wert - K;// L
		if(wert <= 0)
		{
			starclass = STAR_TYP_K;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_K + 1);
			return;
		}
		wert = wert - M;// L
		if(wert <= 0)
		{
			starclass = STAR_TYP_M;
			planetcount = rnd.nextInt(MAX_PLANETS_STAR_TYP_M + 1);
			return;
		}
	}

	/**
	 * Gibt die Klasse des Sterns zurück
	 * 
	 * @return eine der StaticStarSystem.TYP_ Konstanten
	 */
	public String getStarClass()
	{
		return starclass;
	}

	public void setStarclass(String newStarclass)
	{
		starclass = newStarclass;
	}

	/**
	 * Liefert Liste im System vorhandenen natürlichen Ressourcen
	 * 
	 * @return Liste mit NaturalResource Objekten
	 */
	public List<NaturalResource> getNatRessources()
	{
		if(!systemPropertiesGenerated) generateStarSystemProperties(mSeed);
		return natRessources;
	}

	public int getPlanetcount()
	{
		return planetcount;
	}

	public void setPlanetcount(int newPlanetcount)
	{
		planetcount = newPlanetcount;
	}

	/**
	 * Zwei Sterne werden als gleich erkannt wenn sie übereinstimmende Raumkoordinaten haben
	 */
	public boolean equals(Object obj)
	{
		StarSystem ss = null;
		try
		{
			ss = (StarSystem) obj;
		} catch(ClassCastException e)
		{
			return false;
		}
		return ss.getPosition().equals(getPosition());
	}

	public String toString()
	{
		return getName() + " " + GUI.formatPoint(getPosition());
	}

	/*
	 * private void setPosition(Point newPosition) { position = newPosition; // id aus X und Y
	 * -Koordinaten konstruieren id = newPosition.x; id = id < < 32; id = id & 0xFFFF0000; id = id +
	 * newPosition.y; }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.StarSystem#getMeineKolonien()
	 */
	public Colony getMeineKolonie()
	{// In unerforschten Sternen keine eigenen Kolonien möglich
		List cols = Main.instance().getMOUDB().getKolonieDB().getColoniesInSystem(getPosition());
		if(cols.isEmpty()) return null;
		return (Colony) cols.get(0);
	}

	public Collection<ForeignColonyPersistent> getFremdeKolonien()
	{
		return Main.instance().getMOUDB().getFremdeKolonienDB().getObjectsAt(getPosition()).values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.StarSystem#erforscht()
	 */
	public boolean erforscht()
	{
		return Main.instance().getMOUDB().getStarmapDB().isStarsystemVisited(getPosition());
	}

	/*
	 * Methode macht nichts, da dieses Stern noch nicht erforscht wurde
	 * 
	 * @see mou.db.StarSystem#setStarName(java.lang.String)
	 */
	public void setStarName(String name)
	{
		Main.instance().getMOUDB().getStarmapDB().setStarsystemName(getPosition(), name);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see mou.db.StarSystem#getStarposition()
	// */
	// public String getStarposition()
	// {
	// Point pos = getPosition();
	// return "[" + pos.x + ":" + pos.y + "]";
	// }
	//
	/**
	 * Umwandelt Faktor (Werte > 0) in eine Prozentuelle Representation mit Plus und Minus Zeichen
	 */
	final private String bonusFaktorToString(double faktor)
	{
		double val = (faktor * 100) - 100;
		return GUI.formatProzentSigned(val);
	}

	/**
	 * @return ein Wert zwischen 0 und 1
	 */
	public double getFarmingFaktor()
	{
		if(!systemPropertiesGenerated) generateStarSystemProperties(mSeed);
		return farmingFaktor;
	}

	final public String getFarmingFaktorString()
	{
		return bonusFaktorToString(getFarmingFaktor());
	}

	/**
	 * @return ein Wert zwischen 0 und 1
	 */
	public double getMiningFaktor()
	{
		if(!systemPropertiesGenerated) generateStarSystemProperties(mSeed);
		return miningFaktor;
	}

	public String getMiningFaktorString()
	{
		return bonusFaktorToString(getMiningFaktor());
	}

	/**
	 * Bestimmt wieviel Prozentpunkte zum Bevölkerungswachstum addiert werden
	 * 
	 * @return positiver oder negativer wert
	 */
	public double getPopulationGrowBonus()
	{
		if(!systemPropertiesGenerated) generateStarSystemProperties(mSeed);
		return populationGrowBonus;
	}

	public String getPopulationGrowBonusString()
	{
		return bonusFaktorToString(getPopulationGrowBonus() + 1);
	}

	/**
	 * @return ein Wert zwischen 0 und 1
	 */
	public double getProductionFaktor()
	{
		if(!systemPropertiesGenerated) generateStarSystemProperties(mSeed);
		return productionFaktor;
	}

	public String getProductionFaktorString()
	{
		return bonusFaktorToString(getProductionFaktor());
	}

	/**
	 * @return ein Wert zwischen 0 und 1
	 */
	public double getScienceFaktor()
	{
		if(!systemPropertiesGenerated) generateStarSystemProperties(mSeed);
		return scienceFaktor;
	}

	public String getScienceFaktorString()
	{
		return bonusFaktorToString(getScienceFaktor());
	}

	/**
	 * Liefert HTML-formatierte Textdarstellung eines Sternesystems
	 * 
	 * @return
	 */
	public String getTooltipHtmlText()
	{
		if(!erforscht()) return "<html>Nicht erforscht</html>";
		StringBuilder b = new StringBuilder("<html><b>").append(toString()).append("</b><table>");
		b.append("<tr><td><b>Max. Population:</b></td><td>").append(GUI.formatLong(getMaxPopulation())).append("</td></tr>");
		b.append("<tr><td><b>Wachstum:</b></td><td>").append(getPopulationGrowBonusString()).append("</td></tr>");
		b.append("<tr><td><b>Landwirtschaft:</b></td><td>").append(getFarmingFaktorString()).append("</td></tr>");
		b.append("<tr><td><b>Production:</b></td><td>").append(getProductionFaktorString()).append("</td></tr>");
		b.append("<tr><td><b>Bergbau:</b></td><td>").append(getMiningFaktorString()).append("</td></tr>");
		b.append("<tr><td><b>Forschung:</b></td><td>").append(getScienceFaktorString()).append("</td></tr></table>");
		if(getNatRessources().size() > 0)
		{
			b.append("<br>~~<b>Natürliche Ressourcen</b>~~");
			b.append("<ul>");
			for(Iterator iter = getNatRessources().iterator(); iter.hasNext();)
			{
				NaturalResource res = (NaturalResource) iter.next();
				b.append("<li>").append(res.getName()).append("</li>");
			}
			b.append("</ul");
		}
		b.append("</html>");
		return b.toString();
		// String ret = "<html><b>"+toString()+"</b>";
		// if(erforscht())
		// {
		// ret += "<br><b>Max. Population:</b>" + GUI.formatLong(getMaxPopulation())+
		// "<br><b>Wachstum: </b>"+getPopulationGrowBonusString()+
		// "<br><b>Landwirtschaft: </b>"+getFarmingFaktorString()+
		// "<br><b>Production: </b>"+getProductionFaktorString()+
		// "<br><b>Bergbau: </b>"+getMiningFaktorString()+
		// "<br><b>Forschung: </b>"+getScienceFaktorString()+
		// "<br>~~<b>Natürliche Ressourcen</b>~~";
		// for(Iterator iter = getNatRessources().iterator(); iter.hasNext();)
		// {
		// NaturalResource res = (NaturalResource) iter.next();
		// ret += "<br>" + res.getName();
		// }
		// } else
		// {
		// ret += "<br>Nicht erforscht";
		// }
		//			
		// ret += "</html>";
		// return ret;
	}
}