/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import mou.gui.GUI;
import burlov.swing.ErrorDialog;
import burlov.swing.ErrorDialog.ErrorDialogAction;

/**
 * Klasse ient als eine Art Bug-Fededback Assistent
 * 
 * @author pb
 */
public class ErrorReporter
{

	public ErrorReporter(Throwable cause, String comment)
	{
		ErrorDialogAction action = new ErrorDialog.ErrorDialogAction("Bug-Report abschicken", new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
			}
		});
		ErrorDialog dlg = new ErrorDialog(null, cause, comment, action);
		dlg.setTitle("Programmfehler");
		GUI.centreWindow(null, dlg);
		dlg.setVisible(true);
	}
}
