/*
 * $Id: DBQueryResult.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.db;

import java.util.Iterator;
import java.util.List;

/**
 * @author pbu
 */
public class DBQueryResult
{

	private List result;

	DBQueryResult(List result)
	{
		this.result = result;
	}

	public Iterator getIterator()
	{
		return result.iterator();
	}

	public List getList()
	{
		return result;
	}

	public int resultSize()
	{
		return result.size();
	}
}
