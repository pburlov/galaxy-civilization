/*
 * $Id: SpaceBattleResult.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.net.battle;

/**
 * @author pb
 */
public class SpaceBattleResult
{

	private int destroyedEnemy;
	private int damagedEnemy;
	private int destroyedShips;
	private int damagedShips;
	private boolean colonyCaptured;
	private int colonyPop;

	/**
	 * @param destroyedEvil
	 * @param damagedEvil
	 * @param destroyedGood
	 * @param damagedGood
	 */
	public SpaceBattleResult(int destroyedEvil, int damagedEvil, int destroyedGood, int damagedGood, boolean colonyTaked, int colonyPop)
	{
		super();
		this.destroyedEnemy = destroyedEvil;
		this.damagedEnemy = damagedEvil;
		this.destroyedShips = destroyedGood;
		this.damagedShips = damagedGood;
		this.colonyCaptured = colonyTaked;
		this.colonyPop = colonyPop;
	}

	public int getColonyPop()
	{
		return colonyPop;
	}

	public boolean isColonyCaptured()
	{
		return colonyCaptured;
	}

	public int getDamagedEvil()
	{
		return damagedEnemy;
	}

	public int getDamagedGood()
	{
		return damagedShips;
	}

	public int getDestroyedEvil()
	{
		return destroyedEnemy;
	}

	public int getDestroyedGood()
	{
		return destroyedShips;
	}

	public void setColonyCaptured(boolean colonyCaptured)
	{
		this.colonyCaptured = colonyCaptured;
	}

	public void setColonyPop(int colonyPop)
	{
		this.colonyPop = colonyPop;
	}
}
