package net.mabako.migratetool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.mabako.zwickau.autohaendler.Config;
import net.mabako.zwickau.db.Prepared;
import net.mabako.zwickau.db.Result;

import static net.mabako.zwickau.autohaendler.G.db;

/**
 * Kleines Tool, um auf mehreren Rechnern konsistent zu entwickeln.
 * 
 * Schritte:
 * 1. Für jede Datenbankänderung ein .sql-Skript erstellen.
 * 2. Datei YYYYMMDDHHmm_was_passiert_ist.sql in den Ordner 'migrations'
 * speichern.
 * 3. Main-Methode dieser Klasse ausführen (z.b. Eclipse)
 * 
 * @author Marcus Bauer (mabako@gmail.com)
 */
public class MigrateTool
{
	public static void main(String args[])
	{
		String server = args.length >= 1 ? args[0] : Config.getServer();
		try {
			if(args.length >= 3)
				db.connectSQLAuth(server, args[1], args[2]);
			else
				db.connectWindowsAuth(server);
			
			createMigrationsTable();
			doMigrations(getAllMigrations(), getAllCompletedMigrations());

			db.disconnect();
		} catch(Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Immer eine migrations-Tabelle bereitstellen.
	 */
	private static void createMigrationsTable()
	{
		Prepared table = db.prepare("IF OBJECT_ID('migrations','U') IS NULL CREATE TABLE migrations (id bigint NOT NULL PRIMARY KEY)");
		table.executeNoResult();
		table.close();
	}

	/**
	 * Gibt ein Set mit den IDs aller ausgeführten Migrationen zurück.
	 * 
	 * @return
	 */
	private static Set<Long> getAllCompletedMigrations()
	{
		// Alle bisherigen Migrationen abfragen
		Set<Long> migrations = new HashSet<Long>();

		Prepared allMigrations = db.prepare("SELECT id FROM migrations");
		for (Result result : allMigrations.executeWithResult())
		{
			migrations.add(result.getLong("id"));
		}
		allMigrations.close();

		return migrations;
	}

	/**
	 * Gibt einen Array mit allen Dateinamen von Migrationen zurück, dieser
	 * ist nach Dateinamen sortiert.
	 * 
	 * @return
	 */
	private static File[] getAllMigrations()
	{
		File[] files = new File("migrations").listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				try
				{
					return pathname.getName().length() > 10 && Long.valueOf(pathname.getName().substring(0, 12)) > 0;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}
		});
		Arrays.sort(files);
		return files;
	}

	/**
	 * Führt alle noch nicht erledigten Migrationen aus.
	 * 
	 * @param allMigrations
	 * @param allCompletedMigrations
	 */
	private static void doMigrations(File[] allMigrations, Set<Long> allCompletedMigrations)
	{
		System.out.println("Es fehlen " + (allMigrations.length - allCompletedMigrations.size()) + " Migrationen");
		Prepared addMigration = db.prepare("INSERT INTO migrations VALUES (?)");

		for (File file : allMigrations)
		{
			Long migration = Long.valueOf(file.getName().substring(0, 12));
			if (!allCompletedMigrations.contains(migration))
			{
				System.out.println("Migration: " + file.getName() + "...");
				try
				{
					if(db.transaction(db.prepare(readFile(file))))
					{
						addMigration.executeNoResult(migration);
						System.out.println(" ... OK");
					}
					else
					{
						System.out.println(" ... SQL-Fehler");
						return;
					}
				}
				catch(IOException e)
				{
					System.out.println(" ... Datei-Fehler");
					e.printStackTrace();
					return;
				}
			}
		}

		addMigration.close();
	}

	/**
	 * Liest eine Datei ein, gibt den Inhalt zurück
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static String readFile(File file) throws IOException
	{
		FileInputStream stream = new FileInputStream(file);
		try
		{
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally
		{
			stream.close();
		}
	}
}
