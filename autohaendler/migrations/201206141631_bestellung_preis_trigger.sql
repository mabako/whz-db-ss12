CREATE TRIGGER update_bestellung_insert
	ON posten
	FOR INSERT
	AS
		UPDATE bestellung SET
			gesamtpreis = (SELECT TOP 1 SUM(preis) FROM posten p LEFT JOIN auto a ON p.auto_id = a.id WHERE bestellung_id = (SELECT TOP 1 bestellung_id FROM inserted))
		WHERE id = (SELECT TOP 1 bestellung_id FROM inserted);
