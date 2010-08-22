package com.google.android.almanac;

import android.util.Log;

public class EasterException extends Exception {
	
	public EasterException(String msg) {
		Log.e("Almanac: Easter", msg);
	}

}
