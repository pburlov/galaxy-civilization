/*
 * $Id: IntrospectorTreeNodeRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.admin.dbbrowser;

import java.awt.Component;
import java.util.Collection;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author pb
 */
public class IntrospectorTreeNodeRenderer extends DefaultTreeCellRenderer
{

	/**
	 * 
	 */
	public IntrospectorTreeNodeRenderer()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean select, boolean expanded, boolean leaf, int row, boolean focused)
	{
		String classname = "";
		String key = "";
		String val = "";
		IntrospectorTreeNode node = (IntrospectorTreeNode) value;
		value = node.getUserObject();
		if(value instanceof Map.Entry)
		{
			classname = ((Map.Entry) value).getValue().getClass().getSimpleName();
			Object oKey = ((Map.Entry) value).getKey();
			if(oKey == null)
				key = "null";
			else
				key = oKey.toString();
			if(!(node.getEntryValue() instanceof Map) && !(node.getEntryValue() instanceof Collection)) val = " : " + node.getEntryValue().toString();
		} else
			classname = value.getClass().getSimpleName();
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		setText(key + " <" + classname + ">" + val);
		return this;
	}
}
