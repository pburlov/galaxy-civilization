/*
 * $Id: InstalledSystemList.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui.fleetscreen;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import mou.core.research.ResearchableDesign;
import mou.core.ship.ShipClass;

/**
 * @author pbu
 */
public class InstalledSystemList extends JList
{

	private List originList = new ArrayList();
	private Popup popupSystemInfo;
	private int showedPopupForIndex = -1;

	// private InstalledSystemList self;
	public void showSystems(ShipClass ship)
	{
		// self = this;
		originList = ship.getSystems();
		Collections.sort(originList);
		setListData(originList.toArray());
		setCellRenderer(new SystemCellRenderer());
		setOpaque(false);
		addMouseListener(new MouseAdapter()
		{

			public void mouseExited(MouseEvent e)
			{
				if(popupSystemInfo != null) popupSystemInfo.hide();
				getSelectionModel().clearSelection();
				showedPopupForIndex = -1;
			}
		});
		addMouseMotionListener(new MouseMotionListener()
		{

			public void mouseDragged(MouseEvent e)
			{
			}

			public void mouseMoved(MouseEvent e)
			{
				int index = locationToIndex(e.getPoint());
				if(index < 0) return;
				setSelectedIndex(index);
				showInfoPopup(index);
			}
		});
	}

	private void showInfoPopup(int index)
	{
		if(showedPopupForIndex == index) return;
		if(popupSystemInfo != null) popupSystemInfo.hide();
		showedPopupForIndex = index;
		ResearchableDesign design = (ResearchableDesign) getModel().getElementAt(index);
		JComponent resUi = design.getResearchableResource().getScienceViewComponent();
		// resUi.showResearchableDesign(design);
		JPanel panel = new JPanel();
		panel.setDoubleBuffered(true);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));
		panel.add(resUi);
		Dimension size = panel.getPreferredSize();
		Rectangle cellRect = getCellBounds(index, index);
		Point pos = cellRect.getLocation();
		SwingUtilities.convertPointToScreen(pos, this);
		pos.move(pos.x + cellRect.width / 2 - size.width / 2, pos.y - size.height - 10);
		popupSystemInfo = PopupFactory.getSharedInstance().getPopup(null, panel, pos.x, pos.y);
		popupSystemInfo.show();
	}

	public List getSystems()
	{
		return originList;
	}

	private class SystemCellRenderer extends JPanel
			implements ListCellRenderer
	{

		private JLabel labelName = new JLabel();

		public SystemCellRenderer()
		{
			labelName.setAlignmentX(0.0f);
			add(labelName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			ResearchableDesign res = (ResearchableDesign) value;
			// Integer count = ((SystemsListModel)list.getModel()).getCount(res);
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
			setOpaque(false);
			labelName.setText(res.toString());
			return this;
		}
	}
}
