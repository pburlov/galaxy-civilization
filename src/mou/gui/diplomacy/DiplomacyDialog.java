/*
 * $Id: DiplomacyDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy;

import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import mou.Main;
import mou.core.civilization.Civilization;
import mou.gui.GUI;
import mou.gui.diplomacy.DiplActionTable.DefaultOfferSendDialog;
import mou.gui.layout.TableLayout;

/**
 * @author pb
 */
public class DiplomacyDialog extends JDialog
{

	private Civilization showedCiv;
	private CivInfoPanel infoPanel = new CivInfoPanel();
	private JButton btSendSpy = new JButton("Spionageaktion starten");
	private JButton btFreedomOffer = new JButton("Frieden anbieten");
	private JButton btAllyOffer = new JButton("Alliance anbieten");
	private JButton btAllyBreak = new JButton("Alliance lösen");
	private JButton btMessage = new JButton("Nachricht schicken");
	private JButton btGiftOffer = new JButton("Geschenk schicken");
	private SendMessageDialog sendMsgDialog = new SendMessageDialog();
	private DefaultOfferSendDialog offerDialog = new DefaultOfferSendDialog();
	private DiplomacyDialog self;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public DiplomacyDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), true);
		self = this;
		Container contentPane = getContentPane();
		contentPane.setLayout(new TableLayout(2, "FH"));
		contentPane.add("CS=2", infoPanel);// Auf zwei Spalten spannen
		infoPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		infoPanel.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		// contentPane.add(Box.createGlue());//Nur ein Füller
		contentPane.add(btMessage);
		contentPane.add(btGiftOffer);
		contentPane.add(btSendSpy);
		contentPane.add(btFreedomOffer);
		contentPane.add(btAllyOffer);
		contentPane.add(btAllyBreak);
		btMessage.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				sendMsgDialog.showDialog(showedCiv.getID(), null);
				setVisible(false);
			}
		});
//		btSendSpy.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				Main.instance().getNetSubsystem().getDiplomacyServer().startSpyAction(showedCiv, 0);
//				// setVisible(false);
//			}
//		});
//		btFreedomOffer.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				if(!offerDialog.showDialog("Fridensangebot abschicken")) return;
//				setVisible(false);
//				Main.instance().getNetSubsystem().getDiplomacyServer().requestFreedom(showedCiv, offerDialog.getValidInterval(), offerDialog.getComment());
//			}
//		});
//		btAllyOffer.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				if(!offerDialog.showDialog("Allianzangebot abschicken")) return;
//				setVisible(false);
//				Main.instance().getNetSubsystem().getDiplomacyServer().requestAlliance(showedCiv, offerDialog.getValidInterval(), offerDialog.getComment());
//			}
//		});
//		btAllyBreak.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				Main.instance().getNetSubsystem().getDiplomacyServer().breakAlliance(showedCiv);
//				setVisible(false);
//			}
//		});
//		btGiftOffer.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				final GiftOfferDialog giftDialog = new GiftOfferDialog();
//				if(!giftDialog.showDialog(self)) return;
//				setVisible(false);
//				if(giftDialog.isMoneySelected()) Main.instance().getNetSubsystem().getDiplomacyServer().sendMoneyGift(showedCiv, giftDialog.getMoney());
//				if(giftDialog.isColonySelected()) Main.instance().getNetSubsystem().getDiplomacyServer().sendColonyGift(showedCiv, giftDialog.getColony());
//			}
//		});
		/*
		 * Timer zum Erfrischen der Anzeige
		 */
		new Timer(1000, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(showedCiv == null) return;
				showCiv(showedCiv);
			}
		}).start();
	}

	private void showCiv(Civilization civ)
	{
		showedCiv = civ;
		if(civ.isAlly())
		{
			btAllyOffer.setEnabled(false);
			btAllyBreak.setEnabled(true);
			btFreedomOffer.setEnabled(false);
			btSendSpy.setEnabled(true);
		} else if(civ.isEnemy())
		{
			btAllyOffer.setEnabled(false);
			btAllyBreak.setEnabled(false);
			btFreedomOffer.setEnabled(true);
			btSendSpy.setEnabled(true);
		} else
		{
			/*
			 * Neutrale Civ
			 */
			btAllyOffer.setEnabled(true);
			btAllyBreak.setEnabled(false);
			btFreedomOffer.setEnabled(false);
			btSendSpy.setEnabled(true);
		}
		infoPanel.showCiv(civ);
	}

	public void showDialog(Civilization civ)
	{
		showCiv(civ);
		pack();
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
	}
}
