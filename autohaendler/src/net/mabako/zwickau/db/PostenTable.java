package net.mabako.zwickau.db;

import static net.mabako.zwickau.autohaendler.G.db;

import java.util.Vector;

public class PostenTable extends TableHandler
{
	/**
	 * Die Bestellung, die für diese Posten relevant ist.
	 */
	private int bestellung;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Prepared fetchAll(String where, Object... objects)
	{
		if(where == null)
			throw new RuntimeException("where can't be null");
		bestellung = (int) objects[0];
		
		return db.prepare("SELECT * FROM " + getTableName() + " WHERE " + where);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Prepared fetchAssociated(String columnName)
	{
		if("auto".equals(columnName))
		{
			return db.prepare("SELECT a.id AS id, h.name + ' ' + a.bezeichnung + ', ' + CAST(a.preis AS VARCHAR) + '€, ' + f.name AS name FROM auto a LEFT JOIN hersteller h ON a.hersteller_id = h.id LEFT JOIN farbe f ON a.farbe_id = f.id");
		}
		else if("bestellung".equals(columnName))
		{
			return db.prepare("SELECT b.id AS id, CAST(b.id AS VARCHAR) + ': ' + k.name + ' (' + CAST(b.bestelldatum AS VARCHAR) + ')' AS name FROM bestellung b LEFT JOIN kunde k ON k.id = b.kunde_id");
		}
		throw new RuntimeException("unsupported associated column: " + columnName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFieldEditable(Result result, String columnName)
	{
		return "auto".equalsIgnoreCase(columnName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fieldAllowsNull(String columnName)
	{
		return !"auto".equalsIgnoreCase(columnName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result insert(Vector<String> columnNames, Result result)
	{
		Prepared p = db.prepare("INSERT INTO " + getTableName() + "(auto_id, bestellung_id) VALUES (?, ?)");
		boolean success = p.executeNoResult(result.getInt("auto_id"), bestellung);
		p.close();
		
		if(success)
		{
			result.put("bestellung_id", bestellung);
			return result;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(Result result, String column, Object value)
	{
		//  System.out.println(result.getInt("auto_id") + ", " + result.getInt("bestellung_id") + ", " + column + ", " + value);
		Prepared p = db.prepare("UPDATE TOP(1) " + getTableName() + " SET " + column + " = ? WHERE auto_id = ? AND bestellung_id = ?");
		boolean success = p.executeNoResult(value, result.getInt("auto_id"), result.getInt("bestellung_id"));
		p.close();
		
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Result result)
	{
		Integer auto = result.getInt("auto_id"), best = result.getInt("bestellung_id");
		if(auto == null || best == null)
			return false;
		
		Prepared p = db.prepare("DELETE TOP(1) FROM " + getTableName() + " WHERE auto_id = ? AND bestellung_id = ?");
		boolean success = p.executeNoResult(auto, best);
		p.close();
		
		return success;
	}
}
