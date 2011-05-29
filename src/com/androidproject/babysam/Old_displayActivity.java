package com.androidproject.babysam;


import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Old_displayActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
	
	private String [] intentExtra = new String [5];
	private String [] I_intExtra = new String [5];
	private String [] eventDetails = new String [4];
	
    //defining array here
    private ArrayList<String[]> eventData = new ArrayList<String[]>();
    private ArrayList<String> offeventData = new ArrayList<String>();
    private ArrayList<String> stdeventData = new ArrayList<String>();
    private int DB_mode;
    //private final ArrayList<Long> ofRowID = new ArrayList<Long>();
    //private final ArrayList<Long> stRowID = new ArrayList<Long>();
    private Long extra_EID;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_display);
        LoadPref();
        TextView [] text = {(TextView) findViewById(R.id.Event_view),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView01),
    	    	(TextView) findViewById(R.id.TextView02)};
	    //Retrieve listview
	    ListView off = (ListView) findViewById(R.id.listView1);
	    ListView std = (ListView) findViewById(R.id.listView2);
	    
	   
        
        //Log.i(TAG, " this is the intent "+ intentExtra[0]);
	    //get the event id from the intent that was passed
        Intent intent = getIntent();
        
        Log.i(TAG," the extra is end" );
        
        if (DB_mode == 0){
        	for (int i = 0; i < 5 ; i++){
	        	intentExtra[i]= "intExtra"+i ;
	        	//Log.i(TAG, " this is the intent "+ intentExtra[i]);
	        }
        	if ( intent != null){
	        	for (int i = 0; i < 5 ; i++){
	        		if (intent.hasExtra(intentExtra[i])) {
		        		I_intExtra[i] = intent.getStringExtra(intentExtra[i]);
		        		//Log.i(TAG," the extra is " + I_intExtra[i] );
	        		}
	        	}
	        }
	        // Retrieve XML
		    XmlResourceParser eventxml = getResources().getXml(R.xml.persons);
	        // setting the content of the textviews
	        for (int i = 0; i < 4 ; i++){
	    		text[i].setText(I_intExtra[i+1]);
	    	}
	        
		    //primarily should return eventdata which has the content so the file
		    try {
		    	int y = Integer.parseInt(I_intExtra[0]);
		    	//Log.i(TAG," 1" );
		    	processData(eventxml, eventData, y);
		    } catch (Exception e) {
	            Log.e(TAG, "Failed to load Events", e);
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
		    for( int i = 0; i < offeventData.size(); i++)Log.i(TAG,"2 "+ offeventData.get(i) );
		    for( int i = 0; i < stdeventData.size(); i++)Log.i(TAG,"1 "+ stdeventData.get(i) );
	    
	    } else if (DB_mode == 1){
	    	if ( intent != null){	    		
	    		Log.i(TAG,"long extra"+intent.getLongExtra("EventID",1) );
		    	extra_EID = intent.getLongExtra("EventID",1);
		    	eventExtract();
		    	for (int i = 0; i < 4 ; i++){
		    		text[i].setText(eventDetails[i]);
		    	}
		    	personExtract();
	    	}
	    }        
        
	    ArrayAdapter<String> off_adapt = new ArrayAdapter<String>(this, R.layout.list_item, offeventData);        
	    ArrayAdapter<String> std_adapt = new ArrayAdapter<String>(this, R.layout.list_item, stdeventData);
	    off.setAdapter(off_adapt);
	    std.setAdapter(std_adapt);
	    //adapt.notifyDataSetChanged();
	    Log.i(TAG,"3 After call" );        
    }       
    
    private void processData( XmlResourceParser event,ArrayList<String[]> eventData, int xeID) throws XmlPullParserException,IOException {
		int doceventType = -1;
		boolean bFoundEvents = false;   
	    //int xnum = 0;
	    //Log.i(TAG," 1" );
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
		
		// Handle no scores available
		if (bFoundEvents == false) {
			String [] data = {getResources().getString(R.string.no_data)};
			eventData.add(0,data);
		}
	}

    public void personExtract (){
    	//create object of DB
    	DBAdapter db = new DBAdapter(this);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEventPersons(extra_EID);
        /* Get the indices of the Columns we will need */
        //int timeColumn = c.getColumnIndex("timestamp"); 
        int codeColumn = c.getColumnIndex("code");
        int pTypeColumn = c.getColumnIndex("pType");
        //int rowIDColumn = c.getColumnIndex("_id") ;
        Log.i(TAG, " the value for eventID - "+ extra_EID);
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 int pType = c.getInt(pTypeColumn);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == 2){
        			 offeventData.add("  "+c.getLong(codeColumn));
                 //ofRowID.add(c.getLong(rowIDColumn));
        		 }else if (pType == 1){
        			 stdeventData.add("  "+c.getLong(codeColumn));
                 //stRowID.add(c.getLong(rowIDColumn));
        		 }
                 
                 
             } while (c.moveToNext());
        else
            Toast.makeText(this, "No Persons found", 
            		Toast.LENGTH_SHORT).show();
        db.close();
    	
    }
    
    public void eventExtract (){
    	//create object of DB
    	DBAdapter db = new DBAdapter(this);
        
      //---get all events---
        db.open();
        Cursor c = db.getEvent(extra_EID);
        /* Get the indices of the Columns we will need */
        //int timeColumn = c.getColumnIndex("timestamp");         
        int eventTypeColumn = c.getColumnIndex("evType");
        int venueColumn = c.getColumnIndex("venue");
        int courseColumn = c.getColumnIndex("course");
        int durColumn = c.getColumnIndex("duration");
       // int rowIDColumn = c.getColumnIndex("_id") ;
        
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	// do {
        		 /* Add current Entry to meventData. */
                 eventDetails[0] = c.getString(eventTypeColumn);
                 eventDetails[1] = c.getString(venueColumn);
                 eventDetails[2] = c.getString(courseColumn);
                 eventDetails[3] = c.getString(durColumn);
                 
            // } while (c.moveToNext());
        } else {
            Toast.makeText(this, "Event found", 
            		Toast.LENGTH_SHORT).show();
        }
        db.close();
    	
    }
    
  //to load all prefences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        DB_mode = eventSettings.getInt(DB_MODE, 1);	        
    }
}