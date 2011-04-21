/*
 * $Id: ViewMessageDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import mou.core.civilization.CivilizationDB;
import mou.gui.GUI;
import mou.gui.diplomacy.MessagesTable.TextMessage;

/**
 * @author pb
 */
public class ViewMessageDialog extends JDialog
{

	static final private char COMMENT_CHAR = '>';
	private JLabel labelSender = new JLabel();
	private JTextArea textArea = new JTextArea(20, 50);
	private TextMessage message;
	private JDialog self;
	private SendMessageDialog sendDialog = new SendMessageDialog();
	private boolean delete = false;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public ViewMessageDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), "Nachricht lesen", true);
		self = this;
		textArea.setLineWrap(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.add(new JLabel("Sender: "));
		panel.add(labelSender);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);
		getContentPane().add(scroll, BorderLayout.CENTER);
		panel = new JPanel();
		JButton buttonReply = new JButton("Antworten");
		JButton buttonClose = new JButton("Schliessen");
		JButton buttonDelete = new JButton("Löschen");
		panel.add(buttonReply);
		panel.add(buttonClose);
		panel.add(buttonDelete);
		getContentPane().add(panel, BorderLayout.SOUTH);
		buttonReply.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				textArea.requestFocusInWindow();
				replyMessage();
			}
		});
		buttonClose.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				textArea.requestFocusInWindow();
				setVisible(false);
			}
		});
		buttonDelete.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				textArea.requestFocusInWindow();
				delete = true;
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
		textArea.setEditable(false);
		// textArea.setEnabled(false);
		pack();
	}

	private void replyMessage()
	{
		//TODO
//		DiplomacyServer server = Main.instance().getNetSubsystem().getDiplomacyServer();
//		if(server.isCivOnline(message.getSource()))
//		{
//			setVisible(false);
//			sendDialog.showDialog(CivilizationDB.createCivID(message.getSource()), commentOriginText(message.getText()));
//		} else
//			JOptionPane.showConfirmDialog(self, "Der Empfänger ist zur Zeit nicht erreichbar!", "Empfänger offline", JOptionPane.OK_OPTION,
//					JOptionPane.WARNING_MESSAGE);
		setVisible(false);
	}

	/**
	 * Methode stellt vor jeder Zeile den Kommentarzeichen wie bei Reply-Email
	 * 
	 * @param text
	 * @return
	 */
	private String commentOriginText(String text)
	{
		StringBuffer buf = new StringBuffer();
		String[] lines = text.split("\n");
		for(int i = 0; i < lines.length; i++)
		{
			buf.append(COMMENT_CHAR);
			buf.append(lines[i]);
			buf.append('\n');
		}
		return buf.toString();
	}

	public boolean showDialog(TextMessage msg)
	{
		if(msg == null) return false;
		delete = false;
		textArea.setText(msg.getText());
		message = msg;
		labelSender.setText(Main.instance().getMOUDB().getCivilizationDB().getCivName(CivilizationDB.createCivID(msg.getSource())));
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
		return delete;
	}
}
