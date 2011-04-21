/*
 * $Id: KolonieTabelle.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui.civilizationscreen.ColonyTable;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import mou.Main;
import mou.core.colony.Colony;
import mou.gui.MOUNumberTableCellRenderer;
import mou.gui.MainFrame;
import org.jdesktop.swing.JXTable;
import org.jdesktop.swing.table.DefaultTableColumnModelExt;
import org.jdesktop.swing.table.TableColumnExt;

public class KolonieTabelle extends JPanel
{

	private JScrollPane scroll = new JScrollPane();
	private JXTable tableKolonien;
	private KolonieTableModel tableModel = new KolonieTableModel();
	private PopupMenu popupMenu = new PopupMenu();

	public KolonieTabelle()
	{
		tableKolonien = new JXTable(tableModel);
		tableKolonien.setCursor(new Cursor(Cursor.HAND_CURSOR));
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		scroll.getViewport().add(tableKolonien);
		tableKolonien.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent ev)
			{
				if(ev.getButton() == MouseEvent.BUTTON1)
				{// Kolonie detailiert anzeigen
				// Main.instance().getGUI().getMainFrame().selectScreen(MainFrame.SCREEN_COLONY);
					int index = tableKolonien.rowAtPoint(ev.getPoint());
					if(index < 0) return;
					Colony kolonie = (Colony) tableKolonien.getModel().getValueAt(tableKolonien.convertRowIndexToModel(index), KolonieTableModel.COLONY_OBJ);
					Main.instance().getGUI().showColony(kolonie);
				} else if(ev.getButton() == MouseEvent.BUTTON3)
				{// PopupMenu anzeigen
					int row = tableKolonien.rowAtPoint(ev.getPoint());
					if(row < 0) return;
					Colony kolonie = (Colony) tableKolonien.getModel().getValueAt(tableKolonien.convertRowIndexToModel(row), KolonieTableModel.COLONY_OBJ);
					if(kolonie == null) return;
					tableKolonien.getSelectionModel().setSelectionInterval(row, row);
					popupMenu.showForColony(kolonie, ev.getPoint());
				}
			}
		});
		DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
		TableColumnExt column = new TableColumnExt(KolonieTableModel.NAME);
		column.setHeaderValue("Name");
		columnModel.addColumn(column);
		column.setCellRenderer(new NameCellRenderer());
		column = new TableColumnExt(KolonieTableModel.POPULATION);
		column.setHeaderValue("Bevölkerung");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.GROW);
		column.setHeaderValue("Bev.-Wachstum %");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.UNEMPLOYEMENT);
		column.setHeaderValue("Arbeitslos %");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.FARMING);
		column.setHeaderValue("Landwirtschaft");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.INCOME);
		column.setHeaderValue("Einkommen");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.PRODUCTION);
		column.setHeaderValue("Production");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.MINING);
		column.setHeaderValue("Bergbau");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.SCIENCE);
		column.setHeaderValue("Forschung");
		columnModel.addColumn(column);
		column.setCellRenderer(new MOUNumberTableCellRenderer());
		column = new TableColumnExt(KolonieTableModel.BUILD_JOB);
		column.setHeaderValue("Bauauftrag");
		columnModel.addColumn(column);
		tableKolonien.setColumnModel(columnModel);
	}

	private class PopupMenu extends JPopupMenu
	{

		private JMenuItem itemShowMap;
		private Colony colony;

		/**
		 * 
		 */
		public PopupMenu()
		{
			super();
			itemShowMap = new JMenuItem("Auf Sternenkarte zeigen");
			add(itemShowMap);
			itemShowMap.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					if(colony == null) return;
					Point pos = colony.getPosition();
					MainFrame mf = Main.instance().getGUI().getMainFrame();
					mf.getStarmapScreen().centerPosition(pos);
					mf.selectScreen(MainFrame.SCREEN_STARMAP);
				}
			});
		}

		public void showForColony(Colony col, Point pos)
		{
			colony = col;
			show(tableKolonien, pos.x, pos.y);
		}
	}
}