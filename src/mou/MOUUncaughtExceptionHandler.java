/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

/**
 * @author pb
 */
public class MOUUncaughtExceptionHandler
		implements Thread.UncaughtExceptionHandler
{

	/**
	 * 
	 */
	public MOUUncaughtExceptionHandler()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	public void uncaughtException(Thread t, Throwable e)
	{
		// Main.instance().logThrowable("Thread: "+t.getName(), e);
		Main.instance().severeErrorOccured(e, "Ausnahmefehler in Thread " + t.getName(), true);
	}
}
