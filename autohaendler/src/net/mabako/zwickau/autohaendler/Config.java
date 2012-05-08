package net.mabako.zwickau.autohaendler;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Klasse für sämtliche Konfigurationseinstellungen des Programms.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public final class Config
{
	/**
	 * Interne Ablage der Konfiguration.
	 */
	private static HashMap<String, Object> configuration = new HashMap<String, Object>( );
	
	/**
	 * Namen aller Variablen, die in der Konfiguration als String vorliegen.
	 */
	private static String[] stringValues = {"app:name", "db:server", "db:database"};
	
	/**
	 * Namen aller Variablen, die in der Konfiguration als Farbe vorliegen.
	 */
	private static String[] colorValues = {"color:separator", "color:background"};
	
	/**
	 * Liefert ein Konfigurationselement als String.
	 * @param key
	 * @return
	 */
	private static String getString(String key)
	{
		return (String)configuration.get(key);
	}
	
	/**
	 * Liefert ein Konfigurationselement als Color.
	 * @param key
	 * @return
	 */
	private static Color getColor(String key)
	{
		return (Color)configuration.get(key);
	}
	
	/**
	 * Liefert den Namen der Anwendung zurück
	 * @return
	 */
	public static String getAppName()
	{
		return getString("app:name");
	}
	
	/**
	 * Gibt die IP/Port-Kombination zurück, auf dem der SQL-Server läuft
	 * @return
	 */
	public static String getServer()
	{
		return getString("db:server");
	}
	
	/**
	 * Gibt den Namen der verwendeten Datenbank zurück.
	 * @return
	 */
	public static String getDatabaseName()
	{
		return getString("db:database");
	}
	
	/*
	 * Grafische Oberfläche
	 */
	/**
	 * Liefert den Namen des Abstandhalters zwischen oberem (weiß) und unterem Panel
	 * im Hauptfenster.
	 * 
	 * @return
	 */
	public static Color getSeparatorColor()
	{
		return getColor("color:separator");
	}
	
	/**
	 * Liefert die Hintergrundfarbe.
	 * @return
	 */
	public static Color getBackgroundColor()
	{
		return getColor("color:background");
	}
	
	/**
	 * Statischer Konstruktor
	 */
	static
	{
		// XML-Datei einlesen
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			Document doc = factory.newDocumentBuilder().parse(new File("config/app.xml"));
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new NamespaceContext()
			{
				@SuppressWarnings("rawtypes")
				@Override
				public Iterator getPrefixes(String namespaceURI)
				{
					return null;
				}
				
				@Override
				public String getPrefix(String namespaceURI)
				{
					return null;
				}
				
				@Override
				public String getNamespaceURI(String prefix)
				{
					return prefix + "://";
				}
			});
			
			// Alle String-Werte auslesen
			for(String key : stringValues)
				configuration.put(key, xpath.evaluate("//" + key, doc));
			
			for(String key : colorValues)
			{
				int red = Integer.valueOf(xpath.evaluate("//" + key + "/@red", doc));
				int green = Integer.valueOf(xpath.evaluate("//" + key + "/@green", doc));
				int blue = Integer.valueOf(xpath.evaluate("//" + key + "/@blue", doc));
				configuration.put(key, new Color(red, green, blue));
			}
		}
		catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e)
		{
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
