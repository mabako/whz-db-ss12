package net.mabako.zwickau.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.mabako.zwickau.autohaendler.Config;

/**
 * Datenbankanbindung.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Database
{
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
	 * @return <code>true</code> falls die Verbindung erfolgreich war.
	 */
	public boolean connectSQLAuth(String server, String username, String password)
	{
		if (username == null || username.length() == 0 || password == null || password.length() == 0)
			return false;
		return connectInternal(server, username, password);
	}

	/**
	 * Verbindet zu einer Datenbank mittels Windowsauthentifizierung
	 * 
	 * @param server
	 *            der anzusprechende Server mit IP:Port
	 * @return <code>true</code> falls die Verbindung erfolgreich war.
	 */
	public boolean connectWindowsAuth(String server)
	{
		return connectInternal(server, null, null);
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
	 * @return
	 */
	private boolean connectInternal(String server, String username, String password)
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

		try
		{
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		try
		{
			String connectionStr = "jdbc:jtds:sqlserver://" + server + "/" + Config.getDatabaseName();
			con = DriverManager.getConnection(connectionStr, username, password);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return (con != null);
	}

	/**
	 * Trennt die Verbindung zum Server.
	 */
	public void disconnect()
	{
		if (con != null)
			try
			{
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
			return new Prepared(con.prepareStatement(sql));
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
}
