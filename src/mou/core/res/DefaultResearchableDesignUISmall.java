/*
 * $Id: DefaultResearchableDesignUISmall.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mou.core.research.ResearchableDesign;

/**
 * @author pb
 */
public class DefaultResearchableDesignUISmall extends JPanel
{

	private JLabel labelIcon = new JLabel();
	private JLabel labelName = new JLabel();
	private JLabel labelInfo = new JLabel();
	// private JLabel labelStreng = new JLabel();
	private JPanel panelRoot = new JPanel();

	/**
	 * 
	 */
	public DefaultResearchableDesignUISmall()
	{
		super();
		initUI();
	}

	private void initUI()
	{
		setLayout(new BorderLayout());
		add(panelRoot, BorderLayout.WEST);
		panelRoot.setOpaque(false);
		panelRoot.setLayout(new BorderLayout());
		panelRoot.add(labelIcon, BorderLayout.WEST);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panelRoot.add(panel, BorderLayout.CENTER);
		panel.add(labelName, BorderLayout.NORTH);
		panel.add(labelInfo, BorderLayout.CENTER);
	}

	public void showResearchableDesign(ResearchableDesign des)
	{
		labelIcon.setIcon(des.getIcon());
		labelName.setText(" " + des.getName());
		labelInfo.setText(" " + des.getExtendenInfoForListCellRenderer());
		String tooltip = des.getResearchableResource().getHtmlFormattedInfo();
		setToolTipText(tooltip);
	}
}
