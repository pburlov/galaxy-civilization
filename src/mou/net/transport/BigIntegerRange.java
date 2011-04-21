/*
 * $Id: BigIntegerRange.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.net.transport;

import java.math.BigInteger;

/**
 * Klasse darstellt ein Interval zwischen LeftValue inklusive und RightValue exklusive. Linke Wert
 * muss immer kleiner gleich rechtem Wert sein
 * 
 * @author pb
 */
public class BigIntegerRange
{

	private BigInteger left;
	private BigInteger right;

	/**
	 * LeftValue muss kleiner gleich als RightValue sein
	 */
	public BigIntegerRange(BigInteger leftValue, BigInteger rightValue)
	{
		if(leftValue.compareTo(rightValue) > 0) throw new IllegalArgumentException("LeftValue > RightValue");
		this.left = leftValue;
		this.right = rightValue;
	}

	public boolean inRange(BigInteger value)
	{
		return ((left.compareTo(value) <= 0) && (right.compareTo(value) > 0));
	}
}
