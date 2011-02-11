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