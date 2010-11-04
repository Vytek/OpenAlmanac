package it.almanac;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.Color;

import org.stardate.Stardate;
import it.almanac.R;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import astroLib.HijriCalendar;

public class AlmanacList extends Activity {

	private Calendar cal;
	private SQLiteDatabase db;
	private SaintDBEvent current;
	private Stardate m_stardate = null;
	private int m_phaseValue;
	private String[] strDays = null;
	private String[] strMonths = null;
	private String dawn = null;
	private String dusk = null;
	private String hijriDate = null;
	private double lat;
	private double lng;
	private ArrayList<HashMap<String, Object>> data;
	private SimpleAdapter adapter;
	private static final String TAG = "AlmanacList";
	private static final String ALMANAC_DATABASE_NAME = "almanac.db";
	//	private static final String ALMANAC_DATABASE_TABLE = "Saints";
	private static final double MOON_PHASE_LENGTH = 29.530588853;

	private LocationManager locationManager;
	private final static long MIN_TIME = 10 * 60000; //10 minuti
	private final static float MIN_DIST = 1000; //1000 metri
	//	private boolean mIsNorthernHemi = true;

	/**
	 * get if this is the first run
	 * 
	 * @return returns true, if this is the first run
	 */
	public boolean getFirstRun() {
		return mPrefs.getBoolean("firstRun", true);
	}
	
	/**
	 * get the Latitude Pref 
	 * 
	 * @return returns String Latitude Prefs
	 */
	public String getLatPrefs(){
		return mPrefs.getString("Latitude", "0.0");
	}
	
	/**
	 * get the Longitude Pref 
	 * 
	 * @return returns String Latitude Prefs
	 */
	public String getLongPrefs(){
		return mPrefs.getString("Longitude", "0.0");
	}

	/**
	 * store the first run
	 */
	public void setRunned() {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putBoolean("firstRun", false);
		edit.commit();
	}

	SharedPreferences mPrefs;

	/**
	 * setting up preferences storage
	 */
	public void firstRunPreferences() {
		Context mContext = this.getApplicationContext();
		mPrefs = mContext.getSharedPreferences("AlmanacPrefs", 0); // 0 = mode
		// private:
		// only this app can
		// read these preferences
	}

	public static final Criteria getCriteria(){
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setSpeedRequired(false);

		return criteria;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.almanaclist);

		// Attiva controllo FirstRun
		// Activate check FirstRun
		firstRunPreferences();

		// *********************
		// Get GPS with Listener
		// *********************
		// Get a reference to the LocationManager.	  
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String provider = locationManager.getBestProvider(getCriteria(), true);

		Log.d(TAG, "Criteria: " + provider);
		// Registriamo il LocationListener al provider GPS
		if (provider != null) {
			locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DIST, locationListener);
		}else{
			//Nessun provider trovato
			//Do un messaggio d'errore
		}

		// Crea un gradiente di colore in background
		// Create a background gradient
		GradientDrawable grad = new GradientDrawable(Orientation.TOP_BOTTOM,
				new int[] { Color.DKGRAY, Color.BLACK });
		this.getWindow().setBackgroundDrawable(grad);

		// Data di oggi
		// Today date
		cal = Calendar.getInstance();
		Date date = cal.getTime();
		SimpleDateFormat formatPattern = new SimpleDateFormat("yyyy");
		String nowYearFormatted = formatPattern.format(date);

		// Data Stellare 
		// Star Date (Star Trek)
		m_stardate = new Stardate();
		m_stardate.setGregorian(new GregorianCalendar());
		// m_stardate.toString();

		// Calcola Fase Lunare
		// Compute Moon Phase
		double phase = computeMoonPhase();
		Log.i(TAG, "Computed moon phase: " + phase);

		int phaseValue = ((int) Math.floor(phase)) % 30;
		m_phaseValue = phaseValue;
		Log.i(TAG, "Discrete phase value: " + phaseValue);

		// Almanac for Saints
		AlmanacSQLiteDatabaseAdapter aSQLiteDatabaseAdapter = AlmanacSQLiteDatabaseAdapter
		.getInstance(this, ALMANAC_DATABASE_NAME);
		// OK Dovrei usare aSQLiteDatabaseAdapter.getDatabase(); ma questo crea
		// problemi nella fase di OnPause quando premo Back cosi' invece non
		// ottengo errori
		// e viene fatta la normale copia del DB (13/8/2010 23.18)
		// db = aSQLiteDatabaseAdapter.getWritableDatabase();
		// Per ovviare a questo problema controllo se e' la prima volta che
		// chiamo applicazione
		if (getFirstRun()) {
			db = aSQLiteDatabaseAdapter.getDatabase();
			setRunned();
		} else {
			db = aSQLiteDatabaseAdapter.getWritableDatabase();
		}

		Log.d(TAG, "Day: " + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
		Log.d(TAG, "Month: " + Integer.toString(cal.get(Calendar.MONTH) + 1));
		// current=SaintDBEvent.getByDateAndLang(strTest,
		// conf.locale.getLanguage(), db); //locale
		current = SaintDBEvent.getByDateAndLang(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), 
				Integer.toString(cal.get(Calendar.MONTH) + 1) , "it", db);

		// Get GPS Location!
		//AlmanacUtility almanac = AlmanacUtility.getInstance();

		//Log.d(TAG, "Lat: " + Double.toString(Almanaclocation.getLatitude()));
		//Log.d(TAG, "Long: " + Double.toString(Almanaclocation.getLongitude()));
		// Calcola Sunrise/Sunset
		//Metto una location di default (in pratica sara' quella che l'utente
		//sceglie nelle preference)
		Location location = new Location(getLatPrefs(), getLongPrefs());
		SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
				location, cal.getTimeZone().getID());
		Log.d(TAG, "TimeZone Correct: " + cal.getTimeZone().getID());

		// Calendar date = Calendar.getInstance();
		dawn = calculator.getCivilSunriseForDate(cal);
		dusk = calculator.getCivilSunsetForDate(cal);
		// String dawn = calculator.getOfficialSunriseForDate(cal);
		// String dusk = calculator.getOfficialSunsetForDate(cal);
		Log.d(TAG, "CivilSunrise: " + dawn);
		Log.d(TAG, "CivilSunset: " + dusk);

		// Calcola il calendario Islamico
		// Get Hijri Calendar
		HijriCalendar hijriCalendar = new HijriCalendar(cal.get(Calendar.YEAR),
				(cal.get(Calendar.MONTH) + 1),cal.get(Calendar.DATE));
		Log.d(TAG, "Islamic data insert: "+Integer.toString(cal.get(Calendar.YEAR))+","+
				Integer.toString(cal.get(Calendar.MONTH) + 1)+","+
				Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
		hijriDate = hijriCalendar.getHicriTakvim();

		// Create an array of days
		strDays = getResources().getStringArray(R.array.daysofweek_label);

		// Create an array of months
		strMonths = getResources().getStringArray(R.array.monthsofyear_label);

		// Lista degli eventi che la listview visualizzera'
		ArrayList<Event> eventList = new ArrayList<Event>();

		eventList.add(new Event(getResources().getString(R.string.todayis_label),
				strDays[cal.get(Calendar.DAY_OF_WEEK) - 1] + ", "
				+ cal.get(Calendar.DATE) + " "
				+ strMonths[cal.get(Calendar.MONTH)] + " "
				+ nowYearFormatted, R.drawable.clock));
		eventList.add(new Event(getResources().getString(R.string.stardate_label),
				m_stardate.toStardateString(), R.drawable.treklogo));
		eventList.add(new Event(getResources().getString(R.string.islamic_calendar_label),					
				hijriDate, R.drawable.islamlogo));
		eventList.add(new Event(current.getSaintName(),
				current.getSaintDescription(), R.drawable.angel));
		eventList.add(new Event(getResources().getString(
				R.string.sunrisesunsite_label), dawn + "," + dusk,
				R.drawable.sunrise));
		eventList.add(new Event(getResources().getString(R.string.moonphase_label),
				getResources().getString(getPhaseText(phaseValue)),
				IMAGE_LOOKUP[phaseValue]));


		// Questa e' la lista che rappresenta la sorgente dei dati della
		// listview
		// ogni elemento e' una mappa(chiave->valore)
		data = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < eventList.size(); i++) {
			Event p = eventList.get(i);// per ogni evento

			HashMap<String, Object> eventMap = new HashMap<String, Object>();// creiamo
			// una
			// mappa
			// di
			// valori

			eventMap.put("image", p.getPhotoRes()); // per la chiave image,
			// inseriamo la risorsa dell
			// immagine
			eventMap.put("event", p.getEventName()); // per la chiave
			// event,l'informazine
			// sul EventName
			eventMap.put("description", p.getDescription());// per la chiave
			// description,
			// l'informazione
			// sul Description
			data.add(eventMap); // aggiungiamo la mappa di valori alla sorgente
			// dati
		}

		String[] from = { "image", "event", "description" }; // dai valori
		// contenuti in
		// queste chiavi
		int[] to = { R.id.eventImage, R.id.eventName, R.id.eventDescription };// agli
		// id
		// dei
		// layout

		// Costruzione dell' adapter
		adapter = new SimpleAdapter(getApplicationContext(),
				data,// sorgente dati
				R.layout.event_item, // layout contenente gli id di "to"
				from, to);

		// Utilizzo dell'adapter
		((ListView) findViewById(R.id.eventListView)).setAdapter(adapter);

		//Esempio modifica dati nell'adapter
		//data.get(4).put("description", "pippo");
		//adapter.notifyDataSetChanged();
	}

	//Metodi per GPS LocationListener
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(android.location.Location location) {

			// Geocode your current location to find an address.
			String latLongString = "";

			if (location != null) {
				lat = location.getLatitude();
				lng = location.getLongitude();
				latLongString = "Lat:" + lat + "\nLong:" + lng;
				Log.d(TAG, latLongString);
				//m_latlong = {lat,lng};
				// Calcola Sunrise/Sunset
				// Location of sunrise/set, as latitude/longitude.
				Location Almanaclocation = new Location(Double.toString(lat), Double.toString(lng));
				SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
						Almanaclocation, cal.getTimeZone().getID());
				Log.d(TAG, "TimeZone Correct: " + cal.getTimeZone().getID());

				// Calendar date = Calendar.getInstance();
				dawn = calculator.getCivilSunriseForDate(cal);
				dusk = calculator.getCivilSunsetForDate(cal);
				// String dawn = calculator.getOfficialSunriseForDate(cal);
				// String dusk = calculator.getOfficialSunsetForDate(cal);
				Log.d(TAG, "CivilSunrise: " + dawn);
				Log.d(TAG, "CivilSunset: " + dusk); 

				//Modifico con i dati necessari
				data.get(4).put("description", dawn + "," + dusk);
				adapter.notifyDataSetChanged();

				//Finito di aver acquisito i dati spengo
				//il listener in modo da non occupare troppe risorse
				locationManager.removeUpdates(locationListener);
			} else {
				latLongString = "No location found";
				Log.d(TAG, latLongString);
			}

		}

		@Override
		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	};	

	// Close db onDestroy
	// Chiudi il db quando viene distrutta l'Activity
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Close db
		db.close();
	}

	@Override
	public void onPause() {
		super.onPause();
		//Close db
		db.close();
	}

	@Override 
	public void onStop() {
		//	    // Unregister the LocationListener to stop updating the
		//	    // GUI when the Activity isn't visible.
		//	    locationManager.removeUpdates(locationListener);
		super.onStop();
	}

	// Function to copy to Clipboard
	// Funzione per copiare i dati generati nella clipboard
	private String CopyToClipboard() {
		StringBuffer strBuffer = new StringBuffer();

		strBuffer.append(getResources().getString(R.string.todayis_label));
		strBuffer.append(" " + strDays[cal.get(Calendar.DAY_OF_WEEK) - 1]
		                               + ", " + cal.get(Calendar.DATE) + " "
		                               + strMonths[cal.get(Calendar.MONTH)] + "\n");
		// " "+nowYearFormatted;
		strBuffer.append(getResources().getString(R.string.islamic_calendar_label));
		strBuffer.append(" " + hijriDate + "\n");
		strBuffer.append(getResources().getString(R.string.stardate_label));
		strBuffer.append(" " + m_stardate.toStardateString() + "\n");
		strBuffer.append(current.getSaintName() + " "
				+ current.getSaintDescription() + "\n");
		strBuffer.append(getResources().getString(
				R.string.sunrisesunsite_label) + " " + dawn + "," + dusk + "\n");
		strBuffer.append(getResources().getString(R.string.moonphase_label)
				+ " " + getResources().getString(getPhaseText(m_phaseValue)));

		return strBuffer.toString();
	}

	private int getPhaseText(int phaseValue) {
		if (phaseValue == 0) {
			return R.string.new_moon;
		} else if (phaseValue > 0 && phaseValue < 7) {
			return R.string.waxing_crescent;
		} else if (phaseValue == 7) {
			return R.string.first_quarter;
		} else if (phaseValue > 7 && phaseValue < 15) {
			return R.string.waxing_gibbous;
		} else if (phaseValue == 15) {
			return R.string.full_moon;
		} else if (phaseValue > 15 && phaseValue < 23) {
			return R.string.waning_gibbous;
		} else if (phaseValue == 23) {
			return R.string.third_quarter;
		} else {
			return R.string.waning_crescent;
		}
	}

	// Computes moon phase based upon Bradley E. Schaefer's moon phase
	// algorithm.
	private double computeMoonPhase() {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// Convert the year into the format expected by the algorithm.
		double transformedYear = year - Math.floor((12 - month) / 10);
		Log.i(TAG, "transformedYear: " + transformedYear);

		// Convert the month into the format expected by the algorithm.
		int transformedMonth = month + 9;
		if (transformedMonth >= 12) {
			transformedMonth = transformedMonth - 12;
		}
		Log.i(TAG, "transformedMonth: " + transformedMonth);

		// Logic to compute moon phase as a fraction between 0 and 1
		double term1 = Math.floor(365.25 * (transformedYear + 4712));
		double term2 = Math.floor(30.6 * transformedMonth + 0.5);
		double term3 = Math
		.floor(Math.floor((transformedYear / 100) + 49) * 0.75) - 38;

		double intermediate = term1 + term2 + day + 59;
		if (intermediate > 2299160) {
			intermediate = intermediate - term3;
		}
		Log.i(TAG, "intermediate: " + intermediate);

		double normalizedPhase = (intermediate - 2451550.1) / MOON_PHASE_LENGTH;
		normalizedPhase = normalizedPhase - Math.floor(normalizedPhase);
		if (normalizedPhase < 0) {
			normalizedPhase = normalizedPhase + 1;
		}
		Log.i(TAG, "normalizedPhase: " + normalizedPhase);

		// Return the result as a value between 0 and MOON_PHASE_LENGTH
		return normalizedPhase * MOON_PHASE_LENGTH;
	}

	// Return the right image for Moon Pahse
	private static final int[] IMAGE_LOOKUP = { R.drawable.moon0,
		R.drawable.moon1, R.drawable.moon2, R.drawable.moon3,
		R.drawable.moon4, R.drawable.moon5, R.drawable.moon6,
		R.drawable.moon7, R.drawable.moon8, R.drawable.moon9,
		R.drawable.moon10, R.drawable.moon11, R.drawable.moon12,
		R.drawable.moon13, R.drawable.moon14, R.drawable.moon15,
		R.drawable.moon16, R.drawable.moon17, R.drawable.moon18,
		R.drawable.moon19, R.drawable.moon20, R.drawable.moon21,
		R.drawable.moon22, R.drawable.moon23, R.drawable.moon24,
		R.drawable.moon25, R.drawable.moon26, R.drawable.moon27,
		R.drawable.moon28, R.drawable.moon29, };

	private static final int MENUITEM_TODAY_ID = 0;
	private static final int MENUITEM_CHOOSE_DATE_ID = 1;
	private static final int MENUITEM_MAKE_SCREENSHOT_ID = 2;
	private static final int MENUITEM_TEXT_TO_SPEECH_ID = 3;
	private static final int MENUITEM_COPY_TO_CLIPBOARD_ID = 4;
	private static final int MENUITEM_OPTIONS_ID = 5;
	
	// Main menu for AlmanList Activity
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item;
		item = menu.add(0, MENUITEM_TODAY_ID, 0, getResources().getString(
				R.string.today_label));
		item.setIcon(android.R.drawable.ic_menu_today);
		item = menu.add(0, MENUITEM_CHOOSE_DATE_ID, 0, getResources()
				.getString(R.string.choose_date_label));
		item.setIcon(android.R.drawable.ic_menu_month);
		item = menu.add(0, MENUITEM_MAKE_SCREENSHOT_ID, 0, getResources()
				.getString(R.string.screenshot_label));
		item.setIcon(android.R.drawable.ic_menu_camera);
		item = menu.add(0, MENUITEM_TEXT_TO_SPEECH_ID, 0, getResources()
				.getString(R.string.vocalize_label));
		item.setIcon(R.drawable.flash);
		item = menu.add(0, MENUITEM_COPY_TO_CLIPBOARD_ID, 0, getResources()
				.getString(R.string.copytoclipboard_label));
		item.setIcon(R.drawable.copy);
		item = menu.add(0, MENUITEM_OPTIONS_ID, 0, getResources()
				.getString(R.string.options_label));
		item.setIcon(android.R.drawable.ic_menu_preferences);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// setContentView(R.layout.almanaclist);

		Context context = this.getApplicationContext();
		View view = null;
		String appName = getString(R.string.app_name);

		switch (item.getItemId()) {
		case MENUITEM_TODAY_ID:
			Toast.makeText(this,
					getResources().getString(R.string.notyetimplemented_label),
					Toast.LENGTH_SHORT).show();
			return true;
		case MENUITEM_CHOOSE_DATE_ID:
			Toast.makeText(this,
					getResources().getString(R.string.notyetimplemented_label),
					Toast.LENGTH_SHORT).show();
			return true;
		case MENUITEM_MAKE_SCREENSHOT_ID:
			view = (ListView) findViewById(R.id.eventListView);
			// Funziona benissimo!
			// Works well!
			AlmanacScreenShot.shot(context, view.getRootView(), appName);
			return true;
		case MENUITEM_TEXT_TO_SPEECH_ID:
			Toast.makeText(this,
					getResources().getString(R.string.notyetimplemented_label),
					Toast.LENGTH_SHORT).show();
			return true;
		case MENUITEM_COPY_TO_CLIPBOARD_ID:
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(CopyToClipboard());
			Toast.makeText(this,
					getResources().getString(R.string.copiedtoclipboard_label),
					Toast.LENGTH_SHORT).show();
			return true;
		case MENUITEM_OPTIONS_ID:
			startActivity(new Intent(this, AlmanacPreferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}