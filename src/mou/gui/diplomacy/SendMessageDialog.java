/*
 * $Id: SendMessageDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import mou.Main;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * @author pb
 */
public class SendMessageDialog extends JDialog
{

	private JLabel labelReceiver = new JLabel();
	private JTextArea textArea = new JTextArea(20, 50);
	private ID receiver;
	private Set<ID> receivers;
	private JDialog self;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public SendMessageDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), "Nachricht verfassen", true);
		self = this;
		textArea.setLineWrap(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.add(new JLabel("Empfaenger: "));
		panel.add(labelReceiver);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);
		getContentPane().add(scroll, BorderLayout.CENTER);
		panel = new JPanel();
		JButton buttonSend = new JButton("Absenden");
		JButton buttonCancel = new JButton("Abbrechen");
		panel.add(buttonSend);
		panel.add(buttonCancel);
		getContentPane().add(panel, BorderLayout.SOUTH);
		//TODO
//		buttonSend.addActionListener(new ActionListener()
//		{
//
//			public void actionPerformed(ActionEvent e)
//			{
//				// final Set<ID> recs = receivers;
//				DiplomacyServer server = Main.instance().getNetSubsystem().getDiplomacyServer();
//				if(receiver != null)
//				{
//					/*
//					 * Der Fall nur mit einem Empfaenger
//					 */
//					if(server.isCivOnline(receiver.getConstantPart()))
//						server.sendTextMessageTo(receiver.getConstantPart(), textArea.getText());
//					else
//						JOptionPane.showConfirmDialog(self, "Der Empfaenger ist zur Zeit nicht erreichbar!", "Empfaenger offline", JOptionPane.OK_OPTION,
//								JOptionPane.WARNING_MESSAGE);
//					textArea.requestFocusInWindow();
//				} else if(receivers != null)
//				{
//					/*
//					 * Der Fall mit mehreren Empfaenger
//					 */
//					for(ID rec : receivers)
//					{
//						server.sendTextMessageTo(rec.getConstantPart(), textArea.getText());
//					}
//				}
//				setVisible(false);
//			}
//		});
		buttonCancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				textArea.requestFocusInWindow();
				setVisible(false);
			}
		});
		textArea.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		textArea.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		pack();
	}

	public void showDialog(ID target, String text)
	{
		if(text != null) textArea.setText(text);
		receiver = target;
		receivers = null;
		labelReceiver.setText(Main.instance().getMOUDB().getCivilizationDB().getCivName(target));
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
	}

	public void showDialog(Set<ID> targets, String text)
	{
		if(text != null) textArea.setText(text);
		receivers = targets;
		receiver = null;
		labelReceiver.setText(targets.size() + " Empfaenger");
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
	}
}
