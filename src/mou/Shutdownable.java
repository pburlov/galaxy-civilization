/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

public interface Shutdownable
{

	/**
	 * Die Methode muss nur einmal, aufgerufen werden. Wiederholtes Aufrufen soll keine Wirkung
	 * zeigen. Es sollen keine Abhängigkeiten in der Shutdown-Reihenfolge von verschiedenen
	 * Subsystemen geben.
	 */
	public void shutdown();
}