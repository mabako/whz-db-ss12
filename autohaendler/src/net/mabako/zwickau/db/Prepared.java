package net.mabako.zwickau.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import static net.mabako.zwickau.autohaendler.G.db;

/**
 * Wrapper-Klasse für Prepared Statements
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Prepared
{
	private PreparedStatement statement;
	
	/**
	 * Konstruiert ein Objekt mit entsprechendem Prepared-Statmement
	 * @param statement
	 */
	public Prepared(PreparedStatement statement)
	{
		this.statement = statement;
	}
	
	/**
	 * Führt das Statement aus, ohne auf dessen Ergebnisse Rücksicht zu nehmen.
	 * @param objects alle Objekte, die als Parameter (statt ?) übergeben werden sollen
	 * @return
	 */
	public boolean executeNoResult(Object... objects)
	{
		try
		{
			// Alle übergebenen Parameter einsetzen
			for (int i = 0; i < objects.length; i++) {
				statement.setObject(i+1, objects[i]);
			}
			
			statement.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Führt eine Abfrage aus und liefert die Ergebnisse zurück.
	 * @param objects alle Objekte, die als Parameter (statt ?) übergeben werden sollen
	 * @return
	 */
	public Results executeWithResult(Object... objects)
	{
		ResultSet resultSet = null;
		try
		{
			// Alle übergebenen Parameter einsetzen
			for (int i = 0; i < objects.length; i++) {
				statement.setObject(i+1, objects[i]);
			}
			
			resultSet = statement.executeQuery();
			ResultSetMetaData metadata = resultSet.getMetaData();
			
			// Liste mit Spaltennamen erzeugen
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("undefined");
			for(int i = 1; i <= metadata.getColumnCount(); ++i) {
				columnNames.add(metadata.getColumnName(i));
			}
			
			// Ergebnisliste vorbereiten
			Results results = new Results(columnNames);
			while(resultSet.next())
			{
				// Alle Ergebnisse durchlaufen
				Result result = new Result();
				
				// In einer Map speichern
				for (int i = 1; i <= metadata.getColumnCount(); ++i) {
					result.put(String.valueOf(i), resultSet.getObject(i));
					result.put(metadata.getColumnName(i), resultSet.getObject(i));
				}
				results.add(result);
			}
			return results;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(resultSet != null)
			{
				try
				{
					resultSet.close();
				}
				catch(SQLException e)
				{
				}
			}
		}
	}

	/**
	 * Führt eine Abfrage aus und liefert das erste Ergebnisse zurück.
	 * @param objects alle Objekte, die als Parameter (statt ?) übergeben werden sollen
	 * @return das erste Ergebnis, oder <code>null</code> falls keins gefunden.
	 * @see Prepared#executeWithResult(Object...)
	 */
	public Result executeWithSingleResult(Object... objects)
	{
		Results results = executeWithResult(objects);
		if(results.size() > 0)
			return results.get(0);
		return null;
	}
	
	/**
	 * Führt eine INSERT-Abfrage aus und liefert die erste eingefügte ID zurück.
	 * @param objects alle Objekte, die als Parameter (statt ?) übergeben werden sollen
	 * @return die erste eingefügte ID, oder <code>null</code> bei Fehlern.
	 */
	public Integer executeInsert(Object... objects)
	{
		try
		{
			// Alle übergebenen Parameter einsetzen
			for (int i = 0; i < objects.length; i++) {
				statement.setObject(i+1, objects[i]);
			}
			
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			return resultSet.getInt(1);
		}
		catch (SQLException e)
		{
			// e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Schließt das Statement.
	 */
	public void close()
	{
		try
		{
			statement.close();
			db.freePreparedStatement(this);
		}
		catch(SQLException e)
		{
		}
	}
}
