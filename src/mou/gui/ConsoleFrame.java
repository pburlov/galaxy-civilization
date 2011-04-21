/*
 * $Id: ConsoleFrame.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mou.Main;

/**
 * @author pbu
 */
public class ConsoleFrame extends JFrame
{

	private JTextArea textArea = new JTextArea();
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JScrollPane scrollPane = new JScrollPane();
	private ConsoleHandler handler;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public ConsoleFrame() throws HeadlessException
	{
		super("Console");
		handler = new ConsoleFrame.ConsoleHandler(textArea);
		Main.instance().getLogger().addHandler(handler);
		this.getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(textArea);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(false);
		JPanel panel = new JPanel();
		JRadioButton radio = new JRadioButton("Default");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				handler.setLevel(Main.instance().getLogger().getLevel());
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
				handler.setLevel(Level.SEVERE);
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("Info");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				handler.setLevel(Level.INFO);
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("Fine");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				handler.setLevel(Level.FINE);
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		radio = new JRadioButton("All");
		radio.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				handler.setLevel(Level.ALL);
			}
		});
		panel.add(radio);
		buttonGroup.add(radio);
		JButton button = new JButton("Clear");
		button.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent event)
			{
				textArea.setText("");
			}
		});
		panel.add(Box.createHorizontalStrut(20));
		panel.add(button);
		getContentPane().add(panel, BorderLayout.SOUTH);
		setSize(800, 600);
		setLocation(0, 0);
	}

	private class ConsoleHandler extends Handler
	{

		private Level piLevel = Main.instance().getLogger().getLevel();
		// private JTextArea textArea;
		private Formatter formatter = new SimpleFormatter();

		public ConsoleHandler(JTextArea ausgabe)
		{
			textArea = ausgabe;
		}

		public void setLevel(Level level)
		{
			piLevel = level;
		}

		public void publish(final LogRecord record)
		{
			if(piLevel.intValue() <= record.getLevel().intValue())
			{
				EventQueue.invokeLater(new Runnable()
				{

					public void run()
					{
						textArea.append(formatter.format(record));
					}
				});
			}
		}

		public void close()
		{
		};

		public void flush()
		{
		};
	}
}
