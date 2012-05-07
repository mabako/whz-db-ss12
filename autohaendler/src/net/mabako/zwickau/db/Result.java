package net.mabako.zwickau.db;

import java.util.HashMap;

/**
 * Eine einzelne Reihe aus einem Abfrageergebnis.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Result extends HashMap<String, Object>
{
	private static final long serialVersionUID = 7690126500036886450L;

	/**
	 * Gibt den Wert eines Feldes als Integer zurück.
	 * @param field
	 * @return
	 */
	public Integer getInt(String field)
	{
		return (Integer)get(field);
	}
	
	/**
	 * Gibt den Wert eines Feldes als String zurück.
	 * @param field
	 * @return
	 */
	public String getString(String field)
	{
		return (String)get(field);
	}

	/**
	 * Gibt den Wert eines Feldes als Long zurück.
	 * @param field
	 * @return
	 */
	public Long getLong(String field)
	{
		return (Long)get(field);
	}
}
