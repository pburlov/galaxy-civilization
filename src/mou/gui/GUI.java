/*
 * $Id: GUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import mou.Main;
import mou.Subsystem;
import mou.core.colony.Colony;
import mou.core.res.ResourceMenge;
import mou.gui.colonyscreen.ColonyDialog;
import mou.net.battle.SpaceBattleResult;
import mou.storage.ser.ID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import burlov.swing.MessagePanel.Message;

/**
 * Enthält Methoden die füe alle Klassen der GUI-Package nützlich sein könnten
 */
public class GUI extends Subsystem
{

	static final private String STRING_UNBEKANNT = "Unbekannt";
	static final public int MSG_PRIORITY_POPUP = 0;
	static final public int MSG_PRIORITY_NORMAL = 1;
	static final public int MSG_PRIORITY_URGENT = 2;
	static final private DecimalFormat LONG_FORMAT;
	static final private DecimalFormat DOUBLE_FORMAT;
	// static final private DecimalFormat PROZENT_FORMAT;
	static final private DecimalFormat SMART_FORMAT_1;
	static final private DecimalFormat SMART_FORMAT_2;
	static final private DecimalFormat SMART_FORMAT_3;
	static final private DecimalFormat DATE_FORMAT;
	static private MainFrame mainFrame;
	private ColonyDialog colonyScreen;
	// private Font currentFont = GUIConstants.FONT_DEFAULT;
	// private int currentFontSize = GUIConstants.FONT_SIZE;
	/*
	 * Wird bei der Zivilisationserstellung auf die Position der Schiffe gesetzt
	 */
	private Point startStarmapPosition;
	static
	{
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(':');
		DATE_FORMAT = new DecimalFormat("000000000,000", symbols);
		symbols = new DecimalFormatSymbols(Locale.GERMANY);
		LONG_FORMAT = new DecimalFormat("###,###", symbols);
		DOUBLE_FORMAT = new DecimalFormat("###,##0.000", symbols);
		// PROZENT_FORMAT = new DecimalFormat("00.00", symbols);
		SMART_FORMAT_1 = new DecimalFormat("###,##0.0", symbols);
		SMART_FORMAT_2 = new DecimalFormat("###,##0.00", symbols);
		SMART_FORMAT_3 = new DecimalFormat("###,##0.000", symbols);
	}

	public GUI(Subsystem parent)
	{
		super(parent);
		/*
		 * Der Konstruktor wird hier aufgerufen um die Referenz zu dem Fenster zu initialisieren.
		 * Eigentliche Initialisierung des Hauptfensters mit wird in der Methode startModulIntern
		 * durchgeführt.
		 */
		mainFrame = new MainFrame();// Hier wird nur die Referenz zum Hauptfenter geholt
	}

	public void updateGUI()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		// FontUIResource f = new FontUIResource(
		// getCurrentFont().deriveFont(Font.PLAIN, getCurrentFontSize()));
		// // SkinUtils.setFont(getCurrentFont());
		// java.util.Enumeration keys = UIManager.getDefaults().keys();
		// while (keys.hasMoreElements())
		// {
		// Object key = keys.nextElement();
		// Object value = UIManager.get (key);
		// if (value instanceof javax.swing.plaf.FontUIResource)
		// UIManager.put (key,f);
		// }
		SwingUtilities.updateComponentTreeUI(Main.instance().getGUI().getMainFrame());
	}

	/**
	 * Nachricht wird im NAchrichtenfester für den Benutzer sichtbar gemacht
	 * 
	 * @param msg
	 *            die Nachricht selbst
	 * @param prioritet
	 *            Eine der MSG_PRIORITY Konstanten
	 */
	public void promtMessage(final String titel, final String msg, final int prioritet)
	{
		promtMessage(titel, msg, prioritet, null);
	}

	public void promtMessage(final String titel, final String msg, final int prioritet, final Runnable run)
	{
		if(!EventQueue.isDispatchThread())
		{
			EventQueue.invokeLater(new Runnable()
			{

				public void run()
				{
					promtMessage(titel, msg, prioritet, run);
				}
			});
			return;
		}
		String text = "<html><font color=green>" + formatDate(Main.instance().getTime()) + "</font> " + "<font color=blue>[" + titel + "] </font>" + msg
				+ "</html>";
		Message message = new Message(run, text, false);
		switch(prioritet)
		{
			case MSG_PRIORITY_POPUP:
				message.setUrgent(true);
				if(mainFrame == null) return;
				mainFrame.getMessagesPanel().appendMessage(message);
				/*
				 * Vorerst auskommentiert, weil bei geoeffneten anderen modalen Fenster zu einem
				 * Deadlock kommt.
				 */
				// JOptionPane.showMessageDialog(mainFrame,
				// msg,titel,JOptionPane.INFORMATION_MESSAGE);
				return;
			case MSG_PRIORITY_URGENT:
				message.setUrgent(true);
			case MSG_PRIORITY_NORMAL:
				if(mainFrame == null) return;
				mainFrame.getMessagesPanel().appendMessage(message);
		}
	}

	public void promtBattleResult(final Point pos, SpaceBattleResult res, ID gegner)
	{
		if(res == null) return;
		String msg = "<br><font color=red>Eigene Verluste (Schiffe): Zerstört " + GUI.formatLong(res.getDestroyedGood()) + " Beschädigt "
				+ GUI.formatLong(res.getDamagedGood()) + ".</font>" + "<font color=green> Feindliche Verluste (Schiffe): Zerstört "
				+ GUI.formatLong(res.getDestroyedEvil()) + " Beschädigt " + GUI.formatLong(res.getDamagedEvil()) + ".</font>";
		if(res.isColonyCaptured())
		{
			msg += " Eine feindliche Kolonie wurde erobert!";
		}
		// msg += "</html>";
		promtMessage("Raumkampf bei Koordinaten " + GUI.formatPoint(pos) + " gegen " + Main.instance().getMOUDB().getCivilizationDB().getCivName(gegner), msg,
				GUI.MSG_PRIORITY_URGENT, new Runnable()
				{

					public void run()
					{
						centreStarmaponPosition(pos);
					}
				});
	}

	public void centreStarmaponPosition(Point pos)
	{
		getMainFrame().getStarmapScreen().centerPosition(pos);
		getMainFrame().selectScreen(MainFrame.SCREEN_STARMAP);
	}

	public Font loadFont(File file)
	{
		try
		{
			FileInputStream in = new FileInputStream(file);
			Font ret = Font.createFont(Font.TRUETYPE_FONT, in);
			in.close();
			return ret;
		} catch(Exception e1)
		{
			logThrowable("Fehler bei Font laden.", e1);
		}
		return GUIConstants.FONT_DEFAULT;
	}

	static public Image loadImage(String path)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = mainFrame.getClass().getResourceAsStream(path);
		if(in == null) throw new RuntimeException("Ressource " + path + " not found");
		try
		{
			IOUtils.copy(in, out);
			in.close();
			out.flush();
		} catch(IOException e)
		{
			e.printStackTrace();
			new RuntimeException("Error reading ressource " + path, e);
		}
		return Toolkit.getDefaultToolkit().createImage(out.toByteArray());
		// URL ret = ClassLoader.getSystemResource(path);
		// if(ret == null)throw new RuntimeException("Ressource "+path+" not found");
		// return ret;
	}

	/**
	 * Um den Konstruktor zu entlasten werden alles Zweitrangiges hier gestartet
	 */
	public void startModulIntern() throws Exception
	{
		try
		{
			// String font = getPreferences().getProperty("Font","res/fonts/font.ttf");
			// setCurrentFont(loadFont(new File(font)));
			// try
			// {
			// setCurrentFontSize(Integer.parseInt(getPreferences().getProperty(
			// "FontSize",Integer.toString(GUIConstants.FONT_SIZE))));
			// }catch(NumberFormatException e)
			// {
			// }
			// JFrame.setDefaultLookAndFeelDecorated(true);
			// JDialog.setDefaultLookAndFeelDecorated(true);
			// String themepack = getPreferences().getProperty("Themepack","themes/themepack.zip");
			// SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePack(themepack));
			// SkinLookAndFeel.enable();
			// FontUIResource f = new FontUIResource(
			// getCurrentFont().deriveFont(Font.PLAIN, getCurrentFontSize()));
			// java.util.Enumeration keys = UIManager.getDefaults().keys();
			// while (keys.hasMoreElements())
			// {
			// Object key = keys.nextElement();
			// Object value = UIManager.get (key);
			// if (value instanceof javax.swing.plaf.FontUIResource)
			// UIManager.put (key, f);
			// }
			// UIManager.put("Button.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("CheckBox.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ColorChooser.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ComboBox.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("EditorPane.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Label.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("List.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Menu.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("MenuBar.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("MenuItem.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("OptionPane.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Panel.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("PasswordField.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("PopupMenu.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ProgressBar.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("RadioButton.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ScrollPane.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Table.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TableHeader.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Text.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Table.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TableHeader.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TextArea.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TextField.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TextPane.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ToggleButton.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ToolBar.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("ToolTip.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("Tree.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TitledBorder.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TitledBorder.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.put("TitledBorder.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			//			
			// UIManager.put("TabbedPane.font", new FontUIResource(GUIConstants.FONT_DEFAULT));
			// UIManager.setLookAndFeel("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
		} catch(Throwable e)
		{
			logThrowable("Fehler bei Look-and-Feel initialisierung", e);
		}
		mainFrame.initFrame();// Hier wird die eigentliche Initialiseirung durchgeführt
		mainFrame.restoreSettings(getPreferences());
		if(startStarmapPosition != null) mainFrame.getStarmapScreen().centerPosition(startStarmapPosition);
		mainFrame.setVisible(true);
		colonyScreen = new ColonyDialog(mainFrame);
		ToolTipManager man = ToolTipManager.sharedInstance();
		man.setDismissDelay(1000 * 60);
		man.setInitialDelay(0);
		// centreWindow(getMainFrame(), ass);
	}

	public MainFrame getMainFrame()
	{
		return mainFrame;
	}
	
	public ColonyDialog getColonyDialog()
	{
		return colonyScreen;
	}

	/**
	 * Preferences File wird in dem Benutzerverzeichnis abgelegt.
	 */
	public File getPreferencesFile()
	{
		/*
		 * Dateiname beginnt absichtilch mit einem Punkt. So wird diese Datei unter Linux nicht
		 * angezeigt
		 */
		File ret = new File(Main.APPLICATION_USER_DATA_DIR, "gui.cfg");
		if(Main.isDebugMode()) System.out.println(ret);
		return ret;
	}

	public void shutdownIntern()
	{
		mainFrame.shutdown(getPreferences());
		// Groesse und Lokation von MainFrame abspeichern
		/*
		 * int x = mainFrame.getLocationOnScreen().x; int y = mainFrame.getLocationOnScreen().y; int
		 * height = mainFrame.getSize().height; int width = mainFrame.getSize().width;
		 * getPreferences().setProperty("MainWindow_X",new Integer(x).toString());
		 * getPreferences().setProperty("MainWindow_Y",new Integer(y).toString());
		 * getPreferences().setProperty("MainWindow_Height",new Integer(height).toString());
		 * getPreferences().setProperty("MainWindow_Width",new Integer(width).toString());
		 */
	}

	public String getModulName()
	{
		return "GUI";
	}

	public Level getDefaultLoggerLevel()
	{
		return Level.ALL;
	}

	/**
	 * Zentriert ein Window relativ zu dem Parent-Window
	 * 
	 * @param parent
	 *            Parent-Window, wenn null, dann wird relativ zu dem Bildschirm zentriert
	 * @param child
	 *            Window das zentrirt werden soll.
	 */
	static public void centreWindow(Window parent, Window child)
	{
		if(child == null) return;
		Point parentLocation = null;
		Dimension parentSize = null;
		if(parent == null)
		{
			parentLocation = new Point(0, 0);
			parentSize = child.getToolkit().getScreenSize();
		} else
		{
			parentLocation = parent.getLocationOnScreen();
			parentSize = parent.getSize();
		}
		Dimension childSize = child.getSize();
		child.setLocation((int) (parentLocation.getX() + parentSize.getWidth() / 2 - childSize.getWidth() / 2), (int) (parentLocation.getY()
				+ parentSize.getHeight() / 2 - childSize.getHeight() / 2));
	}

	// /**
	// * @return Returns the currentFont.
	// */
	// public Font getCurrentFont()
	// {
	// return currentFont;
	// }
	//
	// /**
	// * @param currentFont The currentFont to set.
	// */
	// public void setCurrentFont(Font currentFont)
	// {
	// this.currentFont = currentFont;
	// }
	//
	// /**
	// * @return Returns the currentFontSize.
	// */
	// public int getCurrentFontSize()
	// {
	// return currentFontSize;
	// }
	//
	// /**
	// * @param currentFontSize The currentFontSize to set.
	// */
	// public void setCurrentFontSize(int currentFontSize)
	// {
	// this.currentFontSize = currentFontSize;
	// }
	static synchronized public String formatLong(long val)
	{
		return LONG_FORMAT.format(val);
	}

	static synchronized public String formatLong(Number val)
	{
		if(val == null) return STRING_UNBEKANNT;
		return LONG_FORMAT.format(val);
	}
	
	/**
	 * Formatiert Zahlen immer mit 3 Wertstellen
	 * 
	 * @param val
	 * @return
	 */
	static synchronized public String formatDouble(double val)
	{
		return DOUBLE_FORMAT.format(val);
	}

	/**
	 * Formatiert Zahlen immer mit 3 Wertstellen
	 * 
	 * @param val
	 * @return
	 */
	static synchronized public String formatDouble(Number val)
	{
		if(val == null) return STRING_UNBEKANNT;
		return DOUBLE_FORMAT.format(val);
	}

	static synchronized public String formatDouble(double val, int place)
	{
		switch(place)
		{
			case 1:
				return SMART_FORMAT_1.format(val);
			case 2:
				return SMART_FORMAT_2.format(val);
			case 3:
				return SMART_FORMAT_3.format(val);
		}
		
		return DOUBLE_FORMAT.format(val);
	}
	
	/**
	 * Formatiert Zahlen immer mit mindestens 3 Wertstellen. Beispiele: Zahl < 1: drei
	 * Nachkommastellen 1 < Zahl < 10: zwei Nachkommastellen 10 < Zahl < 100: eine Nachkommastelle
	 * 100 < Zahl: keine Nachkommastellen
	 * 
	 * @param val
	 * @return
	 */
	static synchronized public String formatSmartDouble(Number val)
	{
		return formatSmartDouble(val.doubleValue());
	}

	/**
	 * Formatiert Zahlen immer mit mindestens 3 Wertstellen. Beispiele: Zahl < 1: drei
	 * Nachkommastellen 1 < Zahl < 10: zwei Nachkommastellen 10 < Zahl < 100: eine Nachkommastelle
	 * 100 < Zahl: keine Nachkommastellen
	 * 
	 * @param val
	 * @return
	 */
	static synchronized public String formatSmartDouble(double val)
	{
		double absVal = Math.abs(val);
		if(absVal < 0.001) return "0";
		if(absVal < 1) return SMART_FORMAT_3.format(val);
		if(absVal < 10) return SMART_FORMAT_2.format(val);
		if(absVal < 100) return SMART_FORMAT_1.format(val);
		return LONG_FORMAT.format(val);
	}

	static synchronized public String formatPoint(Point p)
	{
		if(p == null) return STRING_UNBEKANNT;
		return "[" + p.x + ":" + p.y + "]";
	}

	static public String formatPoints(Collection<Point> points)
	{
		StringBuffer buf = new StringBuffer("{");
		for(Point pos : points)
			buf.append(formatPoint(pos));
		buf.append("}");
		return buf.toString();
	}

	/**
	 * Formatiert mit Prozentformat und ein + oder -
	 * 
	 * @param val
	 * @return
	 */
	static synchronized public String formatProzentSigned(double val)
	{
		String ret = formatSmartDouble(val);
		if(val >= 0) ret = "+" + ret;
		return ret + "%";
	}

	/**
	 * Formatiert mit Prozentformat ohne Pluszeichen
	 * 
	 * @param val
	 * @return
	 */
	static synchronized public String formatProzent(double val)
	{
		String ret = formatSmartDouble(val);
		return ret + "%";
	}

	static synchronized public String formatDate(long date)
	{
		return DATE_FORMAT.format(date);
	}

	static synchronized public String formatDate(Long date)
	{
		if(date == null) return STRING_UNBEKANNT;
		return DATE_FORMAT.format(date);
	}

	/**
	 * Darstelle Liste mit ResourceMenge Objekten in HTML-Format, ohne anführenden und
	 * abschließenden html-Tags
	 * 
	 * @param resources
	 * @return
	 */
	static public String htmlFormatRessourceMenge(Collection<ResourceMenge> resources)
	{
		StringBuffer buf = new StringBuffer();
		for(ResourceMenge res : resources)
		{
			buf.append("<b>");
			buf.append(res.getRessource().getName());
			buf.append(": </b>");
			buf.append(formatSmartDouble(res.getMenge()));
			buf.append("<br>");
		}
		return buf.toString();
	}

	public void showColony(Colony col)
	{
		colonyScreen.showKolonie(col);
		colonyScreen.pack();
		centreWindow(mainFrame, colonyScreen);
		colonyScreen.setVisible(true);
	}

	/**
	 * Bestimmt Position auf der Sternenkarte die nach dem Start gezeigt wird
	 */
	public void setStartPosition(Point pos)
	{
		startStarmapPosition = pos;
	}

	/**
	 * Zeigt ein Auswahldialog mit bestehenden Kolonien
	 * 
	 * @param msg
	 * @return Kolonie oder null
	 */
	public Colony selectKolony(String msg)
	{
		Map<ID, Colony> cols = Main.instance().getMOUDB().getKolonieDB().getAlleKolonien();
		if(cols.isEmpty()) return null;
		Object[] array = cols.values().toArray();
		Colony col = (Colony) JOptionPane.showInputDialog(getMainFrame(), msg, "Kolonie auswählen", JOptionPane.QUESTION_MESSAGE, null, array, null);
		return col;
	}

	public void selectDiplomacyScreen()
	{
		getMainFrame().selectScreen(MainFrame.SCREEN_DIPLOMACY);
	}
}