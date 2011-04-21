/*
 * $Id: MainFrame.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import mou.Main;
import mou.Preferences;
import mou.gui.civilizationscreen.CivOverviewHeader;
import mou.gui.civilizationscreen.CivilizationScreen;
import mou.gui.diplomacy.DiplomacyScreen;
import mou.gui.sciencescreen.ScienceScreen;
import mou.gui.shipdesignscreen.ShipDesignScreen;
import mou.gui.starmapscreen.StarmapScreen;
import mou.gui.tradescreen.TradeScreen;
import burlov.swing.MessagePanel.ActionMessagesPanel;

/**
 * Hauptfenster des Spiels
 */
public class MainFrame extends JFrame
{

	static final public String SCREEN_STARMAP = "starmap";
	static final public String SCREEN_KOLONIEN = "kolonien";
	static final public String SCREEN_FLOTTEN = "flotten";
	static final public String SCREEN_SCIENCE = "SCREEN_SCIENCE";
	static final public String SCREEN_SHIP_DESIGN = "SCREEN_SHIP_DESIGN";
	static final public String SCREEN_COLONY = "SCREEN_COLONY";
	static final public String SCREEN_UNIVERSE = "SCREEN_UNIVERSE";
	static final public String SCREEN_WAREHOUSE = "SCREEN_WAREHOUSE";
	static final public String SCREEN_DIPLOMACY = "SCREEN_DIPLOMACY";
	static private ConsoleFrame consoleFrame = new ConsoleFrame();
	private JMenuBar menuBar;
	private JPanel panel_1;
	private JPanel panelScreens;
	private CardLayout cardLayout1;
	private StarmapScreen starmapScreen;
	private CivilizationScreen panelKolonienScreen;
	private FleetScreen fleetScreen;
	private JToolBar screenToolbar;
	private JButton btMapView;
	private JButton btKolonienView;
	private JPanel panelGlobalStatus;
	private JPanel panelZeit;
	private JLabel jLabel1;
	private JLabel labelZeit;
	private CivOverviewHeader civOverview;
	private JButton btFleetScreen;
	private JButton btConstructionScreen;
	private ScienceScreen constructionScreen;
	private ShipDesignScreen shipDesignScreen;
	private JButton btShipDesignScreen;
	// private ColonyScreen colonyScreen;
	private JButton btUniverseMap;
	private TradeScreen warehouseScreen;
	private JButton btWarehouseScreen;
	private JButton btDiplomacy;
	private ActionMessagesPanel messagesPanel = new ActionMessagesPanel(100);
	private JSplitPane splitPane_0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private DiplomacyScreen diplomacyScreen;
	private MainFrame instance;

	public MainFrame()
	{
		// Bitte Konstruktor leer lassen. Er ist nur dazu da damit man Referenz
		// zu diesem Frame ganz am Anfang bekommt
	}

	/**
	 * Das ist der eigentliche Konstruktor
	 */
	public void initFrame()
	{
		// URL url = GUI.loadImage("/res/images/logo_32.png");
		setIconImage(GUI.loadImage("/res/images/logo_32.png"));
		// #####################################
		// Interne Variable initialisieren.
		menuBar = new JMenuBar();
		panel_1 = new JPanel();
		panelScreens = new JPanel();
		cardLayout1 = new CardLayout();
		starmapScreen = new StarmapScreen();
		diplomacyScreen = new DiplomacyScreen();
		panelKolonienScreen = new CivilizationScreen();
		fleetScreen = new FleetScreen();
		screenToolbar = new JToolBar();
		screenToolbar.setLayout(new FlowLayout());
		btMapView = new JButton();
		btKolonienView = new JButton();
		panelGlobalStatus = new JPanel();
		panelZeit = new JPanel();
		jLabel1 = new JLabel();
		labelZeit = new MOUDateLabel();
		civOverview = new CivOverviewHeader();
		btFleetScreen = new JButton();
		btConstructionScreen = new JButton();
		constructionScreen = new ScienceScreen();
		shipDesignScreen = new ShipDesignScreen();
		btShipDesignScreen = new JButton("Schiffe konstruieren");
		// colonyScreen = new ColonyScreen();
		// universeMapScreen = new UniverseMapScreen();
		btUniverseMap = new JButton("Galaxis");
		warehouseScreen = new TradeScreen();
		btWarehouseScreen = new JButton("Handel");
		btDiplomacy = new JButton("Diplomatie");
		// ######################################
		instance = this;
		// this.enableEvents(WindowEvent.WINDOW_EVENT_MASK);
		setTitle("Galaxy Civilization (Ver. " + Main.VERSION + ")    " + Main.instance().getMOUDB().getCivilizationDB().getMyCivilization().getName());
		this.setJMenuBar(menuBar);
		JMenuItem item;
		JMenu menuOptions = new JMenu();
		menuBar.add(menuOptions);
		menuOptions.setText("Options");
		if(Main.isDebugMode())
		{
			/*
			 * Menubar Initialisieren
			 */
			item = new JMenuItem("Konsole");
			menuOptions.add(item);
			item.addActionListener(new ActionListener()
			{
				
				public void actionPerformed(ActionEvent event)
				{
					consoleFrame.setVisible(true);
				}
			});
		}
		item = new JMenuItem("Spielstand zurücksetzten");
		menuOptions.add(item);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					int option = JOptionPane.showConfirmDialog(Main.instance().getGUI().getMainFrame(),"Wollen Sie ihren Spielstand wirklich löschen?", "Achtung!",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(option == JOptionPane.OK_OPTION)
						Main.instance().getMOUDB().resetScore();
				} catch(Throwable e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getClass().getName() + "\n" + e.getLocalizedMessage(), "", JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			}
		});
		JMenu menuHelp = new JMenu("Hilfe");
		menuBar.add(menuHelp);
		item = new JMenuItem("Info");
		menuHelp.add(item);
		item.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(instance, "Galaxy Civilization\nVersion: " + Main.VERSION + "\nwww.galaxy-civilization.de"
						+ "\n(c)Copyright Paul Burlov 2002,2005. All rights reserved." + "\n\nThis product includes software developed by the"
						+ "\nApache Software Foundation http://www.apache.org", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// JMenuItem item = new JMenuItem("SkinLF Themes");
		// menuOptions.add(item);
		// item.addActionListener(new ActionListener()
		// {
		//
		// public void actionPerformed(ActionEvent e)
		// {
		// JDialog dlg = new JDialog(instance, "Themes", false);
		// // SkinChooser chooser = new SkinChooser();
		// // chooser.setSkinLocations(new String[]{"themes"});
		// // chooser.setThemePackMode(true);
		// // dlg.getContentPane().add(chooser);
		// dlg.getContentPane().add(new LFOptionPanel());
		// dlg.pack();
		// dlg.setVisible(true);
		// }
		// });
		// item = new JMenuItem("Fonts");
		// menuOptions.add(item);
		// item.addActionListener(new ActionListener()
		// {
		//
		// public void actionPerformed(ActionEvent e)
		// {
		// JDialog dlg = new JDialog(instance, "Fonts", false);
		// dlg.getContentPane().add(new FontsPanel());
		// dlg.pack();
		// dlg.setVisible(true);
		// }
		// });
		// JMenu menu = new JMenu("PLAFs");
		// menuOptions.add(menu);
		// final UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		// for(int i = 0; i < lfs.length; i++)
		// {
		// item = new JMenuItem(lfs[i].getName());
		// final String className = lfs[i].getClassName();
		// item.addActionListener(new ActionListener()
		// {
		// public void actionPerformed(ActionEvent e)
		// {
		// try
		// {
		// UIManager.setLookAndFeel(className);
		// SwingUtilities.updateComponentTreeUI(instance);
		// }
		// catch (Exception e1)
		// {
		// e1.printStackTrace();
		// }
		// }
		// });
		// menu.add(item);
		// }
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel_0 = new JPanel();
		panel_0.setLayout(new BorderLayout());
		getContentPane().add(panel_0);
		// splitPane_0.setDividerLocation(0.25);
		panel_0.add(splitPane_0, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout());
		panelScreens.setLayout(cardLayout1);
		btMapView.setText("Sternenkarte");
		btMapView.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				buttonMapView_actionPerformed(e);
			}
		});
		btKolonienView.setText("Zivilisation");
		btKolonienView.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btKolonienView_actionPerformed(e);
			}
		});
		panelGlobalStatus.setLayout(new BoxLayout(panelGlobalStatus, BoxLayout.X_AXIS));
		panelGlobalStatus.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		panelZeit.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		// panelZeit.setLayout(flowLayout1);
		jLabel1.setText("Zeit: ");
		btFleetScreen.setText("Flotten");
		btFleetScreen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btFleetScreen_actionPerformed(e);
			}
		});
		btConstructionScreen.setText("Forschung");
		btConstructionScreen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btConstructionScreen_actionPerformed(e);
			}
		});
		btShipDesignScreen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btShipDesignScreen_actionPerformed(e);
			}
		});
		btUniverseMap.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btUniverseMap_actionPerformed(e);
			}
		});
		btWarehouseScreen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				selectScreen(SCREEN_WAREHOUSE);
			}
		});
		btDiplomacy.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				selectScreen(SCREEN_DIPLOMACY);
			}
		});
		panel_1.add(panelScreens, BorderLayout.CENTER);
		panel_1.add(screenToolbar, BorderLayout.SOUTH);
		panelScreens.add(starmapScreen, SCREEN_STARMAP);
		panelScreens.add(panelKolonienScreen, SCREEN_KOLONIEN);
		panelScreens.add(fleetScreen, SCREEN_FLOTTEN);
		panelScreens.add(constructionScreen, SCREEN_SCIENCE);
		panelScreens.add(shipDesignScreen, SCREEN_SHIP_DESIGN);
		// screenPanel.add(colonyScreen, SCREEN_COLONY);
		// screenPanel.add(universeMapScreen, SCREEN_UNIVERSE);
		panelScreens.add(warehouseScreen, SCREEN_WAREHOUSE);
		panelScreens.add(diplomacyScreen, SCREEN_DIPLOMACY);
		screenToolbar.add(btMapView, null);
		screenToolbar.add(btKolonienView, null);
		screenToolbar.add(btFleetScreen, null);
		screenToolbar.add(btConstructionScreen, null);
		screenToolbar.add(btShipDesignScreen, null);
		// screenToolbar.add(btUniverseMap, null);
		screenToolbar.add(btWarehouseScreen, null);
		screenToolbar.add(btDiplomacy, null);
		splitPane_0.setTopComponent(panel_1);
		panelZeit.add(jLabel1, null);
		panelZeit.add(labelZeit, null);
		panelZeit.setMaximumSize(panelZeit.getPreferredSize());
		civOverview.setAlignmentX(0.0f);
		panelGlobalStatus.add(civOverview);
		panelGlobalStatus.add(Box.createHorizontalGlue());
		panelZeit.setAlignmentX(1.0f);
		panelGlobalStatus.add(panelZeit);
		this.getContentPane().add(panelGlobalStatus, BorderLayout.NORTH);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 1));
		splitPane_0.setBottomComponent(panel);
		panel.add(messagesPanel);
		ComponentResizerGroup group = new ComponentResizerGroup();
		group.add(btConstructionScreen);
		group.add(btFleetScreen);
		group.add(btKolonienView);
		group.add(btShipDesignScreen);
		group.add(btUniverseMap);
		group.add(btWarehouseScreen);
		group.add(btMapView);
		group.add(btDiplomacy);
		// :CHEAT: Testfehler erzeugen
		if(Main.isDebugMode())
		{
			panel_1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "Exception");
			panel_1.getActionMap().put("Exception", new AbstractAction()
			{

				public void actionPerformed(ActionEvent e)
				{
					throw new RuntimeException("Testfehler");
				}
			});
		}
	}

	public StarmapScreen getStarmapScreen()
	{
		return starmapScreen;
	}

	public FleetScreen getFleetScreen()
	{
		return fleetScreen;
	}

	public ActionMessagesPanel getMessagesPanel()
	{
		return messagesPanel;
	}

	public JToolBar getMainToolBar()
	{
		return screenToolbar;
	}

	/**
	 * Anzeigt einer der Bildschirme. Bildschirmnamen sind in Konstanten von Typ SCREEN_.....
	 * gespeichert.
	 */
	public void selectScreen(String screen)
	{
		cardLayout1.show(panelScreens, screen);
	}

	protected void processWindowEvent(WindowEvent event)
	{
		if(event.getID() == WindowEvent.WINDOW_CLOSING)
		{
			Main.instance().shutdown();
		}
	}

	/**
	 * Wird von GUI Subsystem aufgerufen, damit alle relevante Einstellungen gespeichert werden
	 */
	public void shutdown(Preferences prefs)
	{
		int x = getLocationOnScreen().x;
		int y = getLocationOnScreen().y;
		int height = getSize().height;
		int width = getSize().width;
		prefs.setProperty("MainWindow_X", Integer.toString(x));
		prefs.setProperty("MainWindow_Y", Integer.toString(y));
		prefs.setProperty("MainWindow_Height", Integer.toString(height));
		prefs.setProperty("MainWindow_Width", Integer.toString(width));
		prefs.setProperty("MainWindow_Dividor_0", Integer.toString(splitPane_0.getDividerLocation()));
		starmapScreen.saveProperties(prefs);
	}

	/**
	 * Wird von GUI-Subsystem aufgerufen, um die zuvor gespeicherte Einstellungen wiederherzustellen
	 */
	public void restoreSettings(Preferences prefs)
	{
		try
		{
			int x = prefs.getAsInteger("MainWindow_X", new Integer(0)).intValue();
			int y = prefs.getAsInteger("MainWindow_Y", new Integer(0)).intValue();
			int height = prefs.getAsInteger("MainWindow_Height", new Integer(600)).intValue();
			int width = prefs.getAsInteger("MainWindow_Width", new Integer(800)).intValue();
			if(x > 800 || x < 0) x = 0;// Sicherheitsabfregen
			if(y > 800 || y < 0) y = 0;// Damit MainWindow unter allen Umständen sichtbar bleibt
			Dimension size = getToolkit().getScreenSize();
			if(height > size.height || x < 0) height = size.height;
			if(width > size.width || x < 0) width = size.width;
			setSize(width, height);
			setLocation(x, y);
			int dividor = prefs.getAsInteger("MainWindow_Dividor_0", new Integer(600)).intValue();
			splitPane_0.setDividerLocation(dividor);
			starmapScreen.restoreProperties(prefs);
		} catch(Throwable th)
		{
			Main.instance().getGUI().getLogger().warning("Fehler bei Wiederherstellung der Einstellungen: " + th.getLocalizedMessage());
			Main.instance().getGUI().getLogger().throwing("MainFrame", "restoreSettings(..)", th);
		}
	}

	private void buttonMapView_actionPerformed(ActionEvent e)
	{
		selectScreen(SCREEN_STARMAP);
	}

	private void btKolonienView_actionPerformed(ActionEvent e)
	{
		selectScreen(SCREEN_KOLONIEN);
	}

	private void btFleetScreen_actionPerformed(ActionEvent e)
	{
		selectScreen(SCREEN_FLOTTEN);
	}

	private void btConstructionScreen_actionPerformed(ActionEvent e)
	{
		selectScreen(SCREEN_SCIENCE);
	}

	private void btShipDesignScreen_actionPerformed(ActionEvent e)
	{
		selectScreen(SCREEN_SHIP_DESIGN);
	}

	private void btUniverseMap_actionPerformed(ActionEvent ev)
	{
		selectScreen(SCREEN_UNIVERSE);
	}
}