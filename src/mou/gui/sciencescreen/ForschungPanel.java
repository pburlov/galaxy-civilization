/*
 * $Id: ForschungPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.sciencescreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.core.civilization.NaturalRessourceDescriptionDB;
import mou.core.res.ResearchableResource;
import mou.core.res.ResourceAbstract;
import mou.core.res.natural.DefaultNaturalRessourceListCellRenderer;
import mou.core.res.natural.NaturalResource;
import mou.core.research.ResearchDB;
import mou.core.research.ResearchableDesign;
import mou.event.RowClickedEvent;
import mou.event.RowClickedEventListener;
import mou.gui.GUI;
import mou.gui.civilizationscreen.NaturalResourceTableDialog;
import mou.storage.ser.ID;

/**
 * Klasse zum Azeigen, Starten und Beenden eines Forschungsauftrages
 * 
 * @author pb
 */
public class ForschungPanel extends JPanel
{

	// private JLabel labelForschungspunkteFerfuegbar = new JLabel("----");
	private JLabel labelBenoetigteForschungspunkte = new JLabel("----");
	private JLabel labelFortschritt = new JLabel("---%");
	private JLabel labelBerechnetePunkte = new JLabel("----");
	private JComboBox comboForschungsobjekt = new JComboBox(new Vector(Main.instance().getMOUDB().getResearchDB().getResearchTargets()));
	protected JList listModifikatoren = new JList();
	protected JList listVorlaufigeErgebnisse = new JList();
	private JButton buttonAddModifikator = new JButton("Material hinzufügen");
	private JButton buttonRemoveModifikator = new JButton("Material entfernen");
	private JButton buttonErgebnissUebernehmen = new JButton("Selektiertes Design übernehmen");
	// private JButton buttonAlleUebernehmen = new JButton("Alles übernehmen");
	private JButton buttonStartForschung = new JButton("Forschung starten");
	private JButton buttonStopForschung = new JButton("Forschung stoppen");
	// private JButton buttonNeueForschung = new JButton("Neues Projekt");
	protected GUI gui = Main.instance().getGUI();
	private NaturalRessourceDescriptionDB naturalResourceDB = Main.instance().getMOUDB().getNaturalRessourceDescriptionDB();
	protected NaturalResourceTableDialog naturalResDialog = new NaturalResourceTableDialog(Main.instance().getGUI().getMainFrame());
	private ResearchDB researchDB = Main.instance().getMOUDB().getResearchDB();
	protected Component self;

	/**
	 * 
	 */
	public ForschungPanel()
	{
		super();
		self = this;
		setBorder(new TitledBorder("Aktuelles Projekt"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// Container panel = new Box(BoxLayout.X_AXIS);
		// panel.add(new JLabel("Verfügbare Forschungspunkte: "));
		// panel.add(Box.createHorizontalGlue());
		// panel.add(labelForschungspunkteFerfuegbar);
		// add(panel);
		Container panel = new Box(BoxLayout.X_AXIS);
		panel.add(new JLabel("Forschungsaufwand: "));
		panel.add(Box.createHorizontalGlue());
		panel.add(labelBenoetigteForschungspunkte);
		add(panel);
		panel = new Box(BoxLayout.X_AXIS);
		panel.add(new JLabel("Erforscht: "));
		panel.add(Box.createHorizontalGlue());
		panel.add(labelBerechnetePunkte);
		add(panel);
		panel = new Box(BoxLayout.X_AXIS);
		panel.add(new JLabel("Fortschritt: "));
		panel.add(Box.createHorizontalGlue());
		panel.add(labelFortschritt);
		add(panel);
		panel = new Box(BoxLayout.X_AXIS);
		panel.add(new JLabel("Forschungsobjekt: "));
		panel.add(Box.createHorizontalGlue());
		panel.add(comboForschungsobjekt);
		add(panel);
		add(Box.createVerticalStrut(10));
		panel = new Box(BoxLayout.X_AXIS);
		add(panel);
		panel.add(buttonStartForschung);
		panel.add(Box.createHorizontalStrut(5));
		// panel.add(Box.createHorizontalGlue());
		panel.add(buttonStopForschung);
		JPanel panel1 = new JPanel();
		add(panel1);
		panel1.setLayout(new BorderLayout());
		panel1.setBorder(new TitledBorder("Materialen"));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(listModifikatoren);
		// Dimension size = new Dimension(400,200);
		// panel1.setPreferredSize(size);
		// panel1.setMinimumSize(size);
		// panel1.setMaximumSize(size);
		panel1.add(scroll, BorderLayout.CENTER);
		panel = new JPanel();
		panel.add(buttonAddModifikator);
		// panel.add(Box.createHorizontalStrut(5));
		// panel.add(Box.createHorizontalGlue());
		panel.add(buttonRemoveModifikator);
		panel1.add(panel, BorderLayout.SOUTH);
		panel1 = new JPanel();
		add(panel1);
		panel1.setLayout(new BorderLayout());
		panel1.setBorder(new TitledBorder("Vorläufige Forschungsergebnisse"));
		scroll = new JScrollPane();
		scroll.setViewportView(listVorlaufigeErgebnisse);
		panel1.add(scroll, BorderLayout.CENTER);
		// size = new Dimension(400,200);
		// panel1.setPreferredSize(size);
		// panel1.setMinimumSize(size);
		// panel1.setMaximumSize(size);
		panel = new JPanel();
		panel.add(buttonErgebnissUebernehmen);
		// panel.add(Box.createHorizontalStrut(5));
		// panel.add(Box.createHorizontalGlue());
		// panel.add(buttonAlleUebernehmen);
		panel1.add(panel, BorderLayout.SOUTH);
		// ComponentResizerGroup group = new ComponentResizerGroup();
		// group.add(buttonAddModifikator);
		// group.add(buttonRemoveModifikator);
		// // group.add(buttonAlleUebernehmen);
		// group.add(buttonErgebnissUebernehmen);
		// group.add(buttonStartForschung);
		// group.add(buttonStopForschung);
		listModifikatoren.setCellRenderer(new DefaultNaturalRessourceListCellRenderer());
		listVorlaufigeErgebnisse.setCellRenderer(new ErgebnisseListCellRenderer());
		listModifikatoren.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listVorlaufigeErgebnisse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		comboForschungsobjekt.setEditable(false);
		comboForschungsobjekt.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				ResearchableResource system =
					(ResearchableResource)comboForschungsobjekt.getSelectedItem();
				// getForschungAuftrag().setForschungsobjektID(system.getID());
				comboForschungsobjekt.setToolTipText(system.getShortDescription());
				researchDB.setCurrentResearchableID(((ResearchableResource) comboForschungsobjekt.getSelectedItem()).getID());
			}
		});
		buttonAddModifikator.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				GUI.centreWindow(gui.getMainFrame(), naturalResDialog);
				naturalResDialog.setVisible(true);
				// ID id = addModifikatorDialog.show(true);
				// if(id == null)return;
				// getForschungAuftrag().addModifikator(id);
				// showModifikatoren();
				// refreshPanel();
			}
		});
		buttonRemoveModifikator.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				NaturalResource res = (NaturalResource) listModifikatoren.getSelectedValue();
				if(res == null) return;
				researchDB.getResearchMaterials().remove(res.getID());
				showModifikatoren();
				refreshPanel();
			}
		});
		buttonStartForschung.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				startNeuesProjekt();
				refreshPanel();
			}
		});
		buttonStopForschung.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				researchDB.setResearchRunning(false);
				refreshPanel();
			}
		});
		buttonErgebnissUebernehmen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				ResearchableDesign des = (ResearchableDesign) listVorlaufigeErgebnisse.getSelectedValue();
				if(des == null) return;
				String name = JOptionPane.showInputDialog(self, "Bitte Name des neuen Design eingeben.", des.getName());
				if(name != null && name.trim().length() > 0) des.setName(name);
				if(name != null) Main.instance().getMOUDB().getResearchDB().saveResearchableDesign(des);
			}
		});
		naturalResDialog.addRowClickedEventListener(new RowClickedEventListener()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.event.RowClickedEventListener#rowClicked(mou.event.RowClickedEvent)
			 */
			public void rowClicked(RowClickedEvent event)
			{
				researchDB.getResearchMaterials().add(((NaturalResource) event.getValue()).getID());
				showModifikatoren();
				refreshPanel();
			}
		});
		initPanel();
		// Timer starten, damit in regelmäsigen Zeitintervallen die Anzeige aktualisiert wird
		new Timer(1000, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				refreshPanel();
			}
		}).start();
	}

	protected void startNeuesProjekt()
	{
		if(researchDB.isResearchRunning())
		{
			int option = JOptionPane.showConfirmDialog(this, "Es wird gerade an einem Projekt geforscht!\nDen laufenden Projekt abbrechen?", "Warnung",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if(option != JOptionPane.OK_OPTION) return;
		}
		if(researchDB.getResearchResults().size() > 0)
		{
			int option = JOptionPane.showConfirmDialog(this, "Die nicht übernommene Forschungsergebnisse werden verworfen!\n Sind Sie sicher?", "Warnung",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if(option != JOptionPane.OK_OPTION) return;
		}
		if(researchDB.getResearchMaterials().size() < 1)
		{
			JOptionPane.showMessageDialog(this, "Keine Forschungsmaterialen ausgewählt!");
			return;
		}
		researchDB.startNewResearch((ResearchableResource) comboForschungsobjekt.getSelectedItem());
	}

	private void initPanel()
	{
		ResearchableResource system = researchDB.getCurrentResearchObject();
		if(system != null && system instanceof ResearchableResource)
		{
			comboForschungsobjekt.setSelectedItem(system);
			comboForschungsobjekt.setToolTipText(system.getShortDescription());
		}
		showModifikatoren();
	}

	protected void showModifikatoren()
	{
		// ####################################################
		// Modifikatoren anzeigen
		Iterator iter = researchDB.getResearchMaterials().iterator();
		Vector<ResourceAbstract> listData = new Vector<ResourceAbstract>();
		while(iter.hasNext())
		{
			ID id = (ID) iter.next();
			ResourceAbstract res = naturalResourceDB.getNaturalResource(id);
			listData.add(res);
		}
		Collections.sort(listData);
		listModifikatoren.setListData(listData);
	}

	protected void refreshPanel()
	{
		labelBenoetigteForschungspunkte.setText(GUI.formatLong(researchDB.getNeededResearchPoints()));
		labelFortschritt.setText(GUI.formatDouble(researchDB.getFortschritt() * 100) + "%");
		labelBerechnetePunkte.setText(GUI.formatLong(researchDB.getInvestedResearchPoints()));
		if(researchDB.isResearchRunning())
		{// Projekt läuft, alle unnötige Schaltflächen deaktivieren
			buttonAddModifikator.setEnabled(false);
			buttonRemoveModifikator.setEnabled(false);
			// buttonAlleUebernehmen.setEnabled(false);
			// buttonErgebnissUebernehmen.setEnabled(false);
			buttonStartForschung.setEnabled(false);
			buttonStopForschung.setEnabled(true);
			comboForschungsobjekt.setEnabled(false);
		} else
		{// Projekt gestoppt, entsprechend Schlatflächen einschalten
			buttonAddModifikator.setEnabled(true);
			buttonRemoveModifikator.setEnabled(true);
			// buttonAlleUebernehmen.setEnabled(true);
			buttonErgebnissUebernehmen.setEnabled(true);
			buttonStartForschung.setEnabled(true);
			buttonStopForschung.setEnabled(false);
			comboForschungsobjekt.setEnabled(true);
		}
		// ###################################################
		// Vorläufige Ergebnisse anzeigen
		int selectedIndex = listVorlaufigeErgebnisse.getSelectedIndex();
		Vector listData = new Vector(researchDB.getResearchResults());
		listVorlaufigeErgebnisse.setListData(listData);
		if(selectedIndex >= listData.size()) selectedIndex = listData.size() - 1;
		if(selectedIndex >= 0) listVorlaufigeErgebnisse.setSelectedIndex(selectedIndex);
	}

	/**
	 * JList enthält Einträge der Klasse ShipSystemDesign
	 * 
	 * @return
	 */
	public JList getJListForschungsergebnisse()
	{
		return listVorlaufigeErgebnisse;
	}

	private class ErgebnisseListCellRenderer extends JLabel
			implements ListCellRenderer
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			ResearchableDesign des = (ResearchableDesign) value;
			setText(des.getName());
			if(isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setEnabled(list.isEnabled());
			setOpaque(true);
			return this;
		}
	}
}
