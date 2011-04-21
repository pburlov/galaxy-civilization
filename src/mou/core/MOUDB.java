/*
 * $Id: MOUDB.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright Paul Burlov
 * 2001-2006
 */
package mou.core;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import javax.swing.JOptionPane;
import mou.Main;
import mou.Subsystem;
import mou.core.civilization.Civilization;
import mou.core.civilization.CivilizationDB;
import mou.core.civilization.NaturalRessourceDescriptionDB;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.colony.ColonyDB;
import mou.core.colony.ForeignColonyDB;
import mou.core.db.AbstractDB;
import mou.core.res.natural.NaturalResource;
import mou.core.research.ResearchDB;
import mou.core.security.SecuritySubsystem;
import mou.core.ship.FremdeSchiffeDB;
import mou.core.ship.Ship;
import mou.core.ship.ShipClassDB;
import mou.core.ship.ShipDB;
import mou.core.ship.ShipMovementOrderDB;
import mou.core.starmap.StarmapDB;
import mou.core.trade.TraderDB;
import mou.storage.ser.ID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import burlov.net.UrlCommunicator;

/**
 * Klasse dient als Schnitstelle zwischen der Anwendung und der Datenbank
 */
public class MOUDB extends Subsystem
{

	static final private String STATISTIC_SCRIPT = "submit-statistic.php";
	static final private String LOAD_DB_SCRIPT = "load-db.php";
	static final private String STORE_DB_SCRIPT = "store-db.php";
	static final private String DB_FILE_SUFFIX = ".db";
	static final private String DB_VERSION = "DB_VERSION";
	static final private String DB_SER_NUMBER = "DB_SER_NUMBER";
	static final private String DB_KEY_CIVILIZATION = "CivilizationDB";
	static final private String DB_KEY_KOLONIEN = "KolonieDB";
	static final private String DB_KEY_STARMAP = "StarmapDB";
	static final private String DB_KEY_MAINTENANCE = "MaintenanceDB";
	static final private String DB_KEY_SHIPCLASS = "ShipclassDB";
	static final private String DB_KEY_SHIP = "ShipDB";
	static final private String DB_KEY_REBEL_SHIP = "RebelShipDB";
	static final private String DB_KEY_SHIPMOVEMENT = "DB_KEY_SHIPMOVEMENT";
	static final private String DB_KEY_FREMDE_KOLONIEN = "DB_KEY_FREMDE_KOLONIEN";
	static final private String DB_KEY_STORAGE = "DB_KEY_STORAGE";
	static final private String DB_KEY_TRADE = "DB_KEY_TRADE";
	static final private String DB_KEY_RESEARCH = "DB_KEY_RESEARCH";
	/*
	 * Zentrale Datenspeicherungsobject. Um die Obfuscation zu erm?glichen
	 * werden hier keine Anwendungsspezifische Objekte abgelegt. Alle Daten
	 * werden in Hashtabellen aufbewahrt. Key: Datenname(String) Value: Daten
	 * (Hashtable)
	 */
	private Hashtable database = new Hashtable();
	/**
	 * Zuordnung der DB-Namen zu DB-Objekten Key: Name(String); Value:
	 * AbstractDB
	 */
	private HashMap<String, Object> mapNamesToDB = new HashMap<String, Object>();
	// private ReentrantLock moudbLock = new ReentrantLock();
	private MaintenanceDB maintenanceDB;
	private StarmapDB starmapDB;
	private ColonyDB kolonieDB;
	private CivilizationDB civDB;
	private ShipClassDB shipclassDB;
	private ShipDB shipDB;
	private ShipDB rebelShipDB;
	private NaturalRessourceDescriptionDB ressourceDescriptionDB;
	private ShipMovementOrderDB shipMovementOrderDB;
	private ForeignColonyDB fremdeKolonienDB;
	private FremdeSchiffeDB fremdeSchiffeDB;
	private NaturalRessourcesStorageDB storageDB;
	private TraderDB tradeDB;
	private ResearchDB researchDB;

	/**
	 * Konstruktor ist protected damit man nur eine globale Instanz von dieser
	 * Klasse mit getInstance() bekommt
	 */
	public MOUDB(Main parent)
	{
		super(parent);
	}

	public Object getLockObject()
	{
		return this;
	}

	/**
	 * Liefert bei der Erstellung der Datenbankes gespeichertes Serienummer
	 */
	/*
	 * private Long getDBSerNumber() { return (Long) db.get(DB_SER_NUMBER); }
	 */

	public Integer getDBVersion()
	{
		return (Integer) database.get(DB_VERSION);
	}

	/**
	 * Erstellt einen File Objekt der wocher die Datenbank geladen oder wochin
	 * gespeichert werden soll.
	 */
	private File constructDbFile()
	{
		File file = new File(Main.APPLICATION_USER_DATA_DIR, Long.toHexString(SecuritySubsystem.instance().getSerialNumber()) + DB_FILE_SUFFIX);
		if (Main.isDebugMode()) System.out.println("DB-File: " + file.getAbsolutePath());
		return file;
	}

	/**
	 * Erstellt eine neue Datenbank und initialisiert sie mit allen nötigen
	 * Werten.
	 */
	private Hashtable createNewDB()
	{
		Hashtable ret = new Hashtable();
		ret.put(DB_VERSION, new Integer(Main.VERSION));
		ret.put(DB_SER_NUMBER, new Long(SecuritySubsystem.instance().getSerialNumber()));
		return ret;
	}

	protected void startModulIntern() throws Exception
	{
		try
		{
			database = loadDB();
			if (database == null) database = createNewDB();
		} catch (Exception e)
		{
			logThrowable("Kann die DB-Daten nicht laden: " + e.getLocalizedMessage(), e);
			/*
			 * Nicht normal das Programm runterfahren, sondern direkt mit
			 * System.exit() Sonst wird versucht fehlerhaft geladenen Datenbank
			 * zu speichern.
			 */
			Main.instance().severeErrorOccured(e, "Kann die DB-Daten nicht laden.", false);
			Main.instance().forceExit("Kann die DB-Daten nicht laden. " + e.getLocalizedMessage());
		}
		initDBs();
		/*
		 * Nach der Initialisierung der DBs, prüfen ob eine lokale Zivilisation
		 * schon eingerichtet wurde
		 */
		if (Main.instance().getMOUDB().getCivilizationDB().getMyCivilization() == null)
		{/*
		 * Wenn keine eigene Civilization gefunden wurde, dann neue erzeugen und
		 * mit Anfangswerten initialisieren.
		 */
			createNewCivilization();
		}
	}

	public void resetScore() throws Exception
	{
		database = createNewDB();

		Main.instance().restart();
	}

	private void createNewCivilization() throws Exception
	{
		String name = null;
		boolean ok = false;
		while (!ok)
		{
			/*
			 * Name der Zivilisation abfragen
			 */
			JOptionPane op = new JOptionPane("Name der Zivilisation eingeben (max. 100 Zeichen)", JOptionPane.PLAIN_MESSAGE);
			op.setWantsInput(true);
			op.setOptions(new String[] { "Ok", "Abbrechen" });
			while (name == null || name.trim().length() == 0)
			{
				op.createDialog(null, "Zivilization erstellen").setVisible(true);
				if ("Ok" != op.getValue()) Main.instance().forceExit("Zivilisationsdaten wurden nicht vollständig initialisiert.");
				name = (String) op.getInputValue();
			}
			/*
			 * Länge der Zivilisationsnamen vorsichtshalber begrenzen
			 */
			if (name.length() > 100) name = name.substring(0, 100);
			if (Main.isOnlineMode())
			{
				/*
				 * Prüfen ob der Name schon von jemanden verwendet wird
				 */
				UrlCommunicator com = new UrlCommunicator(Main.GAME_SERVER_URL + "check-civname.php");
				Map<String, String> param = new HashMap<String, String>(2);
				param.put("name", name);
				param.put("id", Long.toString(SecuritySubsystem.instance().getSerialNumber()));
				com.sendCommand(param);
				if (com.getError() != null)
				{
					JOptionPane.showMessageDialog(null, com.getError());
					name = null;
				} else ok = true;
			}else{
				ok = true;
			}
		}
		getCivilizationDB().createMyCivilization(name);
		/*
		 * Startgeld spenden
		 */
		getCivilizationDB().addMoney(1000000);
		/*
		 * Startressourcen spenden
		 */
		ArrayList<NaturalResource> res = new ArrayList<NaturalResource>(NaturalRessourceDescriptionDB.getAllKnownMaterials());
		Set<ID> ids = new HashSet<ID>();
		while (ids.size() < 3)
		{
			/*
			 * 3 Materialien zufällig wählen
			 */
			ids.add(res.get((int) (Math.random() * res.size())).getID());
		}
		for (ID id : ids)
			getStorageDB().addMenge(id, (long) 20E6, true);
		/*
		 * Schiffe erzeugen
		 */
		Ship ship = new Ship();
		ship.setCrew(ColonyDB.STANDARD_COLONY_SIZE);
		ship.setSettler(ColonyDB.STANDARD_COLONY_SIZE);
		ship.setShipClassName("Kolonieschiff");
		ship.setSpeed(5);
		ship.setMasse(3000000);
		ship.setStruktur(3000000d);
		ship.setBuildcost(3000000);
		ID shipClassId = new ID();
		ship.setShipClassID(shipClassId);
		Point pos = getStarmapDB().getRandomStarPosition();
		getShipDB().addNewShip(ship, pos);

		ship = new Ship();
		ship.setCrew(ColonyDB.STANDARD_COLONY_SIZE);
		ship.setSettler(ColonyDB.STANDARD_COLONY_SIZE);
		ship.setShipClassName("Kolonieschiff");
		ship.setSpeed(5);
		ship.setMasse(3000000);
		ship.setBuildcost(3000000);
		ship.setStruktur(3000000d);
		ship.setShipClassID(shipClassId);
		getShipDB().addNewShip(ship, pos);

		ship = new Ship();
		ship.setCrew(10);
		ship.setShipClassName("Erkunder");
		ship.setSpeed(15);
		ship.setMasse(100);
		ship.setStruktur(100d);
		ship.setBuildcost(3000);
		shipClassId = new ID();
		ship.setShipClassID(shipClassId);
		getShipDB().addNewShip(ship, pos);

		ship = new Ship();
		ship.setCrew(10);
		ship.setShipClassName("Erkunder");
		ship.setSpeed(15);
		ship.setMasse(100);
		ship.setStruktur(100d);
		ship.setBuildcost(3000);
		ship.setShipClassID(shipClassId);
		getShipDB().addNewShip(ship, pos);

		ship = new Ship();
		ship.setCrew(1000);
		ship.setShipClassName("Begleitschiff");
		ship.setSpeed(5);
		ship.setMasse(10000);
		ship.setStruktur(10000d);
		ship.setArmor(10000);
		ship.setShild(2000);
		ship.setWeapon(1000);
		ship.setBuildcost(300000);
		shipClassId = new ID();
		ship.setShipClassID(shipClassId);
		getShipDB().addNewShip(ship, pos);

		ship = new Ship();
		ship.setCrew(1000);
		ship.setShipClassName("Begleitschiff");
		ship.setSpeed(5);
		ship.setMasse(10000);
		ship.setStruktur(10000d);
		ship.setArmor(10000);
		ship.setShild(2000);
		ship.setWeapon(1000);
		ship.setBuildcost(300000);
		ship.setShipClassID(shipClassId);
		getShipDB().addNewShip(ship, pos);
		getMaintenaceDB().setStarmapPosition(pos);
	}

	/**
	 * Methode prüft ob eine gerade geladene DB kompartibel mit dem Laufendem
	 * Client ist. Wenn nicht dann wird eine Exception geworfen.
	 * 
	 * @param db
	 */
	public void checkLoadedDb(Hashtable db) throws Exception
	{
		/*
		 * Serialnummern vergleichen
		 */
		Long ser = (Long) db.get(DB_SER_NUMBER);
		if (ser == null) throw new Exception("Die Datenbank ist beschädigt.");
		if (ser.longValue() != SecuritySubsystem.instance().getSerialNumber()) throw new Exception("Falsche Datenbank!");
	}

	private Hashtable loadDBFromServer()
	{
		try
		{
			URL url = new URL(Main.GAME_SERVER_URL + LOAD_DB_SCRIPT + "?password=" + Main.instance().getSecuritySubsystem().getPassword() + "&serial=" + Main.instance().getClientSerNumber());
			URLConnection con = url.openConnection();
			String sdata = IOUtils.toString(new GZIPInputStream(con.getInputStream()), "ASCII");
			if (StringUtils.isBlank(sdata)) return null;
			System.out.println(sdata.length());
			// if(StringUtils.isNotBlank(olddata) &&
			// !olddata.equals(sdata))throw new RuntimeException();
			sdata = sdata.replace('-', '+');
			sdata = sdata.replace('_', '/');
			sdata = sdata.replace('.', '=');

			byte[] buf = Base64.decodeBase64(sdata.getBytes("ASCII"));
			ObjectInputStream oin = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(buf)));
			Hashtable data = (Hashtable) oin.readObject();
			return data;
		} catch (Exception e)
		{
			return null;
		}
	}

	private void storeDBToServer(Hashtable data) throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(new GZIPOutputStream(bout));
		oout.writeObject(data);
		oout.flush();
		oout.close();
		byte[] encodedData = Base64.encodeBase64(bout.toByteArray());
		String base64String = new String(encodedData, "ASCII");
		base64String = base64String.replace('+', '-');
		base64String = base64String.replace('/', '_');
		base64String = base64String.replace('=', '.');
		String encodedString = base64String;
		URL url = new URL(Main.GAME_SERVER_URL + STORE_DB_SCRIPT);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		// con.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// con.setRequestProperty("Content-Encoding", "gzip");
		con.setDoOutput(true);
		con.setDoInput(true);
		bout = new ByteArrayOutputStream();
		OutputStreamWriter textout = new OutputStreamWriter(bout, "ASCII");
		textout.write("password=" + Main.instance().getSecuritySubsystem().getPassword());
		textout.write("&serial=" + Main.instance().getClientSerNumber());
		textout.write("&data=");
		textout.write(encodedString);
		textout.flush();
		encodedData = bout.toByteArray();
		con.setRequestProperty("Content-Length", Integer.toString(encodedData.length));
		con.setFixedLengthStreamingMode(encodedData.length);
		con.getOutputStream().write(encodedData);
		getLogger().fine("Server response: " + con.getResponseMessage());
		CRC32 crc32 = new CRC32();
		crc32.update(encodedString.getBytes());
		long origincrc = crc32.getValue();
		long reccrc = 0;
		try
		{
			String response = IOUtils.toString(con.getInputStream());
			if (StringUtils.isBlank(response)) throw new Exception("No CRC response from server");
			response = response.replaceAll("[^0-9]", "");
			reccrc = Long.parseLong(response);
		} catch (NumberFormatException e)
		{
		}
		if (origincrc != reccrc) throw new Exception("CRCs are not equal ");
	}

	private Hashtable loadDB() throws Exception
	{
		/*
		 * Zuerst versuchen Datenbank vom Server zu laden
		 */
		Hashtable ret = null;
		if (Main.isOnlineMode()) ret = loadDBFromServer();
		if (ret == null)
		{
			/*
			 * Noch keine Daten auf dem Server, vesuche lokale Datan zu laden
			 */
			File file = constructDbFile();
			if (!file.exists())
			{
				getLogger().warning("Keine existierende DB-File gefunden. Erstelle eine neue Datenbank.");
				return null;
			}
			/*
			 * Datenbank von Platte lesen und dekodieren
			 */
			ret = readDBFile(file);
		}
		if (ret != null) checkLoadedDb(ret);
		return ret;
	}

	/**
	 * Methode führt die tatsächliche Leseoperation vom Datei durch. Methode ist
	 * static damit auch andere Klassen (z.B. DBBrowser) die Datenbanken lesen
	 * können.
	 * 
	 * @return
	 * @throws Exception
	 */
	static public Hashtable readDBFile(File file) throws Exception
	{
		JarInputStream jIn = new JarInputStream(new BufferedInputStream(new FileInputStream(file)));
		// Manifest manifest = jIn.getManifest();
		// String serNumber =
		// manifest.getMainAttributes().getValue("Client-Serial-Number");
		// long number = Long.parseLong(serNumber);
		// if(number != SecuritySubsystem.instance().getSerialNumber())throw new
		// Exception("Falsche
		// Serialnummer");
		for (ZipEntry entry = jIn.getNextEntry(); entry != null;)
		{
			if (entry.getName().equals("db.data"))
			{
				ObjectInputStream in = new ObjectInputStream(jIn);
				Hashtable ret = (Hashtable) in.readObject();
				in.close();
				return ret;
			}
		}
		return null;
	}

	/**
	 * Methode führt die tatsächliche Schreiboperation durch. Das Gegenstuck ist
	 * readDBFile(..) Methode. Die Schreiboperation wird mit Hilfe tempraeren
	 * Dateien ausgefuhrt. So wird das transaktionale Verhalten sicherer.
	 * 
	 * @param db
	 * @throws Exception
	 */
	static public void writeDBFile(Hashtable db, File file) throws IOException
	{
		File tmpFile = File.createTempFile("galaxy-civilization", null, file.getParentFile());
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().putValue("Application", Main.APPLICATION_NAME);
		manifest.getMainAttributes().putValue("Application-Version", Long.toString(Main.VERSION));
		manifest.getMainAttributes().putValue("Client-Serial-Number", Main.APPLICATION_NAME);
		ZipEntry entry = new ZipEntry("db.data");
		FileOutputStream fout = new FileOutputStream(tmpFile);
		JarOutputStream jOut = new JarOutputStream(new BufferedOutputStream(fout), manifest);
		jOut.putNextEntry(entry);
		ObjectOutputStream out = new ObjectOutputStream(jOut);
		out.writeObject(db);
		out.flush();
		jOut.finish();
		jOut.close();
		fout.close();
		if (file.exists()) file.delete();
		if (!tmpFile.renameTo(file)) throw new IOException("Cannot rename tmp-file to target file: " + file.getAbsolutePath());
	}

	/**
	 * Initalisiert alle ****DB-Objecte
	 */
	private void initDBs()
	{
		civDB = new CivilizationDB(getDBData(DB_KEY_CIVILIZATION, -1));
		mapNamesToDB.put(civDB.getDBName(), civDB);
		kolonieDB = new ColonyDB(getDBData(DB_KEY_KOLONIEN, -1));
		mapNamesToDB.put(kolonieDB.getDBName(), kolonieDB);
		maintenanceDB = new MaintenanceDB(getDBData(DB_KEY_MAINTENANCE, -1));
		mapNamesToDB.put(maintenanceDB.getDBName(), maintenanceDB);
		ressourceDescriptionDB = new NaturalRessourceDescriptionDB();
		/*
		 * In der Version 15 wurde Format der Schiffsysteme geändert
		 */
		shipclassDB = new ShipClassDB(getDBData(DB_KEY_SHIPCLASS, 15));
		mapNamesToDB.put(shipclassDB.getDBName(), shipclassDB);
		/*
		 * In der Version 9 wurde Format der Schiffe geändert
		 */
		shipDB = new ShipDB(getDBData(DB_KEY_SHIP, 9));
		mapNamesToDB.put(shipDB.getDBName(), shipDB);

		rebelShipDB = new ShipDB(getDBData(DB_KEY_REBEL_SHIP, -1));
		shipMovementOrderDB = new ShipMovementOrderDB(getDBData(DB_KEY_SHIPMOVEMENT, -1));
		mapNamesToDB.put(shipMovementOrderDB.getDBName(), shipMovementOrderDB);
		starmapDB = new StarmapDB(getDBData(DB_KEY_STARMAP, -1));
		mapNamesToDB.put(starmapDB.getDBName(), starmapDB);
		fremdeKolonienDB = new ForeignColonyDB(getDBData(DB_KEY_FREMDE_KOLONIEN, -1));// getDBData(DB_KEY_FREMDE_KOLONIEN));
		mapNamesToDB.put("FremdeKolonienDB", fremdeKolonienDB);
		fremdeSchiffeDB = new FremdeSchiffeDB();// Daten werden beim nächsten
												// Start vom Netz
		// geholt
		mapNamesToDB.put("FremdeSchiffeDB", fremdeSchiffeDB);
		/*
		 * Änderungen in der Rohstofflagerung in Version 15
		 */
		storageDB = new NaturalRessourcesStorageDB(getDBData(DB_KEY_STORAGE, 15));
		mapNamesToDB.put(storageDB.getDBName(), storageDB);
		/*
		 * In der Version 9 wurde Format der Schiffe geändert
		 */
		tradeDB = new TraderDB(getDBData(DB_KEY_TRADE, 9));
		mapNamesToDB.put(tradeDB.getDBName(), tradeDB);
		/*
		 * In der Version 15 wurde Format der Forschungen geändert
		 */
		researchDB = new ResearchDB(getDBData(DB_KEY_RESEARCH, 15));

		/*
		 * In Version 15 hat sich einiges geändert. Deshalb Datenbank auf neue
		 * Version vorbereiten
		 */
		updateToVersion15();
	}

	public void updateToVersion15()
	{
		/*
		 * In Version 15 wurden Gebäudewerte verändert, alle Kolonien
		 * zurücksetzten, um Rebellionen zu verhindern Neue Startressourcen
		 * spendieren (3x20mio Tonnen eines vorhandenen Rohstoffs)
		 */
		if (getDBVersion().intValue() < 15)
		{
			getKolonieDB().resetAllColonies();

			Set<ID> ids = new HashSet<ID>();
			Set<ID> res = getStorageDB().getAllRessources().keySet();

			if (res.size() < 3)
			{
				int rest = 3 - res.size();
				for (int i = 0; i < res.size(); i++)
					ids.add((ID) res.toArray()[i]);
				ArrayList<NaturalResource> newRes = new ArrayList<NaturalResource>(NaturalRessourceDescriptionDB.getAllKnownMaterials());
				while (ids.size() < rest)
				{
					// Restliche Materialien zufällig wählen
					ids.add(newRes.get((int) (Math.random() * newRes.size())).getID());
				}

			} else
			{
				while (ids.size() < 3)
				{
					// 3 Materialien zufällig wählen

					ids.add((ID) res.toArray()[(int) (Math.random() * res.size())]);
				}
			}
			for (ID id : ids)
				getStorageDB().addMenge(id, (long) 20E6, true);
		}
	}

	public AbstractDB getDB(String name)
	{
		AbstractDB ret = (AbstractDB) mapNamesToDB.get(name);
		if (ret == null) throw new RuntimeException("Es existiert keine Datenbank mit dem Namen: " + name);
		return ret;
	}

	protected File getPreferencesFile()
	{
		return null;
	}

	protected Level getDefaultLoggerLevel()
	{
		return Level.ALL;
	}

	public String getModulName()
	{
		return "MOUDB";
	}

	/**
	 * F?hrt die DB runter
	 */
	public void shutdownIntern()
	{
		getLogger().info("Fahre die DB herunter.");
		try
		{
			saveData();
		} catch (Exception e)
		{
			Main.instance().severeErrorOccured(e, "Kann die DB-Daten nicht abspeichern", false);
			logThrowable("Kann die DB-Daten nicht abspeichern: " + e.getLocalizedMessage(), e);
			return;
		}
		if (Main.isOnlineMode() && !Main.isDebugMode())
		{
			/*
			 * Jetzt statistische Daten zur Auswertung hochladen
			 */
			try
			{
				UrlCommunicator com = new UrlCommunicator(Main.GAME_SERVER_URL + STATISTIC_SCRIPT);
				Civilization civ = getCivilizationDB().getMyCivilization();
				String name = civ.getName();
				float pop = civ.getBevolkerung().floatValue();
				float money = civ.getMoney().floatValue();
				int planets = civ.getKolonienAnzahl().intValue();
				int ships = civ.getSchiffsanzahl().intValue();
				SecuritySubsystem sec = SecuritySubsystem.instance();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("id", Long.toString(sec.getSerialNumber()));
				params.put("psw", sec.getPassword());
				params.put("money", Float.toString(money));
				params.put("population", Float.toString(pop));
				params.put("planets", Integer.toString(planets));
				params.put("ships", Integer.toString(ships));
				params.put("name", name);
				params.put("bsp", Long.toString((long) getCivilizationDB().getCivDayReport().getBSP()));
				com.sendCommand(params);
				if (com.getError() != null) getLogger().severe("Fehler bei Übermittlung der statischen Daten: " + com.getError());
			} catch (Throwable unwichtig)
			{
				unwichtig.printStackTrace();
			}
		}
		getLogger().info("DB ist heruntergefahren.");
	}

	/**
	 * Speicher auf der Platte alle Daten
	 */
	public boolean saveData()
	{
		getLogger().info("Speichere DB-Daten");
		/*
		 * Ganz wichtig! Die Programmversion abspeichern, mit der dieser
		 * Datenbank erstellt oder verändert wurde.
		 */
		database.put(DB_VERSION, new Integer(Main.VERSION));
		Hashtable clonedDatabase = null;
		synchronized (getLockObject())
		{
			clonedDatabase = (Hashtable) SerializationUtils.clone(database);
		}
		/*
		 * 3 Mal versuchen die Daten auf den Webserver hoch zu laden
		 */
		for (int attemp = 0; attemp < 3; attemp++)
		{
			try
			{
				if (Main.isOnlineMode()) storeDBToServer(clonedDatabase);
				/*
				 * Wenn das Hochladen erfolgreich war, dann vorherige lokale
				 * Kopie mit neuen Daten ueberschreiben
				 */
				try
				{
					writeDBFile(clonedDatabase, constructDbFile());
				} catch (Exception e)
				{
					logThrowable("Error writing local copy of db", e);
				}
				return true;
			} catch (Exception e)
			{
				logThrowable("Error saving database", e);
			}
		}
		return false;
	}

	/**
	 * Wenn Version der DB kleiner al minVersion, dann wird alte DB gelöscht und
	 * neue zurückgegeben
	 * 
	 * @param dbKey
	 * @param minVersion
	 *            minimale Version der gespeicherten Daten
	 * @return
	 */
	private Hashtable getDBData(String dbKey, int minVersion)
	{
		Hashtable dat = (Hashtable) database.get(dbKey);
		if (dat == null || getDBVersion().intValue() < minVersion)
		{
			dat = new Hashtable();
			database.put(dbKey, dat);
		}
		return dat;
	}

	public ResearchDB getResearchDB()
	{
		return researchDB;
	}

	/**
	 * Liefert Civilization-Object f?r eigene Zivilisation
	 */
	public CivilizationDB getCivilizationDB()
	{
		return civDB;
	}

	public ColonyDB getKolonieDB()
	{
		return kolonieDB;
	}

	public StarmapDB getStarmapDB()
	{
		return starmapDB;
	}

	public MaintenanceDB getMaintenaceDB()
	{
		return maintenanceDB;
	}

	public ShipClassDB getShipClassDB()
	{
		return shipclassDB;
	}

	public ShipDB getShipDB()
	{
		return shipDB;
	}

	public NaturalRessourceDescriptionDB getNaturalRessourceDescriptionDB()
	{
		return ressourceDescriptionDB;
	}

	public ShipMovementOrderDB getShipMovementOrderDB()
	{
		return shipMovementOrderDB;
	}

	public ForeignColonyDB getFremdeKolonienDB()
	{
		return fremdeKolonienDB;
	}

	public FremdeSchiffeDB getFremdeSchiffeDB()
	{
		return fremdeSchiffeDB;
	}

	public NaturalRessourcesStorageDB getStorageDB()
	{
		return storageDB;
	}

	public TraderDB getTraderDB()
	{
		return tradeDB;
	}

	public ShipDB getRebelShipDB()
	{
		return rebelShipDB;
	}
}