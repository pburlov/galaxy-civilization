/*
 * $Id: CivManagerPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mou.Main;
import mou.core.civilization.CivilizationDB;
import mou.gui.layout.TableLayout;

/**
 * @author pb
 */
public class CivManagerPanel extends JPanel
{

	private CivDayInfoPanel civDayInfoPanel = new CivDayInfoPanel();
	private JSpinner spinnerTaxRate = new JSpinner();
	private JSpinner spinnerWorkHours = new JSpinner();
	private CivilizationDB civDB = Main.instance().getMOUDB().getCivilizationDB();

	/**
	 * 
	 */
	public CivManagerPanel()
	{
		super();
		spinnerTaxRate.setModel(new SpinnerNumberModel(civDB.getTaxRate() * 100, 0, CivilizationDB.MAX_TAX_RATE * 100, 1.0));
		spinnerWorkHours.setModel(new SpinnerNumberModel(civDB.getWorkHours(), 0, CivilizationDB.MAX_WORK_HOURS, 1));
		setLayout(new TableLayout(3, "NW"));
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// setLayout(new FlowLayout(FlowLayout.LEFT));
		add(civDayInfoPanel);
		// civDayInfoPanel.setBorder(new TitledBorder("Tagesdaten der Civilization"));
		// JPanel panel2 = new JPanel(new BorderLayout());
		// add(panel2);
		// panel2.setBorder(LineBorder.createGrayLineBorder());
		JPanel panel = new JPanel(new TableLayout(2, "NW"));
		add(panel);
		// panel.setBorder(new TitledBorder("Zivilisationssteuerung"));
		panel.add(new JLabel("Steuersatz (%): "));
		panel.add(spinnerTaxRate);
		panel.add(new JLabel("Arbeitstag (Stunden): "));
		panel.add(spinnerWorkHours);
		panel = new NaturalResTable();
		panel.setBorder(new TitledBorder("Vorhandene Resourcen"));
		Dimension size = civDayInfoPanel.getPreferredSize();
		// panel.setMaximumSize(size);
		panel.setPreferredSize(new Dimension(400, size.height));
		add(panel);
		spinnerTaxRate.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				civDB.setTaxRate(((Number) spinnerTaxRate.getValue()).doubleValue() / 100);
			}
		});
		spinnerWorkHours.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				civDB.setWorkHours(((Number) spinnerWorkHours.getValue()).doubleValue());
			}
		});
	}
}
