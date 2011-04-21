/*
 * $Id: CivDayReport.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.civilization;

/**
 * Classe zum Sammeln und Anzeigen aller relevanten Informationen zur eigenen Zivilisation
 * 
 * @author pb
 */
public class CivDayReport
{
	
	/*
	 * !!! Achtung:
	 * 
	 * Interface verändert - getFoodBalance() gibt globalen Überschuß, bzw. Bedarf an
	 * getFood() und steFood dienen jetzt zum Zivilisations-internen Nahrungsaustausch! 
	 */

	private long time;// Zeit der Datenerfassung
	private double workFaktor;
	private double taxRate;
	// private double bruttoIncome;
	private double shipSupportCost;
	private double colonySupportcost;
	private double sciencePoints;
	private double production;
	private double food;
	private double foodBalance;
	private double farming;
	private double mining;
	private double population;
	private long colonies;
	private double medianPopulationGrow;
	private double moralFactor;

	/**
	 * 
	 */
	public CivDayReport()
	{
		super();
	}

	public double getMoralFactor()
	{
		return moralFactor;
	}

	public void setMoralFaktor(double rebelFaktor)
	{
		this.moralFactor = rebelFaktor;
	}

	public double getTaxRate()
	{
		return taxRate;
	}

	public void setTaxRate(double taxRate)
	{
		this.taxRate = taxRate;
	}

	public double getWorkFaktor()
	{
		return workFaktor;
	}

	public void setWorkFaktor(double workFaktor)
	{
		this.workFaktor = workFaktor;
	}

	public long getColonies()
	{
		return colonies;
	}

	public void setColonies(long colonies)
	{
		this.colonies = colonies;
	}

	public double getMedianPopulationGrow()
	{
		return medianPopulationGrow;
	}

	public void setMedianPopulationGrow(double medianPopulationGrow)
	{
		this.medianPopulationGrow = medianPopulationGrow;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public double getMining()
	{
		return mining;
	}

	public void setMining(double mining)
	{
		this.mining = mining;
	}

	public double getPopulation()
	{
		return population;
	}

	public void setPopulation(double population)
	{
		this.population = population;
	}

	public double getProduction()
	{
		return production;
	}

	public void setProduction(double prodiction)
	{
		this.production = prodiction;
	}

	public double getSciencePoints()
	{
		return sciencePoints;
	}

	public void setSciencePoints(double points)
	{
		this.sciencePoints = points;
	}

	public double getSupportCostShips()
	{
		return shipSupportCost;
	}

	public void setShipSupportCost(double shipSupportCost)
	{
		this.shipSupportCost = shipSupportCost;
	}

	public double getBruttoIncome()
	{
		return getBSP() * getTaxRate();
	}

	/*
	 * public void setBruttoIncome(double totalIncome) { this.bruttoIncome = totalIncome; }
	 */
	/**
	 * Berechnet aus Gesamteinkommen abzüglich Supportkosten den verfügbaren Nettoeinkommen
	 * 
	 * @return
	 */
	public double computeNettoIncome()
	{
		return getBruttoIncome() - getSupportCostShips() - getSupportCostBuildings();
	}

	public double getSupportCostBuildings()
	{
		return colonySupportcost;
	}

	public void setColonySupportcost(double colonySupportcost)
	{
		this.colonySupportcost = colonySupportcost;
	}

	public double getFood()
	{
		return food;
	}

	public void setFood(double food)
	{
		this.food = food;
	}
	
	public double getFoodBalance()
	{
		return foodBalance;
	}
	
	public void setFoodBalance(double foodBalance)
	{
		this.foodBalance = foodBalance;
	}

	public double getFarming()
	{
		return farming;
	}

	public void setFarming(double farming)
	{
		this.farming = farming;
	}

	public double getBSP()
	{
		return getMining() + getSciencePoints() + getProduction() + getMining() + (getFarming() / 1000);
	}

	public double getBspPerCapita()
	{
		return getBSP() / getPopulation();
	}
}
