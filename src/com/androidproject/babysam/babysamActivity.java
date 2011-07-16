package com.androidproject.babysam;

import android.app.Activity;
import android.content.SharedPreferences;

public class babysamActivity extends Activity {
	public static final String APP_PREFERENCES = "AppPrefs";
	public static final String ST_SCAN = "Student_Scan";
	public static final String OF_SCAN = "Officials_Scan";
	public static final String EV_SCAN = "Event_Scan";
	public static final String URL_SCAN = "URL_Scan";
	public static final String DB_MODE = "DATA_mode";
	public static final String USER = "Current_User";
	public static final String EVENTID = "EventID";
	public static final String URI = "aries_link";
	public static final String [] T_SCAN = { ST_SCAN, OF_SCAN, EV_SCAN, URL_SCAN, DB_MODE};
	public static final String [] NEW_SET = { EVENTID, URI};
	
	public static final String TAG = "BabySAM";
	SharedPreferences eventSettings;
	
	
	
	
	
	
}