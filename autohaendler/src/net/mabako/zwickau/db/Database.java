package net.mabako.zwickau.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import net.mabako.zwickau.autohaendler.Config;

/**
 * Datenbankanbindung.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Database
{
	HashMap<Prepared, String> openStatements = new HashMap<Prepared, String>( );
	
	/**
	 * Unsere Verbindung zur Datenbank
	 */
	private Connection con = null;
	
	/**
	 * Verbindet zu einer Datenbank mittels SQL-Server-Authentifizierung
	 * 
	 * @param server
	 *            der anzusprechende Server mit IP:Port
	 * @param username
	 *            der Benutzername
	 * @param password
	 *            das Passwort
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void connectSQLAuth(String server, String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		if (username == null || username.length() == 0 || password == null || password.length() == 0)
			throw new SQLException("Benutzername und Passwort dürfen nicht leer sein.");
		connectInternal(server, username, password);
	}

	/**
	 * Verbindet zu einer Datenbank mittels Windowsauthentifizierung
	 * 
	 * @param server
	 *            der anzusprechende Server mit IP:Port
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	public void connectWindowsAuth(String server) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		connectInternal(server, null, null);
	}

	/**
	 * Ermöglicht sowohl Windows- als auch SQL-Server-Authentifizierung:
	 * 
	 * Falls im %PATH% eine für x86/x64-ntlmauth.dll (nach Plattform) vorhanden
	 * ist UND
	 * der Benutzername "" ist, wird Windowsauth verwendet, sonst SQL-Server
	 * 
	 * @param server
	 * @param username
	 * @param password
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 */
	private void connectInternal(String server, String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		try
		{
			if (con != null)
				con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

		String connectionStr = "jdbc:jtds:sqlserver://" + server + "/" + Config.getDatabaseName();
		con = DriverManager.getConnection(connectionStr, username, password);
	}

	/**
	 * Trennt die Verbindung zum Server.
	 */
	public void disconnect()
	{
		if (con != null)
			try
			{
				if(openStatements.size() > 0)
				{
					System.out.println("Offene Statements (" + openStatements.size() + ")");
					for(String statement : openStatements.values())
						System.out.println("  " + statement);
				}
				
				con.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Liefert ein prepared statement zurück.
	 * 
	 * @param sql
	 *            ein beliebiger SQL-string mit ? statt Parametern
	 * @return prepared statement falls gültiger SQL-Code, sonst
	 *         <code>null</code>
	 */
	public Prepared prepare(String sql)
	{
		try
		{
			Prepared p = new Prepared(con.prepareStatement(sql));
			openStatements.put(p, sql);
			return p;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Versucht, eine Transaktion durchzuführen.
	 * @param prepared
	 * @return <code>true</code> falls erfolgreich.
	 */
	// TODO: Testen und sowas
	public boolean transaction(Prepared prepared)
	{
		// Nicht mehr automatisch alles an die Datenbank senden
		boolean autoCommit = true;
		try
		{
			autoCommit = con.getAutoCommit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}

		try
		{
			con.setAutoCommit(false);
			
			// Befehl "jetzt schon" ausführen
			boolean retVal = prepared.executeNoResult();

			con.setAutoCommit(autoCommit);
			return retVal;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			try
			{
				con.rollback();
			}
			catch (SQLException e2)
			{
				e2.printStackTrace();
			}
			return false;
		}
		finally
		{
			try
			{
				con.setAutoCommit(autoCommit);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Löscht ein Prepared Statement in der Liste der offenen Statements.
	 * @param p
	 * @see Prepared#close()
	 */
	void freePreparedStatement(Prepared p)
	{
		openStatements.remove(p);
	}
}
