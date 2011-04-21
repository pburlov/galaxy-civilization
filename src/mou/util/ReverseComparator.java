/*
 * $Id: ReverseComparator.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.util;

import java.util.Comparator;

public class ReverseComparator<O extends Comparable>
		implements Comparator<O>
{

	public int compare(O o1, O o2)
	{
		return o2.compareTo(o1);
	}
}