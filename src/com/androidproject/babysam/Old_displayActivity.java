package com.androidproject.babysam;


import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Old_displayActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
	
	private String [] intentExtra = new String [5];
	private String [] I_intExtra = new String [5];
	private String [] eventDetails = new String [5];
	private String upload, user;
	
    //defining array here
    private ArrayList<String[]> eventData = new ArrayList<String[]>();
    private ArrayList<String> offeventData = new ArrayList<String>();
    private ArrayList<String> stdeventData = new ArrayList<String>();
    private int DB_mode,age;
    private Long extra_EID;
    private functions f;
    
    private ProgressDialog progDialog; 
    private int typeBar;                        // Determines type progress bar: 0 = spinner, 1 = horizontal
	
    private String loadfail;//, loadinvalid, add_session, importxml, mod_listview;
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_display);
        LoadPref();
        loadMessages();
        
        TextView [] text = {(TextView) findViewById(R.id.Event_view),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView01),
    	    	(TextView) findViewById(R.id.TextView02)};
	    //Retrieve listview
	    ListView off = (ListView) findViewById(R.id.listView1);
	    ListView std = (ListView) findViewById(R.id.listView2);
	    f = new functions(this);
	    age = 1;
        
        //Log.i(TAG, " this is the intent "+ intentExtra[0]);
	    //get the event id from the intent that was passed
        Intent intent = getIntent();
        
        //Log.i(TAG," the extra is end" );        
        if (DB_mode == 0){
        	for (int i = 0; i < 5 ; i++){
	        	intentExtra[i]= "intExtra"+i ;
	        }
        	if ( intent != null){
	        	for (int i = 0; i < 5 ; i++){
	        		if (intent.hasExtra(intentExtra[i])) {
		        		I_intExtra[i] = intent.getStringExtra(intentExtra[i]);
	        		}
	        	}
	        }
	        // Retrieve XML
		    XmlResourceParser eventxml = getResources().getXml(R.xml.persons);
	        // setting the content of the text views
	        for (int i = 0; i < 4 ; i++){
	    		text[i].setText(I_intExtra[i+1]);
	    	}
	        
		    //primarily should return eventdata which has the content so the file
		    try {
		    	int y = Integer.parseInt(I_intExtra[0]);
		    	processData(eventxml, eventData, y);
		    } catch (Exception e) {
	            Log.e(TAG, loadfail, e);
	        }
		     
		    //put data in meventData after eventData comes back
		    //if you send eventdata to another class you will need a way to call it	
		    //organizing the data for the layout
		    int num = 0;
		    for( int i = 0; i < eventData.size(); i++){
		    	num = Integer.parseInt(eventData.get(i)[2]);
		    	//Log.i(TAG,"num "+ num );
		    	if (num == 2 )offeventData.add("  "+eventData.get(i)[3]);
		    	if (num == 1 )stdeventData.add("  "+eventData.get(i)[3]);
		    }		     
	    } else if (DB_mode == 1){
	    	if ( intent != null){	    		
	    		Log.d(TAG,"long extra"+intent.getLongExtra("EventID",1) );
		    	extra_EID = intent.getLongExtra("EventID",1);
		    	// - trying to introduce the functions object so i can re-use common methods
		    	//eventExtract();
		    	eventDetails = f.eventExtract(extra_EID);
		    	for (int i = 0; i < 4 ; i++){
		    		text[i].setText(eventDetails[i]);
		    	}
		    	offeventData=f.personExtract(extra_EID, 2);
		    	stdeventData=f.personExtract(extra_EID, 1);
	    	}
	    }        
        
	    ArrayAdapter<String> off_adapt = new ArrayAdapter<String>(this, R.layout.list_item, offeventData);        
	    ArrayAdapter<String> std_adapt = new ArrayAdapter<String>(this, R.layout.list_item, stdeventData);
	    off.setAdapter(off_adapt);
	    std.setAdapter(std_adapt);
	    Log.v(TAG,"End Activity" );        
    }  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.old_dis_options, menu);
	    menu.findItem(R.id.help_menu_item).setIntent( new Intent(this, HelpActivity.class));
	    return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	//this section will run the barcode scanner as an intent if student barcode scanning is enabled
		LoadPref();		
		switch(item.getItemId()) { 	
        	case R.id.event_aries:
	        	//f.sendAries(extra_EID,0,LoadUriPref());
        		upload = LoadUriPref();
        		if (DB_mode == 1)showDialog(typeBar);
            return true;
        	case R.id.event_email:
        		upload = "Mail";
        		if (DB_mode == 1)showDialog(typeBar);
            return true;
        	case R.id.event_file:
			String filename=f.saveasFile(extra_EID,user);
			if(!filename.equalsIgnoreCase(null))Toast.makeText(this, "File created in "+filename, Toast.LENGTH_LONG).show();	
            return true;
        }
		return super.onOptionsItemSelected(item);
    }
    
    private String LoadUriPref(){
    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return  eventSettings.getString("aries_link"," Enter URL plesae");
    }
    
    //Method for debugging purposes only uses local resource XML
    private void processData( XmlResourceParser event,ArrayList<String[]> eventData, int xeID) throws XmlPullParserException,IOException {
		int doceventType = -1;
		boolean bFoundEvents = false;   
	    Log.i(TAG," Method - processData for resource xml" );
		// Find Event records from XML
		while (doceventType != XmlResourceParser.END_DOCUMENT) {
		    if (doceventType == XmlResourceParser.START_TAG) {		    	
		        // Get the name of the tag (eg scores or score)
		    	//Log.i(TAG," 2" );
		        String strName = event.getName();
		        if (strName.equals("persons")) {
		            bFoundEvents = true;
		         //extracting information from xml
		            //int eventID = Integer.parseInt(event.getAttributeValue(null, "ID"));
		            String [] data = new String [5];
		            data[0] = event.getAttributeValue(null, "ID");
		            data[1] = event.getAttributeValue(null, "EID");
		            data[2] = event.getAttributeValue(null, "PType");
		            data[3] = event.getAttributeValue(null, "code");		            
		            data[4] = event.getAttributeValue(null, "timestamp");		            
		            int rnum = 0;
		            if (data[1] != null) rnum = Integer.parseInt(data[1]);
		           // String [] eventData = { Integer.toString(eventID), eventType,};
		           if (rnum == xeID){
		        	   eventData.add(0,data);
		           }		           
		        }
		    }
		    doceventType = event.next();
		}
		// Handle no events available
		if (bFoundEvents == false) {
			String [] data = {getResources().getString(R.string.no_data)};
			eventData.add(0,data);
		}
	}
        
  //to load all preferences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        DB_mode = eventSettings.getInt(DB_MODE, 1);
	        user = eventSettings.getString(USER, "");
    }
    
    protected Dialog onCreateDialog(int id) {
    	Log.i(TAG," diaolg start" );
        switch(id) {
        case 0:                      // Spinner
        	//Log.v(TAG," dialog start" );
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage(getResources().getString(R.string.send_email));
            ProgressThread progThread = new ProgressThread(handler, getApplicationContext(), extra_EID, eventDetails, upload, age, user);
            progThread.start();
            return progDialog;        
        default:
            return null;
        }
    }
    
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	//dismissDialog(typeBar);
        	removeDialog(typeBar);
        }
    };
    
    private void loadMessages() {
		//loadinvalid = getResources().getString(R.string.invalid_data);
		loadfail = getResources().getString(R.string.load_fail);
		/*add_session = getResources().getString(R.string.ses_detail);
		importxml = getResources().getString(R.string.import_xml);
		mod_listview = getResources().getString(R.string.modify_listview);*/
	}
}