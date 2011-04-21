/*
 * $Id: InfluenceFaktorHtmlRenderer.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.gui.civilizationscreen;

import java.util.Collection;
import mou.core.civilization.InfluenceFactor;
import mou.gui.GUI;

public class InfluenceFaktorHtmlRenderer
{

	protected InfluenceFaktorHtmlRenderer()
	{
		super();
	}

	/**
	 * Formatiert Werte zwischen -1 und 1 als Prozente zwischen -100 und 100
	 * 
	 * @param faktoren
	 * @return
	 */
	static final public String renderSignedPercent(Collection<InfluenceFactor> faktoren)
	{
		StringBuilder s = new StringBuilder("<html><table>");
		for(InfluenceFactor f : faktoren)
		{
			s.append("<tr><td><b>");
			s.append(f.getDescription());
			s.append(": </b></td><td>");
			s.append(GUI.formatProzentSigned(f.getValue().doubleValue() * 100));
			s.append("</td></tr>");
		}
		s.append("</table></html>");
		return s.toString();
	}
}
