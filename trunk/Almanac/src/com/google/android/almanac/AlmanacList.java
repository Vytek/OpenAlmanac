package com.google.android.almanac;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.Color;

import org.stardate.Stardate;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

public class AlmanacList extends Activity {
    
    private Calendar cal;
    private SQLiteDatabase db;
    private SaintDBEvent current;
    private Stardate m_stardate = null;
    private int m_phaseValue;
    private String[] strDays = null;
    private String[] strMonths = null;
    private double[] m_latlong;
    private static final String TAG = "AlmanacList";
    private static final String ALMANAC_DATABASE_NAME = "almanac.db";
    private static final String ALMANAC_DATABASE_TABLE = "Saints";
    private static final double MOON_PHASE_LENGTH = 29.530588853;
    
    private boolean mIsNorthernHemi;
   	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.almanaclist);
        
        //Crea un gradiente di colore in background
        //Create a gradient
        GradientDrawable grad = new GradientDrawable(
        		Orientation.TOP_BOTTOM,
        	    new int[] {Color.DKGRAY, Color.BLACK}
        );
        this.getWindow().setBackgroundDrawable(grad);
        
        //Data di oggi
        //Today date
        cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat formatPattern = new SimpleDateFormat("yyyy");
        String nowYearFormatted = formatPattern.format(date);
        
        //Data Stellare / Star Date (Start Trek)
        m_stardate = new Stardate();
    	m_stardate.setGregorian( new GregorianCalendar());
    	//m_stardate.toString();
    	
    	//Calcola Fase Lunare
    	//Compute Moon Phase
    	double phase = computeMoonPhase();        
        Log.i(TAG, "Computed moon phase: " + phase);
        
        int phaseValue = ((int) Math.floor(phase)) % 30;
        m_phaseValue = phaseValue;
        Log.i(TAG, "Discrete phase value: " + phaseValue);
        
        //Almanac for Saints
        AlmanacSQLiteDatabaseAdapter aSQLiteDatabaseAdapter = AlmanacSQLiteDatabaseAdapter.getInstance(this, ALMANAC_DATABASE_NAME);
        //OK Dovrei usare aSQLiteDatabaseAdapter.getDatabase(); ma questo crea
        //problemi nella fase di OnPause quando premo Back così invece non ottengo errori
        //e viene fatta la normale copia del DB (13/8/2010 23.18)
        //db = aSQLiteDatabaseAdapter.getWritableDatabase();
        db = aSQLiteDatabaseAdapter.getDatabase();
        Configuration conf = new Configuration();
        Settings.System.getConfiguration(getContentResolver(), conf);
        //String strTest = Integer.toString(cal.get(Calendar.DATE))+ "/" + Integer.toString(cal.get(Calendar.MONTH));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM");

        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        Log.d(TAG,reportDate);
       
        //current=SaintDBEvent.getByDateAndLang(strTest, conf.locale.getLanguage(), db); //locale
        current=SaintDBEvent.getByDateAndLang(reportDate, "it", db);
        
    	//Get GPS Location!
    	AlmanacUtility almanac=AlmanacUtility.getInstance();
    	m_latlong=almanac.getGPS(this);
    	Log.d(TAG,"Lat: "+Double.toString(m_latlong[0]));
    	Log.d(TAG,"Long: "+Double.toString(m_latlong[1]));
    	//Calcola Sunrise/Sunset
    	//Location of sunrise/set, as latitude/longitude.  
    	Location location = new Location(Double.toString(m_latlong[0]), Double.toString(m_latlong[1]));  
    	   
    	// Create calculator object with the location and time zone identifier.  
    	SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, cal.getTimeZone().getDisplayName(conf.locale));  
    	Log.d(TAG, "TimeZone: "+cal.getTimeZone().getDisplayName(conf.locale));
    	
    	//Calendar date = Calendar.getInstance();  
    	String dawn = calculator.getCivilSunriseForDate(cal);  
    	String dusk = calculator.getCivilSunsetForDate(cal);
    	//String dawn = calculator.getOfficialSunriseForDate(cal);
    	//String dusk = calculator.getOfficialSunsetForDate(cal);
    	Log.d(TAG,"CivilSunrise: "+dawn);
    	Log.d(TAG,"CivilSunset: "+dusk);
    	    	
        //Create an array of days
    	strDays = getResources().getStringArray(R.array.daysofweek_label);
      	
        //Create an array of months
    	strMonths = getResources().getStringArray(R.array.monthsofyear_label);
    	                
        //Lista degli eventi che la listview visualizzerà 
        ArrayList<Event> eventList=new ArrayList<Event>(); 
              
        Event [] events={
        		new Event(getResources().getString(R.string.todayis_label), strDays[cal.get(Calendar.DAY_OF_WEEK) - 1]+", "+
        				cal.get(Calendar.DATE)+" "+
        				strMonths[cal.get(Calendar.MONTH)]+
        				" "+nowYearFormatted, R.drawable.clock),
        		new Event(getResources().getString(R.string.stardate_label), m_stardate.toStardateString() , R.drawable.treklogo),
        		new Event(current.getSaintName(),current.getSaintDescription(),R.drawable.angel),
        		//new Event(getResources().getString(R.string.saintofday_label),"Santa Elisabetta d'Ungheria",R.drawable.creep_3),
        		new Event(getResources().getString(R.string.sunrisesunsite_label),dawn+","+dusk,R.drawable.sunrise),
        		new Event(getResources().getString(R.string.moonphase_label),getResources().getString(getPhaseText(phaseValue)),IMAGE_LOOKUP[phaseValue]),
        		new Event("Vytek","Scheflo",R.drawable.creep_2)
        		};
        
        //riempimento casuale della lista delle persone
        //Random r=new Random();
        for(int i=0;i<6;i++){
        	//eventList.add(events[r.nextInt(events.length)]);
        	eventList.add(events[i]);
        }    
       
        //Questa e' la lista che rappresenta la sorgente dei dati della listview
        //ogni elemento e' una mappa(chiave->valore)
        ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
        
        
        for(int i=0;i<eventList.size();i++){
        	Event p=eventList.get(i);// per ogni persona all'inteno della ditta
        	
        	HashMap<String,Object> eventMap=new HashMap<String, Object>();//creiamo una mappa di valori
        	
        	eventMap.put("image", p.getPhotoRes()); // per la chiave image, inseriamo la risorsa dell immagine
        	eventMap.put("event", p.getEventName()); // per la chiave name,l'informazine sul nome
        	eventMap.put("description", p.getDescription());// per la chiave surnaname, l'informazione sul cognome
        	data.add(eventMap);  //aggiungiamo la mappa di valori alla sorgente dati
        }
       
        
        String[] from={"image","event","description"}; //dai valori contenuti in queste chiavi
        int[] to={R.id.eventImage,R.id.eventName,R.id.eventDescription};//agli id dei layout
        
        //Costruzione dell' adapter
        SimpleAdapter adapter=new SimpleAdapter(
        		getApplicationContext(),
        		data,//sorgente dati
        		R.layout.event_item, //layout contenente gli id di "to"
        		from,
        		to);
       
        //Utilizzo dell'adapter
        ((ListView)findViewById(R.id.eventListView)).setAdapter(adapter);
    }
	
	//Close db onDestroy
	//Chiudi il db quando viene distrutta l'Activity
	@Override
    public void onDestroy() {
         super.onDestroy();
         
         db.close();
    }
	
	@Override
	public void onPause() {
		super.onPause();
		
		db.close();
	}
	
	//Function to copy to Clipboard
	//Funzione per copiare i dati generati nella clipboard
	private String CopyToClipboard()
	{
		StringBuffer strBuffer = new StringBuffer();

		strBuffer.append(getResources().getString(R.string.todayis_label));
		strBuffer.append(" " +  strDays[cal.get(Calendar.DAY_OF_WEEK) - 1]+", "+
		cal.get(Calendar.DATE)+" "+strMonths[cal.get(Calendar.MONTH)]+"\n");
		//" "+nowYearFormatted;
		strBuffer.append(getResources().getString(R.string.stardate_label));
		strBuffer.append(" " + m_stardate.toStardateString()+"\n");
		strBuffer.append(current.getSaintName()+" "+current.getSaintDescription()+"\n");
		strBuffer.append(getResources().getString(R.string.moonphase_label)+" "+getResources().getString(getPhaseText(m_phaseValue)));
		
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
	
	  // Computes moon phase based upon Bradley E. Schaefer's moon phase algorithm.
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
	   double term3 = Math.floor(Math.floor((transformedYear / 100) + 49) * 0.75) - 38;
	   
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
	  
	  //Return the right image for Moon Pahse
	  private static final int [] IMAGE_LOOKUP = {
	    R.drawable.moon0,
	    R.drawable.moon1,
	    R.drawable.moon2,
	    R.drawable.moon3,
	    R.drawable.moon4,
	    R.drawable.moon5,
	    R.drawable.moon6,
	    R.drawable.moon7,
	    R.drawable.moon8,
	    R.drawable.moon9,
	    R.drawable.moon10,
	    R.drawable.moon11,
	    R.drawable.moon12,
	    R.drawable.moon13,
	    R.drawable.moon14,
	    R.drawable.moon15,
	    R.drawable.moon16,
	    R.drawable.moon17,
	    R.drawable.moon18,
	    R.drawable.moon19,    
	    R.drawable.moon20,
	    R.drawable.moon21,
	    R.drawable.moon22,
	    R.drawable.moon23,
	    R.drawable.moon24,
	    R.drawable.moon25,
	    R.drawable.moon26,
	    R.drawable.moon27,
	    R.drawable.moon28,
	    R.drawable.moon29,
	  };
    
    private static final int MENUITEM_TODAY_ID = 0;
    private static final int MENUITEM_CHOOSE_DATE_ID = 1;
    private static final int MENUITEM_MAKE_SCREENSHOT_ID = 2;
    private static final int MENUITEM_TEXT_TO_SPEECH_ID = 3;
    private static final int MENUITEM_COPY_TO_CLIPBOARD_ID = 4;
    
    //Main menu for AlmanList Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
            
      MenuItem item;    
      item = menu.add(0, MENUITEM_TODAY_ID, 0, getResources().getString(R.string.today_label));           
      item.setIcon(android.R.drawable.ic_menu_today);
      item = menu.add(0, MENUITEM_CHOOSE_DATE_ID, 0, getResources().getString(R.string.choose_date_label));           
      item.setIcon(android.R.drawable.ic_menu_month);
      item = menu.add(0, MENUITEM_MAKE_SCREENSHOT_ID, 0, getResources().getString(R.string.screenshot_label));           
      item.setIcon(android.R.drawable.ic_menu_camera);
      item = menu.add(0, MENUITEM_TEXT_TO_SPEECH_ID, 0, getResources().getString(R.string.vocalize_label));           
      item.setIcon(R.drawable.flash);
      item = menu.add(0, MENUITEM_COPY_TO_CLIPBOARD_ID, 0, getResources().getString(R.string.copytoclipboard_label));           
      item.setIcon(R.drawable.copy);
      
      return true;
    }    
       
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
      
    	//setContentView(R.layout.almanaclist);
    	
    	Context context = this.getApplicationContext();
    	View view = null;
    	String appName = getString(R.string.app_name);
    	
    	switch (item.getItemId()) {
        case MENUITEM_TODAY_ID:
        	Toast.makeText(this, getResources().getString(R.string.notyetimplemented_label), Toast.LENGTH_SHORT).show();
          return true;
        case MENUITEM_CHOOSE_DATE_ID:
        	Toast.makeText(this, getResources().getString(R.string.notyetimplemented_label), Toast.LENGTH_SHORT).show();     
          return true;
        case MENUITEM_MAKE_SCREENSHOT_ID:     	
        	view = (ListView)findViewById(R.id.eventListView);
        	//Funziona benissimo!
        	//Works well!
        	AlmanacScreenShot.shot(context, view.getRootView(), appName);
          return true;
        case MENUITEM_TEXT_TO_SPEECH_ID:
        	Toast.makeText(this, getResources().getString(R.string.notyetimplemented_label), Toast.LENGTH_SHORT).show();	
          return true;
        case MENUITEM_COPY_TO_CLIPBOARD_ID:
        	ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
        	clipboard.setText(CopyToClipboard());
        	Toast.makeText(this, getResources().getString(R.string.copiedtoclipboard_label), Toast.LENGTH_SHORT).show();	
          return true;	
          
      }

      return super.onOptionsItemSelected(item);
    } 
}