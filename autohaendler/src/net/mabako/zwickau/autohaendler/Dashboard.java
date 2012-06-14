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
		
		JButton lblBestand = new JButton("Bestand");
		lblBestand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.AUTO));
			}
		});
		add(lblBestand, "cell 1 1,growx");
		
		JButton lblKunden = new JButton("Kunden");
		lblKunden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.KUNDE));
			}
		});
		add(lblKunden, "cell 1 2,growx");
		
		JButton lblBestellungen = new JButton("Bestellungen");
		lblBestellungen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.BESTELLUNG));
			}
		});
		add(lblBestellungen, "cell 1 3,growx");
		
		JButton lblHersteller = new JButton("Hersteller");
		lblHersteller.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.HERSTELLER));
			}
		});
		add(lblHersteller, "cell 2 1,growx");
		
		JButton lblFarben = new JButton("Farben");
		lblFarben.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.FARBE));
			}
		});
		add(lblFarben, "cell 2 2,growx");
		
		JButton lblZahlungsarten = new JButton("Zahlungsarten");
		lblZahlungsarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.ZAHLUNGSART));
			}
		});
		add(lblZahlungsarten, "cell 2 3,growx");
		
		JButton lblBenutzer = new JButton("Benutzer");
		lblBenutzer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(Table.BENUTZER));
			}
		});
		add(lblBenutzer, "cell 2 4,growx");
	}
}
