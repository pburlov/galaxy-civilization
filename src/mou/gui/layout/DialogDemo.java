/*
 * $Id: DialogDemo.java 11 2006-03-25 16:18:20Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.gui.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogDemo extends JDialog
{

	String fileName;
	JTextField nameField, addressField, stateField;
	JButton helpButton, exitButton, saveButton;

	public DialogDemo()
	{
		setTitle("Dialog Demo");
		setInstances();
		addContent();
		exitButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
	}

	private void setInstances()
	{
		nameField = new JTextField(12); // try changing these widths
		addressField = new JTextField(20);
		stateField = new JTextField(2);
		helpButton = new JButton("Help");
		exitButton = new JButton("Exit");
		saveButton = new JButton("Save");
	}

	private void addContent()
	{
		JPanel contentPanel = new JPanel();
		SCLayout contentLayout = new SCLayout(2, SCLayout.FILL, SCLayout.FILL, 3);
		contentLayout.setScale(0, 1.5);
		contentPanel.setLayout(contentLayout);
		JPanel displayPanel = new JPanel();
		SGLayout displayLayout = new SGLayout(3, 2, SGLayout.LEFT, SGLayout.CENTER, 5, 0);
		displayLayout.setColumnScale(0, 0.4);
		displayLayout.setColumnAlignment(0, SGLayout.RIGHT, SGLayout.CENTER);
		displayPanel.setLayout(displayLayout);
		// displayPanel.setLayout(new GridLayout(3, 2, 5, 2)); // try this instead
		displayPanel.add(new JLabel("Name"));
		displayPanel.add(nameField);
		displayPanel.add(new JLabel("Address"));
		displayPanel.add(addressField);
		displayPanel.add(new JLabel("State"));
		displayPanel.add(stateField);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		SRLayout buttonLayout = new SRLayout(3, SRLayout.FILL, SRLayout.CENTER, 10);
		buttonLayout.setMargins(5, 15, 5, 15);
		buttonPanel.setLayout(buttonLayout);
		buttonPanel.add(helpButton);
		buttonPanel.add(exitButton);
		buttonPanel.add(saveButton);
		contentPanel.add(displayPanel);
		contentPanel.add(buttonPanel);
		setContentPane(contentPanel);
		pack();
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	static public void main(String[] args)
	{
		String fileName = "C:\\DialogDemo.jpg";
		if(args.length > 0) fileName = args[0];
		DialogDemo dialog = new DialogDemo();
		dialog.setFileName(fileName);
		dialog.setVisible(true);
	}
}