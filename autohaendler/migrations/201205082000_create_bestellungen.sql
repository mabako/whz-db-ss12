CREATE TABLE bestellungen (
	id int IDENTITY NOT NULL,
	kunde_id int NOT NULL,
	gesamtpreis decimal(10,2) NOT NULL,
	zahlungsart_id int NOT NULL,
	ausgeliefert tinyint NOT NULL,
	bezahlt tinyint NOT NULL,
	bestelldatum date NOT NULL
);
