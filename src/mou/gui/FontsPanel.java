/*
 * $Id: FontsPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mou.Main;

/**
 * @author pb
 */
public class FontsPanel extends JPanel
{

	static final private File FONT_DIR = new File("res/fonts");
	private JList listFonts = new JList();
	private JSpinner spinnerSize = new JSpinner(new SpinnerNumberModel(GUIConstants.FONT_SIZE, 8, 36, 1));

	/**
	 * 
	 */
	public FontsPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane();
		add(scroll);
		scroll.setViewportView(listFonts);
		add(spinnerSize);
		spinnerSize.setMaximumSize(new Dimension(Short.MAX_VALUE, spinnerSize.getPreferredSize().height));
		JButton button = new JButton("Ok");
		button.setAlignmentX(0.5f);
		add(button);
		// add(Box.createVerticalGlue());
		button.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				File file = (File) listFonts.getSelectedValue();
				try
				{
					FileInputStream in = new FileInputStream(file);
					// Main.instance().getGUI().setCurrentFont(Font.createFont(Font.TRUETYPE_FONT,in));
					in.close();
					Main.instance().getGUI().getPreferences().setProperty("Font", file.getAbsolutePath());
					Main.instance().getGUI().updateGUI();
				} catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		spinnerSize.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				GUI gui = Main.instance().getGUI();
				// gui.setCurrentFontSize(((Number)spinnerSize.getValue()).intValue());
				gui.getPreferences().setProperty("FontSize", spinnerSize.getValue().toString());
				gui.updateGUI();
			}
		});
		Dimension size = new Dimension(300, 400);
		setMinimumSize(size);
		setPreferredSize(size);
		if(!FONT_DIR.exists() || FONT_DIR.isFile()) return;
		File[] files = FONT_DIR.listFiles();
		if(files == null) return;
		listFonts.setListData(files);
	}
}
