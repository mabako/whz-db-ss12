package net.mabako.zwickau.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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
			
			// Ergebnisliste vorbereiten
			Results results = new Results();
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
	 * Schließt das Statement.
	 */
	public void close()
	{
		try
		{
			statement.close();
		}
		catch(SQLException e)
		{
		}
	}
}
