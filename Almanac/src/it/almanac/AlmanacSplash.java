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
import android.util.Log;
import android.view.KeyEvent;
import android.os.Bundle;
import android.content.Intent;
import android.view.MotionEvent;

public class AlmanacSplash extends Activity {
	/** Called when the activity is first created. */

	boolean m_bSplashActive;
	boolean m_bPaused;

	long m_dwSplashTime = 2000;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		m_bPaused = false;
		m_bSplashActive = true;

		// Very simple timer thread
		Thread splashTimer = new Thread() {
			public void run() {
				try {
					// Wait loop
					long ms = 0;
					while (m_bSplashActive && (ms < m_dwSplashTime)) {
						sleep(100);
						Log.d("Almanac:Debug", "While cicle "+ms);
						// Only advance the timer if we're running.
						if (!m_bPaused)
							ms += 100;
					}
					// Advance to the next screen.
					startActivity(new Intent(
							"com.google.app.splashy.CLEARSPLASH"));
					Log.d("Almanac:Debug",
							"Start: com.google.app.splashy.CLEARSPLASH");
				} catch (Exception e) {
					// Thread exception
					// System.out.println(e.toString());
					Log.e("Almanac:Splash", e.toString());
				} finally {
					finish();
				}
			}
		};
		splashTimer.start();

		setContentView(R.layout.splash);

		return;
	}

	// If we're stopped, make sure the splash timer stops as well.
	protected void onStop() {
		super.onStop();
	}

	protected void onPause() {
		super.onPause();
		m_bPaused = true;
	}

	protected void onResume() {
		super.onResume();
		m_bPaused = false;
	}

	protected void onDestroy() {
		super.onDestroy();
	}
	
	//When you touch a key, clear the Splash Screen
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we get any key, clear the Splash Screen
		super.onKeyDown(keyCode, event);
		m_bSplashActive = false;
		return true;
	}
	
	//When you touch the screen, clear the Splash Screen
	public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        	m_bSplashActive = false;
        }
        return true;
    }
}