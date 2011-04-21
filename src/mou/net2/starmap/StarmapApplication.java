/*
 * $Id$
 * Created on Jul 1, 2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.net2.starmap;

import java.awt.Point;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServer;
import org.jboss.remoting.InvocationRequest;
import org.jboss.remoting.ServerInvoker;
import org.jboss.remoting.callback.InvokerCallbackHandler;
import mou.Main;
import mou.Modul;
import mou.Subsystem;
import mou.core.civilization.CivilizationDB;
import mou.core.colony.Colony;
import mou.core.colony.ColonyDB;
import mou.core.colony.ForeignColonyDB;
import mou.core.db.DBEventListener;
import mou.core.db.DBObjectImpl;
import mou.core.db.ObjectChangedEvent;
import mou.core.ship.FremdeSchiffeDB;
import mou.core.ship.FremdeSchiffeInfo;
import mou.core.ship.Ship;
import mou.core.ship.ShipDB;
import mou.core.starmap.PositionChangedEvent;
import mou.core.starmap.PositionChangedEventListener;
import mou.net2.NetSubsystem;
import mou.net2.PeerHandle;
import mou.net2.rpc.RpcApplication;
import mou.net2.rpc.RpcException;
import mou.net2.starmap.msg.PutColonyData;
import mou.net2.starmap.msg.PutShipData;
import mou.net2.starmap.msg.RemoveColonyData;
import mou.net2.starmap.msg.RemoveShipData;
import mou.net2.starmap.msg.StarmapMsg;
import mou.net2.subscribe.Topic;
import mou.net2.subscribe.TopicListener;

public class StarmapApplication extends Modul	implements TopicListener
{
	static final private long SUBMIT_POS_DATA_DELAY = 1000;

	private ForeignColonyDB fremdeKolonieDB;
	private FremdeSchiffeDB fremdeSchiffeDB;
	private ColonyDB colonyDB;
	private CivilizationDB civDB;
	private ShipDB shipDB;
	private ShipDB rebelShipDB;
	private NetSubsystem netSubsystem;
	private Hashtable<Topic,Point> topicToPoint = new Hashtable<Topic,Point>();
	private HashSet<Point> sendingShipData = new HashSet<Point>();

	public StarmapApplication(Subsystem parent)
	{
		super(parent);
	}

	@Override
	public String getModulName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	protected void shutdownIntern()
	{
	}

	@Override
	protected File getPreferencesFile()
	{
		return null;
	}

	@Override
	protected void startModulIntern() throws Exception
	{
		fremdeKolonieDB = Main.instance().getMOUDB().getFremdeKolonienDB();
		fremdeSchiffeDB = Main.instance().getMOUDB().getFremdeSchiffeDB();
		colonyDB = Main.instance().getMOUDB().getKolonieDB();
		civDB = Main.instance().getMOUDB().getCivilizationDB();
		shipDB = Main.instance().getMOUDB().getShipDB();
		rebelShipDB = Main.instance().getMOUDB().getRebelShipDB();
		netSubsystem = Main.instance().getNetSubsystem();
		colonyDB.addDBEventListener(new DBEventListener()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.core.DBEventListener#objectRemoved(mou.core.DBObjectImpl)
			 */
			public void objectRemoved(DBObjectImpl obj)
			{
				Colony col = (Colony) obj;
				sendColonyData(col.getPosition());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see mou.core.DBEventListener#objectAdded(mou.core.DBObjectImpl)
			 */
			public void objectAdded(DBObjectImpl obj)
			{
				Colony col = (Colony) obj;
				sendColonyData(col.getPosition());
			}

			public void objectChanged(ObjectChangedEvent event)
			{
				/*
				 * Populations?nderungen erstmal ignorieren, weil mit dem Schiffsbau wird auch
				 * Population ge?ndert (Schiffscrew).
				 */
				// if(!event.getAttrName().equals(Colony.ATTR_POPULATION))return;
				// Colony col = (Colony)event.getDbObject();
				// sendMyDataToNetForPos(col.getPosition());
			}
		});
		shipDB.addDBEventListener(new DBEventListener()
		{

			public void objectRemoved(DBObjectImpl obj)
			{
				Ship ship = (Ship)obj;
				sendShipData(ship.getPosition());
			}

			public void objectAdded(DBObjectImpl obj)
			{
				Ship ship = (Ship)obj;
				sendShipData(ship.getPosition());
			}

			public void objectChanged(ObjectChangedEvent event)
			{
			}
		});
		/*
		 * Rebellische Schiffe werden als ganz normale Zivilisationsschiffe für andere Spieler
		 * sichbar
		 */
		rebelShipDB.addDBEventListener(new DBEventListener()
		{

			public void objectRemoved(DBObjectImpl obj)
			{
				Ship ship = (Ship)obj;
				sendShipData(ship.getPosition());
			}

			public void objectAdded(DBObjectImpl obj)
			{
				Ship ship = (Ship)obj;
				sendShipData(ship.getPosition());
			}


			public void objectChanged(ObjectChangedEvent event)
			{
			}
		});
		shipDB.addPositionChangedEventListener(new PositionChangedEventListener()
		{

			public void positionChanged(PositionChangedEvent ev)
			{
				if(ev.getNewPosition() != null) sendShipData(ev.getNewPosition());
				if(ev.getOldPosition() !=null) sendShipData(ev.getOldPosition());
			}
		});

		/*
		 * Jetzt fuer alle die Positionen subscriben wo sich eigene Schiffe oder Kolonien
		 * befinden
		 */
		Set<Point> points = shipDB.getPositionsWithObjects();
		for(Point pos : points)
		{
			sendShipData(pos);
		}
		points = rebelShipDB.getPositionsWithObjects();
		for(Point pos : points)
		{
			sendShipData(pos);
		}
		points = colonyDB.getPositionsWithObjects();
		for(Point pos : points)
		{
			sendColonyData(pos);
		}

	}

	/**
	 * Methode testet ob bei dieser Position sich noch irgendwelche eigene Kolonien
	 * oder Schiffe befinden. Wenn nicht dann wird unsubscribed
	 * @param pos
	 */
	private void checkUnsubscribeForPosition(Point pos)
	{
		if(shipDB.hasObjectsAtPosition(pos) || rebelShipDB.hasObjectsAtPosition(pos)
				|| colonyDB.hasObjectsAtPosition(pos))return;
		fremdeSchiffeDB.removeDataForPos(pos);
		Topic topic = createTopicForPosition(pos);
		topicToPoint.remove(topic);
		netSubsystem.getSubscribeModul().leaveTopic(topic);
	}

	private void sendShipData(final Point pos)
	{
		/*
		 * Damit Daten nicht fuer jeden bewegten Schiff gesended werden
		 * wird an dieser Stelle vom ersten Aufruf bis zum Senden gewisse Sammelpause angelegt.
		 */
		synchronized(sendingShipData)
		{	
			if(sendingShipData.contains(pos))return;
			getLogger().fine("Starting send sequence for sending ship data at position "+pos);
			sendingShipData.add(pos);
			Main.instance().executeDelayed(new Runnable()
			{
				public void run()
				{
					sendShipDataPhase2(pos);
				}
			},SUBMIT_POS_DATA_DELAY);
		}
	}

	private void sendShipDataPhase2(Point pos)
	{
		getLogger().fine("Sending ship data for position "+pos);
		synchronized(sendingShipData)
		{
			sendingShipData.remove(pos);
		}
		StarmapMsg msg = null;
		List<Ship> ships = shipDB.getShipsInStarsystem(pos);
		ships.addAll(rebelShipDB.getShipsInStarsystem(pos));
		long totalMass = 0;
		Topic topic = createTopicForPosition(pos);
		for(Ship ship : ships)totalMass += ship.getMasse();
		if(ships.isEmpty())
		{
			msg = new RemoveShipData();
			msg.setCivId(Main.instance().getClientSerNumber());
			msg.setPosition(pos);
		}else
		{
			netSubsystem.getSubscribeModul().joinTopic(topic, this);
			topicToPoint.put(topic, pos);
			PutShipData m = new PutShipData();
			m.setCivId(Main.instance().getClientSerNumber());
			m.setPosition(pos);
			m.setShipQuantity(ships.size());
			m.setTotalMass(totalMass);
			msg = m;
		}
		netSubsystem.getSubscribeModul().postMessage(topic, msg);
		checkUnsubscribeForPosition(pos);
	}

	private void sendColonyData(Point pos)
	{
		checkUnsubscribeForPosition(pos);
		StarmapMsg msg = null;
		Colony col = colonyDB.getColonyAt(pos);
		Topic topic = createTopicForPosition(pos);
		if(col == null)
		{
			msg = new RemoveColonyData();
			msg.setPosition(pos);
			msg.setCivId(Main.instance().getClientSerNumber());
			getLogger().fine("Sending RemoveColonyData for position"+pos);
		}else
		{
			PutColonyData m = new PutColonyData();
			m.setPosition(pos);
			m.setCivId(Main.instance().getClientSerNumber());
			m.setPopulation(col.getPopulation().intValue());
			getLogger().fine("Sending PutColonyData for position"+pos);
			msg = m;
		}
		netSubsystem.getSubscribeModul().postMessage(topic, msg);
		checkUnsubscribeForPosition(pos);
	}

	public void peerJoined(PeerHandle handle, Topic topic)
	{
		//vorerst uninteressant
	}

	private boolean isPosEmpty(Point pos)
	{
		return !shipDB.hasObjectsAtPosition(pos) && !colonyDB.hasObjectsAtPosition(pos);
	}

	public void setMBeanServer(MBeanServer server)
	{
	}

	public void setInvoker(ServerInvoker invoker)
	{
	}

	public void addListener(InvokerCallbackHandler callbackHandler)
	{
	}

	public void removeListener(InvokerCallbackHandler callbackHandler)
	{
	}

	private Topic createTopicForPosition(Point pos)
	{
		return new Topic("Starmap", NetSubsystem.buildId(pos));
	}

	public void receiveMessage(PeerHandle source, Topic topic, Object param)
	{
		if(param instanceof PutShipData)
		{
			getLogger().info("PutShipData");
			PutShipData msg = (PutShipData)param;
			if(!msg.getCivId().equals(netSubsystem.getLocalHandle().getId()))
			{
				fremdeSchiffeDB.putObject(msg.getPosition(), new FremdeSchiffeInfo(
						msg.getCivId().longValue(),msg.getShipQuantity(),msg.getTotalMass()));
			}
		}
		else if(param instanceof RemoveShipData)
		{
			getLogger().info("RemoveShipData");
			RemoveShipData msg = (RemoveShipData)param;
			fremdeSchiffeDB.removeObjecteForCiv(msg.getPosition(), msg.getCivId().longValue());
		}
		else if(param instanceof PutColonyData)
		{
			getLogger().info("PutColonyData");
			PutColonyData msg = (PutColonyData)param;
			if(!msg.getCivId().equals(netSubsystem.getLocalHandle().getId()))
			{
				fremdeKolonieDB.putKolonie(msg.getPosition(),msg);
			}
		}
		else if(param instanceof RemoveColonyData)
		{
			getLogger().info("RemoveColonyData");
			RemoveColonyData msg = (RemoveColonyData)param;
			fremdeKolonieDB.removeKolonieForCiv(msg.getPosition(), msg.getCivId());
		}
	}
}
