/*
 * $Id: StarmapViewer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.starmapscreen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import mou.LoggerOwner;
import mou.Main;
import mou.core.starmap.Navpoint;
import mou.gui.starmapscreen.starmappanel.StarmapPanel;

/**
 * GUI-Element der Sternenkarte mit Bedienelementen anzeigt
 */
public class StarmapViewer extends JPanel
		implements LoggerOwner
{

	private JPanel panelNavigation = new JPanel();
	private StarmapPanel panelMap;
	private BorderLayout borderLayout1 = new BorderLayout();
	// private JPanel panelAktuelleKoordinaten = new JPanel();
	private JPanel panelGoTo = new JPanel();
	private JPanel panelZoom = new JPanel();
	private JLabel labelX = new JLabel();
	private JLabel labelY = new JLabel();
	private JLabel labelKoordinaten = new JLabel();
	private JTextField tfXKoordinate = new JTextField();
	private JTextField tfYKoordinate = new JTextField();
	private JButton btGoTo = new JButton();
	private JButton btZoomPlus = new JButton();
	private JButton btZoomMinus = new JButton();
	private JComboBox comboBookmarks = new JComboBox();

	public StarmapViewer()
	{
		panelMap = new StarmapPanel(this);
		// panelMap.setCentralPoint(new Point(0,0));
		try
		{
			jbInit();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception
	{
		this.setLayout(borderLayout1);
		this.setSize(new Dimension(520, 392));
		panelMap.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		labelX.setText("0000");
		labelX.setToolTipText("X-Koordinate");
		labelY.setText("0000");
		labelY.setToolTipText("Y-Koordinate");
		labelKoordinaten.setText("Koordinaten:");
		// tfXKoordinate.setText("0000");
		tfXKoordinate.setColumns(5);
		// tfYKoordinate.setText("0000");
		tfYKoordinate.setColumns(5);
		tfXKoordinate.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btGoTo_actionPerformed(e);
			}
		});
		tfXKoordinate.addFocusListener(new FocusListener()
		{

			public void focusLost(FocusEvent e)
			{
			}

			public void focusGained(FocusEvent e)
			{
				tfXKoordinate.selectAll();
			}
		});
		tfYKoordinate.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btGoTo_actionPerformed(e);
			}
		});
		tfYKoordinate.addFocusListener(new FocusListener()
		{

			public void focusLost(FocusEvent e)
			{
			}

			public void focusGained(FocusEvent e)
			{
				tfYKoordinate.selectAll();
			}
		});
		btGoTo.setText("Zeige");
		btGoTo.setToolTipText("Zeigt die Karte auf den gewuenschten Koordinaten");
		btGoTo.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btGoTo_actionPerformed(e);
			}
		});
		btZoomPlus.setText("+");
		btZoomPlus.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btZoomPlus_actionPerformed(e);
			}
		});
		btZoomMinus.setText("-");
		btZoomMinus.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				btZoomMinus_actionPerformed(e);
			}
		});
		panelGoTo.setBorder(new TitledBorder("Navigation"));
		panelGoTo.add(labelKoordinaten, null);
		panelGoTo.add(labelX, null);
		panelGoTo.add(new JLabel(":"));
		panelGoTo.add(labelY, null);
		panelGoTo.add(tfXKoordinate, null);
		panelGoTo.add(new JLabel(":"));
		panelGoTo.add(tfYKoordinate, null);
		panelGoTo.add(btGoTo, null);
		panelNavigation.add(panelGoTo, null);
		panelZoom.setBorder(new TitledBorder("Zoom"));
		panelZoom.add(btZoomPlus, null);
		panelZoom.add(btZoomMinus, null);
		panelNavigation.add(panelZoom, null);
		JPanel panelBookmark = new JPanel();
		panelBookmark.setBorder(new TitledBorder("Navpoints"));
		JButton btNewBookmark = new JButton("Setzen");
		JButton btDeleteBookmark = new JButton("Löschen");
		panelBookmark.add(btNewBookmark);
		panelBookmark.add(btDeleteBookmark);
		panelBookmark.add(comboBookmarks);
		comboBookmarks.setMaximumSize(new Dimension(btNewBookmark.getWidth() * 2, 100));
		comboBookmarks.setMinimumSize(new Dimension(btNewBookmark.getWidth() * 2, 10));
		panelNavigation.add(panelBookmark);
		this.add(panelNavigation, BorderLayout.SOUTH);
		this.add(panelMap, BorderLayout.CENTER);
		btNewBookmark.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				String val = JOptionPane.showInputDialog("Anmerkung", "Navpoint");
				if(val == null) return;
				Main.instance().getMOUDB().getStarmapDB().addNavpoint(getCenteredPosition(), val);
				initNavpointCombobox();
			}
		});
		btDeleteBookmark.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Navpoint nav = (Navpoint) comboBookmarks.getSelectedItem();
				if(nav == null) return;
				Main.instance().getMOUDB().getStarmapDB().deleteNavpoint(nav);
				initNavpointCombobox();
			}
		});
		comboBookmarks.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				Navpoint nav = (Navpoint) comboBookmarks.getSelectedItem();
				if(nav == null) return;
				goTo(nav);
			}
		});
		initNavpointCombobox();
	}

	private void initNavpointCombobox()
	{
		comboBookmarks.setModel(new DefaultComboBoxModel(Main.instance().getMOUDB().getStarmapDB().getNavpoints().toArray()));
	}

	/**
	 * Wird von StarmapPanel aufgerufen, um die aktuellen Starmap-Koordinaten anzeigen, über die
	 * Maus gerade schwebt
	 */
	public void showMouseLocation(Point location)
	{
		labelX.setText(Integer.toString(location.x));
		labelY.setText(Integer.toString(location.y));
	}

	private void btGoTo_actionPerformed(ActionEvent e)
	{
		try
		{
			int x = Integer.parseInt(tfXKoordinate.getText());
			int y = Integer.parseInt(tfYKoordinate.getText());
			panelMap.setCentralPoint(new Point(x, y));
		} catch(Throwable t)
		{
		}
	}

	public Point getCenteredPosition()
	{
		return panelMap.getCentralPoint();
	}

	/**
	 * Zentriert Karte auf dem angegeben Punkt in Sternenkoordinaten
	 */
	public void goTo(Point point)
	{
		panelMap.setCentralPoint(point);
	}

	private void btZoomPlus_actionPerformed(ActionEvent e)
	{
		this.panelMap.increaseZoom();
	}

	private void btZoomMinus_actionPerformed(ActionEvent e)
	{
		this.panelMap.decreaseZoom();
	}

	public void locationChanged(Point location)
	{
		tfXKoordinate.setText(Integer.toString(location.x));
		tfYKoordinate.setText(Integer.toString(location.y));
	}

	public Logger getLogger()
	{
		return Main.instance().getGUI().getLogger();
	}

	synchronized public void switchToTargetSelectMode(List ships)
	{
		panelMap.switchToTargetSelectMode(ships);
	}
	// /**
	// *
	// */
	// public void switchFromTargetSelectMode()
	// {
	// panelMap.switchFromTargetSelectMode();
	// }
}