package com.androidproject.babysam;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
	   
	// the en_stscan should eventually become a share preference that would determine if information is scanned or not
	   // en_stscan - student scan , scformat - scanformat to use , format - format of scanned code
	   // en_ofscan - Official scan, contents - data in scanned code
	   // en_evsxan - event scan
	   private int en_stscan, en_ofscan, en_evscan, en_stPerson; 
	   private String format, scformat, contents, content_delimiter;
	   private String [] ev_contents = new String [4];
	   private long RowID, pRowID, stateID;
	   
	   
	   private final ArrayList<String> offeventData = new ArrayList<String>();
	   private final ArrayList<String> stdeventData = new ArrayList<String>();
       private ArrayAdapter<String> off_adapt;        
	   private ArrayAdapter<String> std_adapt;
       
	   //to set student or official (1 for student 2 for official 3 for event)--ensure that this value is
	   //sustained even when an intent is called else save it as a preference
	   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        LoadPref(); 
        getNextRow();
        checkstatus();
        content_delimiter = "\\|";
        
		//Retrieve listview
	    ListView off = (ListView) findViewById(R.id.officials_view);
	    ListView std = (ListView) findViewById(R.id.students_view);
	    off_adapt = new ArrayAdapter<String>(this, R.layout.list_item, offeventData);        
	    std_adapt = new ArrayAdapter<String>(this, R.layout.list_item, stdeventData);
	    
	    
	    off.setAdapter(off_adapt);
	    std.setAdapter(std_adapt);
	    //Log.i(TAG,"3 After call" );
    }
    
	   @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		   
		   Log.i(TAG,"pause oh" );
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub		
		super.onStop();
		Log.i(TAG,"stop oh" );
	}

	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			//   alert.dismiss();		
			super.onDestroy();Log.i(TAG,"destroy oh" );
		//clean db incase officials or students added and session not saved
		   if(RowID != getLastEventRow() && checkperson()){
			   event_cancel(RowID);
			   Log.i(TAG,"Event not added session cleared" );
			   Toast.makeText(this, "Event not added session cleared", Toast.LENGTH_SHORT).show();
		   }
		} 
      
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG,"resume oh and "+ RowID );
	}

	private void checkstatus (){
		//you should have a row id that doesnt change...on load 
		//on load if row id loaded from db is same as rowid 
	}
	private Boolean checkperson(){
		// method used by dialog to check if person or offical have been added to the canceled unidentified session
		//so it can remove them
		DBAdapter db = new DBAdapter(this); 
        db.open();
        Cursor c = db.getAllEventPersons(RowID);
        /* Get the indices of the Columns we will need */
        Boolean check = false;
        if (c.moveToFirst()){
        	check = true;
        	Log.i(TAG, " students or officals added to empty session - "+ RowID);       
        }
        
        db.close();
		EventActivity.this.finish();
		return check ;
	}
    //to load all prefences to their variables only used in event
       private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        en_stscan = eventSettings.getInt(ST_SCAN, 1);
	        en_ofscan = eventSettings.getInt(OF_SCAN, 1);
	        en_evscan = eventSettings.getInt(EV_SCAN, 1);
       }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.eventoptions, menu);
	    menu.findItem(R.id.event_help).setIntent( new Intent(this, HelpActivity.class));
	    menu.findItem(R.id.event_set).setIntent( new Intent(this, settingsActivity.class));
	    return true;
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	//super.onActivityResult(requestCode, resultCode, intent);
		// it will not come here if the scan option isn't enabled
    	Log.i(TAG,"to check that onactivity result happens" );
    	
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Log.i(TAG,"requestCode = "+requestCode+" / resultCode = " +resultCode );
                Log.i(TAG,"Format = "+format+" / Contents = " +contents );
              //split_content();
    			ev_contents = contents.split(content_delimiter);
                add_dbdata(en_stPerson);
                entryDialog();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Log.i(TAG,"It failed oh" );
            }
        }
    	
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	//this section will run the barcode scanner as an intent if student barcode scanning is enabled
		LoadPref();
		switch(item.getItemId()) {        	
        	case R.id.event_addst:
	        	en_stPerson = 1;
	        	scformat = "CODABAR";
	        	scanSet(en_stscan, scformat);
	        return true;
        	case R.id.event_addof:
	        	en_stPerson = 2;
	        	scformat = "CODABAR";
	        	scanSet(en_ofscan, scformat);	
            return true;
        	case R.id.event_cancel:
	        	event_cancel(RowID);	
            return true;
        	case R.id.event_addse:
        		Log.i(TAG,"2" );
	        	en_stPerson = 3;
	        	scformat = "QR_CODE";
	        	scanSet(en_evscan, scformat);	
            return true;            
        }
		
		return super.onOptionsItemSelected(item);
	}
    
	// the if would contain an or to join the 3 conditions
	//this method is used to load the barcode scanner if option enabled
	public void scanSet(int sett, String scan_format){
    	if (sett == 1){
			Log.i(TAG,"2 scan" );
				
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_FORMATS", scan_format);
			intent.putExtra("SCAN_WIDTH", 300 );
			intent.putExtra("SCAN_HEIGHT", 240 );
			startActivityForResult(intent, 0);
    	} else {
               	//method to create the dialog box straight
    		contents = "test";
    		Log.i(TAG,"2 no scan" );
    		entryDialog();
        }
	}
	
	public void entryDialog (){
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		//TODO-the value for en_stperson might be lost when calling intent please check  
		//Log.i(TAG,"fail 1 " );
	    LayoutInflater inflater = getLayoutInflater();
			final View dialoglayout = inflater.inflate(R.layout.session, (ViewGroup) findViewById(R.id.layout_root3));
		if (en_stPerson == 3){
			
			alert.setView(dialoglayout);
			EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
					(EditText) dialoglayout.findViewById(R.id.EditText03), (EditText) dialoglayout.findViewById(R.id.EditText04) };
			
			if (ev_contents != null){
				for (int i = 0 ; i < ev_contents.length; i++){				
					eventresult[i].setText(ev_contents[i]);
				}
			}
           // Log.i(TAG,"fail 2 " );
		} else {
		//	final EditText input = new EditText(this);
			alert.setView(input);			
			input.setText(contents);			
			
		}
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 //HERE YOU UPDATE THE DATABASE AND NOT INSERT 
				//VALUES ARE ADDED ON SCANNING UNLESS SCANNING ISNT SET
			
				pRowID = 0;
				if (en_stPerson == 3){
					
					EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
							(EditText) dialoglayout.findViewById(R.id.EditText03), (EditText) dialoglayout.findViewById(R.id.EditText04) };
					
					for (int i = 0 ; i < eventresult.length; i++){				
						ev_contents[i]= eventresult[i].getText().toString();
					}
					
					
					//update data if data was scanned, insert data if other wise
					Log.i(TAG,"fail 4 " );
					if (en_evscan == 1){
						upd_dbdata(en_stPerson, pRowID,RowID);
					} else if (en_evscan == 0){
						add_dbdata(en_stPerson);
					}
					
					TextView [] text = {(TextView) findViewById(R.id.textView1),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView02),
			    	    	(TextView) findViewById(R.id.TextView01)};
					for (int i = 0; i < 4 ; i++){
			    		text[i].setText(ev_contents[i]);
			    	}
				}else {
					contents = (String) input.getText().toString();
					//this section adds to the database from the text box and also populates the listview
					if (en_stPerson == 1){						
						if (en_stscan == 1){
							//the value of pRowID must be the id of the recrd added by the scan intent
							pRowID = getLastPersonRow();
							upd_dbdata(en_stPerson, pRowID,RowID);
						} else if (en_ofscan == 0){
							add_dbdata(en_stPerson);
						}
						stdeventData.add(contents);
						std_adapt.notifyDataSetChanged();
					} else if (en_stPerson == 2){
						if (en_ofscan == 1){
							pRowID = getLastPersonRow();
							upd_dbdata(en_stPerson, pRowID,RowID);
						} else if (en_ofscan == 0){
							add_dbdata(en_stPerson);
						}
						offeventData.add(contents);
						off_adapt.notifyDataSetChanged();
					} 
				}
				Toast.makeText(getApplicationContext(), "content: " + contents, Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
		
	}
	
	
	//if i try to use a method that returns a value it would always try to recalculate 
	// the next row id which would make the programming wrong as this activity would increase the 
	// the rows during operation
	public long getLastEventRow(){
		DBAdapter db = new DBAdapter(this);
        
	      //---get all events---
	        db.open();
	        Cursor c = db.getAllEvents();
	        int rowIDColumn = c.getColumnIndex("_id") ;
	        long LRowID=0;
			if (c.moveToLast())LRowID = c.getLong(rowIDColumn);	        
	        db.close();
	        Log.i(TAG,"Last Row ID : "+ LRowID );	
		return LRowID;
	}
	public void getNextRow(){
			RowID = getLastEventRow()+1;
	        Log.i(TAG,"Next Row ID : "+ RowID );		
	}
	
	public long getLastPersonRow(){
		DBAdapter db = new DBAdapter(this);
        
	      //---get all events---
	        db.open();
	        Cursor c = db.getAllEventPersons(RowID);
	        int rowIDColumn = c.getColumnIndex("_id") ;
	        long LRowID=0;
			if (c.moveToLast())LRowID = c.getLong(rowIDColumn);	        
	        db.close();
	        Log.i(TAG,"Last Person Row ID : "+ LRowID );	
		return LRowID;
	}
	
	private void upd_dbdata(int ptype,long pID, long rID){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(this); 
        db.open();       
        Log.i(TAG,"add data 1" );
        if (ptype == 3){
        	try{
	        db.updateEvent(
	        		rID,
	        		ev_contents[0],
	        		ev_contents[1],
	        		ev_contents[2],
	        		Integer.parseInt(ev_contents[3]),//TODO ENSURE U CORRECT THIS FORMAT PROBLEM
	        		//60,
	        		0,
	        		timeStamp());
	        Log.i(TAG,"add data 2a" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(this, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else {    
        	try{
        		Long code = new Long (contents);
        		String cdate = timeStamp();
	        	Log.i(TAG,"add data 2" );
	        	db.updatePerson(pID,rID,ptype,code,cdate);
        	} catch (NumberFormatException e){
       		 Toast.makeText(this, "Invalid data format", 
              		Toast.LENGTH_SHORT).show();
        	}
        	
        }
        db.close();
    }
    
	private void add_dbdata(int ptype){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(this); 
        db.open();       
        Log.i(TAG,"add data 1" );
        if (ptype == 3){
        	try{
	        db.insertEvent(
	        		ev_contents[0],
	        		ev_contents[1],
	        		ev_contents[2],
	        		Integer.parseInt(ev_contents[3]),//TODO ENSURE U CORRECT THIS FORMAT PROBLEM
	        		//60,
	        		0,
	        		timeStamp());
	        Log.i(TAG,"add data 2a" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(this, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else {    
        	try{
        		Long code = new Long (contents);
        		String cdate = timeStamp();
	        	Log.i(TAG,"add data 2" );
	        	db.insertPerson(RowID,ptype,code,cdate);	        	
        	} catch (NumberFormatException e){
       		 Toast.makeText(this, "Invalid data format", 
              		Toast.LENGTH_SHORT).show();
        	}
        	
        }
        db.close();
    }
	
	public void event_cancel(long rID){
		// TODO - make a method that would cancel all the current status of events	i.e. deleting from the database
		DBAdapter db = new DBAdapter(this); 
        db.open();
        db.deleteEvent(rID);
        db.close();
		EventActivity.this.finish();
	}
	
	public String timeStamp(){
		return (String)android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
	}
	
	//junk code
	//String value = input.getText().toString().trim();
}