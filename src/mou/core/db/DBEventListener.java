/*
 * $Id: DBEventListener.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.db;

public interface DBEventListener
{

	abstract public void objectRemoved(DBObjectImpl obj);

	abstract public void objectAdded(DBObjectImpl obj);

	abstract public void objectChanged(ObjectChangedEvent event);
}