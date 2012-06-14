package net.mabako.zwickau.db;

import java.util.Vector;

public abstract class TableHandler
{
	private Table table;
	
	final void setTable(Table table)
	{
		this.table = table;
	}
	
	final String getTableName()
	{
		return table.toString().toLowerCase();
	}
	
	/**
	 * Gibt ein Prepared Statement für alle Datensätze zurück
	 * @param objects 
	 * @param where 
	 * @param objects 
	 * @return
	 */
	public abstract Prepared fetchAll(String where, Object... objects);

	/**
	 * Liefert die Inhalte einer verwandten Tabelle zurück
	 * @param columnName
	 * @return
	 */
	public abstract Prepared fetchAssociated(String columnName);

	/**
	 * Kann das Feld bearbeitet werden?
	 * @param result
	 * @param columnName
	 * @return
	 */
	public abstract boolean isFieldEditable(Result result, String columnName);

	/**
	 * Darf in dem Feld null stehen?
	 * @param columnName
	 * @return
	 */
	public abstract boolean fieldAllowsNull(String columnName);

	/**
	 * Fügt einen Datensatz ein
	 * @param columnNames
	 * @param result
	 * @return
	 */
	public abstract Result insert(Vector<String> columnNames, Result result);

	/**
	 * Aktualisiert einen Datensatz
	 * @param result
	 * @param column
	 * @param value
	 * @return
	 */
	public abstract boolean update(Result result, String column, Object value);

	/**
	 * Entfernt einen Datensatz
	 * @param result
	 * @return
	 */
	abstract boolean remove(Result result);
}
