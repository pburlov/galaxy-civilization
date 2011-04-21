/*
 * $Id: StatusBalken.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 * Diese Componente anzeigt ein Wert in Form von gefärbte Balke.
 * 
 * @author pbu
 */
public class StatusBalken extends JComponent
{

	private Color pBorderColor = Color.BLUE;
	// private Color paintColor = Color.RED;
	private Color minColor = Color.RED;
	private Color maxColor = Color.GREEN;
	private double pMin;
	private double pMax;
	private double pValue;

	public StatusBalken(Dimension size, double min, double max)
	{
		this(min, max);
		setMaximumSize(size);
		setMinimumSize(size);
		setSize(size);
		setPreferredSize(size);
	}

	public StatusBalken(double min, double max)
	{
		pMin = min;
		pMax = max;
	}

	public void setBorderColor(Color col)
	{
		pBorderColor = col;
	}

	// /**
	// * Setzt Farbe mit der die Balke gezeichnet werden soll
	// * @param col
	// */
	// private void setPaintColor(Color col)
	// {
	// paintColor = col;
	// }
	/**
	 * Setzt Farbe für minimale Wert. Die Balke wird dann mit einem Farbverlauf gezeichnet wo Farbe
	 * von MinFarbe bis zum MaxFarbe verläuft
	 * 
	 * @param col
	 */
	public void setMinColor(Color col)
	{
		minColor = col;
	}

	/**
	 * Setzt Farbe für maximale Wert. Die Balke wird dann mit einem Farbverlauf gezeichnet wo Farbe
	 * von MinFarbe bis zum MaxFarbe verläuft
	 * 
	 * @param col
	 */
	public void setMaxColor(Color col)
	{
		maxColor = col;
	}

	/**
	 * @return
	 */
	public double getMax()
	{
		return pMax;
	}

	/**
	 * @param max
	 */
	public void setMax(double max)
	{
		pMax = max;
	}

	/**
	 * @return
	 */
	public double getMin()
	{
		return pMin;
	}

	/**
	 * @param min
	 */
	public void setMin(double min)
	{
		pMin = min;
	}

	/**
	 * @return
	 */
	public double getValue()
	{
		return pValue;
	}

	public void setValue(double value)
	{
		pValue = value;
		repaint();
	}

	private Color computePaintColor()
	{
		if(pValue > getMax())
			pValue = getMax();
		else if(pValue < getMin()) pValue = getMin();
		double faktor = pValue / (getMax() - getMin());
		int red = (int) (minColor.getRed() + ((maxColor.getRed() - minColor.getRed()) * faktor));
		int green = (int) (minColor.getGreen() + ((maxColor.getGreen() - minColor.getGreen()) * faktor));
		int blue = (int) (minColor.getBlue() + ((maxColor.getBlue() - minColor.getBlue()) * faktor));
		return new Color(red, green, blue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(pBorderColor != null)
		{
			g.setColor(pBorderColor);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		/*
		 * Länge der gefüllten Balke ausrechnen
		 */
		double faktor = (getWidth() - 2) / (getMax() - getMin());
		int wide = (int) (getValue() * faktor);
		g.setColor(computePaintColor());
		g.fillRect(1, 1, wide, getHeight() - 2);
	}
	// public static void main(String[] args)
	// {
	// JFrame frame = new JFrame("Balkentest");
	// JPanel panel = new JPanel();
	// final StatusBalken balken = new StatusBalken(new Dimension(800, 10), 0, 100);
	// balken.setValue(0);
	// /*
	// * Setze verschiedene Anfang- und Endfarben um den Verlauf zu begutachten
	// */
	// balken.setMinColor(new Color(255, 100, 0));
	// balken.setMaxColor(new Color(100, 255, 0));
	// panel.add(balken);
	// frame.getContentPane().add(panel);
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.pack();
	// frame.setVisible(true);
	// Timer timer = new Timer(50, new ActionListener()
	// {
	//
	// int value = 0;
	// int step = 1;
	//
	// public void actionPerformed(ActionEvent event)
	// {
	// balken.setValue(value);
	// if(value == balken.getMax()) step = -1;
	// if(value == balken.getMin()) step = 1;
	// value += step;
	// }
	// });
	// timer.start();
	// }
}
