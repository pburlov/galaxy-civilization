/*
 * $Id: DefaultResearchableScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import mou.core.ClockGenerator;
import mou.gui.GUI;

/**
 * Konfiguruerbare UI für Schiffsysteme. Abgeleitete Klassen müssen nur die benötigte JLabels mit
 * der Methode addLabel(..) addieren und sie mit entsprechenden Werten besetzen. Spinners für die
 * Entwicklungsstufe und Ausgangsleistung und die Liste mit den zum Bau benötigten Ressourcen werden
 * automatisch platziert.
 * 
 * @author pb
 */
public class DefaultResearchableScienceView extends JPanel
{

	static final protected String MASSE = "Masse:";
	static final protected String BAUKOSTEN = "Bauaufwand:";
	static final protected String UNTERHALTUNGSKOSTEN = "Unterhaltskosten p.a.:";
	static final protected String ENERGIEBALANCE = "Energiebalance:";
	static final protected String CREW = "Wartungspersonal:";
	// static final private NaturalRessourceDescriptionDB natuResDB =
	// Main.instance().getMOUDB().getNaturalRessourceDescriptionDB();
	private TitledBorder border = new TitledBorder("");
	private JList listNeededRes = new JList();
	private JPanel panelComponente = new JPanel();
	// private Box boxLabels = new Box(BoxLayout.Y_AXIS);//Beschriftungen für einzelne Felder
	private Box boxComponents = new Box(BoxLayout.Y_AXIS);// Wertfelder
	// private Box boxEinheiten = new Box(BoxLayout.Y_AXIS);//Meßeinheiten der Werten
	private JPanel panelNeededRes = new JPanel(new BorderLayout());
	private JLabel labelWorkCost = new JLabel();
	private JLabel labelEnergyInput = new JLabel();
	private JLabel labelMasse = new JLabel();
	private JLabel labelCrew = new JLabel();
	private JLabel labelInstandhaltung = new JLabel();
	// private JLabel labelEnergieNutzfaktor = new JLabel();
	// Key: Name; Value: Component;
	private Hashtable addedComponents = new Hashtable();
	private ResearchableResource researchableResource;

	public DefaultResearchableScienceView(ResearchableResource res)
	{
		researchableResource = res;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(border);
		super.add(panelComponente);
		panelComponente.setAlignmentY(0.0f);
		super.add(panelNeededRes);
		panelNeededRes.setAlignmentY(0.0f);
		super.add(Box.createVerticalGlue());
		panelNeededRes.setBorder(new TitledBorder("Benötigte Materialien"));
		listNeededRes.setOpaque(false);
		panelNeededRes.add(listNeededRes, BorderLayout.NORTH);
		panelComponente.setLayout(new BoxLayout(panelComponente, BoxLayout.X_AXIS));
		panelComponente.add(boxComponents);
		addComponent(MASSE, labelMasse, "t");
		addComponent(BAUKOSTEN, labelWorkCost, "Baupunkte");
		addComponent(UNTERHALTUNGSKOSTEN, labelInstandhaltung, "Credits");
		// addComponent(ENERGIEEFFITIENZ,labelEnergieNutzfaktor,"");
		addComponent(ENERGIEBALANCE, labelEnergyInput, "MW");
		addComponent(CREW, labelCrew, "Mann");
		ResearchableResource system = res;
		border.setTitle(system.getName());
		updateValues();
	}

	/**
	 * Methode wird aufgerufen wenn einer der beiden Spinner ihren Wert geändert haben Abgeleitete
	 * Klasse sollen diese Methode überschreiben um ihre Labels zu aktualisieren. ACHTUNG!: Beim
	 * Überschreiben super.updateValues() aufrufen!!!
	 */
	protected void updateValues()
	{
		if(researchableResource == null) return;
		labelWorkCost.setText(GUI.formatSmartDouble(researchableResource.computeBuildCost()));
		/*
		 * Supportkosten pro Jahr zeigen. Sonst sind die Werte zu klein.
		 */
		labelInstandhaltung.setText(GUI.formatSmartDouble(researchableResource.computeSupportCost() * ClockGenerator.SECONDARY_TO_PRIMARY_RATIO));
		labelMasse.setText(GUI.formatLong(researchableResource.computeMasse()));
		double crew = researchableResource.computeNeededCrew();
		String text = "";
		if(crew > 10)
			text = GUI.formatLong(crew);
		else
			text = GUI.formatSmartDouble(crew);
		labelCrew.setText(text);
		listNeededRes.setListData(researchableResource.getNeededRes().toArray());
		double energy = researchableResource.computeEnergyBalance();
		labelEnergyInput.setText((energy > 0 ? "+" : "") + GUI.formatSmartDouble(energy));
		// labelEnergieNutzfaktor.setText(energieFormat.format(shipSystem.getShipsystemAbstract().getEnergieNutzungsgrad()));
	}

	/**
	 * Methode überschrieben, damit bei der Größenänderungen an der zu zeigendem Researchable System
	 * die geänderte Eigenschafte gezeigt werden können.
	 */
	@Override
	public void repaint()
	{
		updateValues();
		super.repaint();
	}

	/**
	 * Hinzufügt ein Component zu den übrigen Componenten.
	 * 
	 * @param name
	 *            Unter diesem Namen kann man den hinzugefügten Component später wieder abrufen.
	 *            Name wird auch als Beschriftungstext für diesen Component verwendet
	 * @param comp
	 */
	protected void addComponent(String name, JComponent comp, String einheit)
	{
		Box panel = new Box(BoxLayout.X_AXIS);
		addedComponents.put(name, panel);
		panel.setMaximumSize(new Dimension(300, Short.MAX_VALUE));
		boxComponents.add(panel);
		JLabel label = new JLabel(name);
		label.setAlignmentX(0.0f);
		panel.add(label);
		panel.add(Box.createHorizontalStrut(4));
		panel.add(Box.createHorizontalGlue());
		comp.setAlignmentX(1.0f);
		panel.add(comp);
		label = new JLabel(einheit);
		label.setAlignmentX(1.0f);
		panel.add(Box.createHorizontalStrut(4));
		// panel.add(Box.createHorizontalGlue());
		panel.add(label);
		// boxLabels.add(new JLabel(name));
		// boxComponents.add(comp);
		// boxEinheiten.add(new JLabel(einheit));
	}

	/**
	 * Entfernt einen zuvor hinzugefügte Komponent
	 * 
	 * @param name
	 * @return
	 */
	protected void removeComponent(String name)
	{
		Component comp = (Component) addedComponents.remove(name);
		if(comp == null) return;
		boxComponents.remove(comp);
	}

	// /* (non-Javadoc)
	// * @see
	// mou.res.shipsystem.ShipSystemDesignUI#showShipSystem(mou.res.shipsystem.ShipsystemAbstract,
	// int, int)
	// */
	// private void showResearchableDesign(ResearchableDesign des)
	// {
	// researchableDesign = des;
	// ResearchableResource system = (ResearchableResource)des.getResearchableResource();
	// border.setTitle(system.getName());
	// updateValues();
	// }
	//
	// public ResearchableDesign getShowedShipSystem()
	// {
	// return researchableDesign;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
	 */
	public void add(Component comp, Object constraints)
	{
		throw new RuntimeException("Zugriff auf die Methode add(Component,Object) nicht erlaubt");
		// Überschreibt diese Methode und den abgeleiteten Klassen den Zugriff
		// zu verwehren
	}
}
