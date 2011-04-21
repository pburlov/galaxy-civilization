/*
 * $Id: ObjectChangedEvent.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.db;

/**
 * @author pbu
 */
public class ObjectChangedEvent
{

	private String attrName;
	private Object oldValue;
	private Object newValue;
	private DBObjectImpl dbObject;

	public ObjectChangedEvent(DBObjectImpl dbObject, String attrName, Object oldValue, Object newValue)
	{
		setDbObject(dbObject);
		setAttrName(attrName);
		setOldValue(oldValue);
		setNewValue(newValue);
	}

	/**
	 * @return
	 */
	public DBObjectImpl getDbObject()
	{
		return dbObject;
	}

	/**
	 * @param dbObject
	 */
	private void setDbObject(DBObjectImpl dbObject)
	{
		this.dbObject = dbObject;
	}

	/**
	 * @return
	 */
	public String getAttrName()
	{
		return attrName;
	}

	/**
	 * @param attrName
	 */
	private void setAttrName(String attrName)
	{
		this.attrName = attrName;
	}

	/**
	 * @return
	 */
	public Object getNewValue()
	{
		return newValue;
	}

	/**
	 * @param newValue
	 */
	private void setNewValue(Object newValue)
	{
		this.newValue = newValue;
	}

	/**
	 * @return
	 */
	public Object getOldValue()
	{
		return oldValue;
	}

	/**
	 * @param oldValue
	 */
	private void setOldValue(Object oldValue)
	{
		this.oldValue = oldValue;
	}
}
