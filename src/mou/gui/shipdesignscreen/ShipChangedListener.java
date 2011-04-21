/*
 * $Id: ShipChangedListener.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.shipdesignscreen;

import mou.core.research.ResearchableDesign;

/**
 * @author pb
 */
public interface ShipChangedListener
{

	/**
	 * @param system
	 */
	public void shipsystemRemoved(ResearchableDesign system);

	/**
	 * Wenn ein Schiffssystem geändert wurde
	 * 
	 * @param system
	 */
	public void shipsystemChanged(ResearchableDesign system);
}
