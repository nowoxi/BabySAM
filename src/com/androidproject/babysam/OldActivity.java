package com.androidproject.babysam;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OldActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old);
        
        //defining array here
        final ArrayList<String[]> eventData = new ArrayList<String[]>();
        final ArrayList<String> meventData = new ArrayList<String>();
	    
        // Retrieve XML
	    //XmlResourceParser mockAllScores = getResources().getXml(R.xml.persons);
	    XmlResourceParser eventxml = getResources().getXml(R.xml.event);
	    
	    //Retrieve listview
	    ListView old = (ListView) findViewById(R.id.listView1);
	    
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
	    
	    ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.menu_item, meventData);        
	    old.setAdapter(adapt);
	    //adapt.notifyDataSetChanged();
	    Log.i(TAG,"3 After call" );
   
    
    
	    old.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
	    		
	    		//send the event id to the new activity to be started
	    		
	    		Log.i(TAG,"4 After call "+ eventData.get(position)[0]+"  "+eventData.get(position)[1] );
			    Intent intent = new Intent(OldActivity.this,Old_displayActivity.class);	
				/*intent.putExtra("EventID", eventData.get(position)[0]);
				intent.putExtra("EventType", eventData.get(position)[1]);
				intent.putExtra("Venue", eventData.get(position)[2]);
				intent.putExtra("Course", eventData.get(position)[3]);
				intent.putExtra("Duration", eventData.get(position)[4]);*/
				//String [] intExtra = new String [5] ;
				String [] intentExtra = new String [5];
				for (int i = 0; i < 5 ; i++){
		        	intentExtra[i]= "intExtra"+i ;
		        	Log.i(TAG, " this is the intent 2 "+ intentExtra[i]);
		        }
				for (int i = 0; i <= 4 ; i++)intent.putExtra(intentExtra[i], eventData.get(position)[i]);
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
		           /* //extracting information from xml
		            //int eventID = Integer.parseInt(event.getAttributeValue(null, "ID"));
		            String eventID = event.getAttributeValue(null, "ID");
		            String eventType = event.getAttributeValue(null, "EType");
		            String eventVenue = event.getAttributeValue(null, "Venue");
		            String eventCourse = event.getAttributeValue(null, "course");
		            //int eventDuration = Integer.parseInt(event.getAttributeValue(null, "duration"));
		            String eventDuration = event.getAttributeValue(null, "duration");
		            String eventTime = event.getAttributeValue(null, "timestamp");
		            //int eventaresREG = Integer.parseInt(event.getAttributeValue(null, "ariesREG"));
		            String eventaresREG = event.getAttributeValue(null, "ariesREG");
		            
		           // String [] eventData = { Integer.toString(eventID), eventType,};
		           if (eventVenue != null)eventData.add(0,eventType+"  "+eventCourse);*/
		           
		           
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

	//private void insertEvent(ListView dataList, String scoreValue, String scoreRank, String scoreUserName) {
   //  private void insertEvent(ListView dataList,  ArrayList<String> eventData) {
		// TODO Auto-generated method stub
    	 //ListView menuList = (ListView) findViewById(R.id.menu_list);
    	 
   
     //   Log.i(TAG,"After call" );
	//}
    public void eventExtract (){
    	
    }
    
    public void personExtract (){
    	
    }
}