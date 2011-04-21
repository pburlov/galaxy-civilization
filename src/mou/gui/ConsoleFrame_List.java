/*
 * $Id: ConsoleFrame_List.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.DateFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import mou.Main;
import burlov.swing.MessagePanel.MessagesPanel;

/**
 * @author pbu
 */
public class ConsoleFrame_List extends JFrame
{

	private MessagesPanel messagesPanel = new MessagesPanel(1000);
	private ButtonGroup buttonGroup = new ButtonGroup();
	private Level logLevel = Level.ALL;
	// private Formatter formatter = new HTMLFormatter();
	private DateFormat dateFormat = DateFormat.getTimeInstance();

	/**
	 * @throws java.awt.HeadlessException
	 */
	public ConsoleFrame_List() throws HeadlessException
	{
		super("Console");
		Main.instance().getLogger().addHandler(new Handler()
		{

			synchronized public void publish(LogRecord rec)
			{
				if(logLevel.intValue() > rec.getLevel().intValue()) return;
				String msg = "<" + dateFormat.format(new Time(rec.getMillis())) + ">";
				msg += " " + rec.getSourceClassName() + "." + rec.getSourceMethodName() + ": " + rec.getMessage();
				messagesPanel.appendMessage(msg);
			}

			public void flush()
			{
			}

			public void close() throws SecurityException
			{
			}
		});
		this.getContentPane().setLayout(new BorderLayout());
		getContentPane().add(messagesPanel, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		JRadioButton radio = new JRadioButton("Default");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				logLevel = Main.instance().getLogger().getLevel();
			}
		});
		radio.setSelected(true);
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("Severe");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				logLevel = Level.SEVERE;
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("Info");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				logLevel = Level.INFO;
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("Fine");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				logLevel = Level.FINE;
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("All");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				logLevel = Level.ALL;
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		getContentPane().add(panel, BorderLayout.SOUTH);
		setSize(800, 600);
		setLocation(0, 0);
	}
}
