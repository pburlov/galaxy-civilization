/*
 * $Id: MapWrapper.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author PB
 */
public class MapWrapper
{

	private Map _data;

	public MapWrapper()
	{
	}

	/**
	 * 
	 */
	public MapWrapper(Map data)
	{
		this._data = data;
		if(data == null) this._data = new Hashtable();
	}

	public void initWithData(Map data)
	{
		this._data = data;
	}

	public Object getAttribute(Object key)
	{
		Object ret = null;
		if(_data != null) ret = _data.get(key);
		return ret;
	}

	public void setAttribute(Object key, Object value)
	{
		_data.put(key, value);
	}

	public Map getObjectData()
	{
		return _data;
	}

	public void removeAttribute(Object key)
	{
		_data.remove(key);
	}

	public Object getAttribute(Object key, Object lazyValue)
	{
		Object ret = getAttribute(key);
		if(ret == null) ret = lazyValue;
		return ret;
	}
}
