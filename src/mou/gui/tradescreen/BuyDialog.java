/*
 * $Id: BuyDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui.tradescreen;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mou.Main;
import mou.gui.GUI;

/**
 * @author pb
 */
public class BuyDialog extends JDialog
{

	protected JSlider slider = new JSlider(0, 0);
	private JLabel labelName = new JLabel();
	private JLabel labelPrice = new JLabel();
	private JLabel labelMenge = new JLabel();
	private JButton buttonBuy = new JButton("Kaufen");
	private JButton buttonCancel = new JButton("Abbrechen");
	private double price = 0;
	private boolean ok = false;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public BuyDialog() throws HeadlessException
	{
		super(Main.instance().getGUI().getMainFrame(), "Kaufen", true);
		Box boxY = Box.createVerticalBox();
		getContentPane().add(boxY);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		boxY.add(panel);
		panel.add(new JLabel("Kaufen: "));
		panel.add(labelName);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		boxY.add(panel);
		panel.add(new JLabel("Gesamtpreis: "));
		panel.add(labelPrice);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		boxY.add(panel);
		panel.add(new JLabel("Anzahl: "));
		panel.add(labelMenge);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		boxY.add(panel);
		panel.add(slider);
		panel = new JPanel();
		boxY.add(panel);
		panel.add(buttonBuy);
		buttonBuy.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				ok = true;
				setVisible(false);
			}
		});
		panel.add(buttonCancel);
		buttonCancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				slider.setValue(0);
				setVisible(false);
			}
		});
		slider.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				labelMenge.setText(GUI.formatLong(slider.getValue()));
				updateOverallPrice();
			}
		});
		slider.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		slider.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
	}

	protected void updateOverallPrice()
	{
		double summe = slider.getValue() * price;
		labelPrice.setText(GUI.formatLong((long) summe));
		if(Main.instance().getMOUDB().getCivilizationDB().getMoney() < summe)
		{
			buttonBuy.setEnabled(false);
		} else
			buttonBuy.setEnabled(true);
	}

	public int showDialog(int maxQuantity, double itemPrice, String itemName)
	{
		slider.setMaximum(maxQuantity);
		slider.setValue(0);
		labelName.setText(itemName);
		price = itemPrice;
		Hashtable<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(new Integer(0), new JLabel("0"));
		labels.put(new Integer(maxQuantity), new JLabel(GUI.formatLong(maxQuantity)));
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		updateOverallPrice();
		pack();
		GUI.centreWindow(Main.instance().getGUI().getMainFrame(), this);
		setVisible(true);
		if(ok)
		{
			ok = false;
			return slider.getValue();
		} else
			return 0;
	}
}
