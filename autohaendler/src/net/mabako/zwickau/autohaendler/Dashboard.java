package net.mabako.zwickau.autohaendler;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.mabako.zwickau.db.Table;
import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import static net.mabako.zwickau.autohaendler.G.main;

public class Dashboard extends JPanel
{
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -2754350246072768836L;

	/**
	 * Erstellt das Dashboard.
	 */
	public Dashboard() {
		setLayout(new MigLayout("", "[20%:20%:20%][grow][grow][20%:20%:20%]", "[][][][][]"));
		
		createButton("Bestand", Table.AUTO, 1, 1);
		createButton("Kunden", Table.KUNDE, 1, 2);
		createButton("Bestellungen", Table.BESTELLUNG, 1, 3);
		
		createButton("Hersteller", Table.HERSTELLER, 2, 1);
		createButton("Farben", Table.FARBE, 2, 2);
		createButton("Zahlungsarten", Table.ZAHLUNGSART, 2, 3);
		createButton("Benutzer", Table.BENUTZER, 2, 4);
	}

	private void createButton(String label, final Table table, int x, int y)
	{
		if(table.hasPermissionTo("SELECT"))
		{
			JButton button = new JButton(label);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.addContent(new TableView(table));
				}
			});
			add(button, "cell " + x + " " + y + " ,growx");
		}
	}
}
