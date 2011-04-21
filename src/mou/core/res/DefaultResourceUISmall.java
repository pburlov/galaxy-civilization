/*
 * $Id: DefaultResourceUISmall.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.res;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

/**
 * Hiermit wird eine Ressource mit Hilfe eines JLabel dargestellt, mit Icon und Ressourcename
 * 
 * @author pbu
 */
public class DefaultResourceUISmall extends ResourceUI
{

	private JLabel mLabel = new JLabel();
	private boolean opaque = true;

	/**
	 * @param res
	 */
	public DefaultResourceUISmall()
	{
		this(true);
	}

	/**
	 * @param res
	 */
	public DefaultResourceUISmall(boolean opaque)
	{
		this.opaque = opaque;
		initUI();
	}

	private void initUI()
	{
		setOpaque(true);
		setOpaque(true);
		mLabel.setAlignmentX(0.0f);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// mLabel.setAlignmentX(0f);
		add(mLabel);
		add(Box.createHorizontalGlue());
		setOpaque(opaque);
	}

	public void showResource(ResourceAbstract res)
	{
		mLabel.setText(res.getName());
		mLabel.setToolTipText(res.getShortDescription());
		mLabel.setIcon(res.getIcon());
		setMaximumSize(mLabel.getPreferredSize());
	}
}
