/*
 * $Id: DBObjectImpl.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul
 * Burlov 2001-2006
 */
package mou.core.db;

import java.util.Iterator;
import java.util.Map;
import mou.Main;
import mou.core.MOUDB;
import mou.storage.ser.ID;

/**
 * Superklasse aller DBObjekten. Klasse ist Thread-sicher, braucht also keine weitere externe
 * Synchronisierung wenn es nur um einfachen Schreib- und Lesezugriffen auf Attributen geht. Um die
 * Transaktionen zu realisieren, die kopmlexere Folgen der Schreib- und/oder Lesezugriffen haben
 * wird die Methode getLockObject() geboten.
 */
public class DBObjectImpl
		implements Comparable
{

	static final public Double ZERO_DOUBLE = new Double(0);
	static final public String ATTR_ID = "id";
	static final public String ATTR_TIMESTAMP = "ATTR_TIMESTAMP";
	// static final public String ATTR_CREATE_TIME = "ATTR_CREATE_TIME";
	private Map _data;
	/**
	 * Member-Variable enthält Referenz zu dem entsprechenden DB-Objekt, der für Objekte der
	 * abgeleiteten DB-Klassen zuständig ist. Da DB-Referenz transient ist muss sie beim Empfang der
	 * DBObjektImpl-Objekten mit Referenz des lokal gültigen DB-Objektes initialisiert werden
	 */
	transient private AbstractDB _db;

	/**
	 * Datenhaltungsobject für diesen DBObject. Abgeleitete Klasse dürfen Ihre Daten nur hier
	 * aufbewahren, das heisst KEINE Membervariablen weder hier noch in abgeleiteten Klassen!!!!!
	 */
	// private Map data;
	/**
	 * Dieser Konstruktor wird nur für Reflektion API verwendet, um die rohe Hashdata in einem
	 * DBObject zu wrappen. NICHT für Instanziierungen verwenden!!!! Abgeleitete Klassen sollen auch
	 * ein leeren parameterlosen Konstruktor bereitstellen
	 */
	protected DBObjectImpl()
	{
	}

	/**
	 * Konstruktor für erstellung neuen DBObjecte mit neuen Daten Nach dem volstänndigen
	 * Initialisieren der Attribute, und NUR dann, insertInDB() aufrufen. Sonst kann es passieren,
	 * dass über einem noch nicht zu Ende initialisierten DBObject die DBListeners benachrichgt
	 * werden.
	 * 
	 * @param db
	 */
	protected DBObjectImpl(Map data)
	{
		initWithData(data);
		setAttribute(ATTR_ID, new ID(), false);
	}

	protected DBObjectImpl(Map data, ID id)
	{
		initWithData(data);
		setAttribute(ATTR_ID, id, false);
	}

	protected void setId(ID id)
	{
		setAttribute(ATTR_ID, id, false);
	}

	// public void setCreateTime(Long time)
	// {
	// setAttribute(ATTR_CREATE_TIME, time, false);
	// }
	//
	// public Long getCreateTime()
	// {
	// return (Long) getAttribute(ATTR_CREATE_TIME);
	// }
	//
	public void initWithData(Map data)
	{
		this._data = data;
	}

	// private void setAttribute(Object key, Object value)
	// {
	// setAttribute(key.toString(), value, false);
	// }
	//
	public Map getObjectData()
	{
		return _data;
	}

	public void insertInDB(AbstractDB db, boolean fireEvent)
	{
		setDB(db);
		if(db != null) db.putData(this, fireEvent);
	}

	public Long getTimestamp()
	{
		return (Long) getAttribute(ATTR_TIMESTAMP);
	}

	/**
	 * Methode setzt die aktuelle MOU-Zeit als Zeitstempel
	 */
	public void setTimestamp()
	{
		setAttribute(ATTR_TIMESTAMP, new Long(Main.instance().getTime()), false);
	}

	public Object getLockObject()
	{
		return getMOUDB().getLockObject();
	}

	public ID getID()
	{
		return (ID) getAttribute(ATTR_ID);
	}

	/**
	 * Liefert den Attributenwert, wenn er exisitiert. Methode ist intern auf Attributdatenspeicher
	 * synchronisiert wie auch andere Methoden, die in einer oder anderer Weise auf Attributwerte
	 * zugreifen. (getAttribute(..),setAttribute(..),removeAttribute(..),hasAttribute(..))
	 */
	public Object getAttribute(String name)
	{
		synchronized(getLockObject())
		{
			Object ret = getObjectData().get(name);
			return ret;
		}
	}

	public Object getAttribute(String name, Object lazyValue)
	{
		Object ret = getAttribute(name);
		if(ret == null) ret = lazyValue;
		return ret;
	}

	/**
	 * Versucht den gewünschten Attribut zu geben. Wenn Attribut null, dann wird LazyValue
	 * zurücgegeben ohne ihn in dem DBObjekt zu speichern.
	 * 
	 * @param name
	 * @param lazyValue
	 * @return
	 */
	public Object getAttributLazy(String name, Object lazyValue)
	{
		synchronized(getLockObject())
		{
			Object ret = getObjectData().get(name);
			if(ret == null) ret = lazyValue;
			return ret;
		}
	}

	/**
	 * Setzt den Attributenwert. Methode ist intern auf Attributdatenspeicher synchronisiert wie
	 * auch andere Methoden, die in einer oder anderer Weise auf Attributwerte zugreifen.
	 * (getAttribute(..),setAttribute(..),removeAttribute(..),hasAttribute(..))
	 */
	public void setAttribute(String name, Object value, boolean fireChangeEvent)
	{
		synchronized(getLockObject())
		{
			if(value == null)
			{
				removeAttribute(name, fireChangeEvent);
				return;
			}
			if(!ATTR_TIMESTAMP.equals(name)) setTimestamp(); // Für
			// diese
			// Attribute
			// keine
			// Timestampaktualisierungen
			if(ATTR_ID.equals(name))
			{// Für IDs keine weitere Aktionen, nur Object in Hashtable
				// reinschieben
				// weil mit noch nicht initialisierten IDs kann man keine
				// weitere
				// Operationen asführen
				getObjectData().put(name, value);
				return;
			}
			Object oldValue = getObjectData().get(name);
			/*
			 * Bei sich nicht geänderten Werten nichts tun
			 */
			if(oldValue != null && oldValue.equals(value)) return;
			getObjectData().put(name, value);
			AbstractDB db = getDB();
			if(db != null && fireChangeEvent) db.dataChanged(this, name, oldValue, value);
		}
	}

	// /**
	// * Auslöst einen allgemeinen ChangeEvent
	// *
	// */
	// public void fireEmptyChangeEvent()
	// {
	// AbstractDB db = getDB();
	// if(db == null) return;
	// db.dataChanged(new DataChangedEvent(this,"empty",null, null));
	//	    
	// }
	/**
	 * Entfernt ein Attribut Methode ist intern auf Attributdatenspeicher synchronisiert wie auch
	 * andere Methoden, die in einer oder anderer Weise auf Attributwerte zugreifen.
	 * (getAttribute(..),setAttribute(..),removeAttribute(..),hasAttribute(..))
	 * 
	 * @param name
	 */
	protected void removeAttribute(String name, boolean fireChangeEvent)
	{
		synchronized(getLockObject())
		{
			Object oldValue = getAttribute(name);
			getObjectData().remove(name);
			AbstractDB db = getDB();
			if(db != null && oldValue != null && fireChangeEvent) db.dataChanged(this, name, oldValue, null);
		}
	}

	/**
	 * Abfragt ob ein Attribute exisitiert Methode ist intern auf Attributdatenspeicher
	 * synchronisiert wie auch andere Methoden, die in einer oder anderer Weise auf Attributwerte
	 * zugreifen. (getAttribute(..),setAttribute(..),removeAttribute(..),hasAttribute(..))
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasAttribute(String name)
	{
		synchronized(getLockObject())
		{
			boolean ret = false;
			Iterator iter = getObjectData().keySet().iterator();
			while(iter.hasNext())
			{
				if(iter.next().equals(name)) ret = true;
			}
			return ret;
		}
	}

	public void setDB(AbstractDB db)
	{
		_db = db;
	}

	public AbstractDB getDB()
	{
		return _db;
	}

	// /**
	// * Wird von der abgeleiteten Klassen aufgerufen, wenn ihre interne Daten
	// geändert wurden
	// * Und als Folge werden dann alle Listeners über diese Änderung informiert
	// */
	// protected void dataChanged()
	// {
	// AbstractDB db = getDB();
	// if(db != null)db.dataChanged(this);
	// }
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DBObjectImpl)) return false;
		return ((DBObjectImpl) obj).getID().equals(getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		DBObjectImpl dbO = (DBObjectImpl) o;
		if(equals(o)) return 0;
		return getID().compareTo(dbO.getID());
	}

	public int hashCode()
	{
		return getID().hashCode();
	}

	public MOUDB getMOUDB()
	{
		return Main.instance().getMOUDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mou.db.DBObject#computeDataAge()
	 */
	public long computeDataAge()
	{
		return Main.instance().getTime() - getTimestamp().longValue();
	}
}