/*
 * $Id: NaturalResourceTableDialog.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import mou.event.RowClickedEventListener;

/**
 * @author pb
 */
public class NaturalResourceTableDialog extends JDialog
{

	private NaturalResTable resTable = new NaturalResTable();

	// private NaturalResourceTableDialog self;
	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public NaturalResourceTableDialog(Frame owner) throws HeadlessException
	{
		super(owner, true);
		setTitle("Vorhandene Baumaterialien");
		// self = this;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(resTable, BorderLayout.CENTER);
		setAlwaysOnTop(isAlwaysOnTop());
		resTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		resTable.getActionMap().put("Escape", new AbstractAction()
		{

			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		setSize(300, 400);
	}

	public void addRowClickedEventListener(RowClickedEventListener listener)
	{
		resTable.addRowClickedEventListener(listener);
	}
}
