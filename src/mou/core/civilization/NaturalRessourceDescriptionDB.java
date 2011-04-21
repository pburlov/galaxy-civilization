/*
 * $Id: NaturalRessourceDescriptionDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.civilization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import mou.core.res.natural.NaturalResource;
import mou.storage.ser.ID;

/**
 * Diese DB h�lt die Beschreibungen der vorkommeden nat�rlichen Resourcen
 * 
 * @author pbu
 */
public class NaturalRessourceDescriptionDB
{

	private static final Random _random = new Random();
	/**
	 * Prim�re Datenspeicher Key: ID; Value: NaturalResource-Objekte
	 */
	private static final HashMap<ID, NaturalResource> data = new HashMap<ID, NaturalResource>();

	public NaturalRessourceDescriptionDB()
	{
		NaturalResource res = new NaturalResource("Scandium", 21, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Beryllium", 4, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Silizium", 14, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Titan", 22, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Vanadium", 23, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Chrom", 24, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Mangan", 25, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Eisen", 26, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Kobalt", 27, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Nikel", 28, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Kupfer", 29, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Yttrium", 39, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Zirkonium", 40, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Niob", 41, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Molybd�n", 42, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Ruthenium", 44, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Rhodium", 45, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Hafnium", 72, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Tantal", 73, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Wolfram", 74, 2.0f);
		data.put(res.getID(), res);
		res = new NaturalResource("Rhenium", 75, 2.0f);
		data.put(res.getID(), res);
	}

	/**
	 * Liefert Liste mit allen bekannten Materialien
	 * 
	 * @return
	 */
	public static Collection<NaturalResource> getAllKnownMaterials()
	{
		return data.values();
	}

	/**
	 * @param seed
	 *            Initialisierungswert f�r Random-Generator
	 * @param planet
	 *            Planet, f�r die Ressourcen generiert werden m�ssen
	 * @return Map mit NaturalResource Objekten
	 */
	public List<NaturalResource> generateRessource(final long seed)
	{
		ArrayList<NaturalResource> ret = new ArrayList<NaturalResource>();
		synchronized(_random)
		{
			_random.setSeed(seed);
			Iterator iter = data.values().iterator();
			while(iter.hasNext())
			{
				NaturalResource res = (NaturalResource) iter.next();
				float wert = _random.nextFloat() * 100;// von 0% bis 100%
				if(wert < res.getProbality())// Wahrscheinlichkeit pr�fen
				{
					ret.add(res);
				}
			}
		}
		return ret;
	}

	// /**
	// * Collection mit allen m�glichen Metallen (NaturalResource-Objekte)
	// * @return
	// */
	// public Collection getAllMetalls()
	// {
	// return Collections.unmodifiableCollection(metalls);
	// // Collection ret = new ArrayList();
	// // ret.addAll(getDataWhere(
	// // RessourceDescription.ATTR_TYP, RessourceDescription.TYP_METALL).getList());
	// // //mit dynamischen Ressourcen fusionieren
	// //// ret.addAll(getDataWhere(
	// //// IRessourceDescription.ATTR_TYP, IRessourceDescription.TYP_METALL).getList());
	// // return Collections.unmodifiableCollection(ret);
	// }
	public NaturalResource getNaturalResource(ID id)
	{
		NaturalResource res = (NaturalResource) data.get(id);
		if(res == null) throw new IllegalStateException("Nat�rliche Resource mit ID: " + id + " nicht gefunden.");
		return res;
	}
	/*
	 * private class NatRes { public String name; public float probality; public NatRes(String name,
	 * float prob) { this.name = name; probality = prob ; } }
	 */
}
