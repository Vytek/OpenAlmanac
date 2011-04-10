//OpenAlmanac
//Copyright (C) 2011 Enrico Speranza
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

/**
 * Class to query the Almanac db table Saints
 * Classe per richiesta verso db Almanac tabella Saints
 */
package it.openalmanac;

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
