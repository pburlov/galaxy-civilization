/*
 * $Id: TradeScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.tradescreen;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mou.Main;
import mou.Preferences;
import mou.core.civilization.Civilization;
import mou.core.trade.RemoteShipTradeOffer;
import mou.core.trade.TraderDB;
import mou.gui.GUIScreen;
import mou.storage.ser.ID;
import burlov.collections.TimeOutHashtable;

/**
 * Bildschirm zur Darstellung der gelagerten Waren und Ressourcen
 * 
 * @author PB
 */
public class TradeScreen extends JPanel
		implements GUIScreen
{

	private JTabbedPane tabbedPane = new JTabbedPane();
	private ShipOffersTable localShipOffersTable = new ShipOffersTable(new LocalShipOffersTableModel());
	private JButton buttonCloseTab = new JButton("Panel schliessen");
	private JButton buttonRefresh = new JButton("Erneuern");
	private JButton buttonNewTab = new JButton("Angebot holen");
	private JCheckBox ckSellONAllyOnly = new JCheckBox("Nur an Alliierte verkaufen");
	private BuyDialog buyDialog = new BuyDialog();
	private CivilizationsTableDialog civTableDialog = new CivilizationsTableDialog();
	private TimeOutHashtable<ID, ID> requestedShipOffers = new TimeOutHashtable<ID, ID>(60 * 1000);
//	private TradeServer tradeServer = Main.instance().getNetSubsystem().getTradeServer();
	private HashMap<ShipOffersTable, Civilization> mapTableToCiv = new HashMap<ShipOffersTable, Civilization>();
	private TraderDB tradeDB = Main.instance().getMOUDB().getTraderDB();

	/**
	 * 
	 */
	public TradeScreen()
	{
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.add(buttonNewTab);
		panel.add(buttonRefresh);
		panel.add(buttonCloseTab);
		add(panel, BorderLayout.SOUTH);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(panel, BorderLayout.NORTH);
		panel.setBorder(new TitledBorder("Optionen"));
		panel.add(ckSellONAllyOnly);
		ckSellONAllyOnly.setSelected(tradeDB.isSellOnAllyOnly());
		ckSellONAllyOnly.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				tradeDB.setSellOnAllyOnly(ckSellONAllyOnly.isSelected());
			}
		});
		/*
		 * Da eigene Angebote zuerst gezeigt werden, m?ssen die Buttons deaktiwiert werden
		 */
		buttonCloseTab.setEnabled(false);
		buttonRefresh.setEnabled(false);
		tabbedPane.addTab("Mein Angebot", localShipOffersTable);
		tabbedPane.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				if(localShipOffersTable == tabbedPane.getSelectedComponent())
				{
					/*
					 * F?r eigene Angebote Buttons deaktivieren
					 */
					buttonCloseTab.setEnabled(false);
					// buttonNewTab.setEnabled(false);
					buttonRefresh.setEnabled(false);
				} else
				{
					buttonCloseTab.setEnabled(true);
					// buttonNewTab.setEnabled(true);
					buttonRefresh.setEnabled(true);
				}
			}
		});
		localShipOffersTable.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				RemoteShipTradeOffer offer = localShipOffersTable.getSelectedOffer();
				if(offer == null) return;
				int menge = buyDialog.showDialog(offer.getQuantity(), offer.getPrice(), offer.getName());
				if(menge < 1) return;
				Main.instance().getMOUDB().getTraderDB().buyShipsLocal(offer.getOfferID(), menge);
			}
		});
		buttonNewTab.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Civilization civ = civTableDialog.showDialog();
				if(civ == null) return;
				if(civ.isEnemy())
				{
					JOptionPane.showMessageDialog(tabbedPane, "Diese Zivilisation ist feindlich", "", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				/*
				 * Pr?fen, ob shon eine Anfrage zu dieser Zivilisation l?uft
				 */
				if(requestedShipOffers.contains(civ.getID())) return;
				requestedShipOffers.put(civ.getID(), civ.getID());
				createNewTabForCiv(civ);
			}
		});
		buttonCloseTab.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				ShipOffersTable table = (ShipOffersTable) tabbedPane.getSelectedComponent();
				tabbedPane.remove(table);
				mapTableToCiv.remove(table);
			}
		});
		buttonRefresh.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				refreshCurrentTab();
			}
		});
	}

	private void refreshCurrentTab()
	{
		final ShipOffersTable table = (ShipOffersTable) tabbedPane.getSelectedComponent();
		final Civilization civ = mapTableToCiv.get(table);
		if(requestedShipOffers.contains(civ.getID())) return;
		requestedShipOffers.put(civ.getID(), civ.getID());
		//TODO
//		/*
//		 * Zuerst Daten asynchron aus dem Netz anfordern
//		 */
//		new Thread(new Runnable()
//		{
//
//			public void run()
//			{
//				final List<RemoteShipTradeOffer> offers = tradeServer.requestShipOffers(new Long(civ.getID().getConstantPart()));
//				/*
//				 * Bekommene Daten in Swing Thread anzeigen
//				 */
//				SwingUtilities.invokeLater(new Runnable()
//				{
//
//					public void run()
//					{
//						table.showData(offers);
//						requestedShipOffers.remove(civ.getID());
//					}
//				});
//			}
//		}).start();
	}

	private void createNewTabForCiv(final Civilization civ)
	{
		//TODO
//		/*
//		 * Zuerst Daten asynchron aus dem Netz anfordern
//		 */
//		new Thread(new Runnable()
//		{
//
//			public void run()
//			{
//				final List<RemoteShipTradeOffer> offers = tradeServer.requestShipOffers(new Long(civ.getID().getConstantPart()));
//				/*
//				 * Bekommene Daten in Swing Thread anzeigen
//				 */
//				SwingUtilities.invokeLater(new Runnable()
//				{
//
//					public void run()
//					{
//						createNewTabForCiv(civ, offers);
//					}
//				});
//			}
//		}).start();
	}

	private void createNewTabForCiv(final Civilization civ, List<RemoteShipTradeOffer> data)
	{
		final ShipOffersTable newTable = new ShipOffersTable(new ShipOffersTableModel());
		newTable.showData(data);
		newTable.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				//TODO
//				final RemoteShipTradeOffer offer = newTable.getSelectedOffer();
//				if(offer == null) return;
//				final int menge = buyDialog.showDialog(offer.getQuantity(), offer.getPrice(), offer.getName());
//				if(menge < 1) return;
//				new Thread(new Runnable()
//				{
//
//					public void run()
//					{
//						int count = Main.instance().getNetSubsystem().getTradeServer().buyShips(offer, menge);
//						if(count < 1)
//						{
//							Main.instance().getGUI().promtMessage("Kauf gescheitert",
//									"Der Versuch Schiffe von \"" + civ.getName() + "\" zu kaufen ist gescheitert.", GUI.MSG_PRIORITY_NORMAL);
//						} else
//						{
//							Main.instance().getGUI().promtMessage("Schiffe gekauft", count + " Schiffe von \"" + civ.getName() + "\" gekauft.",
//									GUI.MSG_PRIORITY_NORMAL);
//						}
//						SwingUtilities.invokeLater(new Runnable()
//						{
//
//							public void run()
//							{
//								refreshCurrentTab();
//							}
//						});
//					}
//				}).start();
			}
		});
		tabbedPane.addTab(civ.getName(), newTable);
		tabbedPane.setSelectedComponent(newTable);
		mapTableToCiv.put(newTable, civ);
		requestedShipOffers.remove(civ.getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#saveProperties(mou.Preferences)
	 */
	public void saveProperties(Preferences prefs)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.gui.GUIScreen#restoreProperties(mou.Preferences)
	 */
	public void restoreProperties(Preferences prefs)
	{
	}
}
