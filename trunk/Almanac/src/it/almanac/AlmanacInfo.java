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

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.Locale;

public class AlmanacInfo extends Activity {
	
	private static final String TAG = "AlmanacInfo";
	final String mimeType = "text/html";
	final String encoding = "utf-8";
	// final String html =
	// "<h1>Header</h1><p>Custom HTML</p><p><img src=\"file:///android_asset/image1.jpg\" /></p>";
	final String html = "<html><body>Informazioni <b>Almanac</b></body></html>";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.almanacinfo);

		WebView web = (WebView) findViewById(R.id.webView);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		web.getSettings().setPluginsEnabled(false);
		web.getSettings().setSupportMultipleWindows(false);
		web.getSettings().setSupportZoom(false);
		web.setVerticalScrollBarEnabled(true);
		web.setHorizontalScrollBarEnabled(false);
		
		//Check della lingua di default
		//Default language check
		String locale_long = getApplicationContext().getResources().getConfiguration().locale.getDisplayLanguage();
		String locale_short  = java.util.Locale.getDefault().getLanguage();
		//Debug
		Log.d(TAG, locale_long);
		Log.d(TAG, locale_short);

		if (locale_short.equals("it")) {
			web.loadUrl("file:///android_asset/almanac_it.html");
		}
		else if (locale_short.equals("en")) {
			web.loadUrl("file:///android_asset/almanac_en.html");
		}
		else
		{
			 //Load internal page
			 web.loadDataWithBaseURL("fake://not/needed", html, mimeType,
			 encoding, "");
		}
	}
}
