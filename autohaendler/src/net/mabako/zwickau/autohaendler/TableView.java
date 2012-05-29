package net.mabako.zwickau.autohaendler;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.mabako.zwickau.db.Prepared;
import net.mabako.zwickau.db.Results;

import static net.mabako.zwickau.autohaendler.G.db;
import net.miginfocom.swing.MigLayout;

public class TableView extends JPanel
{
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 8718776316766923104L;
	
	/**
	 * Die Tabelle, in welcher die Daten dargestellt werden
	 */
	private JTable table;
	
	/**
	 * Erstellt eine neue Tabellenansicht
	 * @param tableName Name der entsprechenden Datenbanktabelle
	 */
	public TableView(String tableName) {
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 0,grow");
		
		
		Prepared p = db.prepare("SELECT * FROM " + tableName);
		Results model = p.executeWithResult();
		model.setTableName(tableName);
		table = new JCustomTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		p.close();
		
		scrollPane.setViewportView(table);
	}
	
	private class JCustomTable extends JTable {
		/**
		 * Serial
		 */
		private static final long serialVersionUID = 1710037847160457126L;
		
		private HashMap<String, TableCellRenderer> renderers = new HashMap<String, TableCellRenderer>();
		
		public JCustomTable(Results model)
		{
			super(model);
			
			for(final String[] data : new String[][]{
					new String[]{"%s€", "preis"},
					new String[]{"%s km", "distanz"},
					new String[]{"%s l/100km", "verbrauch"}
				})
			{
				
				TableCellRenderer renderer = new TableCellRenderer()
				{
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
					{
						DefaultTableCellRenderer.UIResource component = (DefaultTableCellRenderer.UIResource) getDefaultRenderer(Double.class).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						String text = component.getText();
						if(text != null && text.length() > 0)
							component.setText(String.format(data[0], text));
						return component;
					}
				};
				for(int i = 1; i < data.length; ++ i)
				{
					renderers.put(data[i], renderer);
				}
			}
		}
		
		/**
		 * Liefert einen Renderer für die Zeile und Spalte zurück.
		 */
		@Override
		public TableCellRenderer getCellRenderer(int row, int column) {
			TableCellRenderer renderer = renderers.get(getModel().getColumnName(column));
			if(renderer != null)
				return renderer;
			
			return super.getCellRenderer(row, column);
		}
		
		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			// TODO: Auswahlboxen für Hersteller
			return super.getCellEditor(row, column);
		}
	}
}
