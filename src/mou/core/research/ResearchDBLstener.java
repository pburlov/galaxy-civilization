/*
 * $Id: ResearchDBLstener.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.research;

import mou.core.res.ResearchableResource;

public interface ResearchDBLstener
{

	public void researchResultAdded(ResearchableDesign<? extends ResearchableResource> des);

	public void researchResultRemoved(ResearchableDesign<? extends ResearchableResource> des);
}
