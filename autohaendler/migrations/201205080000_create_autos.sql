CREATE TABLE autos (
	id int IDENTITY NOT NULL,
	hersteller_id int NOT NULL,
	typ_id int NOT NULL,
	bezeichnung varchar(50) NOT NULL,
	baujahr int NOT NULL,
	distanz decimal(10,1) NOT NULL,
	ps int NOT NULL,
	verbrauch decimal(3,1) NOT NULL,
	lagerbestand int NOT NULL,
	preis decimal(10,2) NOT NULL
);
