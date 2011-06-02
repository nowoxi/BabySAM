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
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class OldActivity extends babysamActivity {
    /** Called when the activity is first created. */
	private int DB_mode;
	private final ArrayList<String[]> eventData = new ArrayList<String[]>();
    private final ArrayList<String> meventData = new ArrayList<String>();
    private final ArrayList<Long> RowID = new ArrayList<Long>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old);
        LoadPref();
        //defining array here
        
	    //Retrieve listview
	    ListView old = (ListView) findViewById(R.id.listView1);
	    
	    
	    
	    if (DB_mode == 0){
	        // Retrieve XML
		    //XmlResourceParser mockAllScores = getResources().getXml(R.xml.persons);
		    XmlResourceParser eventxml = getResources().getXml(R.xml.event);
		     
		    //primarily should return eventdata which has the content so the file
		    try {
		    	processData(old,eventxml, eventData);
		    } catch (Exception e) {
	            Log.e(TAG, "Failed to load Events", e);
	        }
		    
		    //put data in meventData after eventData comes back
		    //if you send eventdata to another class you will need a way to call it	        
		    for( int i = 0; i < eventData.size(); i++){
		    		meventData.add(eventData.get(i)[5]+"  "+eventData.get(i)[1]);
		    }
	    } else if (DB_mode == 1){
	    	eventExtract();
	    }
	    	    
	    ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.menu_item, meventData);        
	    old.setAdapter(adapt);
	    //adapt.notifyDataSetChanged();
	    Log.i(TAG,"3 After call" );
           
	    old.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
	    		Intent intent = new Intent(OldActivity.this,Old_displayActivity.class);	
	    		if (DB_mode == 0){
		    		//Log.i(TAG,"4 After call "+ eventData.get(position)[0]+"  "+eventData.get(position)[1] );
					String [] intentExtra = new String [5];
					for (int i = 0; i < 5 ; i++){
			        	intentExtra[i]= "intExtra"+i ;
			        	//Log.i(TAG, " this is the intent 2 "+ intentExtra[i]);
			        }
					for (int i = 0; i <= 4 ; i++)intent.putExtra(intentExtra[i], eventData.get(position)[i]);
													
		    	} else if (DB_mode == 1){
		    		//send the event id to the new activity to be started
		    		Log.i(TAG,"4 After call list postision: "+ position +" rowID:  "+RowID.get(position)+ ". I beleive it is easier to use " + (position+1)+" as Row ID");
		    		intent.putExtra("EventID", RowID.get(position));
		    	}
	    		startActivity(intent);
	    	}
	    });
	    
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.oldoptions, menu);
	    menu.findItem(R.id.help_menu_item).setIntent( new Intent(this, HelpActivity.class));
	    return true;
    }
    
    
    //designed to fetch data from database or xml 
    //so i would advice you make constructors that will be called processdata and will fetch either xml or database
    //depending on the structure of the constructor then insert the vlaue through the method
    private void processData(final ListView dataList, XmlResourceParser event,ArrayList<String[]> eventData) throws XmlPullParserException,IOException {
		int doceventType = -1;
		boolean bFoundEvents = false;   
	    //int xnum = 0;
	    
		// Find Event records from XML
		while (doceventType != XmlResourceParser.END_DOCUMENT) {
		    if (doceventType == XmlResourceParser.START_TAG) {		    	
		        // Get the name of the tag (eg scores or score)
		    	
		        String strName = event.getName();
		        if (strName.equals("event")) {
		            bFoundEvents = true;
		           		           
		         //extracting information from xml
		            //int eventID = Integer.parseInt(event.getAttributeValue(null, "ID"));
		            String [] data = new String [7];
		            data[0] = event.getAttributeValue(null, "ID");
		            data[1] = event.getAttributeValue(null, "EType");
		            data[2] = event.getAttributeValue(null, "Venue");
		            data[3] = event.getAttributeValue(null, "course");
		            //int eventDuration = Integer.parseInt(event.getAttributeValue(null, "duration"));
		            data[4] = event.getAttributeValue(null, "duration");
		            data[5] = event.getAttributeValue(null, "timestamp");
		            //int eventaresREG = Integer.parseInt(event.getAttributeValue(null, "ariesREG"));
		            data[6] = event.getAttributeValue(null, "ariesREG");
		            
		           // String [] eventData = { Integer.toString(eventID), eventType,};
		           if (data[2] != null){
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
			 //eventData.add(0,getResources().getString(R.string.no_data));
		}
	//	Log.i(TAG,"before call" );
	//	insertEvent(dataList, eventData);
	//	Log.i(TAG,"2 After call" );
	}

	
    public void eventExtract (){
    	//create object of DB
    	DBAdapter db = new DBAdapter(this);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEvents();
        /* Get the indices of the Columns we will need */
        int timeColumn = c.getColumnIndex("timestamp");         
        int eventTypeColumn = c.getColumnIndex("evType");
        int rowIDColumn = c.getColumnIndex("_id") ;
        
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 /* Add current Entry to meventData. */
                 meventData.add(c.getString(timeColumn)+"  "+c.getString(eventTypeColumn));
                 RowID.add(c.getLong(rowIDColumn));
             } while (c.moveToNext());
        else
            Toast.makeText(this, "No Events found", 
            		Toast.LENGTH_SHORT).show();
        db.close();
    	
    }
    
    public void personExtract (){
    	
    }
    
  //to load all prefences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        DB_mode = eventSettings.getInt(DB_MODE, 1);	        
    }   
    
}