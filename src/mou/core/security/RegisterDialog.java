/*
 * $Id: RegisterDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.security;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import mou.Main;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;
import burlov.net.UrlCommunicator;

/**
 * @author pb
 */
public class RegisterDialog extends JDialog
{
	static final private String REGISTER_SCRIPT = "register.php";
	static final private int MAX_LOGIN_DATA_LENGTH = 20;// Wieviel Zeichen maximal für Password
	private JLabel infoText = new JLabel(
			"<html>Der Benutzername dient nur der Anmeldung im System<br>"
					+ "und wird im Spiel weder benutzt noch sichtbar.<br>"
					+ "Bitte vergessen Sie Ihre Anmeldedaten nicht, denn sonst<br>"
					+ "wird Ihr Spielstand verloren gehen." + "<br>" +
							"<b>Achtung!</b> Es dürfen nur Ziffern und Buchstaben für das <br>" +
							"Passwort verwendet werden</html>");
	private JTextField tfUsername = new JTextField(MAX_LOGIN_DATA_LENGTH);
	private JPasswordField tfPassword = new JPasswordField(MAX_LOGIN_DATA_LENGTH);
	private JPasswordField tfPasswordConfirm = new JPasswordField(MAX_LOGIN_DATA_LENGTH);
	private JCheckBox ckAGB = new JCheckBox(
			"Ich habe die beigelegte AGB gelesen und zugestimmt",false);
	private boolean success = false;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public RegisterDialog(JDialog parent) throws HeadlessException
	{
		super(parent, "Registrierung", true);
		setLayout(new BorderLayout());
		add(infoText, BorderLayout.NORTH);
		JPanel panel = new JPanel(new TableLayout(2, "W"));
		add(panel, BorderLayout.CENTER);
		panel.add("CS=2",ckAGB);
		panel.add(new JLabel("Benutzername: "));
		panel.add(tfUsername);
		panel.add(new JLabel("Passwort (5-20 Zeichen): "));
		panel.add(tfPassword);
		panel.add(new JLabel("Passwort wiederholen:"));
		panel.add(tfPasswordConfirm);
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		final JButton btRegister = new JButton("Registrieren");
		JButton btCancel = new JButton("Abbrechen");
		panel.add(btRegister);
		panel.add(btCancel);
		ckAGB.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(ckAGB.isSelected())btRegister.setEnabled(true);
				else btRegister.setEnabled(false);
			}
		});
		btRegister.setEnabled(false);
		btRegister.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				startRegister();
				if(success) setVisible(false);
			}
		});
		btCancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				success = false;
				setVisible(false);
			}
		});
		/*
		 * Aus dem Namen wird ehe ein Hashwert berechnet, also ist es nicht nötig ihn hier zu
		 * bergrenzen
		 */
		// ((AbstractDocument) tfUsername.getDocument()).setDocumentFilter(new LoginDocumentFilter(
		// MAX_LOGIN_DATA_LENGTH));
		((AbstractDocument) tfPassword.getDocument()).setDocumentFilter(new LoginDocumentFilter(
				MAX_LOGIN_DATA_LENGTH));
		/*
		 * Bei drücken der Enter Taste programmatisch Send Button drücken
		 */
		tfPassword.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Submit");
		tfPassword.getActionMap().put("Submit", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				btRegister.doClick();
			}
		});
		tfPasswordConfirm.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Submit");
		tfPasswordConfirm.getActionMap().put("Submit", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				btRegister.doClick();
			}
		});
		tfUsername.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Submit");
		tfUsername.getActionMap().put("Submit", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				btRegister.doClick();
			}
		});
		pack();
		setResizable(false);
	}

	private void startRegister()
	{
		tfPassword.setText(tfPassword.getText().replaceAll("[^a-zA-Z0-9]", ""));
		tfPasswordConfirm.setText(tfPasswordConfirm.getText().replaceAll("[^a-zA-Z0-9]", ""));
		if(!tfPassword.getText().equals(tfPasswordConfirm.getText()))
		{
			JOptionPane.showMessageDialog(this, "Passwörter stimmen nicht überein", "Fehler",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(tfPassword.getText().length() < 5)
		{
			JOptionPane.showMessageDialog(this, "Das Passwort ist zu kurz", "Fehler",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		UrlCommunicator com = new UrlCommunicator(Main.GAME_SERVER_URL + REGISTER_SCRIPT);
		String[] params = {
				"id=" + SecuritySubsystem.instance().generateSerialNumber(tfUsername.getText()),
				"password=" + URLEncoder.encode(tfPassword.getText())};
		try
		{
			com.sendCommand(params);
		} catch(IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Übertragungsfehler", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String error = com.getError();
		if(error != null)
		{
			JOptionPane.showMessageDialog(this, "Fehlermeldung vom Server:\n" + error, "Fehler",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// System.out.println(com.getReplyValues());
		// System.out.println(com.getError());
		success = true;
	}

	public boolean showDialog()
	{
		success = false;
		tfUsername.requestFocusInWindow();
		GUI.centreWindow(null, this);
		setVisible(true);
		return success;
	}
	
	public String getUsername()
	{
		return tfUsername.getText();
	}
	
	public String getPassword()
	{
		return tfPassword.getText();
	}
}
