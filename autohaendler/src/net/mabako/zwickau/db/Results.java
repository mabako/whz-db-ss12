package net.mabako.zwickau.db;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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

	public Results(Vector<String> columnNames)
	{
		this.columnNames = columnNames;
	}

	@Override
	public int getRowCount()
	{
		return size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.size() - 1;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames.get(columnIndex+1);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		// TODO: richtige Datentypen nutzen
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return get(rowIndex).get(String.valueOf(columnIndex+1));
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		throw new UnsupportedOperationException();
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
