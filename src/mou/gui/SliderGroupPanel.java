/*
 * $Id: SliderGroupPanel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author pb
 */
public class SliderGroupPanel extends JPanel
		implements ChangeListener
{

	private JSlider[] sliders;
	private JRadioButton[] radioButtons;
	private JProgressBar[] progress;
	private JLabel[] labels;
	private JLabel[] labelValues;
	private boolean adjustingValues = true;
	private boolean showSlider;
	private boolean showProgress;
	private boolean fixed = true;
	private final int MAX_VALUE = 1000000;
	private final int MIN_VALUE = 0;
	private Vector changeListeners = new Vector();

	/**
	 * @param sliderCount
	 *            Anzahl der Slider
	 */
	public SliderGroupPanel(int sliderCount, boolean showSlider, boolean showProgress)
	{
		super();
		int sliderVal = MAX_VALUE / sliderCount;
		this.showSlider = showSlider;
		this.showProgress = showProgress;
		
		labels = new JLabel[sliderCount];
		labelValues = new JLabel[sliderCount];

		sliders = new JSlider[sliderCount];
		radioButtons = new JRadioButton[sliderCount];
		progress = new JProgressBar[sliderCount];

		// maxValue = max;
		// minValue = min;
		// setLayout(new FlowLayout(FlowLayout.LEFT));
		// setLayout(new BorderLayout());
		Box box_y = Box.createVerticalBox();
		add(box_y, BorderLayout.CENTER);
		// Dictionary labelMap = new Hashtable();
		// labelMap.put(new Integer(0), new JLabel("0%"));
		// labelMap.put(new Integer(50), new JLabel("50%"));
		// labelMap.put(new Integer(100), new JLabel("100%"));
		for(int i = 0; i < sliders.length; i++)
		{
			final int index = i;

			/* Labels */
			labels[i] = new JLabel();
			labelValues[i] = new JLabel();
			
			Box box_x = Box.createHorizontalBox();
			box_x.add(labels[i]);
			labels[i].setAlignmentX(LEFT_ALIGNMENT);
			box_x.add(Box.createHorizontalGlue());
			box_x.add(labelValues[i]);
			labelValues[i].setAlignmentX(RIGHT_ALIGNMENT);
			box_y.add(box_x);
			
			/* Show Slider */
			if(showSlider)
			{
				sliders[i] = new JSlider(MIN_VALUE, MAX_VALUE);
				radioButtons[i] = new JRadioButton("");
				radioButtons[i].addActionListener(new ActionListener()
		        {

					public void actionPerformed(ActionEvent e)
					{
						sliders[index].setEnabled(!sliders[index].isEnabled());
					}
		        });
				
				box_x = Box.createHorizontalBox();
				box_x.add(radioButtons[i]);
				box_x.add(sliders[i]);
				box_x.add(Box.createHorizontalStrut(10));
				// box_x.add(Box.createHorizontalGlue());
				box_y.add(box_x);
				sliders[i].setValue(sliderVal);
				sliders[i].addChangeListener(this);
				sliders[i].setMajorTickSpacing(50);
				sliders[i].setMinorTickSpacing(5);
				sliders[i].setSnapToTicks(true);
				sliders[i].setPaintTicks(false);
				sliders[i].setPaintTrack(true);
				// sliders[i].setMaximumSize(sliders[i].getPreferredSize());
				// sliders[i].setPaintLabels(false);
				// sliders[i].setLabelTable(labelMap);
			}
			
			/* Show Progress */
			if(showProgress)
			{
				progress[i] = new JProgressBar(MIN_VALUE, MAX_VALUE);
				progress[i].setValue(0);
				progress[i].setStringPainted(true);
				
				box_x = Box.createHorizontalBox();
				box_x.add(progress[i]);
				box_x.add(Box.createHorizontalStrut(10));
				// box_x.add(Box.createHorizontalGlue());
				box_y.add(box_x);

			}
		}
		box_y.add(Box.createVerticalGlue());
		setMaximumSize(getPreferredSize());
		// setPreferredSize(new Dimension(getPreferredSize().width,1000));
		refreshValueLabels();
		adjustingValues = false;
	}

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	private void fireChangeEvent()
	{
		for(Enumeration en = changeListeners.elements(); en.hasMoreElements();)
		{
			ChangeListener listener = (ChangeListener) en.nextElement();
			listener.stateChanged(new ChangeEvent(this));
		}
	}

	private double computeFaktorForSlider(int index)
	{
		return ((double) getValue(index)) / ((double) MAX_VALUE);
	}

	public int getSliderCount()
	{
		return sliders.length;
	}

	public void setSliderLabel(int index, String label)
	{
		labels[index].setText(label);
	}

	private int getValue(int index)
	{
		return sliders[index].getValue();
	}

	/*
	 * private void setValues(int[] values) { adjustingValues = true; for(int i = 0; i <
	 * sliders.length; i++) { sliders[i].setValue(values[i]); } adjustingValues = false; }
	 */
	/*
	 * private int[] getValues() { int[] ret = new int[sliders.length]; for(int i = 0; i <
	 * ret.length; i++) { ret[i] = getValue(i); } return ret; }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		if(adjustingValues) return;
		adjustingValues = true;
		JSlider source = (JSlider) e.getSource();
		for(int i = 0; i < sliders.length; i++)
		{
			if(sliders[i] != source) continue;
			adjustValues(i);
			refreshValueLabels();
			break;
		}
		adjustingValues = false;
		fireChangeEvent();
	}

	private void refreshValueLabels()
	{
		if(showSlider)
		{
			for(int i = 0; i < sliders.length; i++)
			{
				double proz = computeFaktorForSlider(i) * 100;
				labelValues[i].setText(GUI.formatProzent(proz));
			}
		}
	}

	private void adjustValues(int changedIndex)
	{
		if(sliders.length < 2) return;
		// double restVal = MAX_VALUE - sliders[changedIndex].getValue();
		int gesamtsumme = sumAllValues();
		int deltaSum = MAX_VALUE - gesamtsumme;
		if(!fixed && MAX_VALUE > gesamtsumme) return;
		if(deltaSum == 0) return;
		int enabledCount = getCountOfEnabledSliders();
		boolean increase = true;
		if(deltaSum < 0)
		{
			increase = false;
			deltaSum = -deltaSum;
		}
		while(enabledCount > 0 && deltaSum > 0)
		{
			if(!increase)
			{
				enabledCount = getCountOfDecreasableEnabledSliders(changedIndex);
			} else
			{
				enabledCount = getCountOfIncreasableEnabledSliders(changedIndex);
			}
			if(enabledCount > 0)
			{// Zuerst Werte nicht fixierten Sliders abändern
				int deltaVal = (int) ((float) deltaSum / (float) enabledCount);
				if(Math.abs(deltaVal) < 1)
				{
					if(deltaSum < 0)
						deltaVal = -1;
					else
						deltaVal = 1;
				}
				for(int i = 0; i < sliders.length; i++)
				{
					if(!sliders[i].isEnabled() || i == changedIndex) continue;
					deltaSum -= deltaVal;
					int val = sliders[i].getValue();
					if(increase)
						val += deltaVal;
					else
						val -= deltaVal;
					if(val < MIN_VALUE)
					{// Nicht abziehbare Punkte der Restmenge hinzufügen
						deltaSum += (MIN_VALUE - val);
						val = MIN_VALUE;
					}
					if(val > MAX_VALUE)
					{
						deltaSum += (val - MAX_VALUE);
						val = MAX_VALUE;
					}
					sliders[i].setValue(val);
					if(deltaSum <= 0) return;
				}
			}
		}
		int disabledSliders = getCountOfDisabledSliders();
		while(disabledSliders > 0 && deltaSum > 0)
		{
			if(!increase)
			{
				disabledSliders = getCountOfDecreasableDisabledSliders(changedIndex);
			} else
				disabledSliders = getCountOfIncreasableDisabledSliders(changedIndex);
			if(disabledSliders > 0)
			{// Zuerst Werte nicht fixierten Sliders abändern
				int deltaVal = (int) ((float) deltaSum / (float) disabledSliders);
				if(Math.abs(deltaVal) < 1)
				{
					if(deltaSum < 0)
						deltaVal = -1;
					else
						deltaVal = 1;
				}
				for(int i = 0; i < sliders.length; i++)
				{
					if(sliders[i].isEnabled() || i == changedIndex) continue;
					deltaSum -= deltaVal;
					int val = sliders[i].getValue();
					if(increase)
						val += deltaVal;
					else
						val -= deltaVal;
					if(val < MIN_VALUE)
					{// Nicht abziehbare Punkte der Restmenge hinzufügen
						deltaSum += (MIN_VALUE - val);
						val = MIN_VALUE;
					}
					if(val > MAX_VALUE)
					{
						deltaSum += (val - MAX_VALUE);
						val = MAX_VALUE;
					}
					sliders[i].setValue(val);
					if(deltaSum <= 0) return;
				}
			}
		}
	}

	private int sumAllValues()
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			sum += sliders[i].getValue();
		}
		return sum;
	}

	private int getCountOfEnabledSliders()
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			if(sliders[i].isEnabled()) sum++;
		}
		return sum;
	}

	private int getCountOfDisabledSliders()
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			if(!sliders[i].isEnabled()) sum++;
		}
		return sum;
	}

	private int getCountOfDecreasableEnabledSliders(int withoutIndex)
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			if(sliders[i].isEnabled() && (sliders[i].getValue() > MIN_VALUE) && (i != withoutIndex)) sum++;
		}
		return sum;
	}

	private int getCountOfIncreasableEnabledSliders(int withoutIndex)
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			if(sliders[i].isEnabled() && (sliders[i].getValue() < MAX_VALUE) && (i != withoutIndex)) sum++;
		}
		return sum;
	}

	private int getCountOfDecreasableDisabledSliders(int withoutIndex)
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			if((!sliders[i].isEnabled()) && (sliders[i].getValue() > MIN_VALUE) && (i != withoutIndex)) sum++;
		}
		return sum;
	}

	private int getCountOfIncreasableDisabledSliders(int withoutIndex)
	{
		int sum = 0;
		for(int i = 0; i < sliders.length; i++)
		{
			if((!sliders[i].isEnabled()) && (sliders[i].getValue() < MAX_VALUE) && (i != withoutIndex)) sum++;
		}
		return sum;
	}

	/**
	 * Liefert ein Array mit aktuellen Faktoren zu jedem Slider
	 * 
	 * @return
	 */
	public double[] getFaktors()
	{
		double[] ret = new double[sliders.length];
		for(int i = 0; i < sliders.length; i++)
		{
			ret[i] = computeFaktorForSlider(i);
		}
		return ret;
	}

	/**
	 * Setzt Faktoren für jeden Slider. Array soll genauso groß sein wie die Anzahl der Sliders
	 * 
	 * @param faktors
	 */
	public void setSliderFaktors(double[] faktors)
	{
		if(showSlider)
		{
			for(int i = 0; i < sliders.length && i < faktors.length; i++)
				sliders[i].setValue((int) (MAX_VALUE * faktors[i]));
		}
	}
	
	public void setProgressFaktors(double[] faktors)
	{
		if(showProgress)
		{
			for(int i = 0; i < sliders.length && i < faktors.length; i++)
			{
				progress[i].setValue((int) (MAX_VALUE * faktors[i]));
				progress[i].setString(GUI.formatProzent(faktors[i]*100));
			}
		}

	}
	
	public double getTotalFaktor()
	{
		/* Ergebnis auf 4 Nachkommastellen runden */
		return Math.round(sumAllValues()* 10000d/MAX_VALUE)/10000d;
	}
	
	/* Fixiert die Sliders auf 100%,
	 * bzw. erlaubt eine Reduzierung des Gesamtwertes
	 */
	public void setFixed(boolean b)
	{
		fixed = b;
	}
}