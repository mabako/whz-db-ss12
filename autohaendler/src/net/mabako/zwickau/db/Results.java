package net.mabako.zwickau.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.mabako.zwickau.autohaendler.Config;
import net.mabako.zwickau.autohaendler.TableViewLink;

import static net.mabako.zwickau.autohaendler.G.db;

/**
 * Vektor mit Ergebnissen, d.h. trivial - dient nur der Vereinfachung von Programmcode.
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class Results extends Vector<Result> implements TableModel
{
	private static final long serialVersionUID = 5130770815927271116L;
	
	/**
	 * Vector mit allen Spaltennamen, von 1 bis [spaltenzahl]. <0> ist "undefined"
	 */
	private Vector<String> columnNames;
	
	/**
	 * Name der passenden Tabelle
	 */
	private String tableName;

	public Results(Vector<String> columnNames)
	{
		this.columnNames = columnNames;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Gibt die Anzahl der tatsächlichen Zeilen + 1 zurück.
	 * 
	 * Das +1 kommt daher, dass eine leere Zeile zum Einfügen eingeblendet wird.
	 */
	@Override
	public int getRowCount()
	{
		return size() + 1;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.size() - 1;
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		String str = columnNames.get(columnIndex+1).replace("_id", "");
		if(str.length() == 2)
			return str.toUpperCase();
		
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		try
		{
			if(columnNames.get(columnIndex+1).endsWith("_id"))
				return JComboBox.class;
			
			return getValueAt(0, columnIndex).getClass();
		}
		catch(Exception e)
		{
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		// 0 = id
		return columnIndex > 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		try
		{
			return get(rowIndex).get(String.valueOf(columnIndex+1));
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(tableName == null)
			throw new IllegalStateException("no tableName given");
		
		if(aValue instanceof TableViewLink)
			aValue = ((TableViewLink)aValue).getID();
		
		// Neuer Datensatz -> keine ID -> kein Update
		Result result = rowIndex < size() ? get(rowIndex) : null;
		if(result == null || result.getInt("1") == null)
		{
			if(result == null)
			{
				result = new Result();
				add(result);
			}
			
			result.put(String.valueOf(columnIndex+1), aValue);
			result.put(columnNames.get(columnIndex+1), aValue);
			
			// Jetzt versuchen wir, einen neuen Datensatz mit allen bisher eingegebenen Parametern zu erzeugen
			Prepared checkAllowsNull = db.prepare("SELECT COUNT(*) AS count FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = ? AND TABLE_NAME = ? AND COLUMN_NAME = ? AND IS_NULLABLE = 'YES'");
			try
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
					else
					{
						if(!"id".equalsIgnoreCase(columnName) && checkAllowsNull.executeWithSingleResult(Config.getDatabaseName(), tableName, columnName).getInt("count") == 0)
							throw new Exception(columnName + " doesn't allow null");
					}
				}
				
				// INSERT ausführen
				Prepared insert = db.prepare("INSERT INTO " + tableName + " (" + fields.substring(1) + ") VALUES (" + paramsQuery.substring(1) + ")");
				
				Integer id = insert.executeInsert(params.toArray());
				insert.close();
				
				if(id != null)
				{
					// Kompletten Datensatz abfragen, um auch Standardwerte zu definieren.
					Prepared p = db.prepare("SELECT * FROM " + tableName + " WHERE id = ?");
					set(rowIndex, p.executeWithSingleResult(id));
					p.close();
				}
			}
			catch(Exception e)
			{
			}
			checkAllowsNull.close();
			
			for(TableModelListener l : listener)
			{
				l.tableChanged(new TableModelEvent(this, rowIndex));
			}
		}
		else
		{
			Prepared update = db.prepare("UPDATE " + tableName + " SET " + columnNames.get(columnIndex+1) + " = ? WHERE " + columnNames.get(1) + " = ?");
			boolean success = update.executeNoResult(aValue, getValueAt(rowIndex, 0));
			update.close();
			
			if(success)
			{
				result.put(String.valueOf(columnIndex+1), aValue);
				result.put(columnNames.get(columnIndex+1), aValue);
				for(TableModelListener l : listener)
				{
					l.tableChanged(new TableModelEvent(this, rowIndex));
				}
			}
			else
				throw new RuntimeException("failed to update " + tableName);
		}
	}
	
	private Set<TableModelListener> listener = new HashSet<TableModelListener>();

	@Override
	public void addTableModelListener(TableModelListener l)
	{
		listener.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
		listener.remove(l);
	}

	public void removeRow(int row)
	{
		Integer id = (Integer) getValueAt(row, 0);
		if(id != null)
		{
			Prepared p = db.prepare("DELETE FROM " + tableName + " WHERE id = ?");
			boolean success = p.executeNoResult(id);
			p.close();
			
			if(!success)
				return;
		}
		remove(row);
		
		for(TableModelListener l : listener)
			l.tableChanged(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
	}
}
