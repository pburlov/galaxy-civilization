/*
 * $Id: DefaultOfferSendDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.diplomacy.DiplActionTable;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.gui.GUI;

/**
 * @author pb
 */
public class DefaultOfferSendDialog extends JDialog
{

	private boolean ok = false;
	public JSpinner spinnerDauer = new JSpinner(new SpinnerNumberModel(10, 1, 60, 1));
	private JTextArea taComment = new JTextArea();
	private JButton btOk = new JButton("Ok");
	private JButton btCancel = new JButton("Abbrechen");

	/**
	 * @throws java.awt.HeadlessException
	 */
	public DefaultOfferSendDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), true);
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Angebotsdauer (Minuten):"));
		panel.add(spinnerDauer);
		add(panel, BorderLayout.NORTH);
		panel = new JPanel(new BorderLayout());
		panel.setMinimumSize(new Dimension(100, 200));
		panel.setBorder(new TitledBorder("Kommentar"));
		JScrollPane scroll = new JScrollPane();
		panel.add(scroll, BorderLayout.CENTER);
		add(panel, BorderLayout.CENTER);
		scroll.setViewportView(taComment);
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.add(btOk);
		panel.add(btCancel);
		btOk.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				ok = true;
				setVisible(false);
			}
		});
		btCancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				ok = false;
				setVisible(false);
			}
		});
		taComment.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		taComment.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		pack();
	}

	public boolean showDialog(String title)
	{
		ok = false;
		setTitle(title);
		taComment.setText("");
		spinnerDauer.setValue(10);
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
		return ok;
	}

	public String getComment()
	{
		return taComment.getText();
	}

	public int getValidInterval()
	{
		return ((Number) spinnerDauer.getValue()).intValue();
	}
}
