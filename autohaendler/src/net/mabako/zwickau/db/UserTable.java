package net.mabako.zwickau.db;

import static net.mabako.zwickau.autohaendler.G.db;

import java.util.Vector;

public class UserTable extends TableHandler
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Prepared fetchAll(String where, Object... objects)
	{
		if(where != null)
			throw new RuntimeException("Where not supported: " + where);
		return db.prepare("SELECT memberuid AS id, USER_NAME(memberuid) AS name, '*' AS passwort, groupuid AS rechte_id FROM sys.sysmembers");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Prepared fetchAssociated(String columnName)
	{
		return db.prepare("SELECT principal_id AS id, name FROM sys.database_principals WHERE name IN ('lesen', 'schreiben', 'db_owner')");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fieldAllowsNull(String columnName)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result insert(Vector<String> columnNames, Result result)
	{
		// Name und Passwort sollten keine Sonderzeichen enthalten. Unlogisch?
		String name = result.getString("name");
		if(horribleSQLString(name))
			throw new RuntimeException("Invalid user name");
		
		String password = result.getString("passwort");
		if(horribleSQLString(password))
			throw new RuntimeException("Invalid password");
		
		int rechteID = result.getInt("rechte_id");
		String rechte = null;
		
		// Rechtegruppe ermitteln
		Prepared p = fetchAssociated("bla");
		for(Result r : p.executeWithResult())
		{
			if(r.getInt("id") == rechteID)
			{
				rechte = r.getString("name");
				break;
			}
		}
		p.close();
		
		if(rechte == null)
			throw new RuntimeException("rechte = " + rechteID);
		
		// Geht nicht mit parametern. Und ohnehin! Wer braucht die schon?!
		p = db.prepare("CREATE LOGIN " + name + " WITH PASSWORD = '" + password + "'; CREATE USER " + name + " FOR LOGIN " + name + ";");
		boolean success = db.transaction(p);
		p.close();
		if(!success)
			throw new RuntimeException("failed to create user");
		
		addRole(name, rechte, false);
		if(rechte.equals("db_owner"))
		{
			addRole(name, "sysadmin", true);
		}
		
		p = db.prepare("SELECT memberuid AS id, USER_NAME(memberuid) AS name, '*' AS passwort, groupuid AS rechte_id FROM sys.sysmembers WHERE USER_NAME(memberuid) = ?");
		Result r = p.executeWithSingleResult(name);
		p.close();
		
		return r;
	}

	private void addRole(String username, String rolename, boolean server)
	{
		Prepared p;
		if(server)
			// ... Konsistenz? Hallo?!
			p = db.prepare("EXEC sp_addsrvrolemember '" + username + "', '" + rolename + "'");
		else
			p = db.prepare("EXEC sp_addrolemember '" + rolename + "', '" + username + "'");
		db.transaction(p);
		p.close();
	}

	private void removeRole(String username, String rolename, boolean server)
	{
		Prepared p;
		if(server)
			p = db.prepare("EXEC sp_dropsrvrolemember '" + username + "', '" + rolename + "'");
		else
			p = db.prepare("EXEC sp_droprolemember '" + rolename + "', '" + username + "'");
		db.transaction(p);
		p.close();
	}

	/**
	 * Notwendig, um "einige" Sachen mit SQL-Benutzern zu tun. Sonst gibt es haufenweise Fehler.
	 * @param str
	 * @return
	 */
	private boolean horribleSQLString(String str)
	{
		// TODO Schrecklichster Code überhaupt
		char[] chars = str.toLowerCase().toCharArray();
		for(int i = 0; i < chars.length; ++ i)
			if(chars[i] < 'a' || chars[i] > 'z')
				if(chars[i] < '0' || chars[i] > '9')
					return true;
		return false;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(Result result, String column, Object value)
	{
		String name = result.getString("name");
		if("rechte_id".equals(column))
		{
			Prepared p = fetchAssociated("bla");
			for(Result r : p.executeWithResult())
			{
				Integer id = r.getInt("id");
				String rolename = r.getString("name");
				
				// TL;DR - id != value, id.equals(value) funktioniert
				if(id.equals(value))
					addRole(name, rolename, false);
				else
					removeRole(name, rolename, false);
				
				if(rolename.equals("db_owner"))
				{
					if(id.equals(value))
						addRole(name, "sysadmin", true);
					else
						removeRole(name, "sysadmin", true);
				}
			}
			p.close();
			return true;
		}
		else if("passwort".equals(column))
		{
			if(horribleSQLString((String)value))
				throw new RuntimeException("invalid password");
			
			String x = "ALTER LOGIN " + name + " WITH PASSWORD = '" + (String)value + "'";
			System.out.println(x);
			Prepared p = db.prepare(x);
			boolean success = p.executeNoResult();
			p.close();
			return success;
		}
		throw new RuntimeException("wut field = " + column);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Result result)
	{
		String name = result.getString("name");
		if(name.equalsIgnoreCase("dbo"))
			return false;
		
		Prepared p = db.prepare("DROP LOGIN " + name + "; DROP USER " + name + ";");
		boolean success = db.transaction(p);
		p.close();
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFieldEditable(Result result, String columnName)
	{
		if(result != null)
		{
			if(result.getString("name").equalsIgnoreCase("dbo"))
				return false;
			
			if("name".equalsIgnoreCase(columnName) && result.getInt("id") != null)
				return false;
		}
		
		return true;
	}
}
