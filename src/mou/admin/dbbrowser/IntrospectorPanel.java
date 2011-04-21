/*
 * $Id: IntrospectorPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.admin.dbbrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author pb
 */
public class IntrospectorPanel extends JPanel
{

	private JTree tree;
	private JScrollPane scroll = new JScrollPane();
	private TreePopup popup = new TreePopup();
	private boolean changed = false;

	/**
	 * 
	 */
	public IntrospectorPanel()
	{
		super(new BorderLayout());
		tree = new JTree(new IntrospectorTreeNode(new HashMap(0)));
		tree.setCellRenderer(new IntrospectorTreeNodeRenderer());
		scroll.setViewportView(tree);
		add(scroll, BorderLayout.CENTER);
		tree.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if(path != null) tree.setSelectionPath(path);
				if(e.getButton() == MouseEvent.BUTTON3)
				{
					popup.show(tree, e.getX(), e.getY());
				}
			}
		});
	}

	public void showObject(Object data)
	{
		tree.setModel(new DefaultTreeModel(new IntrospectorTreeNode(data)));
	}

	private class TreePopup extends JPopupMenu
	{

		private JMenuItem itemDelete = new JMenuItem("Delete entry");
		private JMenuItem itemSerialize = new JMenuItem("Serialize entry");

		public TreePopup()
		{
			add(itemDelete);
			add(itemSerialize);
			itemDelete.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					((DefaultTreeModel) tree.getModel()).removeNodeFromParent((MutableTreeNode) tree.getSelectionPath().getLastPathComponent());
					changed = true;
				}
			});
			itemSerialize.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent arg0)
				{
					IntrospectorTreeNode node = (IntrospectorTreeNode) tree.getSelectionPath().getLastPathComponent();
					Object value = node.getEntryValue();
					try
					{
						DBBrowser.serializeObject(value);
					} catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			});
		}
	}

	public boolean wasChanged()
	{
		return changed;
	}
}
