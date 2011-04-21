/*
 * $Id: InfluenceFactor.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.civilization;

import java.util.HashMap;
import java.util.Map;
import mou.Main;
import mou.core.MapWrapper;

/**
 * Klasse dient der Speicherung verschiedenen Einflüssfaktoren mit begrenztem Zeitdauer
 * 
 * @author pb
 */
public class InfluenceFactor extends MapWrapper
{

	static final private String ATTR_DESCRIPTION = "ATTR_DESCRIPTION";
	static final private String ATTR_VALUE = "ATTR_VALUE";
	static final private String ATTR_DURATION = "ATTR_DURATION";

	public InfluenceFactor(Map data)
	{
		super(data);
	}

	public InfluenceFactor(long duration, Number value, String description)
	{
		this(new HashMap<String, Object>());
		setDescription(description);
		setDuration(duration);
		setValue(value);
	}

	public String getDescription()
	{
		return (String) getAttribute(ATTR_DESCRIPTION, "");
	}

	public void setDescription(String val)
	{
		setAttribute(ATTR_DESCRIPTION, val);
	}

	public Number getValue()
	{
		return (Number) getAttribute(ATTR_VALUE, Main.ZERO_NUMBER);
	}

	public void setValue(Number val)
	{
		setAttribute(ATTR_VALUE, val);
	}

	public Long getDuration()
	{
		return (Long) getAttribute(ATTR_DURATION, Main.ZERO_NUMBER);
	}

	public void setDuration(long val)
	{
		setAttribute(ATTR_DURATION, val);
	}
}
