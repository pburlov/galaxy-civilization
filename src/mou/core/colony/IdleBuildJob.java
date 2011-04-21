/*
 * $Id: IdleBuildJob.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.colony;

import java.util.Map;

/*
 * Um die Produktionkapazitäten nicht im Leerlauf lassen, werden Konsumprodukte (Einkommen)
 * generiert. Dazu werden Objekte dieser Klasse in die BuildQueue geschoben wenn sie leer ist.
 */
public class IdleBuildJob extends BuildJobAbstract
{

	public IdleBuildJob(Map data)
	{
		super(data);
	}

	public IdleBuildJob()
	{
		super(TYP_IDLE, false);
	}

	@Override
	public String getName()
	{
		return "Handelswaren";
	}

	@Override
	public double computeNeededWorkPoints()
	{
		return 1.0d;
	}

	@Override
	public double getProgress()
	{
		return 1.0d;
	}

	@Override
	public double getInvestedProduction()
	{
		return 1.0d;
	}

	@Override
	public BuildAllowed startBuild(Colony colony)
	{
		return new BuildAllowed(true, "");
	}

	@Override
	public void cancelBuild(Colony colony)
	{
	}

	@Override
	public void completeBuild(Colony colony)
	{
	}

	@Override
	public void proceedBuild(Colony colony, double production)
	{
		/*
		 * 1/10 der Produktion wird in Einkommen umgewandelt
		 */
		colony.setBruttoIncome(colony.getBruttoIncome() + production * 0.1);
	}

	@Override
	public boolean isCompleted()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
