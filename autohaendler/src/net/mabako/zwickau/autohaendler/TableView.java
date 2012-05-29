package net.mabako.zwickau.autohaendler;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
		
		table = new JTable();
		
		Prepared p = db.prepare("SELECT * FROM " + tableName);
		Results model = p.executeWithResult();
		model.setTableName(tableName);
		table.setModel(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		p.close();
		
		scrollPane.setViewportView(table);
	}
}
