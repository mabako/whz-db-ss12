package net.mabako.zwickau.db;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.mabako.zwickau.autohaendler.TableViewLink;

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
	
	private Table details;
	
	/**
	 * 
	 * @param columnNames
	 */
	public Results(Vector<String> columnNames)
	{
		this.columnNames = columnNames;
	}
	
	public void setTableDetails(Table details)
	{
		this.details = details;
	}

	/**
	 * Gibt die Anzahl der tatsächlichen Zeilen + 1 zurück.
	 * 
	 * Das +1 kommt daher, dass eine leere Zeile zum Einfügen eingeblendet wird.
	 */
	@Override
	public int getRowCount()
	{
		return size() + (details.hasPermissionTo("INSERT") ? 1 : 0);
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
			if(details == Table.BESTELLUNG && (columnNames.get(columnIndex+1).equalsIgnoreCase("ausgeliefert") || columnNames.get(columnIndex+1).equalsIgnoreCase("bezahlt")))
				return Boolean.class;
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
		if(!details.hasPermissionTo("UPDATE"))
			return false;
		return details.isColumnEditable(rowIndex < size() ? get(rowIndex) : null, getColumnName(columnIndex).toLowerCase());
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		try
		{
			Object o = get(rowIndex).get(String.valueOf(columnIndex+1));
			if(details == Table.BESTELLUNG && (columnNames.get(columnIndex+1).equalsIgnoreCase("ausgeliefert") || columnNames.get(columnIndex+1).equalsIgnoreCase("bezahlt")))
				return 1 == ((Integer) o);
			return o;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(details == null)
			throw new IllegalStateException("no details given");
		
		if(aValue instanceof TableViewLink)
			aValue = ((TableViewLink)aValue).getID();
		else if(aValue instanceof Boolean)
			aValue = (Boolean) aValue == Boolean.TRUE ? 1 : 0;
		
		// Neuer Datensatz -> keine ID -> kein Update
		Result result = rowIndex < size() ? get(rowIndex) : null;
		if(result == null || result.get("1") == null)
		{
			if(result == null)
			{
				result = new Result();
				add(result);
			}
			
			result.put(String.valueOf(columnIndex+1), aValue);
			result.put(columnNames.get(columnIndex+1), aValue);
			
			// Jetzt versuchen wir, einen neuen Datensatz mit allen bisher eingegebenen Parametern zu erzeugen
			try
			{
				for(int i = 1; i < columnNames.size(); ++ i)
				{
					String columnName = columnNames.get(i);
					if(result.get(columnName) == null)
						if(details == Table.BESTELLUNG && (columnNames.get(i).equalsIgnoreCase("ausgeliefert") || columnNames.get(i).equalsIgnoreCase("bezahlt")))
						{
							result.put(columnName, 0);
							result.put(String.valueOf(i), 0);
						}
						else if(!details.fieldAllowsNull(columnName))
							throw new Exception(columnName + " doesn't allow null");
				}
				
				// INSERT ausführen
				set(rowIndex, details.insert(columnNames, result));
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
			catch(Exception e)
			{
			}
			
			for(TableModelListener l : listener)
			{
				l.tableChanged(new TableModelEvent(this, rowIndex));
			}
		}
		else
		{
			if(details.update(result, columnNames.get(columnIndex+1), aValue))
			{
				result.put(String.valueOf(columnIndex+1), aValue);
				result.put(columnNames.get(columnIndex+1), aValue);
				for(TableModelListener l : listener)
				{
					l.tableChanged(new TableModelEvent(this, rowIndex));
				}
			}
			else
				throw new RuntimeException("failed to update " + details.toString().toLowerCase());
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
		Result result = row < size() ? get(row) : null;
		if(result != null)
		{
			if(!details.remove(result))
				throw new RuntimeException("can't delete row");
		}
		
		remove(row);
		for(TableModelListener l : listener)
			l.tableChanged(new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
	}
	
	public Results fetchAssociated(String columnName)
	{
		return details.fetchAssociated(columnName);
	}
}
