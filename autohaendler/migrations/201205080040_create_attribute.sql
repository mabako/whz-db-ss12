CREATE TABLE hersteller (
	id int IDENTITY NOT NULL,
	name varchar(50) NOT NULL
);

CREATE TABLE typ (
	id int IDENTITY NOT NULL,
	name varchar(50) NOT NULL
);

CREATE TABLE farbe (
	id int IDENTITY NOT NULL,
	name varchar(50) NOT NULL
);

CREATE TABLE autofarbe (
	auto_id int NOT NULL,
	farb_id int NOT NULL
);
