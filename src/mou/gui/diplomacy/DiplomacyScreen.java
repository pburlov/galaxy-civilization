/*
 * $Id: DiplomacyScreen.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.Preferences;
import mou.core.civilization.Civilization;
import mou.gui.GUI;
import mou.gui.GUIScreen;
import mou.gui.diplomacy.DiplActionTable.DiplomacyActionsTable;
import mou.gui.diplomacy.FremdeCivTable.FremdeCivTabelle;
import mou.gui.diplomacy.MessagesTable.MessagesTable;
import mou.gui.diplomacy.MessagesTable.TextMessage;
import mou.net.diplomacy.AbstractDiplomacyAction;
import mou.net.diplomacy.IDiplomacyActionReceiver;
import mou.net.diplomacy.ITextMessageReceiver;
import mou.storage.ser.ID;

/**
 * Bildschrim zu Anzeigen von diplomatischen Informationen
 * 
 * @author pb
 */
public class DiplomacyScreen extends JPanel
		implements GUIScreen
{

	private FremdeCivTabelle civTable = new FremdeCivTabelle();
	private MessagesTable msgTable = new MessagesTable();
	private MessagesTable allyMsgTable = new MessagesTable();
	private DiplomacyActionsTable actionTable = new DiplomacyActionsTable();
	private DiplomacyDialog diplomacyDialog = new DiplomacyDialog();

	/**
	 * 
	 */
	public DiplomacyScreen()
	{
		super(new BorderLayout());
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// add(civTable, BorderLayout.WEST);
		add(split, BorderLayout.CENTER);
		/*
		 * Message Tabellen initialisieren
		 */
		JPanel panel = new JPanel(new GridLayout(3, 1));
		split.setRightComponent(panel);
		panel.add(allyMsgTable);
		panel.add(msgTable);
		panel.add(actionTable);
		panel = new JPanel(new BorderLayout());
		split.setLeftComponent(panel);
		panel.add(civTable, BorderLayout.CENTER);
		split.setDividerLocation(0.5d);
		JPanel panel2 = new JPanel();
		panel.add(panel2, BorderLayout.SOUTH);
		JButton btBroadcastToAlly = new JButton("Nachricht an alle Alliierte");
		panel2.add(btBroadcastToAlly);
		JButton btBroadcast = new JButton("Nachricht an alle schicken");
		panel2.add(btBroadcast);
		//TODO
//		btBroadcastToAlly.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent arg0)
//			{
//				SendMessageDialog dlg = new SendMessageDialog();
//				Set<ID> ally = Main.instance().getMOUDB().getCivilizationDB().getAlliiertCivs();
//				HashSet<ID> allyOnline = new HashSet<ID>(ally.size());
//				DiplomacyServer server = Main.instance().getNetSubsystem().getDiplomacyServer();
//				for(ID id : ally)
//				{
//					if(server.isCivOnline(id.getConstantPart())) allyOnline.add(id);
//				}
//				dlg.showDialog(allyOnline, null);
//			}
//		});
//		btBroadcast.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent arg0)
//			{
//				SendMessageDialog dlg = new SendMessageDialog();
//				Set<ID> allCivs = Main.instance().getMOUDB().getCivilizationDB().getAllCivs().keySet();
//				HashSet<ID> civsOnline = new HashSet<ID>(allCivs.size());
//				DiplomacyServer server = Main.instance().getNetSubsystem().getDiplomacyServer();
//				for(ID id : allCivs)
//				{
//					if(server.isCivOnline(id.getConstantPart())) civsOnline.add(id);
//				}
//				dlg.showDialog(civsOnline, null);
//			}
//		});
		civTable.setBorder(new TitledBorder("Bekannte Zivilisationen online"));
		civTable.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent ev)
			{
				if(ev.getButton() == MouseEvent.BUTTON1)
				{// Kolonie detailiert anzeigen
					Civilization civ = civTable.getSelectedCivilization();
					if(civ == null) return;
					diplomacyDialog.showDialog(civ);
					// sendMsgDialog.showDialog(civ.getID(), null);
				}
			}
		});
		msgTable.setBorder(new TitledBorder("Spielermitteilungen"));
		allyMsgTable.setBorder(new TitledBorder("Mitteilungen von alliierten Zivilisationen"));
		actionTable.setBorder(new TitledBorder("Diplomatische Angebote"));
		//TODO
//		Main.instance().getNetSubsystem().getDiplomacyServer().addDiplomacyActionReceiver(new IDiplomacyActionReceiver()
//		{
//
//			/*
//			 * (non-Javadoc)
//			 * 
//			 * @see mou.net.diplomacy.IDiplomacyActionReceiver#receiveAction(mou.net.diplomacy.AbstractDiplomacyAction)
//			 */
//			public void receiveAction(final AbstractDiplomacyAction action)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//
//					public void run()
//					{
//						actionTable.addDiplomacyAction(action);
//					}
//				});
//			}
//		});
//		/*
//		 * Hier Textnachrichten empfangen und auf entsprechende Tabellen verteilen
//		 */
//		Main.instance().getNetSubsystem().getDiplomacyServer().addTextMessageReceiver(new ITextMessageReceiver()
//		{
//
//			/*
//			 * (non-Javadoc)
//			 * 
//			 * @see mou.net.TextMessageReceiver#receiveMessage(java.lang.Long, java.lang.String)
//			 */
//			public void receiveMessage(final Long sender, final String msg)
//			{
//				/*
//				 * Nur im Swing Thread aktualisieren
//				 */
//				if(!SwingUtilities.isEventDispatchThread())
//				{
//					SwingUtilities.invokeLater(new Runnable()
//					{
//
//						public void run()
//						{
//							receiveMessage(sender, msg);
//						}
//					});
//					return;
//				}
//				TextMessage tmsg = new TextMessage(msg, sender, Main.instance().getTime());
//				Civilization civ = Main.instance().getMOUDB().getCivilizationDB().getCivilization(sender);
//				if(civ == null || !civ.isAlly())
//				{
//					msgTable.addMessage(tmsg);
//					Main.instance().getGUI().promtMessage("Nachricht von fremder Zivilisation empfangen", "", GUI.MSG_PRIORITY_NORMAL, new Runnable()
//					{
//
//						public void run()
//						{
//							Main.instance().getGUI().selectDiplomacyScreen();
//						}
//					});
//				} else
//				{
//					allyMsgTable.addMessage(tmsg);
//					Main.instance().getGUI().promtMessage("Nachricht von alliierter Zivilisation empfangen", "", GUI.MSG_PRIORITY_NORMAL, new Runnable()
//					{
//
//						public void run()
//						{
//							Main.instance().getGUI().selectDiplomacyScreen();
//						}
//					});
//				}
//			}
//		});
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
