/*
 * $Id: ShipMovementManager.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.ai;

import java.awt.Point;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import mou.ClockEvent;
import mou.ClockListener;
import mou.Main;
import mou.Modul;
import mou.Subsystem;
import mou.core.ship.Ship;
import mou.core.ship.ShipDB;
import mou.core.ship.ShipMovementOrder;
import mou.core.ship.ShipMovementOrderDB;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * Klasse verwaltet die Bewegungen der Schiffen. Nur mit Hilfe dieser Klasse dürfen einzelne Schiffe
 * bewegt werden oder ihren derzeitigen Kurs ändern
 * 
 * @author pb
 */
public class ShipMovementManager extends Modul
		implements ClockListener
{

	// private StarmapDB starmapDB;
	private ShipDB shipDB;
	private ShipMovementOrderDB movementOrderDB;

	// private Universum mUniversum;
	public ShipMovementManager(Subsystem parent)
	{
		super(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#getModulName()
	 */
	public String getModulName()
	{
		return "ShipMovementManager";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#getPreferencesFile()
	 */
	protected File getPreferencesFile()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#shutdownIntern()
	 */
	protected void shutdownIntern()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.Modul#startModulIntern()
	 */
	protected void startModulIntern() throws Exception
	{
		// mUniversum = new Universum();
		// starmapDB = Main.instance().getMOUDB().getStarmapDB();
		shipDB = Main.instance().getMOUDB().getShipDB();
		movementOrderDB = Main.instance().getMOUDB().getShipMovementOrderDB();
		Main.instance().getClockGenerator().addClockListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.ClockListener#clockEvent(long)
	 */
	public void dailyEvent(ClockEvent event)
	{
		Iterator iter = movementOrderDB.getAllOrders().values().iterator();
		while(iter.hasNext())
		{
			ShipMovementOrder order = (ShipMovementOrder) iter.next();
			if(order.isArrived())
			{
				final Point dest = order.getTarget();
				Set ids = order.getShipIDs();
				int count = 0;
				for(Iterator iter2 = ids.iterator(); iter2.hasNext();)
				{
					ID id = (ID) iter2.next();
					Ship ship = shipDB.getShip(id);
					if(ship != null)
					{
						count++;
						ship.setPosition(dest);// Hier wird de DataChangedEvent an DBListener
						// ausgelöst
					} else
						iter2.remove();
				}
				movementOrderDB.deleteMovementOrder(order.getID());
				Main.instance().getGUI().promtMessage("Schiffe eingetroffen",
						count + " Schiff(e) haben ihr Ziel " + Main.instance().getMOUDB().getStarmapDB().getStarSystemAt(dest).toString() + " erreicht.",
						GUI.MSG_PRIORITY_NORMAL, new Runnable()
						{

							public void run()
							{
								Main.instance().getGUI().getMainFrame().getStarmapScreen().centerPosition(dest);
							}
						});
			}
		}
	}

	public void yearlyEvent(ClockEvent event)
	{
	}
}