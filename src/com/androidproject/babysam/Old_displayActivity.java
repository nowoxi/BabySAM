package com.androidproject.babysam;


import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Old_displayActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
	public final String EID = "EventID";
	public final String ET = "EventType";
	public final String VEN = "Venue";
	public final String CO = "Course";
	public final String DUR = "Duration";
	
	public String [] intentExtra = new String [5];
	public String [] I_intExtra = new String [5];
	public TextView [] text = {(TextView) findViewById(R.id.Event_view),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView01),
	    	(TextView) findViewById(R.id.TextView02)};
	                   //  for (int i = 0; i == 4 ; i++)intent.putExtra("intExtra"+i, eventData.get(position)[i]);
	
	

    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_display);
        
    	//TextView [] text1 = {(TextView) findViewById(R.id.textView1)};
    	//TextView text2 = (TextView) findViewById(R.id.textView2);
    	//TextView text3 = (TextView) findViewById(R.id.TextView01);
    	//TextView text4 = (TextView) findViewById(R.id.TextView02);
    	
    	//text = {(TextView) findViewById(R.id.Event_view),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView01),
    	//(TextView) findViewById(R.id.TextView02)};
        
        //defining array here
        final ArrayList<String[]> eventData = new ArrayList<String[]>();
        final ArrayList<String> meventData = new ArrayList<String>();
	    
        // Retrieve XML
	    XmlResourceParser eventxml = getResources().getXml(R.xml.persons);
	    
	    //Retrieve listview
	    ListView old_dis = (ListView) findViewById(R.id.listView2);
	    
        for (int i = 0; i < 5 ; i++){
        	intentExtra[i]= "intExtra"+i ;
        	Log.i(TAG, " this is the intent "+ intentExtra[i]);
        }
        //Log.i(TAG, " this is the intent "+ intentExtra[0]);
	    //get the event id from the intent that was passed
        Intent intent = getIntent();
        if ( intent != null){
        	for (int i = 0; i < 5 ; i++){
        		if (intent.hasExtra(intentExtra[i])) {
	        		I_intExtra[i] = intent.getStringExtra(intentExtra[i]);
	        		Log.i(TAG," the extra is " + I_intExtra[i] );
        		}
        	}
        	
        if (intent.hasExtra(EID)) {
            String ev_id = intent.getStringExtra(EID);
            int I_EID = Integer.parseInt(ev_id);
            Log.i(TAG," the extra is " + ev_id );
          }
        }
        Log.i(TAG," the extra is end" );
        
        for (int i = 0; i < 4 ; i++){
    		text[i].setText(I_intExtra[i+1]);
    	}

	    //primarily should return eventdata which has the content so the file
	    /*try {
	    processData(old_dis,eventxml, eventData);
	    } catch (Exception e) {
            Log.e(TAG, "Failed to load Events", e);
        }
	     
	    //put data in meventData after eventData comes back
	    //if you send eventdata to another class you will need a way to call it	        
	    for( int i = 0; i < eventData.size(); i++){
	    		meventData.add(eventData.get(i)[5]+"  "+eventData.get(i)[1]);
	    }
	    
	    ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.menu_item, meventData);        
	    old.setAdapter(adapt);
	    //adapt.notifyDataSetChanged();*/
	    //Log.i(TAG,"3 After call" );
        
    }
    
    
    
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
           
		         //extracting information from xml
		            //int eventID = Integer.parseInt(event.getAttributeValue(null, "ID"));
		            String [] data = new String [7];
		            data[0] = event.getAttributeValue(null, "ID");
		            data[1] = event.getAttributeValue(null, "EID");
		            data[2] = event.getAttributeValue(null, "PType");
		            data[3] = event.getAttributeValue(null, "code");		            
		            data[4] = event.getAttributeValue(null, "timestamp");		            
		            
		           // String [] eventData = { Integer.toString(eventID), eventType,};
		           if (data[2] != null){
		        	   eventData.add(0,data);
		          
		           
			           //Log.i(TAG,"annoying part  -"+ eventData +" and " + xnum);
			           //Log.i(TAG,"2 annoying part  - "+ eventData.get(xnum) );
			           //Log.i(TAG,"eventID = "+eventData.get(xnum)[0]+" / eventType = "+eventData.get(xnum)[1]+" / eventVenue = " +eventData.get(xnum)[2]+ 
			   		   //     		   "eventDuration = "+eventData.get(xnum)[3]+" / eventCourse = "+ eventData.get(xnum)[4]+" / eventTime = " +eventData.get(xnum)[5] );
			           //xnum ++; 
		           } 
		           // show the values gotten from xml in logcat as not all will be shown in listview for now
		           //Log.i(TAG,"eventID = "+eventID+" / eventType = "+eventType+" / eventVenue = " +eventVenue+ 
		        //		   "eventDuration = "+eventDuration+" / eventCourse = "+ eventCourse+" / eventTime = " +eventTime );
		           
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

}