package com.google.android.almanac;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.os.Bundle;
import android.content.Intent;

public class getSplashy extends Activity {
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
						Log.d("Almanac:Debug", "While cicle");
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
					Log.e("Almanac: Splash", e.toString());
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if we get any key, clear the Splash Screen
		super.onKeyDown(keyCode, event);
		m_bSplashActive = false;
		return true;
	}

}