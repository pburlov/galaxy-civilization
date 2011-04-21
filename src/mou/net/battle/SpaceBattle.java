/*
 * $Id: SpaceBattle.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.net.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author pb
 */
public class SpaceBattle
{

	static final private int MAX_ROUNDS = 50;
	private List<ShipInfo> zerstoertAngreifer = new ArrayList<ShipInfo>();
	private List<ShipInfo> beschaedigtAngreifer = new ArrayList<ShipInfo>();
	private List<ShipInfo> zerstoertVerteidiger = new ArrayList<ShipInfo>();
	private List<ShipInfo> beschaedigtVerteidiger = new ArrayList<ShipInfo>();
	private List<ShipInfo> angreifer = new ArrayList<ShipInfo>();
	private List<ShipInfo> verteidiger = new ArrayList<ShipInfo>();
	private Random random = new Random();

	/**
	 * 
	 */
	public SpaceBattle(List<ShipInfo> angreifer, List<ShipInfo> verteidiger, long seed)
	{
		super();
		this.angreifer.addAll(angreifer);
		this.verteidiger.addAll(verteidiger);
		random.setSeed(seed);
	}

	public void battle()
	{
		int count = 0;
		while(count < MAX_ROUNDS && !computeRound())
		{
			count++;
		}
		for(ShipInfo info : verteidiger)
			if(info.getStruktur() != info.getStrukturBackup()) beschaedigtVerteidiger.add(info);
		for(ShipInfo info : angreifer)
			if(info.getStruktur() != info.getStrukturBackup()) beschaedigtAngreifer.add(info);
	}

	/**
	 * Berechnet eine Kampfrunde
	 * 
	 * @return true wenn eine Seite besiegt wurde
	 */
	private boolean computeRound()
	{
		if(verteidiger.size() == 0 || angreifer.size() == 0) return true;
		/*
		 * Zuerst alle Schilden regenerieren
		 */
		resetShilds(angreifer);
		resetShilds(verteidiger);
		/*
		 * Zuerst feuern alle Angreifer auf zufällig ausgewählte Schiffe des Verteidigers
		 */
		ShipInfo vert = null;
		int index = -1;
		for(ShipInfo ang : angreifer)
		{
			int waffe = ang.getWaffen();
			do
			{
				/*
				 * Prüfen ob noch jemand übrig bleibt
				 */
				if(verteidiger.size() == 0) return true;
				if(vert == null)
				{
					index = random.nextInt(verteidiger.size());
					vert = verteidiger.get(index);
				}
				waffe = hitShip(vert, waffe);
				if(vert.getStruktur() == 0)
				{
					verteidiger.remove(index);
					zerstoertVerteidiger.add(vert);
					vert = null;
				}
			} while(waffe > 0);
		}
		vert = null;
		/*
		 * Dann feuern alle verteidiger auf zufällig ausgewählte Schiffe des Angreifers
		 */
		for(ShipInfo ang : verteidiger)
		{
			int waffe = ang.getWaffen();
			do
			{
				/*
				 * Prüfen ob noch jemand übrig bleibt
				 */
				if(angreifer.size() == 0) return true;
				if(vert == null)
				{
					index = random.nextInt(angreifer.size());
					vert = angreifer.get(index);
				}
				waffe = hitShip(vert, waffe);
				if(vert.getStruktur() == 0)
				{
					angreifer.remove(index);
					zerstoertAngreifer.add(vert);
					vert = null;
				}
			} while(waffe > 0);
		}
		return false;
	}

	/**
	 * @param target
	 * @param weapon
	 * @return Unverbrauchte Waffenenergie, wenn das Ziel zerstört wurde
	 */
	private int hitShip(ShipInfo target, int weapon)
	{
		if(target.getShild() >= weapon)
		{
			target.setShild(target.getShild() - weapon);
			return 0;
		}
		weapon -= target.getShild();
		target.setShild(0);
		if(target.getPanzerung() >= weapon)
		{
			target.setPanzerung(target.getPanzerung() - weapon);
			return 0;
		}
		weapon -= target.getPanzerung();
		target.setPanzerung(0);
		if(target.getStruktur() >= weapon)
		{
			target.setStruktur(target.getStruktur() - weapon);
			return 0;
		}
		weapon -= target.getStruktur();
		target.setStruktur(0);
		return weapon;
	}

	private void resetShilds(List<ShipInfo> ships)
	{
		for(ShipInfo info : ships)
			info.setShild(info.getShildBackup());
	}

	/**
	 * Liefert Liste der zerstörten feindlichen Schiffe
	 * 
	 * @return
	 */
	public List<ShipInfo> getDestroyedInvader()
	{
		return zerstoertAngreifer;
	}

	/**
	 * Liefert Liste der beschädigten feindlichen Schiffe
	 * 
	 * @return
	 */
	public List<ShipInfo> getDamagedInvader()
	{
		return beschaedigtAngreifer;
	}

	/**
	 * Liefert Liste der beschädigten eigenen Schiffe
	 * 
	 * @return
	 */
	public List<ShipInfo> getDestroyedDefender()
	{
		return zerstoertVerteidiger;
	}

	/**
	 * Liefert Liste der zerstörten eigenen Schiffes
	 * 
	 * @return
	 */
	public List<ShipInfo> getDamagedDefender()
	{
		return beschaedigtVerteidiger;
	}
}
