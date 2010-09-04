package it.almanac;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AlmanacPreferences extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.almanacpreference);
	}
	//TODO: Aggiungere lettura e scrittura e visulizzazione 
	//altre info Almanac (Version, etc...)
}