/*
 * $Id: ResourceUI.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.res;

import javax.swing.JPanel;

/**
 * @author pbu
 */
abstract public class ResourceUI extends JPanel
{

	abstract public void showResource(ResourceAbstract res);
}
