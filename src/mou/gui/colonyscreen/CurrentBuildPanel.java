/*
 * $Id: CurrentBuildPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.colonyscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import mou.Main;
import mou.core.colony.BuildJobAbstract;
import mou.core.colony.Colony;
import mou.gui.GUI;
import mou.gui.layout.TableLayout;

/**
 * Zeigt was eine Kolonie zur Zeit Baut
 * 
 * @author pbu
 */
public class CurrentBuildPanel extends JPanel
{

	// private JLabel mBautyp = new JLabel("");
	private JLabel labelName = new JLabel("");
	// private JLabel mGesBauzeit = new JLabel("--");
	private JLabel labelProgress = new JLabel("--");
	private JLabel labelKaufpreis = new JLabel();
	private JButton buttonAbbrechen = new JButton("Bau stoppen");
	private JButton buttonKaufen = new JButton("Sofort kaufen");
	private Colony mKolonie;

	/**
	 * 
	 */
	public CurrentBuildPanel()
	{
		super();
		setBorder(new TitledBorder("Aktueller Bauauftrag"));
		setLayout(new TableLayout(2, "NW"));
		add(new JLabel("Bauobjekt:"));
		add(labelName);
		add(new JLabel("Baufortschritt: "));
		add(labelProgress);
		add(new JLabel("Kaufpreis:"));
		add(labelKaufpreis);
		add(buttonKaufen);
		add(buttonAbbrechen);
		buttonKaufen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				mKolonie.getBuildQueue().buyCurrentJob();
				showKolonie(mKolonie);
			}
		});
		buttonAbbrechen.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				if(mKolonie == null) return;
				// Hier wird automatisch ein DBEvent ausgelöst, und der dann in
				// this.processMOUEvent(..) abgearbeitet
				mKolonie.getBuildQueue().cancelCurrentBuildJob();
				showKolonie(mKolonie);
			}
		});
	}

	private void reset()
	{
		labelName.setText("");
		// mGesBauzeit.setText("");
		labelProgress.setText("--");
	}

	public void showKolonie(Colony kolonie)
	{
		mKolonie = kolonie;
		// ### Ein Paar Sicherheitsabfragen ###
		if(kolonie == null) return;
		BuildJobAbstract job = kolonie.getBuildQueue().getCurrentBuildJob();
		if(job == null)
		{// Wenn Auftrag gelöscht wurde
			reset();
			buttonAbbrechen.setEnabled(false);
			buttonKaufen.setEnabled(false);
			return;
		} else
		{
			buttonAbbrechen.setEnabled(true);
			double money = Main.instance().getMOUDB().getCivilizationDB().getMoney();
			double price = job.getBuyPrice();
			if(money < price)
				buttonKaufen.setEnabled(false);
			else
				buttonKaufen.setEnabled(true);
		}
		labelName.setText(job.getName());
		labelProgress.setText(GUI.formatProzent(job.getProgress() * 100));
		labelKaufpreis.setText(GUI.formatLong(job.getBuyPrice()));
		if(kolonie.isRebelled())
		{
			/*
			 * Alle Schaltflächen deaktivieren
			 */
			buttonAbbrechen.setEnabled(false);
			buttonKaufen.setEnabled(false);
		}
	}
}