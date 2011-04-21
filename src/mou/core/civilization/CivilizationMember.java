/*
 * $Id: CivilizationMember.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.civilization;

/**
 * Alle Objekte einer Zilisation die Werte einer Zivilisation aktiv ver�ndern wollen m�ssen diese
 * Interface implementieren, und sich bei CivilizationDB registrieren.
 * 
 * @author pb
 */
public interface CivilizationMember
{

	/**
	 * In dieser Methode sollen Objekte der Zivilisation ihre t�gliche Arbeit machen
	 * 
	 * @param dayValues
	 */
	public void doDailyWork(CivDayReport dayValues);

	/**
	 * In dieser Methode sollen Objekte der Zivilisation ihre j�hrliche Arbeit machen
	 * 
	 * @param yearValues
	 */
	public void doYearlyWork(CivYearReport yearValues);
}
