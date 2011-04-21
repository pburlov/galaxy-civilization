/*
 * $Id: GiftOfferDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.diplomacy;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mou.Main;
import mou.core.colony.Colony;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;

/**
 * @author pb
 */
public class GiftOfferDialog extends JDialog
{

	private JRadioButton ckMoney = new JRadioButton("Geld ", false);
	private JRadioButton ckColony = new JRadioButton("Kolonie ", false);
	private JSpinner spinnerMoney = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1000000));
	private JComboBox cbColonies = new JComboBox();
	private boolean send = false;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public GiftOfferDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), "Ein Geschenk schicken", true);
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new TableLayout(2, "W"));
		add(panel, BorderLayout.CENTER);
		panel.add(ckMoney);
		panel.add(spinnerMoney);
		panel.add(ckColony);
		panel.add(cbColonies);
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		JButton btSend = new JButton("Abschicken");
		JButton btCancel = new JButton("Abbrechen");
		panel.add(btSend);
		panel.add(btCancel);
		ButtonGroup bg = new ButtonGroup();
		bg.add(ckMoney);
		bg.add(ckColony);
		ckMoney.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		ckMoney.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		ckMoney.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				if(ckMoney.isSelected())
					spinnerMoney.setEnabled(true);
				else
					spinnerMoney.setEnabled(false);
			}
		});
		ckColony.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				if(ckColony.isSelected())
					cbColonies.setEnabled(true);
				else
					cbColonies.setEnabled(false);
			}
		});
		btCancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		btSend.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				send = true;
				setVisible(false);
			}
		});
		cbColonies.setEnabled(false);
		spinnerMoney.setEnabled(false);
		setResizable(false);
		pack();
	}

	/**
	 * @param parent
	 *            dient nur zum zentrieren des Fensterns
	 * @return true wenn die angegebene Ressourcen geschickt werden sollen. false wenn abgebrochen
	 */
	public boolean showDialog(JDialog parent)
	{
		send = false;
		Collection<Colony> cols = Main.instance().getMOUDB().getKolonieDB().getAlleKolonien().values();
		cbColonies.setModel(new DefaultComboBoxModel(cols.toArray()));
		int maxVal = (int) Main.instance().getMOUDB().getCivilizationDB().getMoney();
		spinnerMoney.setModel(new SpinnerNumberModel(0, 0, maxVal, maxVal / 20));
		pack();
		GUI.centreWindow(parent, this);
		setVisible(true);
		return send;
	}

	public int getMoney()
	{
		return ((Number) spinnerMoney.getValue()).intValue();
	}

	public Colony getColony()
	{
		return (Colony) cbColonies.getSelectedItem();
	}

	public boolean isMoneySelected()
	{
		return ckMoney.isSelected();
	}

	public boolean isColonySelected()
	{
		return ckColony.isSelected();
	}
}
