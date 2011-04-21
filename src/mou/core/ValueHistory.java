/*
 * $Id: ValueHistory.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse speicher eine begrenzte Liste der numerischen Werten
 * 
 * @author pb
 */
public class ValueHistory extends MapWrapper
{

	static final private String ATTR_MAX_LENGTH = "ATTR_MAX_LENGTH";
	static final private String ATTR_VALUES = "ATTR_VALUES";
	static final private String ATTR_VORLETZTE_VALUE = "ATTR_VORLETZTE_VALUE";

	/**
	 * 
	 */
	public ValueHistory(int maxLenght)
	{
		super(new Hashtable());
		setAttribute(ATTR_MAX_LENGTH, new Integer(maxLenght));
	}

	/**
	 * @param data
	 */
	public ValueHistory(Map data)
	{
		super(data);
	}

	/**
	 * Liefert interne Liste der gespeicherten Werten
	 * 
	 * @return
	 */
	private LinkedList getValuesList()
	{
		LinkedList values = (LinkedList) getAttribute(ATTR_VALUES);
		if(values == null)
		{
			values = new LinkedList();
			setAttribute(ATTR_VALUES, values);
		}
		return values;
	}

	/**
	 * Liefert die maximale Anzahl der gespeicherten Werte
	 * 
	 * @return
	 */
	public Integer getMaxLenght()
	{
		return (Integer) getAttribute(ATTR_MAX_LENGTH);
	}

	/**
	 * Addiert ein Wert zu der Liste ger gespeicherten Werten
	 * 
	 * @param val
	 */
	public void addValue(Number val)
	{
		Number lastVal = getLastValue();
		if(lastVal != null) setAttribute(ATTR_VORLETZTE_VALUE, lastVal);
		List values = getValuesList();
		values.add(val);
		if(values.size() > getMaxLenght().intValue()) values.remove(0);
	}

	public void replaceLastValue(Number val)
	{
		LinkedList values = getValuesList();
		values.removeLast();
		values.addLast(val);
	}

	public Number getVorletzteWert()
	{
		return (Number) getAttribute(ATTR_VORLETZTE_VALUE);
	}

	/**
	 * Liefert Iteratorsichere Liste der gespeicherten Werten
	 * 
	 * @return
	 */
	public List getNumbersList()
	{
		return new ArrayList(getValuesList());
	}

	public Number getLastValue()
	{
		LinkedList values = getValuesList();
		if(values.isEmpty()) return null;
		return (Number) values.getLast();
	}

	/**
	 * Liefert Differenz zwischen den letzten und dem vorletztem Wert
	 * 
	 * @return
	 */
	public Number getLasDiff()
	{
		Number vorletzte = getVorletzteWert();
		Number letzte = getLastValue();
		if(vorletzte != null)
		{
			if(vorletzte instanceof Double || vorletzte instanceof Float) { return new Double(letzte.doubleValue() - vorletzte.doubleValue()); }
			return new Long(letzte.longValue() - vorletzte.longValue());
		}
		return letzte;
	}
}