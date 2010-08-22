/**
 * Class to query the Almanac db table Saints
 * Classe per richiesta verso db Almanac tabella Saints
 */
package com.google.android.almanac;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Enrico Speranza
 */
public class SaintDBEvent {
	
	private String SaintName="";
	private String SaintDescription="";
	
	static SaintDBEvent getByDateAndLang (String Date, String Lang, SQLiteDatabase db)
	{
		String [] args = {Date, Lang};
		Cursor c = db.rawQuery("SELECT * FROM Saints WHERE SaintDate=? AND SaintLanguage=?", args);
		c.moveToFirst();
		
		SaintDBEvent result = new SaintDBEvent().loadFrom(c);
		c.close();
		
		return(result);		
	}
	
	SaintDBEvent loadFrom(Cursor c)
	{
		SaintName=c.getString(c.getColumnIndex("SaintName"));
		SaintDescription=c.getString(c.getColumnIndex("SaintDescription"));
		
		return(this);
	}
	
	public String getSaintName()
	{
		return(SaintName);
	}

	public String getSaintDescription()
	{
		return(SaintDescription);
	}
}
