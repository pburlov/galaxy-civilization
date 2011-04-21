/*
 * $Id: ShipDesigner.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.shipdesignscreen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.core.research.ResearchableDesign;
import mou.core.ship.ShipClass;

/**
 * @author pbu
 */
public class ShipDesigner extends JPanel
{

	private JTextField tfName = new JTextField(30);
	private JList listBenoetigteMaterialien = new JList();
	private ShipClassInfoPanel shipInfoPanel = new ShipClassInfoPanel();
	private InstalledSystemsPanel installedSystems = new InstalledSystemsPanel();
	private JButton buttonSave = new JButton("Speichern");
	private JButton buttonClear = new JButton("Alle Schiffssysteme entfernen");
	private ShipClass showedShip;

	/**
	 * 
	 */
	public ShipDesigner()
	{
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("Neues Schiff"));
		// shipInfoPanel.setBorder(new EtchedBorder());
		tfName.setMaximumSize(tfName.getPreferredSize());
		// listBenoetigteMaterialien.setLayoutOrientation(JList.VERTICAL_WRAP);
		JPanel panelNord = new JPanel();
		panelNord.setLayout(new GridLayout(1, 2));
		add(panelNord, BorderLayout.NORTH);
		JPanel panel = new JPanel(new BorderLayout());
		panelNord.add(panel);
		Box boxX = Box.createHorizontalBox();
		panel.add(boxX, BorderLayout.NORTH);
		boxX.add(new JLabel("Name:"));
		boxX.add(tfName);
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new TitledBorder("Baumaterialien"));
		scroll.setMaximumSize(new Dimension(400, 1000));
		panel.add(scroll, BorderLayout.CENTER);
		scroll.setViewportView(listBenoetigteMaterialien);
		panelNord.add(shipInfoPanel);
		shipInfoPanel.setBorder(new TitledBorder("Schiffsdaten"));
		add(installedSystems, BorderLayout.CENTER);
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.add(buttonSave);
		panel.add(buttonClear);
		buttonSave.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				buttonSave_actionPerformed();
			}
		});
		buttonClear.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				showShipClass(new ShipClass());
			}
		});
		showedShip = new ShipClass();
		showShipClassIntern();
	}

	private void computeWerte()
	{
		showedShip.computeWerte();
		shipInfoPanel.showShipClass(showedShip);
		listBenoetigteMaterialien.setListData(new Vector(showedShip.getNeededRessources()));
		listBenoetigteMaterialien.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	}

	private void showShipClassIntern()
	{
		tfName.setText(showedShip.getName());
		installedSystems.editShip(showedShip, new ShipChangedListener()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.gui.shipdesignscreen.ShipChangedListener#shipsystemRemoved(mou.db.ShipSystemDesign)
			 */
			public void shipsystemRemoved(ResearchableDesign system)
			{
				computeWerte();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.gui.shipdesignscreen.ShipChangedListener#shipsystemChanged(mou.db.ShipSystemDesign)
			 */
			public void shipsystemChanged(ResearchableDesign system)
			{
				computeWerte();
			}
		});
		computeWerte();
	}

	/**
	 * Anzeigt ein Schiff
	 * 
	 * @param shipClass
	 */
	public void showShipClass(ShipClass shipClass)
	{
		showedShip = new ShipClass(shipClass);
		// tfName.setText(shipClass.getName());
		buttonSave.setEnabled(true);
		tfName.setEnabled(true);
		showShipClassIntern();
	}

	public void addSystem(ResearchableDesign system)
	{
		showedShip.addShipSystem(system);
		showShipClassIntern();
	}

	private void buttonSave_actionPerformed()
	{
		if(!checkInput()) return;
		showedShip.setName(tfName.getText());
		Main.instance().getMOUDB().getShipClassDB().addNewShipClass(new ShipClass(showedShip));
	}

	/**
	 * Methode prüft ob alle erforderliche Angaben gemacht wurden
	 * 
	 * @return
	 */
	private boolean checkInput()
	{
		if(showedShip == null) return false;
		if(tfName.getText().trim().length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Bitte Name der Schiffsklasse eingeben.", "Eingabe erforderlich", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		ShipClass.BuildAllowed ba = showedShip.canBuild();
		if(!ba.isAllowed())
		{
			JOptionPane.showMessageDialog(this, ba.getComment(), "Fehlerhafte Konstruktion", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
}
