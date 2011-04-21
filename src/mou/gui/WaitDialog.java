/*
 * $Id: WaitDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui;

import java.awt.Frame;
import java.awt.HeadlessException;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

/**
 * Zeigt einen Fortschrittsbalken in einem modalen Dialog
 * 
 * @author pb
 */
public class WaitDialog extends JDialog
{

	private JProgressBar progressBar = new JProgressBar();
	private Box panel = Box.createVerticalBox();
	private JLabel label = new JLabel();

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public WaitDialog(Frame owner, String title) throws HeadlessException
	{
		super(owner, title);
		setUndecorated(true);
		setModal(true);
		getContentPane().add(panel);
		panel.add(label);
		panel.add(progressBar);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));
		label.setText(title);
		progressBar.setIndeterminate(true);
		pack();
		setResizable(false);
		GUI.centreWindow(owner, this);
	}

	public int getMaximum()
	{
		return progressBar.getMaximum();
	}

	public int getMinimum()
	{
		return progressBar.getMinimum();
	}

	public int getValue()
	{
		return progressBar.getValue();
	}

	public boolean isIndeterminate()
	{
		return progressBar.isIndeterminate();
	}

	public void setIndeterminate(boolean newValue)
	{
		progressBar.setIndeterminate(newValue);
	}

	public void setMaximum(int n)
	{
		progressBar.setMaximum(n);
	}

	public void setMinimum(int n)
	{
		progressBar.setMinimum(n);
	}

	public void setValue(int n)
	{
		progressBar.setValue(n);
	}
}
