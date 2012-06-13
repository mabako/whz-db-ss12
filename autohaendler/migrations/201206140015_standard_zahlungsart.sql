SET IDENTITY_INSERT zahlungsart ON;
INSERT INTO zahlungsart (id, name) VALUES
	(1, 'Rechnung'),
	(2, 'Bar');
SET IDENTITY_INSERT zahlungsart OFF;
