package it.almanac;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class AlmanacPreferences extends PreferenceActivity {
	private static final String TAG = "PreferenceActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Mostra Config
		addPreferencesFromResource(R.xml.almanacpreference);
		
		String StrVersion;
		try {
	             PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
	             StrVersion = pi.versionName;
		     } catch (NameNotFoundException e) {
	             Log.e(TAG, "Package name not found", e);
	             StrVersion = "NOT FOUND!";
		     }
     	findPreference("Version").setSummary(StrVersion);
	}
}