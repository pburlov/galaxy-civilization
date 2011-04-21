/*
 * $Id: DefaultBuildingScienceView.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony;

import mou.core.res.DefaultResearchableScienceView;
import mou.core.res.ResearchableResource;

public class DefaultBuildingScienceView extends DefaultResearchableScienceView
{

	public DefaultBuildingScienceView(ResearchableResource res)
	{
		super(res);
		removeComponent(ENERGIEBALANCE);
	}
}
