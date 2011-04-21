/*
 * $Id: DefaultBuildingUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.res.colony;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mou.gui.GUI;
import mou.gui.SliderGroupPanel;
import mou.gui.layout.TableLayout;

/**
 * Erweiterbare UI-KLasse zum Anzeigen und Steuern der Koloniegebäude
 * 
 * @author pb
 */
abstract public class DefaultBuildingUI<O extends BuildingAbstract> extends BuildingUiAbstract
{

	private JPanel panelInfo = new JPanel(new TableLayout(2, "NW"));
	private JLabel labelAuslastung = new JLabel();
	private JLabel labelNeededCrew = new JLabel();
	private JLabel labelAssignedCrew = new JLabel();
	private JLabel labelSupportcost = new JLabel();
	protected SliderGroupPanel slider;
	private O building;
	private Timer refreshTimer;

	public DefaultBuildingUI(O b)
	{
		this.building = b;
		// setLayout(new TableLayout(1, "NW"));
		setLayout(new BorderLayout());
		add(panelInfo, BorderLayout.CENTER);
		panelInfo.add("CS=2,NW", new JLabel(building.getName()));
		addField("Tatsächliche Auslastung: ", labelAuslastung);
		addField("Benötigtes Personal:", labelNeededCrew);
		addField("Beschäftigtes Personal:", labelAssignedCrew);
		addField("Unterhaltskosten:", labelSupportcost);
		/*
		 * refreshValues() nicht im Konstruktor aufrufen!! Sonst wird in refreshValuesIntern
		 * NullPointer Exception ausgelöst.
		 */
		// refreshValues();

		/* Geänderte Gebäudeeigenschaften regelmäßig anzeigen */
		refreshTimer = new Timer(1000, new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						refreshValues();
					}
				});
	}

	protected void addField(String label, JComponent comp)
	{
		panelInfo.add(new JLabel(label));
		panelInfo.add(comp);
	}
	
	protected void addSliderPanel(int sliderCount)
	{
		addSliderPanel(sliderCount, true, false);
	}
	
	protected void addSliderPanel(int sliderCount, boolean showSlider, boolean showProgress)
	{
		slider = new SliderGroupPanel(sliderCount, showSlider, showProgress);
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Auslastung"));
		add(panel, BorderLayout.SOUTH);
		panel.add(slider);

		slider.setSliderFaktors(building.getUtilizationFactors());
		slider.setProgressFaktors(building.getUtilizationFactors());
		slider.addChangeListener(new ChangeListener()
	    {
			public void stateChanged(ChangeEvent e)
			{
				building.setActivFactor(slider.getTotalFaktor());
				building.setUtilizationFactors(slider.getFaktors());
				refreshValues();
			}
	    });
	}

	//	
	// /**
	// * Methode ist überschrieben, damit bei gänderten Gebäudeeinstellungen
	// * neue Werte auch angezeigt werden
	// */
	// @Override
	// public void repaint()
	// {
	// updateValues(building);
	// super.repaint();
	// }
	//
	// protected void updateValues(BuildingAbstract building)
	// {
	// // listMaterials.setListData(new Vector<ResourceMenge>(building.getNeededRes()));
	// }
	//
	
	@Override
	public void startRefreshTimer()
	{
		refreshTimer.start();
	}
	
	@Override
	public void stopRefreshTimer()
	{
		refreshTimer.stop();
	}
	
	@Override
	public void refreshValues()
	{
		if(slider!=null)
			slider.setProgressFaktors(building.getUtilizationValues());
		
		labelAuslastung.setText(GUI.formatProzent(building.getKPD() * 100));
		labelNeededCrew.setText(GUI.formatLong(building.computeNeededCrew()));
		labelAssignedCrew.setText(GUI.formatLong(building.computeAssignedCrew()));
		labelSupportcost.setText(GUI.formatSmartDouble(building.computeSupportCost()));
		refreshValuesIntern(building);
	}

	abstract protected void refreshValuesIntern(O b);
}
