/*
 * $Id: BuildQueue.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.colony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mou.Main;
import mou.core.MapWrapper;
import mou.core.colony.BuildJobAbstract.BuildAllowed;
import mou.gui.GUI;
import mou.storage.ser.ID;

/**
 * Klasse verwaltet Liste der Bauaufträge einer Kolonie. Aufträge werden automatisch gestartet,
 * gebaut und von der Liste wieder entfernt. Wenn Auftragliste leer ist, dann wird ein Idle-Auftrag
 * ausgeführt.
 * 
 * @author pbu
 */
public class BuildQueue extends MapWrapper
{
	static final private boolean showEmptyBuildMessage = true;

	static final private String ATTR_JOB_LIST = "ATTR_JOB_LIST";
	static final private String ATTR_PROCESSING_JOB = "ATTR_PROCESSING_JOB";
	private Colony mKolonie;

	public BuildQueue(Map data, Colony kolonie)
	{
		super(data);
		mKolonie = kolonie;
	}

	public void investProduction(double production)
	{
		BuildJobAbstract currentJob = getCurrentBuildJob();
		if(currentJob != null)
		{
			if(currentJob.isCompleted())
			{
				if(currentJob.showMessageWhenCompleted())
				{
					Main.instance().getGUI().promtMessage("Bau abgeschlossen",
							"Kolonie " + mKolonie.toString() + " hat " + currentJob.getName() + " fertiggestellt.", GUI.MSG_PRIORITY_NORMAL, new Runnable()
							{

								public void run()
								{
									Main.instance().getGUI().showColony(mKolonie);
								}
							});
				}
				startNextBuildjob();
				currentJob = getCurrentBuildJob();
			} else
			{
				if((currentJob instanceof IdleBuildJob) && getJobQueueSize() > 0)
				{
					/*
					 * Idle Jobs jedes Mal beenden
					 */
					currentJob.setCompleted();
					startNextBuildjob();
					currentJob = getCurrentBuildJob();
				}
			}
		} else
		{
			startNextBuildjob();
			currentJob = getCurrentBuildJob();
		}
		currentJob.proceedBuild(mKolonie, production);
	}

	public int getJobQueueSize()
	{
		LinkedHashMap queue = getJobListRaw();
		if(queue == null) return 0;
		return queue.size();
	}

	/**
	 * Veranlasst, dass dieser Bauauftrag sofort beendet wird
	 * 
	 * @param colony
	 */
	public void buyCurrentJob()
	{
		BuildJobAbstract job = getCurrentBuildJob();
		if(job == null) return;
		double money = Main.instance().getMOUDB().getCivilizationDB().getMoney();
		double price = job.getBuyPrice();
		if(money < price) return;
		Main.instance().getMOUDB().getCivilizationDB().addMoney(-job.getBuyPrice());
		job.completeBuild(mKolonie);
		job.setCompleted();
		startNextBuildjob();
	}

	public void cancelCurrentBuildJob()
	{
		BuildJobAbstract job = getCurrentBuildJob();
		if(job != null)
		{
			job.cancelBuild(mKolonie);
			job.setCompleted();
		}
		startNextBuildjob();
	}

	private void startNextBuildjob()
	{
		BuildJobAbstract job = getFirstItem();
		if(job == null)
		{
			if(showEmptyBuildMessage)
			{
				Main.instance().getGUI().promtMessage("Bau abgeschlossen",
						"Kolonie " + mKolonie.toString() + " hat alle Bauaufträge abgeschlossen.", GUI.MSG_PRIORITY_NORMAL, new Runnable()
						{

							public void run()
							{
								Main.instance().getGUI().showColony(mKolonie);
							}
						});
			}

			job = new IdleBuildJob();
		}
		if(job.isCompleted())
		{
			removeFromBuildQueue(job.getID());
			startNextBuildjob();
			return;
		}
		BuildAllowed al = job.startBuild(mKolonie);
		if(al.isAllowed())
		{
			removeFromBuildQueue(job.getID());
			setCurrentBuildJob(job);
		} else
		{
			Main.instance().getGUI().promtMessage("Kann nicht bauen",
					"Kolonie " + mKolonie.toString() + " kann ihren Bauauftrag "+job.getName()+" nicht ausführen. " + al.getComment(), GUI.MSG_PRIORITY_URGENT, new Runnable()
					{

						public void run()
						{
							Main.instance().getGUI().showColony(mKolonie);
						}
					});
			removeFromBuildQueue(job.getID());
			startNextBuildjob();
		}
	}

	public BuildJobAbstract getCurrentBuildJob()
	{
		BuildJobAbstract ret = null;
		Map data = (Map) getAttribute(ATTR_PROCESSING_JOB);
		if(data != null) ret = BuildJobAbstract.constructBuildQueueItem(data);
		return ret;
	}

	public void setCurrentBuildJob(BuildJobAbstract job)
	{
		setAttribute(ATTR_PROCESSING_JOB, job.getObjectData());
		mKolonie.fireColonyChangedEvent();
	}

	public void addToBuildQueue(BuildJobAbstract item)
	{
		LinkedHashMap<ID, Map> listData = getJobListRaw();
		if(listData == null)
		{
			listData = new LinkedHashMap<ID, Map>();
			setAttribute(ATTR_JOB_LIST, listData);
		}
		listData.put(item.getID(), item.getObjectData());
		mKolonie.fireColonyChangedEvent();
	}

	/**
	 * Liefert View auf interne Liste der Bauaufträge. Diese Liste zu ändern bringt nichts. Um die
	 * Aufträge hinzufügen oder zu entfernen die entsprechende Methoden benutzen
	 * 
	 * @return
	 */
	public List<BuildJobAbstract> getJobList()
	{
		LinkedHashMap<ID, Map> listData = getJobListRaw();
		if(listData == null || listData.isEmpty()) return Collections.EMPTY_LIST;
		List<BuildJobAbstract> ret = new ArrayList<BuildJobAbstract>(listData.size());
		for(Map data : listData.values())
			ret.add(BuildJobAbstract.constructBuildQueueItem(data));
		return ret;
	}

	private LinkedHashMap<ID, Map> getJobListRaw()
	{
		LinkedHashMap<ID, Map> listData = (LinkedHashMap<ID, Map>) getAttribute(ATTR_JOB_LIST);
		return listData;
	}

	// public BuildJobAbstract getFromBuildQueue(ID id)
	// {
	// BuildJobAbstract ret = null;
	// Map data = null;
	// synchronized(mData)
	// {
	// data = (Map)mData.get(id);
	// if(data == null) return null;
	// }
	// ret = BuildJobAbstract.constructBuildQueueItem(data);
	// return ret;
	// }
	//
	public void removeFromBuildQueue(ID id)
	{
		LinkedHashMap<ID, Map> listData = getJobListRaw();
		if(listData == null || listData.isEmpty()) return;
		listData.remove(id);
		mKolonie.fireColonyChangedEvent();
	}
	
	//Löscht alle in der BuildQueue befindlichen Aufträge
	public void clearBuildQueue()
	{
		setAttribute(ATTR_JOB_LIST, null);
		mKolonie.fireColonyChangedEvent();
	}

	public BuildJobAbstract getFirstItem()
	{
		LinkedHashMap<ID, Map> listData = getJobListRaw();
		if(listData == null || listData.isEmpty()) return null;
		Iterator iter = listData.values().iterator();
		if(iter.hasNext())
			return BuildJobAbstract.constructBuildQueueItem((Map) iter.next());
		else
			return null;
	}
}
