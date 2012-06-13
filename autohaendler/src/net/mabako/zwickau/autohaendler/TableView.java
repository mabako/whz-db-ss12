package net.mabako.zwickau.autohaendler;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.mabako.zwickau.db.Prepared;
import net.mabako.zwickau.db.Result;
import net.mabako.zwickau.db.Results;

import static net.mabako.zwickau.autohaendler.G.db;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;

public class TableView extends JPanel
{
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 8718776316766923104L;
	
	/**
	 * Die Tabelle, in welcher die Daten dargestellt werden
	 */
	protected JTable table;
	
	/**
	 * Erstellt eine neue Tabellenansicht
	 * @param details Name der entsprechenden Datenbanktabelle
	 */
	public TableView(TableDetails details) {
		setLayout(new MigLayout("", "[][grow]", "[grow][]"));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 0 2 1,grow");
		
		
		Prepared p = db.prepare("SELECT * FROM " + details.toString().toLowerCase());
		Results model = p.executeWithResult();
		model.setTableName(details.toString().toLowerCase());
		table = new JCustomTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		p.close();
		
		scrollPane.setViewportView(table);
		
		JButton btnAusgewhlteLschen = new JButton("Ausgewählte Löschen");
		btnAusgewhlteLschen.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int[] rows = table.getSelectedRows();

				Results results = (Results) table.getModel();
				for(int i = rows.length-1; i >= 0; -- i)
				{
					int row = rows[i];
					results.removeRow(row);
				}
				table.setModel(results);
			}
		});
		
		JButton btnDrucken = new JButton("Drucken");
		btnDrucken.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try {
					table.print();
				} catch (PrinterException e1) {
					
				}
			}
		});
		
		add(btnDrucken, "cell 0 1,alignx right");
		add(btnAusgewhlteLschen, "cell 1 1,alignx right");
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
			
			// Simple kleine Textformatierung
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
						// Standardmäßig als Double-Wert abfragen
						DefaultTableCellRenderer.UIResource component = (DefaultTableCellRenderer.UIResource) getDefaultRenderer(Double.class).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						
						// den text auslesen
						String text = component.getText();
						
						// Nach Vorlage (1. String im jeweiligen Array) formattieren
						if(text != null && text.length() > 0)
							component.setText(String.format(data[0], text));
						
						return component;
					}
				};
				
				// kann als Renderer für mehrere Spaltennamen dienen.
				for(int i = 1; i < data.length; ++ i)
				{
					renderers.put(data[i], renderer);
				}
			}
			
			// Komboboxen
			for(int columnIndex = 0; columnIndex < model.getColumnCount(); ++ columnIndex)
			{
				if(model.getColumnClass(columnIndex) == JComboBox.class)
				{
					// Kombobox mit name + id
					JComboBox<TableViewLink> box = new JComboBox<TableViewLink>();
					
					// Alle ID->Name-Zuordnungen speichern
					final HashMap<String, String> values = new HashMap<>();
					
					// Alle verlinkten Inhalte abfragen
					Prepared p = db.prepare("SELECT * FROM " + model.getColumnName(columnIndex));
					for(Result result : p.executeWithResult())
					{
						// Zuordnung speichern
						values.put(result.getInt("id").toString(), result.getString("name"));
						
						// Ein Item in die Kombobox einfügen
						box.addItem(new TableViewLink(result.getInt("id"), result.getString("name")));
					}
					p.close();
					
					// Editor, d.h. beim Bearbeiten wird die Kombobox angezeigt
					getColumnModel().getColumn(columnIndex).setCellEditor(new DefaultCellEditor(box));
					
					// Bei der Anzeige werden ID -> Namen aufgelöst.
					renderers.put(model.getColumnName(columnIndex).toLowerCase(), new TableCellRenderer()
					{
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
						{
							// Standard-Text abfragen
							DefaultTableCellRenderer.UIResource component = (DefaultTableCellRenderer.UIResource) getDefaultRenderer(String.class).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
							
							// Eigenen Text setzen
							component.setText(values.get(component.getText()));
							
							return component;
						}
					});
				}
			}
		}
		
		/**
		 * Liefert einen Renderer für die Zeile und Spalte zurück.
		 */
		@Override
		public TableCellRenderer getCellRenderer(int row, int column) {
			TableCellRenderer renderer = renderers.get(getModel().getColumnName(column).toLowerCase());
			if(renderer != null)
				return renderer;
			
			return super.getCellRenderer(row, column);
		}
		
		/**
		 * Editor für eine einzelne Zelle zurückliefern
		 */
		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			// Hier ist der Code, welcher den Auswahlboxen den passenden Standardwert zuweist.
			TableCellEditor editor = super.getCellEditor(row, column);
			if(editor instanceof DefaultCellEditor)
			{
				Component component = ((DefaultCellEditor)editor).getComponent();
				if(component instanceof JComboBox<?>)
				{
					JComboBox<?> comboBox = (JComboBox<?>)component;
					
					// eigentliche ID in der Tabelle
					Integer value = (Integer) getModel().getValueAt(row, column);
					if(value == null)
						return editor;
					
					for(int i = 0; i < comboBox.getItemCount(); ++ i)
					{
						// ID dieses Elements
						int itemID = ((TableViewLink)comboBox.getItemAt(i)).getID();
						if(value == itemID)
						{
							// Als ausgewählt setzen
							comboBox.setSelectedIndex(i);
							break;
						}
					}
				}
			}
			return editor;
		}
	}
}
