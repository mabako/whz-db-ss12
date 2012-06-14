package net.mabako.zwickau.db;

import java.util.Vector;

public enum Table
{
	AUTOS(new GenericTable()),
	KUNDEN(new GenericTable()),
	BESTELLUNGEN(new GenericTable()),
	HERSTELLER(new GenericTable()),
	ZAHLUNGSART(new GenericTable()),
	FARBE(new GenericTable()),
	BENUTZER(new UserTable());
	
	private TableHandler handler;
	
	private Table(TableHandler handler)
	{
		this.handler = handler;
		this.handler.setTable(this);
	}
	
	/**
	 * Liefert alle Datensätze zurück
	 * @return
	 */
	public Results fetchAll()
	{
		Prepared p = handler.fetchAll();
		Results results = p.executeWithResult();
		p.close();
		
		results.setTableDetails(this);
		return results;
	}
	
	public Results fetchAssociated(String columnName)
	{
		Prepared p = handler.fetchAssociated(columnName);
		Results results = p.executeWithResult();
		p.close();
		
		return results;
	}

	public boolean fieldAllowsNull(String columnName)
	{
		if("id".equalsIgnoreCase(columnName))
			return true;
		
		return handler.fieldAllowsNull(columnName);
	}

	public Result insert(Vector<String> columnNames, Result result)
	{
		return handler.insert(columnNames, result);
	}

	public boolean update(Result result, String column, Object value)
	{
		return handler.update(result, column, value);
	}

	public boolean remove(Result result)
	{
		return handler.remove(result);
	}

	public boolean isColumnEditable(Result result, String columnName)
	{
		if(!handler.isFieldEditable(result, columnName))
			return false;
		
		return !columnName.equalsIgnoreCase("id");
	}
}
