/*
 * $Id$
 * Created on May 14, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import mou.net2.PeerHandle;


public class SubscribeData
{
	private Map<Topic, Set<PeerEntry>> data = new HashMap<Topic, Set<PeerEntry>>();
	private long timeout;
	
	/**
	 * Konstruktor fuer Objekte ohne Timeout. Eingefuegte Elemente werden nicht
	 * automatisch entfernt
	 */
	public SubscribeData()
	{
	}

	public SubscribeData(Timer timer, long timeout)
	{
		this.timeout = timeout;
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				checkTimeouts();
			}
		}, timeout / 4, timeout / 4);
	}
	
	synchronized protected void checkTimeouts()
	{
		for(Iterator<Entry<Topic, Set<PeerEntry>>> iter = data.entrySet().iterator(); iter.hasNext();)
		{
			Entry<Topic, Set<PeerEntry>> entry = iter.next();
			if(entry.getValue() == null || entry.getValue().size() == 0)
			{
				iter.remove();
				continue;
			}
			for(Iterator<PeerEntry> peerIter = entry.getValue().iterator(); peerIter.hasNext();)
			{
				PeerEntry peerEntry = peerIter.next();
				if((peerEntry.getTime() + timeout) < System.currentTimeMillis())peerIter.remove();
			}
			if(entry.getValue() == null || entry.getValue().size() == 0)
			{
				iter.remove();
			}
		}
	}
	
	synchronized public boolean addSubscriber(Topic topic, PeerHandle handle)
	{
		Set<PeerEntry> entries = data.get(topic);
		if(entries == null)
		{
			entries = new HashSet<PeerEntry>();
			data.put(topic, entries);
		}
		return entries.add(new PeerEntry(handle,System.currentTimeMillis()));
	}
	
	synchronized public void addSubscribers(Topic topic, Collection<PeerHandle> subscribers)
	{
		for(PeerHandle handle : subscribers)addSubscriber(topic, handle);
	}
	
	synchronized public boolean removeSubscriber(Topic topic, PeerHandle handle)
	{
		Set<PeerEntry> entries = data.get(topic);
		if(entries == null)return false;
		boolean ret = entries.remove(new PeerEntry(handle,0l));
		if(entries.size() == 0)data.remove(topic);
		return  ret;
	}
	
	/**
	 * 
	 * @param topic
	 * @return Kopie der interne Datestruktur
	 */
	synchronized public List<PeerHandle> getSubscribers(Topic topic)
	{
		if(topic == null)return Collections.emptyList();
		Collection<PeerEntry> entries = data.get(topic);
		if(entries == null || entries.size() == 0)return Collections.emptyList();
		List<PeerHandle> handles = new ArrayList<PeerHandle>(entries.size());
		for(PeerEntry entry : entries)handles.add(entry.getHandle());
		return handles;
	}
	
	synchronized public boolean containsTopic(Topic topic)
	{
		return data.containsKey(topic);
	}
	
	synchronized public Set<Topic> getTopics()
	{
		return new HashSet<Topic>(data.keySet());
	}
}
