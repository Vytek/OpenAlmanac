package it.almanac;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/*
 * To call this call:
 * 
 * AlmanacUtility almanac=AlmanacUtility.getInstance();
 * alamanc.isSdPresent();
 *  
 */

public class AlmanacUtility {
	//
	private static AlmanacUtility instance = new AlmanacUtility();

	//
	private AlmanacUtility() {}

	//
	public static AlmanacUtility getInstance() {
	      return instance;
	   }
	
	public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
	}
	
	//Sistema per visualizzare file asset HTML
	public View dialogWebView(Context context, String fileName) {
		View view = View.inflate(context, R.layout.dialog_webview, null);
		WebView web = (WebView) view.findViewById(R.id.wv_dialog);
		web.loadUrl("file:///android_asset/"+fileName);
		return view;
	}
	
	// La scheda SD e' presente?
	/*
	 * @return boolean return true if the application can access the SDCARD on
	 * phone
	 */
	// Modify from: http://www.androidsnippets.org/snippets/10/
	public boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	//Calcola la differenza in giorni tra due date
	/*
	 * @return integer calculate days between 2 given dates 
	 */
	//net.javaiq.examples.date
	// Modify from: http://www.javaiq.net/examples/date/Days-Between-Calculator-java-code.htm
	public int getDaysBetween(java.util.Date date1, java.util.Date date2) {
        if (date1 == null || date2 == null) {
            return -1;
        }

        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTime(date1);

        GregorianCalendar gc2 = new GregorianCalendar();
        gc2.setTime(date2);


        if (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)) {
            return Math.abs(gc1.get(Calendar.DAY_OF_YEAR) - gc2.get(Calendar.DAY_OF_YEAR));
        }

        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long days = (time1 - time2) / (1000 * 60 * 60 * 24);

        return Math.abs((int)days);
    }
	
	// Calcolo età
	/*
	 * @return int return age from date (year, month, day)
	 */
	// http://www.androidsnippets.org/snippets/179/
	public int getAge(int _year, int _month, int _day) {
		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(_year, _month, _day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH))
				|| ((m == cal.get(Calendar.MONTH)) && (d < cal
						.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if (a < 0)
			throw new IllegalArgumentException("Age < 0");
		return a;
	}

	/*
	 * @return string with all SDK Info
	 */
	// Modify from: http://www.androidsnippets.org/snippets/190/
	public StringBuffer SDKVersionInfo() {
		StringBuffer buf = new StringBuffer();
		buf.append("VERSION.RELEASE {" + Build.VERSION.RELEASE + "}");
		buf.append("\nVERSION.INCREMENTAL {" + Build.VERSION.INCREMENTAL + "}");
		buf.append("\nVERSION.SDK {" + Build.VERSION.SDK + "}");
		buf.append("\nBOARD {" + Build.BOARD + "}");
		buf.append("\nBRAND {" + Build.BRAND + "}");
		buf.append("\nDEVICE {" + Build.DEVICE + "}");
		buf.append("\nFINGERPRINT {" + Build.FINGERPRINT + "}");
		buf.append("\nHOST {" + Build.HOST + "}");
		buf.append("\nID {" + Build.ID + "}");
		return buf;
	}

	/*
	 * @return boolean return true if the application can access the network
	 */
	// Modify from: http://www.androidsnippets.org/snippets/78/
	public boolean isNetworkAvailable(Context context) {
		// Remember on: Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.e("AlmanacUtility:Error", "getSystemService rend null");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * @return boolean return true if the application can access the internet
	 */
	// TO DO: Da rivedere questa funzione...
	// Modify from: http://www.androidsnippets.org/snippets/131/
	public boolean haveInternet(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}
		return true;
	}

	/*
	 * @return double[] return the GPS Long, Lat of last location
	 */
	// TO DO: Da rivedere questa funzione...(forse inutile se GPS spento :-(
	// Modify from: http://www.androidsnippets.org/snippets/21/
	public double[] getGPS(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// This fetches a list of available location providers
		List<String> providers = lm.getProviders(true);

		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}

		double[] gps = new double[2];
		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}

	/*
	 * @return boolean return if GPS in enable
	 */
	// Modify from: http://www.androidsnippets.org/snippets/168/
	public boolean isLocationServiceAvaiable(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		if (providers.size() > 0)
			return true;
		else
			return false;
	}

	/*
	 * @return string return the transformation from int to IP
	 */
	private String intToIp(int i) {

		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}

	/*
	 * @return String return device IP
	 */
	// Modify from: http://www.androidsnippets.org/snippets/182/
	public String getLocalIP(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return intToIp(ipAddress);
	}
}
