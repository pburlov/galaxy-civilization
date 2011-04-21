/*
 * $Id: DiplActionViewDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.diplomacy.DiplActionTable;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;
import mou.net.diplomacy.AbstractDiplomacyAction;

/**
 * @author pb
 */
public class DiplActionViewDialog extends JDialog
{

	private boolean deleteOffer = false;
	private JLabel labelCiv = new JLabel();
	private JTextArea taComment = new JTextArea();
	private JLabel labelValidDate = new JLabel();
	private JButton btAccept = new JButton("Akzeptieren");
	private JButton btReject = new JButton("Ablehnen");
	private JButton btWait = new JButton("Abwarten");
	private JButton btDelete = new JButton("Löschen");
	private AbstractDiplomacyAction showedAction;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public DiplActionViewDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), true);
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new TableLayout(2, "W"));
		add(panel, BorderLayout.NORTH);
		panel.add(new JLabel("Absender: "));
		panel.add("FH", labelCiv);
		panel.add(new JLabel("Gültig bis: "));
		panel.add("FH", labelValidDate);
		panel = new JPanel(new BorderLayout());
		panel.setMinimumSize(new Dimension(100, 200));
		add(panel, BorderLayout.CENTER);
		panel.setBorder(new TitledBorder("Kommentar"));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(taComment);
		taComment.setEditable(false);
		panel.add(scroll, BorderLayout.CENTER);
		panel = new JPanel(new TableLayout(4));
		add(panel, BorderLayout.SOUTH);
		panel.add(btAccept);
		panel.add(btReject);
		panel.add(btWait);
		panel.add(btDelete);
		pack();
		btAccept.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if(showedAction.getValidBefor() < Main.instance().getTime())
				{
					/*
					 * Angebot ist abgelaufen
					 */
					int option = JOptionPane.showConfirmDialog(taComment, "Dieses Angebot ist abgelaufen. Entfernen?", "Angebot abgelaufen",
							JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.YES_OPTION)
						deleteOffer = true;
					else
						deleteOffer = false;
				} else
				{
					deleteOffer = true;
					showedAction.accept();
				}
				setVisible(false);
			}
		});
		btReject.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				deleteOffer = true;
				showedAction.reject();
				setVisible(false);
			}
		});
		btWait.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				deleteOffer = false;
				setVisible(false);
			}
		});
		btDelete.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				deleteOffer = true;
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
	}

	/**
	 * @param offer
	 * @return true wenn gezeigte Angebot gelöscht werden soll
	 */
	public boolean showOffer(AbstractDiplomacyAction offer)
	{
		showedAction = offer;
		deleteOffer = false;
		labelCiv.setText(Main.instance().getMOUDB().getCivilizationDB().getCivName(CivilizationDB.createCivID(offer.getSource())));
		taComment.setText(offer.getComment());
		labelValidDate.setText(GUI.formatDate(offer.getValidBefor()));
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
		return deleteOffer;
	}
}
