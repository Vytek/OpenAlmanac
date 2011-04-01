/**
 * Class to query the Almanac db table Saints
 * Classe per richiesta verso db Almanac tabella Saints
 */
package it.almanac;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Enrico Speranza
 */
public class SaintDBEvent {

	private String SaintName = "";
	private String SaintDescription = "";

	static SaintDBEvent getByDateAndLang(String DayNumber, String MonthNumber, String Lang,
			SQLiteDatabase db) {
		String[] args = { DayNumber, MonthNumber, Lang };
		Cursor c = db.rawQuery(
				"SELECT * FROM tbSaints WHERE DayNumber=? AND MonthNumber=? AND DescriptionLanguage=?",
				args);
		c.moveToFirst();

		SaintDBEvent result = new SaintDBEvent().loadFrom(c);
		c.close();

		return (result);
	}

	SaintDBEvent loadFrom(Cursor c) {
		SaintName = c.getString(c.getColumnIndex("Name"));
		SaintDescription = c.getString(c.getColumnIndex("Description"));

		return (this);
	}

	public String getSaintName() {
		return (SaintName);
	}

	public String getSaintDescription() {
		return (SaintDescription);
	}
	
	public void setSaintName(String NameInput){
		SaintName = NameInput;
	}
	
	public void setSaintDescription(String DescriptionInput){
		SaintDescription = DescriptionInput;
	}
}
