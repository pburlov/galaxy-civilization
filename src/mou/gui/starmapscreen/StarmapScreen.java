/*
 * $Id: StarmapScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.starmapscreen;

import java.awt.BorderLayout;
import java.awt.Point;
import java.util.List;
import javax.swing.JPanel;
import mou.Main;
import mou.Preferences;
import mou.gui.GUIScreen;

/**
 * Darstellt den Sternenkarte-Bildschirm
 */
public class StarmapScreen extends JPanel
		implements GUIScreen
{

	// private static final String PREFS_SELECTED_STAR_X = "StarmapCenter_X";
	// private static final String PREFS_SELECTED_STAR_Y = "StarmapCenter_Y";
	private StarmapViewer starmapViewer = new StarmapViewer();

	public StarmapScreen()
	{
		setLayout(new BorderLayout());
		add(starmapViewer, BorderLayout.CENTER);
		// this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	}

	/**
	 * Zentriert Karte auf die angegebene Sternenkoordinaten
	 */
	public void goTo(Point location)
	{
		starmapViewer.goTo(location);
	}

	/**
	 * Zentriert die Sternenkarte auf diese Koordinaten und selektiert ein Sternsystem die bei
	 * diesen Koordinaten liegt. Es wirkt genauso als ob man mit der Maus auf das Stern geclickt
	 * hätte.
	 */
	public void centerPosition(Point location)
	{
		goTo(location);
	}

	/**
	 * @param shipsAndFleets
	 *            Set mit ID Objekten
	 */
	synchronized public void switchToTargetSelectMode(List ships)
	{
		starmapViewer.switchToTargetSelectMode(ships);
	}

	/**
	 * Speichert interne relevante Daten in zur Verfügung gestellten Preferences-Object
	 */
	public void saveProperties(Preferences prefs)
	{
		Point centeredPos = starmapViewer.getCenteredPosition();
		/*
		 * Anstatt aktuelle Position in der Konfigurationdatei zu speicher, lieber direkt in die
		 * Datenbank schreiben
		 */
		// prefs.setProperty(PREFS_SELECTED_STAR_X, Integer.toString(centeredPos.x));
		// prefs.setProperty(PREFS_SELECTED_STAR_Y, Integer.toString(centeredPos.y));
		Main.instance().getMOUDB().getMaintenaceDB().setStarmapPosition(centeredPos);
	}

	/**
	 * Wiederherstellt interne relevante Daten aus dem zur Verfügung gestellten Preferences-Object
	 */
	public void restoreProperties(Preferences prefs)
	{
		// int selectX = prefs.getAsInteger(PREFS_SELECTED_STAR_X, new Integer(0)).intValue();
		// int selectY = prefs.getAsInteger(PREFS_SELECTED_STAR_Y, new Integer(0)).intValue();
		Point pos = Main.instance().getMOUDB().getMaintenaceDB().getStarmapPosition();
		if(pos == null) pos = new Point(0, 0);
		centerPosition(pos);
	}
	/**
	 * Gespeicherte Zustandinformationen wiederherstellen
	 */
	/*
	 * private void initFromPrefs() { // setCentralPoint(new Point(selectX, selectY)); }
	 */
}