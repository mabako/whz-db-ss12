package net.mabako.zwickau.autohaus;

import java.awt.Color;

/**
 * Klasse für sämtliche Konfigurationseinstellungen des Programms.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
// TODO Konfiguration -> XML
public final class Config
{
	/**
	 * Liefert den Namen der Anwendung zurück
	 * @return
	 */
	public static String getAppName()
	{
		return "Datenbanken II - Autohaus";
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
		return new Color(191, 205, 219);
	}
	
	/**
	 * Liefert die Hintergrundfarbe.
	 * @return
	 */
	public static Color getBackgroundColor()
	{
		return new Color(244, 247, 252);
	}
}
