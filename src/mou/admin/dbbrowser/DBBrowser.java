/*
 * $Id: DBBrowser.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.admin.dbbrowser;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mou.Main;
import mou.core.MOUDB;

/**
 * @author pb
 */
public class DBBrowser extends JFrame
{

	private IntrospectorPanel browserPanel = new IntrospectorPanel();
	static private File file;
	static private Hashtable data;

	/**
	 * 
	 */
	public DBBrowser()
	{
		super("MOU DBBrowser");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowListener()
		{

			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub
			}

			public void windowClosing(WindowEvent e)
			{
				if(!browserPanel.wasChanged() && file != null && data != null) return;
				int res = JOptionPane.showConfirmDialog(null, "Geänderte Daten Speichern?", "Speichern?", JOptionPane.YES_NO_OPTION);
				if(res != JOptionPane.OK_OPTION) return;
				try
				{
					MOUDB.writeDBFile(data, file);
				} catch(FileNotFoundException e1)
				{
					e1.printStackTrace();
				} catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}

			public void windowClosed(WindowEvent e)
			{
			}

			public void windowIconified(WindowEvent e)
			{
			}

			public void windowDeiconified(WindowEvent e)
			{
			}

			public void windowActivated(WindowEvent e)
			{
			}

			public void windowDeactivated(WindowEvent e)
			{
			}
		});
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(browserPanel, BorderLayout.CENTER);
		setSize(400, 600);
		setVisible(true);
	}

	static public void serializeObject(Object o) throws IOException
	{
		String filename = JOptionPane.showInputDialog(null, "File name:");
		if(filename == null) return;
		File serFile = new File(filename);
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serFile));
		out.writeObject(o);
		out.close();
		if(Main.isDebugMode()) System.out.println("Data serialized to: " + serFile.getAbsolutePath());
	}

	public static void main(String[] args) throws Exception
	{
		DBBrowser frame = new DBBrowser();
		JFileChooser chooser = new JFileChooser(".");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) System.exit(0);
		file = chooser.getSelectedFile();
		data = MOUDB.readDBFile(file);
		frame.browserPanel.showObject(data);
	}
}
