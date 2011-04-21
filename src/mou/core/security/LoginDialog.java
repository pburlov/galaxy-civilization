/*
 * $Id: LoginDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.security;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
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
public class LoginDialog extends JDialog
{

	static final private String LOGIN_SCRIPT = "login.php";
	static final private int MAX_LOGIN_DATA_LENGTH = 20;// Wieviel Zeichen maximal für Password
	private JTextField tfUsername = new JTextField(MAX_LOGIN_DATA_LENGTH);
	private JPasswordField tfPassword = new JPasswordField(MAX_LOGIN_DATA_LENGTH);
	private JLabel infoText = new JLabel(
			"<html>Vor dem ersten Login müssen Sie sich registrieren.<br>"
					+ "Erst nach einer erfolgreicher Registrierung können Sie sich einloggen.<br><br></html>");
	private boolean success = false;
	private long serial;
	private LoginDialog self;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public LoginDialog() throws HeadlessException
	{
		super((Frame) null, "Spieler Login", true);
		self = this;
		setLayout(new BorderLayout());
		add(infoText, BorderLayout.NORTH);
		// infoText.setEditable(false);
		JPanel panel = new JPanel(new TableLayout(2, "W"));
		add(panel, BorderLayout.CENTER);
		panel.add("CS=2", new JSeparator());
		panel.add(new JLabel("Benutzername: "));
		panel.add(tfUsername);
		panel.add(new JLabel("Passwort: "));
		panel.add(tfPassword);
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		final JButton btLogin = new JButton("Einloggen");
		JButton btRegister = new JButton("Registrieren");
		JButton btCancel = new JButton("Abbrechen");
		panel.add(btLogin);
		panel.add(btRegister);
		panel.add(btCancel);
		btLogin.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				startLogin();
			}
		});
		btRegister.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				RegisterDialog dialog = new RegisterDialog(self);
				if(dialog.showDialog())
				{
					tfUsername.setText(dialog.getUsername());
					tfPassword.setText(dialog.getPassword());
				}
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
				btLogin.doClick();
			}
		});
		tfUsername.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Submit");
		tfUsername.getActionMap().put("Submit", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				btLogin.doClick();
			}
		});
		pack();
		setResizable(false);
	}

	private void startLogin()
	{
		if(Main.isOnlineMode())
		{
			UrlCommunicator com = new UrlCommunicator(Main.GAME_SERVER_URL + LOGIN_SCRIPT);
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
				JOptionPane.showMessageDialog(this, error, "Fehler", JOptionPane.ERROR_MESSAGE);
				return;
			}
			List<String> reply = com.getReplyValues();
			if(reply.size() < 1)
			{
				JOptionPane.showMessageDialog(this, "Fehlerhafter Antwort vom Server", "Fehler",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		serial = SecuritySubsystem.instance().generateSerialNumber(tfUsername.getText());
		success = true;
		setVisible(false);
	}

	public boolean showDialog()
	{
		success = false;
		tfUsername.requestFocusInWindow();
		GUI.centreWindow(null, this);
		setVisible(true);
		return success;
	}

	public long getSerialNumber()
	{
		return serial;
	}

	public String getPassword()
	{
		return tfPassword.getText();
	}
}
