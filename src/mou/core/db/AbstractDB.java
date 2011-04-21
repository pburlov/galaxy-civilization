/*
 * $Id: AbstractDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mou.Main;
import mou.core.MOUDB;
import mou.storage.ser.ID;
import burlov.collections.Index;

/**
 * Superclasse aller xxxDB-Klassen
 */
abstract public class AbstractDB
{

	static final private Integer PRIMARY_DATA = new Integer(1);
	static final private Integer SECONDARY_DATA = new Integer(2);
	private Collection<DBEventListener> dbEventListeners = new ArrayList<DBEventListener>();
	// private Collection<ObjectChangedEventListener> objectChangedEventListeners = new
	// ArrayList<ObjectChangedEventListener>();
	/*
	 * Root Hashtable die alle andere Daten enthält wie Metainformationen und Hashtabelle für
	 * Datenobjekte selbst
	 */
	private Map<Object, Object> allData = new Hashtable<Object, Object>();
	/*
	 * Da die DB-Objekte nicht selbst deserialisiert werden dürfen, werden nur ihre Daten in Form
	 * von Hashtable (de)serelialisiert. Alle primäre daten werden hier gehalten. Abgeleitete
	 * Klassen dürfen hier nicht direkt rumpfuschen.
	 */
	private Map<ID, Map> primaryData = new Hashtable<ID, Map>();
	/*
	 * Hashtabelle für frei formatierte Einträge. Abgeleitete Klassen dürfen Ihre spezifische
	 * Informationen hier reinschreiben.
	 */
	private Map<Object, Object> secondaryData = new Hashtable<Object, Object>();
	/*
	 * Key: Attributname(String), Value: Index
	 */
	private Map<String, Index> indexes = new Hashtable<String, Index>();

	/**
	 * @param data
	 *            Eigentliche Datenspeicherungs-Objekt. Wird von MOUDB zugewiesen
	 */
	public AbstractDB(Map<Object, Object> data)
	{
		if(data == null) data = new Hashtable<Object, Object>();
		allData = data;
		primaryData = (Map<ID, Map>) allData.get(PRIMARY_DATA);
		if(primaryData == null)
		{
			primaryData = new HashMap<ID, Map>();
			allData.put(PRIMARY_DATA, primaryData);
		}
		secondaryData = (Map<Object, Object>) allData.get(SECONDARY_DATA);
		if(secondaryData == null)
		{
			secondaryData = new HashMap<Object, Object>();
			allData.put(SECONDARY_DATA, secondaryData);
		}
	}

	public Object getLockObject()
	{
		return getMOUDB().getLockObject();
	}

	// protected void lockDB()
	// {
	// getMOUDB().lockDB();
	// }
	//	
	// protected void unlockDB()
	// {
	// getMOUDB().unlockDB();
	// }
	//	
	/**
	 * Liefert ein unique String der zur Identifizierung der Datenbank dient
	 * 
	 * @return
	 */
	abstract public String getDBName();

	/**
	 * Erzeugt eine Index-Struktur mit Werten von dem benanntem Attribut als Key
	 * 
	 * @param attrName
	 */
	private Index createIndexForAttribute(String attrName)
	{
		if(DBObjectImpl.ATTR_ID.equals(attrName)) throw new RuntimeException("Darf kein Index für IDs erstellen!");
		Index index = new Index();// Index für Attribut
		indexes.put(attrName, index);
		Iterator iter = getAllDBObjects().values().iterator();
		while(iter.hasNext())
		{
			DBObjectImpl dbOb = (DBObjectImpl) iter.next();
			Object attrValue = dbOb.getAttribute(attrName);
			if(attrValue == null) continue; // Wenn Object überhaupt keine
			// solche Attribute hat.
			index.put(attrValue, dbOb.getID());
		}
		return index;
	}

	/**
	 * Liefert Object, wo alle Daten der DB gehalten werden
	 * 
	 * @return
	 */
	protected Map getDBData()
	{
		return allData;
	}

	private Map getPrimaryHashData()
	{
		return primaryData;
	}

	protected Map<Object, Object> getSecondaryMapData()
	{
		return secondaryData;
	}

	/**
	 * Liefert Anzahl der Datensätze in primären Datenspeicher
	 * 
	 * @return
	 */
	public int getDBSize()
	{
		return getPrimaryHashData().size();
	}

	/**
	 * Liefert ein Object aus der sekundäre Datenspeicher. Wenn unter dem angesprochenem Schlüssel
	 * kein Object gespeicher ist, dann wird der mitgegebe default Object unter diesem Schlüssel
	 * gespeichert und zurückgegeben.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	protected Object getFromSecondaryHashData(Object key, Object defaultValue)
	{
		synchronized(getLockObject())
		{
			Object ret = getSecondaryMapData().get(key);
			if(ret == null && defaultValue != null)
			{
				ret = defaultValue;
				getSecondaryMapData().put(key, defaultValue);
			}
			return ret;
		}
	}

	/*
	 * private Map getIndexes() { return indexes; }
	 */
	protected void removeData(ID key)
	{
		if(key == null) return;
		getMOUDB().getLogger().fine("Lösche Datensatz mit ID: " + key.toString());
		synchronized(getLockObject())
		{
			DBObjectImpl obj = getData(key);
			if(obj != null)
			{
				removeFromIndexes(obj);
				getPrimaryHashData().remove(key);
				fireObjectRemovedEvent(obj);
			}
		}
	}

	private void removeFromIndexes(DBObjectImpl obj)
	{
		Iterator iter = indexes.keySet().iterator();
		while(iter.hasNext())
		{// Aus allen existierenden Indexen die zu löschende ID entfernen
			String attr = (String) iter.next();
			Index index = (Index) indexes.get(attr);
			index.remove(obj.getID());// ID des gelöschten Objektes aus Index
			// entfernen
		}
	}

	// /**
	// * Aktualisiert einen Datensatz mit der mitgegebener ID mit den mitgegebenen
	// * Daten. Wenn keinen Datensatz mit solcher ID gefunden wird, dann passiert
	// * nichts.
	// *
	// * @param id
	// * @param objectData
	// */
	// protected void updateData(ID id, Map objectData, boolean fireChangeEvent)
	// {
	// synchronized(getLockObject())
	// {
	// DBObjectImpl obj = getData(id);
	// if(obj != null)
	// {
	// obj.initWithData(objectData);
	// getPrimaryHashData().put(id, objectData);
	// insertInIndexes(obj);
	// if(fireChangeEvent) dataChanged(obj);
	// }
	// }
	// }
	/**
	 * Setzt Daten in den internen Datenspeicher. Nur Daten die mit dieser Methode gesetzt werden,
	 * werden auch dauerhaft auf der Platte gespeichert
	 */
	protected void putData(DBObjectImpl data, boolean fireEvent)
	{
		synchronized(getLockObject())
		{
			data.setDB(this);
			DBObjectImpl implObj = (DBObjectImpl) data;
			ID id = implObj.getID();
			// /*
			// * neue ID immer setzten
			// */
			// id = new ID();
			// implObj.setAttribute(DBObjectImpl.ATTR_ID, id,false);
			if(id == null)
			{
				id = new ID();
				implObj.setAttribute(DBObjectImpl.ATTR_ID, id, false);
			}
			data.setTimestamp();
			Map hash = implObj.getObjectData();
			getPrimaryHashData().put(id, hash);// nur Die Datenessenz speichern,
			// nicht den DBObject selbst
			insertInIndexes(implObj);
			if(fireEvent)
			{
				fireObjectAddedEvent(implObj);
			}
		}
	}

	/**
	 * Setzt Daten in den internen Datenspeicher. Nur Daten die mit dieser Methode gesetzt
	 * wersynchronized uch dauerhaft auf der Platte gespeichert
	 */
	protected void putData(DBObjectImpl data)
	{
		putData(data, true);
	}

	/**
	 * Fügt neuzugekommen Object in die existierende Index-Strukturen
	 * 
	 * @param obj
	 */
	private void insertInIndexes(DBObjectImpl newObj)
	{
		Iterator iter = indexes.keySet().iterator();
		while(iter.hasNext())
		{// Alle zu dem Zeitpunkt exisitierende Indexe durchgehen
			String attrName = (String) iter.next();
			Object attrValue = newObj.getAttribute(attrName);
			Index index = (Index) indexes.get(attrName);
			index.put(attrValue, newObj.getID());
		}
	}

	/**
	 * Methode sucht nach DBObject die einen bestimmten Wert in einem Attribut haben.
	 * 
	 * @param attrName
	 *            name des Attributes
	 * @param value
	 *            gesuchten Wert
	 * @return DBQueryResult mit DBObjekten
	 */
	protected DBQueryResult getDataWhere(String attrName, Object value)
	{
		HashSet set = new HashSet(1);
		set.add(value);
		return getDataWhere(attrName, set);
	}

	protected DBQueryResult getDataWhere(String attrName, Set values)
	{
		synchronized(getLockObject())
		{
			List resList = new ArrayList();// Liste der zur Anfrage passenden
			// Objecte
			if(DBObjectImpl.ATTR_ID.equals(attrName))
			{// Wenn gesuchte Attribut ID ist, dann erübrigt sich die Indexsuche
				Iterator iter = values.iterator();
				while(iter.hasNext())
				{
					ID id = (ID) iter.next();
					DBObjectImpl obj = getData(id);
					resList.add(obj);
				}
				return new DBQueryResult(resList);
			}
			Iterator iter = values.iterator();
			while(iter.hasNext())
			{
				Object val = iter.next();
				Collection refList = getIDsWhere(attrName, val);
				if(refList != null)
				{
					Iterator iter2 = refList.iterator();
					while(iter2.hasNext())
					{
						ID dbID = (ID) iter2.next();
						DBObjectImpl dbObj = null;
						if(dbID != null) dbObj = getData(dbID);
						if(dbObj != null && dbObj.getAttribute(attrName).equals(val))
						{
							resList.add(dbObj);
						} else
						{// Referenz entfernen wenn indexierte Wert wurde
							// geändert
							// und
							// passt nicht mehr zu diesem Index;
							iter2.remove();
						}
					}
				}
			}
			return new DBQueryResult(resList);
		}
	}

	/**
	 * Methode sucht nach DBObject die einen bestimmten Wert in einem Attribut haben
	 * 
	 * @param attrName
	 *            name des Attributes
	 * @param value
	 *            gesuchten Wert
	 * @return Iterator sichere Collection mit ID der vorhandenen DBObjecten
	 */
	protected Set<ID> getIDsWhere(String attrName, Object value)
	{
		synchronized(getLockObject())
		{
			Index index = getIndex(attrName);
			Set ret = index.getValues(value);// Liste mit ID für den gesuchten
			// Wert
			if(ret == null)
				ret = new HashSet(0);
			else
				ret = new HashSet(ret);
			return ret;
		}
	}

	/**
	 * @param attrName
	 * @return
	 */
	private Index getIndex(String attrName)
	{
		Index index = (Index) indexes.get(attrName);// Einen Index für den
		// Attribut
		// geben lassen
		// Wenn noch kein Index exist. dann einen für späteren Gebrauch
		// erstellen
		if(index == null) index = createIndexForAttribute(attrName);
		return index;
	}

	/**
	 * Liefert Anzahl der Datensätze die einen bestimmten wert haben. Diese Methode ist der
	 * effitienteste Weg um die Vorhandensein der bestimmten Datensätze zu erfragen.
	 * 
	 * @param attrName
	 *            name des Attributes
	 * @param value
	 *            gesuchten Wert
	 * @return Anzahl der Datensätze
	 */
	protected int getCountWhere(String attrName, Object value)
	{
		synchronized(getLockObject())
		{
			Index index = getIndex(attrName);
			Set values = index.getValues(value);
			int ret = 0;
			if(values != null) ret = values.size();
			return ret;
		}
	}

	/**
	 * Gibt Daten aus dem internem permanentem Datenspeicher
	 */
	public DBObjectImpl getData(ID key)
	{
		synchronized(getLockObject())
		{
			Map objData = (Map) getPrimaryHashData().get(key);
			if(objData == null) { return null;// throw new
			}
			DBObjectImpl dbObj = createNewDBObject(objData);
			return dbObj;
		}
	}

	private DBObjectImpl createNewDBObject(Map initData)
	{
		DBObjectImpl dbObject;
		Class objClass = getDBObiectImplClass();
		try
		{
			dbObject = (DBObjectImpl) objClass.newInstance();
		} catch(Exception e)
		{
			String msg = "Kann die Instance von " + objClass.getName() + " nicht erstellen!";
			getMOUDB().logThrowable(msg, e);
			throw new RuntimeException(msg, e);
		}
		dbObject.initWithData(initData);
		dbObject.setDB(this);
		return dbObject;
	}

	/**
	 * Liefert alle DBObjectImpl Objecte. Das Ergebniss wird bei jedem Aufruf neu erstellt, und wird
	 * danach nicht mehr von anderen Threads geändert. Es ist also gefahrlos Iteratoren zu
	 * verwenden.
	 * 
	 * @return Map mit Key:ID Value: DBObject
	 */
	protected Map getAllDBObjects()
	{
		synchronized(getLockObject())
		{
			Map res = new HashMap();
			Iterator iter = getPrimaryHashData().keySet().iterator();
			while(iter.hasNext())
			{
				Object obj = iter.next();
				if(!(obj instanceof ID)) continue;
				ID key = (ID) obj;
				DBObjectImpl dbObj = getData(key);
				if(dbObj == null) iter.remove();
				res.put(key, dbObj);
			}
			return res;
		}
	}

	/**
	 * Registiert DBEventListener für einen bestimmten DBObject aus dieser DB
	 * 
	 * @param listener
	 * @param observerdID
	 *            ID des Objektes über dessen Änderungen der Listener informiert wird
	 */
	public void addDBEventListener(DBEventListener listener)
	{
		synchronized(dbEventListeners)
		{
			dbEventListeners.add(listener);
		}
	}

	// public void addObjectChangedEventListener(ObjectChangedEventListener listener)
	// {
	// synchronized(objectChangedEventListeners)
	// {
	// objectChangedEventListeners.add(listener);
	// }
	// }
	private void fireObjectChangedEvent(ObjectChangedEvent event)
	{
		synchronized(dbEventListeners)
		{
			for(DBEventListener listener : dbEventListeners)
				listener.objectChanged(event);
		}
	}

	/**
	 * Entfernt einen registrierten DBEventListener
	 * 
	 * @param listener
	 * @param observerdID
	 */
	public void removeDBEventListener(DBEventListener listener)
	{
		getMOUDB().getLogger().fine("Entferne DBEventListener: " + listener.toString());
		dbEventListeners.remove(listener);
	}

	protected void fireObjectRemovedEvent(DBObjectImpl obj)
	{
		synchronized(dbEventListeners)
		{
			for(DBEventListener listener : dbEventListeners)
				listener.objectRemoved(obj);
		}
	}

	protected void fireObjectAddedEvent(DBObjectImpl obj)
	{
		synchronized(dbEventListeners)
		{
			for(DBEventListener listener : dbEventListeners)
				listener.objectAdded(obj);
		}
	}

	/**
	 * Methode wird aufgerufen wenn Objekte aus dieser DB lokal geändert wurden. Der Aufruf
	 * geschieht normaleweise aus der DBObject-Objekten automatisch, wenn irgendwelche Daten
	 * geändert wurden.
	 */
	protected void dataChanged(DBObjectImpl obj, String propertyName, Object oldValue, Object newValue)
	{
		synchronized(getLockObject())
		{
			insertInIndexes(obj);
			// Listeners über die Änderung informieren
			fireObjectChangedEvent(new ObjectChangedEvent(obj, propertyName, oldValue, newValue));
			// fireDBEvent(new DBEvent(DBEvent.ELEMENT_CHANGED, obj), obj.getID());
		}
	}

	/**
	 * Methode soll das Class-Object liefern, den das abgeleitete DB-Object als Datenhaltungsklass
	 * verwendet.
	 * 
	 * @return
	 */
	abstract protected Class getDBObiectImplClass();

	/**
	 * Methode wird von Netzwerkschicht aufgerufen, wenn ein neues Object von dem entferntem Rechner
	 * kommt. Normaleweise bedeutet das, dass irgendwelche Daten in dem entferntem Rechner verändert
	 * wurden
	 */
	// abstract public void remoteDataChanged(Object data);
	protected MOUDB getMOUDB()
	{
		return Main.instance().getMOUDB();
	}
}