/*
 * $Id: RowClickedEvent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.event;

import java.util.EventObject;

/**
 * @author pb
 */
public class RowClickedEvent extends EventObject
{

	private int _row;
	private Object _value;

	/**
	 * @param source
	 */
	public RowClickedEvent(Object source, int row, Object value)
	{
		super(source);
		_value = value;
		_row = row;
	}

	public int getRow()
	{
		return _row;
	}

	public void setRow(int _row)
	{
		this._row = _row;
	}

	public Object getValue()
	{
		return _value;
	}

	public void setValue(Object _value)
	{
		this._value = _value;
	}
}
