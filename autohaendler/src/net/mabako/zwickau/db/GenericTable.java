package net.mabako.zwickau.db;

import static net.mabako.zwickau.autohaendler.G.db;

import java.util.ArrayList;
import java.util.Vector;

import net.mabako.zwickau.autohaendler.Config;

public class GenericTable extends TableHandler
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Prepared fetchAll(String where, Object... objects)
	{
		return db.prepare("SELECT * FROM " + getTableName() + ( where != null ? (" WHERE " + where ) : ""));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Prepared fetchAssociated(String columnName)
	{
		return db.prepare("SELECT * FROM " + columnName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fieldAllowsNull(String columnName)
	{
		Prepared checkAllowsNull = db.prepare("SELECT COUNT(*) AS count FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = ? AND TABLE_NAME = ? AND COLUMN_NAME = ? AND IS_NULLABLE = 'YES'");
		boolean allowed = checkAllowsNull.executeWithSingleResult(Config.getDatabaseName(), getTableName(), columnName).getInt("count") == 1;
		checkAllowsNull.close();
		
		return allowed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result insert(Vector<String> columnNames, Result result)
	{
		ArrayList<Object> params = new ArrayList<>();
		
		String fields = ""; // Feldnamen
		String paramsQuery = ""; // Query-Parameter, in etwa ?,?,?,?,?
		for(int i = 1; i < columnNames.size(); ++ i)
		{
			String columnName = columnNames.get(i);
			if(result.get(columnName) != null)
			{
				fields += "," + columnName;
				paramsQuery += ",?";
				params.add(result.get(columnName));
			}
		}
		
		// INSERT ausführen
		Prepared insert = db.prepare("INSERT INTO " + getTableName() + " (" + fields.substring(1) + ") VALUES (" + paramsQuery.substring(1) + ")");
		
		Integer id = insert.executeInsert(params.toArray());
		insert.close();
		
		if(id != null)
		{
			// Kompletten Datensatz abfragen, um auch Standardwerte zu definieren.
			Prepared p = db.prepare("SELECT * FROM " + getTableName() + " WHERE id = ?");
			Result r = p.executeWithSingleResult(id);
			p.close();
			
			return r;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(Result result, String column, Object value)
	{
		Prepared update = db.prepare("UPDATE " + getTableName() + " SET " + column + " = ? WHERE id = ?");
		boolean success = update.executeNoResult(value, result.getInt("id"));
		update.close();
		
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Result result)
	{
		Prepared p = db.prepare("DELETE FROM " + getTableName() + " WHERE id = ?");
		boolean success = p.executeNoResult(result.getInt("id"));
		p.close();
		
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFieldEditable(Result result, String columnName)
	{
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasPermissionTo(String what)
	{
		Prepared prepared = db.prepare("SELECT COUNT(*) AS count FROM sys.fn_my_permissions(?, 'OBJECT') WHERE subentity_name = '' AND permission_name = ?");
		Result result = prepared.executeWithSingleResult(getTableName(), what);
		prepared.close();
		
		return result.getInt("count") > 0;
	}
}
