/*
 * $Id: BuildingUiAbstract.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res.colony;

import javax.swing.JPanel;

abstract public class BuildingUiAbstract extends JPanel
{

	public BuildingUiAbstract()
	{
		super();
	}

	abstract public void startRefreshTimer();
	abstract public void stopRefreshTimer();
	abstract public void refreshValues();
}
