package net.mabako.zwickau.autohaendler;

import javax.swing.JButton;
import javax.swing.JPanel;
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
		setLayout(new MigLayout("", "[20%:20%:20%][grow][20%:20%:20%]", "[][][][][]"));
		
		JButton lblBestand = new JButton("Bestand");
		lblBestand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(TableDetails.AUTOS));
			}
		});
		add(lblBestand, "cell 1 1");
		
		JButton lblKunden = new JButton("Kunden");
		lblKunden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(TableDetails.KUNDEN));
			}
		});
		add(lblKunden, "cell 1 2");
		
		JButton lblBestellungen = new JButton("Bestellungen");
		lblBestellungen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(TableDetails.BESTELLUNGEN));
			}
		});
		add(lblBestellungen, "cell 1 3");
		
		JButton lblHersteller = new JButton("Hersteller");
		lblHersteller.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.addContent(new TableView(TableDetails.HERSTELLER));
			}
		});
		add(lblHersteller, "cell 1 4");
		
	}
}
