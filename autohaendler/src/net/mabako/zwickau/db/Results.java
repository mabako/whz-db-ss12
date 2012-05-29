package net.mabako.zwickau.db;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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
		
		Prepared update = db.prepare("UPDATE " + tableName + " SET " + columnNames.get(columnIndex+1) + " = ? WHERE " + columnNames.get(1) + " = ?");
		boolean success = update.executeNoResult(aValue, getValueAt(rowIndex, 0));
		update.close();
		
		if(success)
		{
			get(rowIndex).put(String.valueOf(columnIndex+1), aValue);
			get(rowIndex).put(getColumnName(columnIndex), aValue);
			for(TableModelListener l : listener)
			{
				l.tableChanged(new TableModelEvent(this, rowIndex));
			}
		}
		else
			throw new RuntimeException("failed to update " + tableName);
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
}
