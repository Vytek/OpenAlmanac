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

import it.almanac.R;

import android.util.Log;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;

public class Almanac extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Show Eula
		Eula.show(this);
		
		TabHost tabHost = getTabHost();
		TabHost.TabSpec tabSpec;
		Intent intent;

		// Create Tab Eventi
		Drawable myTab1 = getResources().getDrawable(R.drawable.list);
		intent = new Intent().setClass(this, AlmanacList.class);
		tabSpec = tabHost.newTabSpec("Eventi").setIndicator(
				getString(R.string.almanacevents), myTab1).setContent(intent);
		tabHost.addTab(tabSpec);

		// Create Tab Info
		Drawable myTab2 = getResources().getDrawable(R.drawable.info);
		intent = new Intent().setClass(this, AlmanacInfo.class);
		tabSpec = tabHost.newTabSpec("Info").setIndicator(
				getString(R.string.almanacinfo), myTab2).setContent(intent);
		tabHost.addTab(tabSpec);
		
		//Prima Tab alla partenza
		tabHost.setCurrentTab(0);

	}
}