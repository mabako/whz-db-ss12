package net.mabako.zwickau.autohaendler;

import net.mabako.zwickau.db.Database;

/**
 * Klasse für pseudoglobale Variablen, die mittels 'import static' importiert
 * werden können.
 * 
 * Insbesondere für die Datenbank wird es hier überflüssig, jeder Klasse eine
 * Instanz zu übergeben oder die Methoden 'static' zu machen.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class G
{
	/**
	 * Instanz der Main-Methode, die hauptsächlich zur Navigation benutzt werden kann.
	 */
	public static Main main = null;
	
	/**
	 * Datenbankinstanz
	 */
	public static Database db = new Database();
	
	/**
	 * Leerer Konstruktor, der das Anlegen neuer Instanzen verhindert.
	 */
	private G()
	{
		
	}
}
