//Almanac
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

package it.almanac;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class AlmanacPreferences extends PreferenceActivity {
	private final String REPORT_AN_ISSUE_BUG_URL = "http://code.google.com/p/almanac/issues/list";
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
     	//Evento Report a BUG
     	Preference reportAnIssueBug = findPreference("Report_an_issue_bug_key");
     	reportAnIssueBug.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPORT_AN_ISSUE_BUG_URL));
				startActivity(browserIntent);
				return false;
			}
		});    	
	}
}