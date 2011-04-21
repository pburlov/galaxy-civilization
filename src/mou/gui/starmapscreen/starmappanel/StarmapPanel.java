/*
 * $Id: StarmapPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.starmapscreen.starmappanel;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import mou.ClockEvent;
import mou.ClockListener;
import mou.LoggerOwner;
import mou.Main;
import mou.core.civilization.Civilization;
import mou.core.civilization.CivilizationDB;
import mou.core.colony.Colony;
import mou.core.colony.ColonyDB;
import mou.core.colony.ForeignColonyDB;
import mou.core.colony.ForeignColonyPersistent;
import mou.core.ship.FremdeSchiffeDB;
import mou.core.ship.FremdeSchiffeInfo;
import mou.core.ship.ShipDB;
import mou.core.ship.ShipMovementOrder;
import mou.core.ship.ShipMovementOrderDB;
import mou.core.starmap.StarSystem;
import mou.core.starmap.StarmapDB;
import mou.gui.GUI;
import mou.gui.GUIConstants;
import mou.gui.StringDrawer;
import mou.gui.WaitDialog;
import mou.gui.starmapscreen.ShipTableDialog;
import mou.gui.starmapscreen.StarmapViewer;
import mou.net.battle.SpaceBattleResult;
import mou.storage.ser.ID;

/**
 * GUI-Element der direkt Sternenkarteausschnitt anzeigt
 */
public class StarmapPanel extends JPanel
		implements LoggerOwner
{

	static final public Color COLOR_O = new Color(0.5f, 0.5f, 1.0f, 1.0f);// Tiefblau
	static final public Color COLOR_B = new Color(0.8f, 0.8f, 1.0f, 1.0f);// Weiss/Blau
	static final public Color COLOR_A = new Color(1.0f, 1.0f, 0.9f, 1.0f);// Weiss/Gelb
	static final public Color COLOR_F = new Color(1.0f, 1.0f, 0.7f, 1.0f);// Hellgelb
	static final public Color COLOR_G = new Color(1.0f, 1.0f, 0.5f, 1.0f);// Gelb
	static final public Color COLOR_K = new Color(1.0f, 0.8f, 0.0f, 1.0f);// Gelb/R?tlich
	static final public Color COLOR_M = new Color(0.9f, 0.0f, 0.0f, 1.0f);// Rot
	static final public Color COLOR_L = new Color(0.7f, 0.0f, 0.0f, 1.0f);// Braun
	static final public Color COLOR_Z0 = new Color(0.7f, 0.7f, 1.0f, 1.0f);// Blau
	static final public Color COLOR_Z1 = new Color(1.0f, 1.0f, 1.0f, 1.0f);// Weiss
	static final public Color COLOR_Z2 = new Color(0.9f, 0.9f, 0.5f, 1.0f);// Gelb
	static final public Color COLOR_Z3 = new Color(0.9f, 0.0f, 0.0f, 1.0f);// Rot
	static final public Color COLOR_R0 = new Color(1.0f, 0.0f, 0.0f, 1.0f);// Rot
	static final public Color COLOR_R1 = new Color(0.9f, 0.0f, 0.0f, 1.0f);// Rot
	static final public Color COLOR_R2 = new Color(0.8f, 0.0f, 0.0f, 1.0f);// Rot
	static final public Color STARNAME_COLOR_UNERFORSCHT = GUIConstants.COLOR_UNERFORSCHT;
	static final public Color STARNAME_COLOR_ERFORSCHT = Color.WHITE;
	static final public Color STARNAME_COLOR_MEINE_KOLONIE = GUIConstants.COLOR_MEIN;
	static final public Color STARNAME_REBEL_KOLONIE = Color.PINK;
	static final public Color STARNAME_COLOR_ALLIIERTE_KOLONIE = GUIConstants.COLOR_ALLIIERT;
	static final public Color STARNAME_COLOR_NEUTRALE_KOLONIE = GUIConstants.COLOR_NEUTRAL;
	static final public Color STARNAME_COLOR_FEINDLICHE_KOLONIE = GUIConstants.COLOR_FEINDLICH;
	static final public Color COLOR_SHIP_ROUTE = Color.GREEN;
	static final public Color COLOR_TEMP_SHIP_ROUTE = Color.BLUE;
	/*
	 * static final private Polygon FLOTTE_PICKTOGRAM = new Polygon(new int[] { 0, 4, 8, 0}, new
	 * int[] { 0, 6, 0, 0}, 4);
	 */
	// static private final int SIZE_LITTLE = 6; // Grosse der Sterndarstellung
	// in Pixel
	// static private final int SIZE_MIDDLE = 10;
	// static private final int SIZE_BIG = 16;
	// static private final int SIZE_VERY_BIG = 20;
	static private final float SIZE_VERY_LITTLE = 0.3f; // Grosse der
	// Sterndarstellung in
	// Pixel
	static private final float SIZE_LITTLE = 0.6f; // Grosse der
	// Sterndarstellung in Pixel
	static private final float SIZE_MIDDLE = 0.9f;
	static private final float SIZE_BIG = 1.2f;
	static private final float SIZE_VERY_BIG = 1.5f;
	static private final Font STARNAME_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private static final int STARNAME_ZOOM_LEVEL = 5; // Ab dieser Zoomstufe
	// werden keine
	// Sternennamen
	// gezeichnet
	private static final int ZOOM_STANDART = 10;
	private static final int MAX_ZOOM_FAKTOR = 80; // Pixel pro LJ
	private static final int MIN_ZOOM_FAKTOR = 2;
	/**
	 * Breite der Randzone die mit dem Eintritt des Mauszeigers die Karte verschiebt in die
	 * entsprechende Richtung
	 */
	private static final int MOVE_BORDER_WIDTH = 40;
	/**
	 * In welchen Zeitintervall wird die Karte verschoben (in millis)
	 */
	private static final int MOVE_DELAY = 100;
	/**
	 * Mit welchen Schritten wird die Karte verschoben ( in Pixel)
	 */
	private static final int MOVE_STEP = 40;
	private static final Cursor CURSOR_ACTION = new Cursor(Cursor.HAND_CURSOR);
	private static final Cursor CURSOR_NORMAL = new Cursor(Cursor.CROSSHAIR_CURSOR);
	/*
	 * Verz?gerung, bei der wird signalisiert, dass der Mauszeiger ?ber eine Position zum stehen
	 * gekommen ist
	 */
	private static final long MOUSE_STOPPED_DELAY = 50;
	private int zoom = ZOOM_STANDART; // Wieviel Bildschirmpunkten ein Lichjahr
	// enth?lt
	private StarmapViewer parent;
	/**
	 * Diese Koordinate markiert linke obere Ecke des Kartenauschnittes in virtuellen
	 * Pixelkoordinaten Sternenkoordinate 0:0 ist der Bezugspunkt f?r die virtuelle Pixelkoordinaten
	 * und hat virtuelle Koordinate 0:0. Pixelkoordinaten f?r andere Sterne werden mit Hilfe von
	 * zoom-Variable berechnet, die besagt wieviele virtuelle Pixel pro Lichtjahr berechnet werden.
	 */
	// private Point viewPoint = new Point(0,0);
	// private Point selectedStar;
	private Point virtualPixelCenterPoint = new Point(0, 0);
	private Point leftUpper = null;
	private Point rightDown = null;
	private List sterne;
	private javax.swing.Timer _moveTimer; // Zust?ndig f?r automatische
	// Verschiebung der Karte
	private Cursor _mapCursor = CURSOR_NORMAL;
	private Cursor _starCursor = CURSOR_ACTION;
	private StarmapDB mStarmapDB = Main.instance().getMOUDB().getStarmapDB();
	// private Universum mUniversum = new Universum();
	private ShipMovementOrderDB mShipMovementOrderDB = Main.instance().getMOUDB().getShipMovementOrderDB();
	private PopupMenu popupMenu = new PopupMenu();
	private ForeignColonyDB fremdeKolonienDB = Main.instance().getMOUDB().getFremdeKolonienDB();
	private ShipDB shipDB = Main.instance().getMOUDB().getShipDB();
	private ShipDB rebelShipDB = Main.instance().getMOUDB().getRebelShipDB();
	private FremdeSchiffeDB fremdeSchiffeDB = Main.instance().getMOUDB().getFremdeSchiffeDB();
	private boolean selectRouteMode = false;
	private List selectedShips;
	private long lastMouseMoveTime = 0;
	private MouseEvent lastMouseEvent;
	private List<ActionArea> areaActions;
	private JToolTip infoPopupContext = new JToolTip();
	// private JPanel panelInfoPopupContext = new JPanel();
	private Popup infoPopup;
	private ShipTableDialog shipTableDialog;
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();

	public StarmapPanel(StarmapViewer parent)
	{
		this.parent = parent;
		shipTableDialog = new ShipTableDialog(Main.instance().getGUI().getMainFrame());
		// setComponentPopupMenu(popupMenu);
		// ToolTipManager.sharedInstance().setDismissDelay(30000);
		// panelInfoPopupContext.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		// panelInfoPopupContext.add(labelInfoPopupContext);
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);// &
		// MouseEvent.MOUSE_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		this.setBackground(Color.BLACK);
		this.setLayout(null);
		this.addComponentListener(new ComponentAdapter()
		{

			public void componentResized(ComponentEvent ev)
			{
				refreshStarmap();
			}
		});
		this.addMouseWheelListener(new MouseWheelListener()
		{

			public void mouseWheelMoved(MouseWheelEvent ev)
			{
				int clicks = ev.getWheelRotation();
				if(clicks == 0) return;
				if(clicks < 0)
					increaseZoom(StrictMath.abs(clicks));
				else
					decreaseZoom();
			}
		});
		/*
		 * Anstatt von vielen DBListener wird hier die gesamte Kartenauschnitt regelm??ig neu
		 * gezeichnet
		 */
		Main.instance().getClockGenerator().addClockListener(new ClockListener()
		{

			public void yearlyEvent(ClockEvent event)
			{
			}

			public void dailyEvent(ClockEvent event)
			{
				if(isShowing()) refreshStarmap();
			}
		});
		/*
		 * Timer zur Erkennung des Mausstillstandes starten
		 */
		new Timer(100, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if((System.currentTimeMillis() - lastMouseMoveTime) >= MOUSE_STOPPED_DELAY)
				{
					mouseStopped(lastMouseEvent);
					lastMouseEvent = null;
				}
			}
		}).start();
	}

	private int getZoom()
	{
		return zoom;
	}

	public Logger getLogger()
	{
		return Main.instance().getGUI().getLogger();
	}

	/**
	 * Berechnet die Eckpunkte des Ausschnittes aus der Sternenkarte in Sternenkoordinaten der
	 * angezeigt werden soll.
	 */
	private void computeStarmapShape()
	{
		Point viewPoint = getViewPoint();
		leftUpper = translateVirtuellPixelToStarmapPoint(viewPoint);
		rightDown = translateVirtuellPixelToStarmapPoint(new Point(viewPoint.x + getWidth(), viewPoint.y - getHeight()));
	}

	/**
	 * Setzt die Sternenkoordinaten auf die Anzeige zetriert werden soll
	 */
	public void setCentralPoint(Point point)
	{
		setVirtualPixelCentralPoint(translateStarmapPointToVirtuellPixel(point));
	}

	/**
	 * Liefert den Starmap Punkt auf dem Anzaige tentriert ist
	 * 
	 * @return
	 */
	public Point getCentralPoint()
	{
		return translateVirtuellPixelToStarmapPoint(virtualPixelCenterPoint);
	}

	/**
	 * Zentriert Kartenauschnitt auf angegebene virtuelle Pixelkoordinate
	 */
	private void setVirtualPixelCentralPoint(Point point)
	{
		virtualPixelCenterPoint = point;
		refreshStarmap();
	}

	/*
	 * private void setViewPoint(Point point) { viewPoint = point; refreshStarmap(); }
	 */
	private Point getViewPoint()
	{
		return new Point(virtualPixelCenterPoint.x - getWidth() / 2, virtualPixelCenterPoint.y + getHeight() / 2);
	}

	public void increaseZoom()
	{
		increaseZoom(2);
	}

	private void increaseZoom(int faktor)
	{
		int newZoom = zoom + faktor;
		if(newZoom > MAX_ZOOM_FAKTOR)
			newZoom = MAX_ZOOM_FAKTOR;
		else
		{
			float proportion = (float) zoom / (float) newZoom;
			Point centralPoint = virtualPixelCenterPoint;// computeViewCenter();//
			// new
			// Point(viewPoint.x /
			// proportion,
			// viewPoint.y
			// /proportion);
			Point newCentralPoint = new Point((int) (centralPoint.x / proportion), (int) (centralPoint.y / proportion));
			zoom = newZoom;
			setVirtualPixelCentralPoint(newCentralPoint);
		}
	}

	public void decreaseZoom()
	{
		decreaseZoom(2);
	}

	public void decreaseZoom(int faktor)
	{
		int newZoom = zoom - faktor;
		if(newZoom < MIN_ZOOM_FAKTOR)
			newZoom = MIN_ZOOM_FAKTOR;
		else
		{
			float proportion = (float) zoom / (float) newZoom;
			Point centralPoint = virtualPixelCenterPoint;// computeViewCenter();//
			// new
			// Point(viewPoint.x /
			// proportion,
			// viewPoint.y
			// /proportion);
			Point newCentralPoint = new Point((int) (centralPoint.x / proportion), (int) (centralPoint.y / proportion));
			zoom = newZoom;
			setVirtualPixelCentralPoint(newCentralPoint);
		}
	}

	/**
	 * Karte wird gel?scht und neu aufgebaut. Diese Methode wird aufgerufen, wenn Zoom oder
	 * Zentralpunkt ge?ndert wurde
	 */
	private void refreshStarmap()
	{
		computeStarmapShape();
		sterne = mStarmapDB.getStarSystemsInPolygon(leftUpper, rightDown); // SternSystem-Objekte
		areaActions = new ArrayList<ActionArea>(sterne.size() + 1);
		repaint();
	}

	// public StarSystem selectStar(Point position)
	// {
	// // Iterator iter = sterne.iterator();
	// StarSystem ss = Main.instance().getMOUDB().getStarmapDB().getStarSystemAt(
	// position);
	// /*
	// * while(iter.hasNext()) { StarmapPanel.StarMetrics metrics = (StarMetrics)iter.next();
	// * if(metrics.star.getPosition().equals(position)) { selectedStar = position; ss =
	// * metrics.star; break; } }
	// */
	// if(ss == null) return null; // Kein Stern mit diesen Koordinaten
	// // gefunden
	// selectedStar = position;
	// // setCentralPoint(position);
	// repaint();
	// fireStarmapEvent(new StarmapEvent(StarmapEvent.STAR_CLICKED, ss));
	// return ss;
	// }
	public void paintComponent(Graphics gr)
	{
		/*
		 * Zuerst alles l?schen
		 */
		gr.setFont(STARNAME_FONT);
		gr.setColor(Color.BLACK);
		gr.fillRect(0, 0, getWidth(), getHeight());
		/*
		 * Dann alle Informationen ?bereinnander zeichnen
		 */
		paintStars(gr);
		paintShipRoutes(gr);// Schiffsroute aus der DB zeichnen
		paintTempShipRoutes(gr);//
	}

	private void paintStars(Graphics gr)
	{
		areaActions.clear();
		for(Iterator iter = sterne.iterator(); iter.hasNext();)
		{
			StarSystem ss = (StarSystem) iter.next();
			paintStar(ss, gr);
		}
	}

	/**
	 * MEthode zeichnet einzelne Stern mit allen Zusatzinformationen
	 * 
	 * @param ss
	 * @param gr
	 */
	private void paintStar(final StarSystem ss, Graphics gr)
	{
		/*
		 * Graphische koordinaten des zu zeichendes Sternens ermitteln
		 */
		Point grPos = translateStarmapPointToViewPixel(ss.getPosition());
		/*
		 * Zuerst Abmessungen und Farbe des Sternes berechnen
		 */
		String starclass = ss.getStarClass();
		Color color = Color.GREEN;
		float size = SIZE_BIG;
		if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_A))
		{
			color = COLOR_A;
			size = SIZE_MIDDLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_B))
		{
			color = COLOR_B;
			size = SIZE_BIG;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_F))
		{
			color = COLOR_F;
			size = SIZE_MIDDLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_G))
		{
			color = COLOR_G;
			size = SIZE_MIDDLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_K))
		{
			color = COLOR_K;
			size = SIZE_MIDDLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_M))
		{
			color = COLOR_M;
			size = SIZE_VERY_LITTLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_L))
		{
			color = COLOR_L;
			size = SIZE_VERY_LITTLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_O))
		{
			color = COLOR_O;
			size = SIZE_VERY_BIG;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_RIESE_0))
		{
			color = COLOR_R0;
			size = SIZE_VERY_BIG;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_RIESE_1))
		{
			color = COLOR_R1;
			size = SIZE_VERY_BIG;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_RIESE_2))
		{
			color = COLOR_R2;
			size = SIZE_BIG;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_ZWERG_0))
		{
			color = COLOR_Z0;
			size = SIZE_LITTLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_ZWERG_1))
		{
			color = COLOR_Z1;
			size = SIZE_LITTLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_ZWERG_2))
		{
			color = COLOR_Z2;
			size = SIZE_LITTLE;
		} else if(starclass.equals(mou.core.starmap.StarSystem.STAR_TYP_ZWERG_3))
		{
			color = COLOR_Z3;
			size = SIZE_LITTLE;
		}
		int starSizePixel = Math.round(getZoom() * size) + 1;// Mindestens 1 Pixel gro?
		int starOvalX = grPos.x - starSizePixel / 2;
		int starOvalY = grPos.y - starSizePixel / 2;
		/*
		 * Stern mit ermittelten Daten zeichnen
		 */
		gr.setColor(color);
		gr.fillOval(starOvalX, starOvalY, starSizePixel, starSizePixel);// Sternenkern
		// if(selectedStar != null && selectedStar.equals(ss.getPosition()))
		// {//Selektionsrahmen zeichnen
		// gr.setColor(SELECT_COLOR);
		// gr.drawRect(starOvalX - 1, starOvalY - 1, starSizePixel + 1, starSizePixel + 1);
		// }
		/*
		 * Zusatzinformationen zum Stern zeichnen
		 */
		if(getZoom() < STARNAME_ZOOM_LEVEL) return;// Ab einem bestimmten Zoomlevel keine
		// Zusatzinformation
		/*
		 * ActionArea f?r die Sternscheibe erzeugen. Registriert wird sie nach ActionAre der
		 * Schiffen, damit sie nicht überdeckt werden
		 */
		ActionArea startActionArea = new ActionArea(new Rectangle(starOvalX, starOvalY, starSizePixel, starSizePixel), new StarMouseEventListener(ss, this));
		/*
		 * Sternname zeichnen
		 */
		gr.setColor(ss.erforscht() ? STARNAME_COLOR_ERFORSCHT : STARNAME_COLOR_UNERFORSCHT);
		String name = ss.toString();
		gr.drawString(name, starOvalX, starOvalY - gr.getFontMetrics().getDescent());
		/*
		 * Koloniebev?lkerung zeichnen (falls vorhanden)
		 */
		StringDrawer stringDrawer = new StringDrawer();
		int x = starOvalX + starSizePixel + 3;
		int y = starOvalY;
		stringDrawer.setGraphics(gr.create(x, y, 1000, 1000));
		Colony col = ss.getMeineKolonie();
		if(col != null)
		{
			String population = GUI.formatDouble(col.getPopulation().doubleValue() / 1000000d);
			population += " Mln.";
			Color nameColor = STARNAME_COLOR_MEINE_KOLONIE;
			if(col.isRebelled()) nameColor = STARNAME_REBEL_KOLONIE;
			Rectangle rect = stringDrawer.drawString(population, nameColor, null);
			/*
			 * Da Rectangle in dem Koordinatensystem von StringDrawer erstellt wurde muss er in das
			 * Koordinatensystem des Panel transliert werden
			 */
			rect.setLocation(x + rect.x, y + rect.y);
			ActionArea actionArea = new ActionArea(rect, new ColonyMouseEventListener(ss, this));
			areaActions.add(actionArea);
		}
		/*
		 * Anzahl der stationierten Schiffe zeichnen wenn eigene Kolonie in diesem Sternensystem
		 * exisitiert
		 */
		int ships = shipDB.getShipCountInStarsystem(ss.getPosition());
		int rebelShips = rebelShipDB.getShipCountInStarsystem(ss.getPosition());
		if((ships > 0 || rebelShips > 0) && col != null)
		{
			if(ships > 0)
			{
				Rectangle rect = stringDrawer.drawString(GUI.formatLong(ships), Color.BLACK, STARNAME_COLOR_MEINE_KOLONIE);
				/*
				 * Da Rectangle in dem Koordinatensystem von StringDrawer erstellt wurde muss er in
				 * das Koordinatensystem des Panel transliert werden
				 */
				rect.setLocation(x + rect.x, y + rect.y);
				ActionArea actionArea = new ActionArea(rect, new ShipsMouseEventListener(ss.getPosition(), this));
				areaActions.add(actionArea);
			}
			if(rebelShips > 0)
			{
				/*
				 * Zusätzlich rebelleische Schiffe Zeichnen
				 */
				/* Rectangle rect = */
				stringDrawer.drawString(GUI.formatLong(rebelShips), Color.BLACK, STARNAME_REBEL_KOLONIE);
			}
		}
		if(col != null) stringDrawer.beginNewLine();
		/*
		 * Koloniebev?lkerung und Schiffsanzahl der fremden Civs zeichnen
		 */
		Map<Long, ForeignColonyPersistent> fremdeKolonien = fremdeKolonienDB.getObjectsAt(ss.getPosition());
		Map<Long, FremdeSchiffeInfo> fremdeSchiffe = fremdeSchiffeDB.getObjectsAt(ss.getPosition());
		if(fremdeKolonien != null)
		{
			for(final ForeignColonyPersistent kol : fremdeKolonien.values())
			{
				Civilization civilisation = civDB.getCivilization(kol.getCivSerialNumber());
				Long civSer = kol.getCivSerialNumber();
				String population = GUI.formatDouble(kol.getPopulation().doubleValue() / 1000000d);
				population += " Mln.";
				Rectangle rect = stringDrawer.drawString(population, civDB.getGuiColorForCiv(kol.getCivId()), null);
				FremdeSchiffeInfo info = null;
				if(fremdeSchiffe != null)
				{
					info = fremdeSchiffe.remove(civSer);
					if(info != null)
					{
						rect.add(stringDrawer.drawString(Integer.toString(info.getShipQuantity()), Color.BLACK, civDB.getGuiColorForCiv(info.getCivID())));
					}
				}
				/*
				 * Nur dann Action zum Raumkampf einf?gen, wenn auch eigen Schiffe pr?sent sind und
				 * Zivilisation nicht alliirt ist
				 */
				ActionListener aListener = null;
				if(ships > 0 && (civilisation == null || ((civilisation != null) && !civilisation.isAlly()))) aListener = new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						startBattle(ss.getPosition(), kol.getCivId());
					}
				};
				ActionArea actionArea = new ActionArea(rect, new PopupAreaMouseEventListener("<html>Zivilisation: " + civDB.getCivName(kol.getCivId())
						+ (info != null ? "<br>Gesamtmasse der Schiffe: " + GUI.formatLong(info.getGesamttonnage()) + "T" : "") + "</html>", this, aListener));
				areaActions.add(actionArea);
				/*
				 * Da Rectangle in dem Koordinatensystem von StringDrawer erstellt wurde muss er in
				 * das Koordinatensystem des Panel transliert werden
				 */
				rect.setLocation(x + rect.x, y + rect.y);
				stringDrawer.beginNewLine();
			}
		}
		/*
		 * Meine Schiffe zeichnen wenn keine eigene Kolonien bei dieser Position
		 */
		if(ships > 0 && col == null)
		{
			String val = GUI.formatLong(ships);
			Rectangle rect = stringDrawer.drawString(val, Color.BLACK, STARNAME_COLOR_MEINE_KOLONIE);
			/*
			 * Da Rectangle in dem Koordinatensystem von StringDrawer erstellt wurde muss er in das
			 * Koordinatensystem des Panel transliert werden
			 */
			rect.setLocation(x + rect.x, y + rect.y);
			ActionArea actionArea = new ActionArea(rect, new ShipsMouseEventListener(ss.getPosition(), this));
			areaActions.add(actionArea);
		}
		/*
		 * Jetz Anzahl der fremde Schiffe zeichnen, die ohne Kolonien hier sind
		 */
		if(fremdeSchiffe != null && !fremdeSchiffe.isEmpty())
		{
			for(final FremdeSchiffeInfo info : fremdeSchiffe.values())
			{
				Civilization civilisation = civDB.getCivilization(info.getCivID());
				stringDrawer.insertStrut(1);// drawString("|", Color.BLACK, null);
				Rectangle rect = stringDrawer.drawString(Integer.toString(info.getShipQuantity()), Color.BLACK, civDB.getGuiColorForCiv(info.getCivID()));
				/*
				 * Da Rectangle in dem Koordinatensystem von StringDrawer erstellt wurde muss er in
				 * das Koordinatensystem des Panel transliert werden
				 */
				rect.setLocation(x + rect.x, y + rect.y);
				/*
				 * Nur dann Action zum Raumkampf einf?gen, wenn auch eigen Schiffe pr?sent sind und
				 * Zivilisation nicht alliiert
				 */
				ActionListener aListener = null;
				if(ships > 0 && (civilisation == null || ((civilisation != null) && !civilisation.isAlly()))) aListener = new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						startBattle(ss.getPosition(), info.getCivID());
					}
				};
				ActionArea actionArea = new ActionArea(rect, new PopupAreaMouseEventListener("<html>Zivilisation: " + civDB.getCivName(info.getCivID())
						+ (info != null ? "<br>Gesamtmasse der Schiffe: " + GUI.formatLong(info.getGesamttonnage()) + "T" : "") + "</html>", this, aListener));
				areaActions.add(actionArea);
				// stringDrawer.beginNewLine();
			}
		}
		/*
		 * ActionAre für die Strenscheibe ganz am Ende registrieren, damit sie andere wichtigere
		 * ActionAreas nicht überdeckt
		 */
		areaActions.add(startActionArea);
	}

	private void startBattle(final Point pos, final ID civID)
	{
		//TODO
//		int option = JOptionPane.showConfirmDialog(this, "Wollen Sie die Zivilisation: " + civDB.getCivName(civID) + " angreifen?", "Bestätigung",
//				JOptionPane.OK_CANCEL_OPTION);
//		if(option != JOptionPane.OK_OPTION) return;
//		final WaitDialog dlg = new WaitDialog(Main.instance().getGUI().getMainFrame(), "Übertragung der Schiffsdaten");
//		/*
//		 * Ein Timer zur Sicherung, dass die Anwendung nicht f?r immer einfriert
//		 */
//		Timer timer = new Timer(20000, new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				dlg.setVisible(false);
//			}
//		});
//		timer.start();
//		new Thread(new Runnable()
//		{
//
//			public void run()
//			{
//				try
//				{
//					SpaceBattleResult res = Main.instance().getNetSubsystem().getBattleServer().initiateBattle(pos, shipDB.getShipsInStarsystem(pos),
//							civDB.getCivilization(civID));
//					dlg.setVisible(false);
//					if(res == null) return;
//					Main.instance().getGUI().promtBattleResult(pos, res, civID);
//					// new ResultStarBattleDialog(Main.instance().getGUI().getMainFrame(), res);
//				} catch(Throwable e)
//				{
//					Main.instance().severeErrorOccured(e, "Fehler in Battle Initiator Thread", false);
//				}
//			}
//		}, "Battle Initiator Thread").start();
//		dlg.setVisible(true);
//		timer.stop();
	}

	/**
	 * Zeichten Schiffsroute die in ShipMovementOrderDB gespeichert sind
	 * 
	 * @param gr
	 */
	private void paintShipRoutes(Graphics gr)
	{
		Iterator iter = mShipMovementOrderDB.getAllOrders().values().iterator();
		gr.setColor(COLOR_SHIP_ROUTE);
		while(iter.hasNext())
		{
			ShipMovementOrder order = (ShipMovementOrder) iter.next();
			paintRoute(gr, order, true);
		}
	}

	/**
	 * Zeichten Schiffsroute die in ShipMovementOrderDB gespeichert sind
	 * 
	 * @param gr
	 */
	private void paintTempShipRoutes(Graphics gr)
	{
		Iterator iter = mShipMovementOrderDB.getTempMovementOrders().iterator();
		gr.setColor(COLOR_TEMP_SHIP_ROUTE);
		while(iter.hasNext())
		{
			ShipMovementOrder order = (ShipMovementOrder) iter.next();
			paintRoute(gr, order, false);
		}
	}

	/**
	 * Zeichnet Schiffsroute
	 * 
	 * @param gr
	 */
	private void paintRoute(Graphics gr, ShipMovementOrder order, boolean drawShip)
	{
		// if(order.isArrived())return;
		Color oldColor = gr.getColor();
		Point pos = order.computeCurrentApproxStarmapPoint();
		Point target = order.getTarget();
		// if(pos.equals(target))return;
		pos = translateStarmapPointToViewPixel(pos);
		target = translateStarmapPointToViewPixel(target);
		gr.drawLine(target.x, target.y, pos.x, pos.y);// Linie zum Ziel zeichnen
		/*
		 * Anzahl der stationierten Schiffe zeichnen
		 */
		int ships = order.getShipIDs().size();
		String val = GUI.formatLong(ships);
		LineMetrics metrics = gr.getFontMetrics().getLineMetrics(val, gr);
		// Rechteck f?llen
		int y = pos.y - (int) (metrics.getAscent() / 2);
		int width = gr.getFontMetrics().stringWidth(val);
		int height = (int) metrics.getAscent();
		int x = pos.x - (width / 2);
		gr.setColor(STARNAME_COLOR_MEINE_KOLONIE);
		gr.fillRect(x - 1, y, width + 2, height + 1);
		// In dem Rechteck Schiffsanzahl schreiben
		gr.setColor(Color.BLACK);
		gr.drawString(val, x, y + height);
		gr.setColor(oldColor);// Alte Farbe wiederherstellen
		ActionArea actionArea = new ActionArea(new Rectangle(x, y, width, height), new FlyingShipsMouseEventListener(order, this));
		areaActions.add(actionArea);
	}

	/**
	 * Umrechnet Sternenkoordinaten in virtuelle Pixelkoordinaten
	 */
	private Point translateStarmapPointToVirtuellPixel(Point starmapPoint)
	{
		int x = starmapPoint.x * zoom;
		int y = starmapPoint.y * zoom;
		return new Point(x, y);
	}

	private Point translateStarmapPointToViewPixel(Point point)
	{
		return translateVirtuellPixelToViewPixel(translateStarmapPointToVirtuellPixel(point));
	}

	/**
	 * Umrechnet virtuelle Pixelkoordinaten in Sternenkartenkoordinaten
	 */
	private Point translateVirtuellPixelToStarmapPoint(Point location)
	{
		int x = location.x / zoom;
		int y = location.y / zoom;
		return new Point(x, y);
	}

	/**
	 * Umrechnet absolute virtuelle Pixelkoordinate in die Koordinatensystem des sichtbaren
	 * Kartensuschnittes
	 */
	private Point translateVirtuellPixelToViewPixel(Point point)
	{
		Point viewPoint = getViewPoint();
		int x = point.x - viewPoint.x;
		int y = viewPoint.y - point.y;
		return new Point(x, y);
	}

	private Point translateViewPixelToVirtuellPixel(Point point)
	{
		Point viewPoint = getViewPoint();
		return new Point(viewPoint.x + point.x, viewPoint.y - point.y);
	}

	void showInfoPopup(Point pos, String text)
	{
		if(infoPopup != null) infoPopup.hide();
		infoPopupContext.setTipText(text);
		SwingUtilities.convertPointToScreen(pos, this);
		infoPopup = PopupFactory.getSharedInstance().getPopup(this, infoPopupContext, pos.x + 15, pos.y + 5);
		infoPopup.show();
	}

	private MouseEventListener getMouseEventListenerForPosition(Point pos)
	{
		if(areaActions == null || areaActions.size() == 0) return null;
		for(Iterator iter = areaActions.iterator(); iter.hasNext();)
		{
			ActionArea act = (ActionArea) iter.next();
			if(act.getArea().contains(pos)) return act.getListener();
		}
		return null;
	}

	/**
	 * Methode wird aufgerufen wenn der Mauszeiger ?ber einer Position zum Stehen gekommen ist.
	 * 
	 * @param pos
	 *            Koordinaten des Mauszeigers
	 */
	private void mouseStopped(MouseEvent event)
	{
		if(event == null) return;
		// System.out.println("Mouse Stopped: "+System.currentTimeMillis());
		MouseEventListener listener = getMouseEventListenerForPosition(event.getPoint());
		if(listener != null)
		{
			listener.processMouseEvent(event);
		}
	}

	/**
	 * Methode berechnet welche virtuelle Pixelkoordinate ist im Zentrum des sichtbaren
	 * Kartenausschnittes
	 */
	/*
	 * private Point computeViewCenter() { Point viewPoint = getViewPoint(); int x = viewPoint.x +
	 * (getWidth()/2); int y = viewPoint.y - (getHeight()/2); return new Point(x,y); }
	 */
	protected void processMouseMotionEvent(MouseEvent event)
	{
		super.processMouseMotionEvent(event);
		if(event.getID() == MouseEvent.MOUSE_MOVED)
		{
			// if(event == lastMouseEvent)return;//MouseEvent neugespeist von mouseStopped Methode
			// //damit die ToolTips richtig funktionieren;
			lastMouseMoveTime = System.currentTimeMillis();
			lastMouseEvent = event;
			setToolTipText(null);
			setCursor(_mapCursor);
			if(infoPopup != null)
			{
				infoPopup.hide();
				infoPopup = null;
			}
			Point location = translateVirtuellPixelToStarmapPoint(translateViewPixelToVirtuellPixel(event.getPoint()));
			// StarSystem ss = isStarArea(event.getPoint());
			// if(ss != null)
			// {
			// if(selectRouteMode && selectedShipsAndFleets != null)
			// {
			// if(mShipMovementOrderDB.createTempShipMovementOrders(ss.getPosition(),
			// selectedShipsAndFleets))
			// setCursor(GUIConstants.CURSOR_SELECT_TARGET_GREEN);
			// else
			// setCursor(GUIConstants.CURSOR_SELECT_TARGET_RED);
			// repaint();
			// } else
			// setCursor(_starCursor);
			// } else
			// {
			// setCursor(_mapCursor);
			// }
			parent.showMouseLocation(location);
			/**
			 * Pr?fen ob Mauszeiger nicht in der Randzone der Karte befindet Wenn ja wird der
			 * Kartenausschnitt in die neue Position gebracht
			 */
			if(event.getX() < MOVE_BORDER_WIDTH || getWidth() - event.getX() < MOVE_BORDER_WIDTH || event.getY() < MOVE_BORDER_WIDTH
					|| getHeight() - event.getY() < MOVE_BORDER_WIDTH)
			{
				if(_moveTimer != null) _moveTimer.stop();
				Point currentLocation = virtualPixelCenterPoint;// computeViewCenter();
				Point eventLocation = translateViewPixelToVirtuellPixel(event.getPoint());
				double streckeLaenge = Math.sqrt(Math.pow((double) (currentLocation.x - eventLocation.x), 2d)
						+ Math.pow((double) (currentLocation.y - eventLocation.y), 2d));
				double k = streckeLaenge / MOVE_STEP;
				double x = (eventLocation.x + k * currentLocation.x) / (1 + k);
				double y = (eventLocation.y + k * currentLocation.y) / (1 + k);
				int deltaX = (int) (x - currentLocation.x);
				int deltaY = (int) (y - currentLocation.y);
				_moveTimer = new Timer(MOVE_DELAY, new StarmapMover(deltaX, deltaY));
				_moveTimer.start();
			} else
			{
				if(_moveTimer != null)
				{
					_moveTimer.stop();
					_moveTimer = null;
				}
			}
		}
	}

	protected void processMouseEvent(MouseEvent event)
	{
		if(event.getID() == MouseEvent.MOUSE_CLICKED)
		{
			popupMenu.setVisible(false);
		}
		if(event.getID() == MouseEvent.MOUSE_CLICKED && SwingUtilities.isRightMouseButton(event))
		{// Wenn rechte Maustaste gedr?ckt, dann aus SelectMode in NormaleMode
			// wechseln
			switchFromTargetSelectMode(false);
		}
		if(event.getID() == MouseEvent.MOUSE_EXITED)
		{
			if(_moveTimer != null) _moveTimer.stop();
			_moveTimer = null;
		}
		MouseEventListener listener = getMouseEventListenerForPosition(event.getPoint());
		if(listener != null) listener.processMouseEvent(event);
	}

	/**
	 * Umschaltet Starmap in sogenannter SelectMode. In diesem Modus wird der Kursor ge?ndert und
	 * der mitgegebener SelectTargetModeListener ?ber die Clicks auf die Sterne informiert. Starmap
	 * befindet sich solange in diesem Modus, bis switchFromTargetSelectMode() aufgerufen wird.
	 * 
	 * @param Set
	 *            mit ID Objekten der Schiffen und Flotten
	 */
	public void switchToTargetSelectMode(List ships)
	{
		_starCursor = GUIConstants.CURSOR_SELECT_TARGET_RED;
		_mapCursor = GUIConstants.CURSOR_SELECT_TARGET_GREY;
		selectedShips = ships;
		selectRouteMode = true;
		shipTableDialog.setVisible(false);
	}

	/**
	 * SChaltet den SelectMode aus, und setzt SelectTargetModeListener-Referenz auf null
	 */
	public void switchFromTargetSelectMode(boolean acceptRoute)
	{
		_starCursor = new Cursor(Cursor.HAND_CURSOR);
		_mapCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		setCursor(_mapCursor);
		selectRouteMode = false;
		selectedShips = null;
		if(acceptRoute)
			mShipMovementOrderDB.acceptTempMovementOrders();
		else
			mShipMovementOrderDB.removeTempShipMovementOrders();
		repaint();
	}

	public boolean isInTargetSelectMode()
	{
		return selectRouteMode;
	}

	/**
	 * Klasse verschiebt Kartenansicht mit dem gegebenen Richtungsvektor
	 */
	class StarmapMover
			implements ActionListener
	{

		/**
		 * Verschiebungsvektor in Pixel
		 */
		private int _deltaX;
		private int _deltaY;

		StarmapMover(int deltaX, int deltaY)
		{
			_deltaX = deltaX;
			_deltaY = deltaY;
		}

		public void actionPerformed(ActionEvent event)
		{
			Point newLocation = new Point(virtualPixelCenterPoint.x + _deltaX, virtualPixelCenterPoint.y + _deltaY);
			setVirtualPixelCentralPoint(newLocation);
		}
	}

	private class PopupMenu extends JPopupMenu
	{

		private JMenuItem itemRename = new JMenuItem("Stern umbenennen");
		private JMenuItem itemBuildColony = new JMenuItem("Kolonie gründen");
		private StarSystem star;

		public PopupMenu()
		{
			/*
			 * :CHEAT: In Debug Modus kann man ohne Kolonieschiffe Kolonie gründen.
			 */
			if(Main.isDebugMode()) add(itemBuildColony);
			itemBuildColony.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					if(star == null) return;
					Main.instance().getMOUDB().getKolonieDB().createNewKolonie(star.getPosition(), ColonyDB.STANDARD_COLONY_SIZE);
					// Main.instance().getGUI().getMainFrame().getStarmapScreen()
					// .selectStar(star.getPosition());
				}
			});
			add(itemRename);
			itemRename.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					if(star == null) return;
					String name = JOptionPane.showInputDialog("Neue Name:", star.getName());
					if(name != null)
					{
						star.setStarName(name);
						Main.instance().getGUI().getMainFrame().getStarmapScreen().centerPosition(star.getPosition());
					}
				}
			});
		}

		public void setStar(StarSystem ss)
		{
			star = ss;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
		 */
		public void show(Component invoker, int x, int y)
		{
			if(star == null || !star.erforscht())
				itemRename.setEnabled(false);
			else
				itemRename.setEnabled(true);
			super.show(invoker, x, y);
		}
	}

	class StarMouseEventListener
			implements MouseEventListener
	{

		StarSystem star;
		StarmapPanel panel;

		StarMouseEventListener(StarSystem ss, StarmapPanel panel)
		{
			star = ss;
			this.panel = panel;
		}

		public void processMouseEvent(MouseEvent event)
		{
			if(event.getID() == MouseEvent.MOUSE_MOVED)
			{
				panel.showInfoPopup(event.getPoint(), star.getTooltipHtmlText());
				if(panel.selectRouteMode && panel.selectedShips != null)
				{
					if(mShipMovementOrderDB.createTempShipMovementOrders(star.getPosition(), selectedShips))
						setCursor(GUIConstants.CURSOR_SELECT_TARGET_GREEN);
					else
						setCursor(GUIConstants.CURSOR_SELECT_TARGET_RED);
					repaint();
				} else
					setCursor(_starCursor);
			}
			if(event.getID() == MouseEvent.MOUSE_CLICKED)
			{
				// selectStar(star.getPosition());
				if(!isInTargetSelectMode())
				{
					panel.popupMenu.setStar(star);
					panel.popupMenu.show(panel, event.getX(), event.getY());
				}
				panel.switchFromTargetSelectMode(true);
				// panel.selectStar(selectedStar); // Flottenanzeige f?r Starsystem
				// erfrischen
				// Colony col = star.getMeineKolonie();
			}
		}
	}

	class ColonyMouseEventListener
			implements MouseEventListener
	{

		StarSystem star;
		StarmapPanel panel;

		ColonyMouseEventListener(StarSystem ss, StarmapPanel panel)
		{
			star = ss;
			this.panel = panel;
		}

		public void processMouseEvent(MouseEvent event)
		{
			if(panel.isInTargetSelectMode()) return;
			// System.out.println("Kolony area action");
			if(event.getID() == MouseEvent.MOUSE_CLICKED)
			{
				Colony col = star.getMeineKolonie();
				if(col != null) Main.instance().getGUI().showColony(col);
			}
			if(event.getID() == MouseEvent.MOUSE_MOVED)
			{
				Colony col = star.getMeineKolonie();
				panel.showInfoPopup(event.getPoint(), "Population: " + GUI.formatLong(col.getPopulation().longValue()));
				panel.setCursor(CURSOR_ACTION);
			}
		}
	}

	class PopupAreaMouseEventListener
			implements MouseEventListener
	{

		String text;
		StarmapPanel panel;
		ActionListener listener;

		PopupAreaMouseEventListener(String text, StarmapPanel panel, ActionListener listener)
		{
			this.text = text;
			this.panel = panel;
			this.listener = listener;
		}

		public void processMouseEvent(MouseEvent event)
		{
			if(panel.isInTargetSelectMode()) return;
			// System.out.println("Kolony area action");
			if(event.getID() == MouseEvent.MOUSE_MOVED)
			{
				panel.showInfoPopup(event.getPoint(), text);
				if(listener != null) panel.setCursor(CURSOR_ACTION);
			}
			if(event.getID() == MouseEvent.MOUSE_CLICKED && listener != null)
			{
				listener.actionPerformed(new ActionEvent(this, 0, ""));
			}
		}
	}

	class ShipsMouseEventListener
			implements MouseEventListener
	{

		Point pos;
		StarmapPanel panel;

		ShipsMouseEventListener(Point pos, StarmapPanel panel)
		{
			this.pos = pos;
			this.panel = panel;
		}

		public void processMouseEvent(MouseEvent event)
		{
			if(panel.isInTargetSelectMode()) return;
			if(event.getID() == MouseEvent.MOUSE_CLICKED)
			{
				if(SwingUtilities.isRightMouseButton(event))
				{
					/*
					 * Bei rechter Maustaste gleich Zielauswahl-Popup zeigen
					 */
					new ShipPopup().show(event.getComponent(), event.getX(), event.getY());
				} else
				{
					shipTableDialog.showShipsAtPosition(pos);
					shipTableDialog.setVisible(true);
				}
			}
			if(event.getID() == MouseEvent.MOUSE_MOVED)
			{
				panel.setCursor(CURSOR_ACTION);
				panel.showInfoPopup(event.getPoint(), "Meine Raumschiffe");
			}
		}

		class ShipPopup extends JPopupMenu
		{

			JMenuItem itemTargetSelect = new JMenuItem("Flugziel wählen");

			public ShipPopup()
			{
				add(itemTargetSelect);
				itemTargetSelect.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						switchToTargetSelectMode(shipDB.getShipsInStarsystem(pos));
					}
				});
			}
		}
	}

	class FlyingShipsMouseEventListener
			implements MouseEventListener
	{

		ShipMovementOrder order;
		StarmapPanel panel;

		FlyingShipsMouseEventListener(ShipMovementOrder order, StarmapPanel panel)
		{
			this.order = order;
			this.panel = panel;
		}

		public void processMouseEvent(MouseEvent event)
		{
			if(panel.isInTargetSelectMode()) return;
			// System.out.println("Kolony area action");
			if(event.getID() == MouseEvent.MOUSE_MOVED)
			{
				// panel.setCursor(CURSOR_ACTION);
				panel.showInfoPopup(event.getPoint(), "Noch " + Long.toString((long) order.computeOverallFlyingTime() + 1 - order.getAppliedTime()) + " Tage");
			}
		}
	}
}