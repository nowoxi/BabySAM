package com.androidproject.babysam;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
	   private int en_stscan, en_ofscan, en_evscan, en_stPerson, pEdit, reScan; 
	   private String format, scformat, contents, content_delimiter;
	   private String [] ev_contents = new String [5];
	   private long RowID, pRowID, stateID, iRowID, stPos, offPos, rPos;
	   private functions f;
	   private ProgressDialog dialog;
	   
	   
	   private final ArrayList<String> offeventData = new ArrayList<String>();
	   private final ArrayList<String> stdeventData = new ArrayList<String>();
       private ArrayAdapter<String> off_adapt;        
	   private ArrayAdapter<String> std_adapt;
       
	   //to set student or official (1 for student 2 for official 3 for event)--ensure that this value is
	   //sustained even when an intent is called else save it as a preference
	   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        stateID=0;
    }
    

	@Override
		protected void onDestroy() {
			//  Auto-generated method stub
			//   alert.dismiss();		
			super.onDestroy();
			Log.i(TAG,"destroy oh and row id is "+ RowID );
		//clean db incase officials or students added and session not saved
		   if(RowID != getLastEventRow() && checkperson()){
			   event_cancel(RowID);
			   Log.i(TAG,"Event not added session cleared" );
			   Toast.makeText(this, "Event not added session cleared", Toast.LENGTH_SHORT).show();
		   }
		   //startActivity(new Intent(this, EventActivity.class));
		  
		} 
      
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setupViews();
		checkstatus();
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  if (v.getId() == R.id.officials_view)en_stPerson = 2;
	  if (v.getId() == R.id.students_view)en_stPerson = 1;	  
	  inflater.inflate(R.menu.event_context2, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  //iRowID is the row id of the person to be edited or deleted 
		// get last row id of previous session then add it to the contextmenu +1
	 // long ilRowID=getLastEventLastPersonRow(RowID)+ info.id + 1 ;
	  long ilRowID = f.getPersonID(info.id,en_stPerson,RowID);
	  
	  switch (item.getItemId()) {
	  case R.id.event_edit_item2:
		  Log.i(TAG,"item id: "+info.id +" fetched row id is " + ilRowID);
		  editContext(info.id, en_stPerson, RowID, ilRowID );
	    return true;
	  case R.id.event_delete_item:
		  Log.i(TAG,"item id: "+info.id );
	    deleteContext(en_stPerson,(int) info.id,ilRowID);
	    return true;
	  case R.id.event_rescan_item:
		  rescan("CODABAR",en_stPerson,ilRowID);
		  return true;
	  case R.id.event_edit_item:
		;
		return true;
	  case R.id.event_cancel_item:
		;
		return true;  
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	private void rescan(String scformat, int Person, long ilRowID) {
		//TODO you need to set values for ptype and rpos from db
		int en=0;
		if (Person == 1)en = en_stscan;
		if (Person == 2)en = en_ofscan;
		  scanSet(en,scformat,1, ilRowID);//3rd value set to 1 because it is a rescan
		
	}


	private void deleteContext(int pType, int pos, long ilRowID) {
		//  TODO you need to update the pos of all persons after the deleted position
		//  i am hopin u do this by fetching all persons then adding the Row ID of persons
		//  that have positions higher than deleted position and reducing by one then update them
		f.updatePos(pType,pos,RowID);
		if (f.deletePerson(ilRowID)){
			if(pType == 1){
				stdeventData.remove(pos);
				std_adapt.notifyDataSetChanged();		   
			}else if(pType == 2){
				offeventData.remove(pos);
				off_adapt.notifyDataSetChanged();		   
			}
			
		}
	}

	private void editContext(long pos, int lpType, long lRowID, long ilRowID) {
		//Log.i(TAG,"Before hangin ");
		// = f.getPersonID(pos,lpType,lRowID);// get the row id for a person that is of type 2 and event .... and pos....
		
		// ilRowID is the row id of the person to be edited 
		// get last row id of previous session then add it to the contextmenu +1
		//long ilRowID=getLastPersonRow(lRowID-1)+ contextmenuID + 1 ;
		//pEdit=1; //used to inform the entryDialog that
		Log.i(TAG,"the row id of selected: "+ilRowID);
		if (en_stPerson == 2) offPos = pos;
		if (en_stPerson == 1) stPos = pos;
		contents = Long.toString(f.getPersonCode(ilRowID));		
		entryDialog(1,ilRowID);
		
	}

	/*private long getPersonCode(long ilRowID) {
		// Auto-generated method stub
		DBAdapter db = new DBAdapter(this);
		//---get person---
        db.open();
        Cursor c = db.getPerson(ilRowID);
        int codeIDColumn = c.getColumnIndex("code") ;
        long LcodeID=0;
		if (c.moveToFirst())LcodeID = c.getLong(codeIDColumn);	        
        db.close();
		return LcodeID;
	}*/

	private void setupViews() {
		
		
		setContentView(R.layout.event);
        f = new functions(this);
        LoadPref(); 
        getNextRow();        
        
        content_delimiter = "\\|";
        registerForContextMenu(findViewById(R.id.officials_view));
        registerForContextMenu(findViewById(R.id.students_view));
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
	protected void onResume() {
		// Auto-generated method stub
		super.onResume();
		Log.i(TAG,"resume oh and roowid now is "+ RowID );
	}

	private void checkstatus (){
		//you should have a row id that doesnt change...on load 
		//on load if row id loaded from db is same as rowid 
		RowID -= stateID;
		String [] leventDetails = f.eventExtract(RowID);
		TextView [] text = {(TextView) findViewById(R.id.textView1),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView02),
    	    	(TextView) findViewById(R.id.TextView01)};
    	for (int i = 0; i < 4 ; i++){
    		text[i].setText(leventDetails[i]);
    	}
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
		
		//stateId will always be 1 as long as event details have been entered
		//persons dont need to be edited with 
		
		
    	Log.i(TAG,"to check that onactivity result happens" );
    	
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Log.i(TAG,"requestCode = "+requestCode+" / resultCode = " +resultCode );
                Log.i(TAG,"Format = "+format+" / Contents = " +contents );
                int pos = 0;
                if (en_stPerson==3){
                	ev_contents = setevContent(contents, en_stPerson, content_delimiter);
                	 if(stateID == 0)add_dbdata(en_stPerson, ev_contents, contents, RowID, pos);
                     //if(pos != 0)pos-=1;
                     if(stateID == 1)upd_dbdata(en_stPerson, pRowID, RowID, ev_contents, contents, pos);
                }else{
                    Log.i(TAG, "Array size"+offeventData.size() );
                
	                if (en_stPerson == 1) pos = stdeventData.size();
	                if (en_stPerson == 2) pos = offeventData.size();
	                if(reScan == 0)add_dbdata(en_stPerson, ev_contents, contents, RowID, pos);                
	                if(reScan == 1)upd_dbdata(en_stPerson, pRowID, RowID, ev_contents, contents, rPos);
                 
                }
                
                Log.i(TAG,"the stateID after scanning: "+stateID  );
               
                if(reScan == 0)entryDialog(0,0);
                if(reScan == 1)entryDialog(1,pRowID);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Log.i(TAG,"It failed oh" );
            }
        }    	
	}

	private String[] setevContent(String contents2, int p, String delimit) {		
		String[] sev_contents= new String [4];
		if (p==3)sev_contents = contents2.split(delimit);
		String[] tev_contents= new String [5];
		for (int i = 0 ; i < sev_contents.length; i++){				
			tev_contents[i]=sev_contents[i];
		}
		tev_contents[4]=timeStamp();
		return tev_contents;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	//this section will run the barcode scanner as an intent if student barcode scanning is enabled
		LoadPref();
		int scanNew = 0;
		switch(item.getItemId()) {        	
        	case R.id.event_addst:
	        	en_stPerson = 1;
	        	scformat = "CODABAR";
	        	scanSet(en_stscan, scformat, scanNew,0);
	        return true;
        	case R.id.event_addof:
	        	en_stPerson = 2;
	        	scformat = "CODABAR";
	        	scanSet(en_ofscan, scformat,scanNew,0);	
            return true;
        	case R.id.event_cancel:
	        	event_cancel(RowID);	
            return true;
        	case R.id.event_addse:
        		Log.i(TAG,"2" );
	        	en_stPerson = 3;
	        	scformat = "QR_CODE";
	        	scanSet(en_evscan, scformat, scanNew,0);	
            return true;  
        	case R.id.event_aries:
	        	sendAries();	
            return true;
        	case R.id.event_email:
	        	sendMail();        		
            return true;
        }
		
		return super.onOptionsItemSelected(item);
	}
    
	private void sendMail() {
		Log.i(TAG,"send email" );
		dialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.send_email), true);
		Log.i(TAG,"send email middle" );
		ProgressThread progThread = new ProgressThread(handler, getApplicationContext(), RowID, ev_contents, offeventData, stdeventData);
		progThread.start();
		Log.i(TAG,"send email end" );
	}

	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	Log.i(TAG,"send email handler" );
        	dialog.dismiss();
        }
    };
	// the if would contain an or to join the 3 conditions
	//this method is used to load the barcode scanner if option enabled
	public void scanSet(int sett, String scan_format, int scanT, long ilRowID){
		reScan = scanT; //used to learn if this is a rescan or a 1scan
    	if (sett == 1){
			Log.i(TAG,"2 scan" );
				
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_FORMATS", scan_format);
			intent.putExtra("SCAN_WIDTH", 310 );
			intent.putExtra("SCAN_HEIGHT", 240 );
			startActivityForResult(intent, 0);
    	} else {
               	//method to create the dialog box straight
    		contents = "";
    		if (scanT == 1){ //to edit the list view correctly on rescan
    			contents = Long.toString(f.getPersonCode(ilRowID));
    			entryDialog(1,ilRowID);
    		} else{
    			Log.i(TAG,"2 no scan" );
    			entryDialog(0,0);
    		}
    		
        }
	}
	
	public void sendAries(){
		
	}
		
	public void entryDialog (int psEdit, long psRowID){
		pEdit=psEdit;
		iRowID=psRowID;  //setting the row id of person
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		//the value for en_stperson might be lost when calling intent please check  
		//Log.i(TAG,"fail 1 " );
	    LayoutInflater inflater = getLayoutInflater();
			final View dialoglayout = inflater.inflate(R.layout.session, (ViewGroup) findViewById(R.id.layout_root3));
		if (en_stPerson == 3){
			
			alert.setView(dialoglayout);
			EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
					(EditText) dialoglayout.findViewById(R.id.EditText03), (EditText) dialoglayout.findViewById(R.id.EditText04) };
			
			if (ev_contents != null){
				for (int i = 0 ; i < eventresult.length; i++){				
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
				 //HERE YOU UPDATE THE DATABASE with  dialog if scanning is set AND NOT INSERT.				
				//you insert values from dialog if scanning is not set
			
				pRowID = 0;
				if (en_stPerson == 3){
					long eventPos=-1;// variable to fill the requirements of the method inserting to db
					EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
							(EditText) dialoglayout.findViewById(R.id.EditText03), (EditText) dialoglayout.findViewById(R.id.EditText04) };
					
					for (int i = 0 ; i < eventresult.length; i++){				
						ev_contents[i]= eventresult[i].getText().toString();
					}
					
					
					//update data if data was scanned, insert data if other wise
					Log.i(TAG,"on click ok for event dialog " );
					if (en_evscan == 1){
						upd_dbdata(en_stPerson, pRowID,RowID, ev_contents, contents,eventPos);
					} else if (en_evscan == 0){
						add_dbdata(en_stPerson, ev_contents, contents, RowID,eventPos);
					}
					
					TextView [] text = {(TextView) findViewById(R.id.textView1),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView02),
			    	    	(TextView) findViewById(R.id.TextView01)};
					for (int i = 0; i < 4 ; i++){
			    		text[i].setText(ev_contents[i]);
			    		
			    	}
					stateID=1;		//variable used to control the correct session to load on rotation
				}else {
					contents = (String) input.getText().toString();
					int pos = 0 ;//calculating position of item edited 
					//this section adds to the database from the text box and also populates the list view
					if (en_stPerson == 1){						
						if (en_stscan == 1 || pEdit == 1){ //if en_stscan is enabled scan would have added thats why we are updating only
							//the value of pRowID must be the id of the record added by the scan intent TODO
							Log.i(TAG,"fail" );
							pRowID = getLastPersonRow(RowID);
							pos = stdeventData.size();
							Log.i(TAG,"rescanning or editing student" );
							if (pEdit==1){
								pRowID = iRowID;  //set the person rowID to the id to be edited, done a the unset of entrydialog
								pos =(int) stPos ;//value of person to be edited
							}
							upd_dbdata(en_stPerson, pRowID,RowID, ev_contents, contents,pos);
						} else if (en_ofscan == 0){
							stPos = stdeventData.size();
							add_dbdata(en_stPerson, ev_contents, contents, RowID, stPos);
						}
						
						//this section is for editing the list view						
						if (pEdit == 0 )stdeventData.add(contents);
						if (pEdit == 1 ){							
							stdeventData.remove(pos);
							stdeventData.add(pos, contents);
						}
						std_adapt.notifyDataSetChanged();
					} else if (en_stPerson == 2){
						if (en_ofscan == 1|| pEdit == 1){//update info if scan enable or editing TODO
							pRowID = getLastPersonRow(RowID);
							pos = offeventData.size();
							if (pEdit==1){
								pRowID = iRowID;  //set the person rowID to the id to be edited
								pos = (int) offPos ;//value of person to be edited
							}
							upd_dbdata(en_stPerson, pRowID,RowID, ev_contents, contents,pos);
						} else if (en_ofscan == 0){
							offPos = offeventData.size();
							add_dbdata(en_stPerson, ev_contents, contents, RowID, offPos);
						}
						if (pEdit == 0 )offeventData.add(contents);
						if (pEdit == 1 ){
							;//calculating position of item editted 
							offeventData.remove(pos);
							offeventData.add(pos, contents);
						}
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
			//getLastPersonRow(RowID-1);
	        Log.i(TAG,"Next Row ID : "+ RowID );		
	}
	
	public long getLastEventLastPersonRow(long lRowID){ //should be called getlasteventlastpersonrow
		DBAdapter db = new DBAdapter(this);
        long r = lRowID-1;
	      //---get all persons---
        //this basically helps to find the last person row id so we can trace the rowid of the selected session to be created
	        db.open();
	        Cursor c = db.getAllPersons();
	        if (c != null){
	        	c = db.getAllEventPersons(r);	
		        while (!c.moveToLast()){
		        	r--;
		        	c = db.getAllEventPersons(r);
		        }
	        }
	        int rowIDColumn = c.getColumnIndex("_id") ;
	        long LRowID=0;
			if (c.moveToLast()) LRowID = c.getLong(rowIDColumn);//	Log.i(TAG,"test " );        
	        db.close();
	        Log.i(TAG,lRowID+ " Last Person Row ID : "+ LRowID+"got info from "+ r );	
		return LRowID;
	}
	
	public long getLastPersonRow(long lRowID){ 
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllEventPersons(lRowID);        	
        int rowIDColumn = c.getColumnIndex("_id") ;
        long LRowID=0;
		if (c.moveToLast()) LRowID = c.getLong(rowIDColumn);//	Log.i(TAG,"test " );        
        db.close();
	    //    Log.i(TAG,lRowID+ " Last Person Row ID : "+ LRowID+"got info from "+ r );	
		return LRowID;
	}
	
	private void upd_dbdata(int ptype,long pID, long rID, String [] lev_contents, String lcontents,long pos){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(this); 
        db.open();       
        Log.i(TAG,"update db method" );
        if (ptype == 3){
        	try{
	        db.updateEvent(
	        		rID,
	        		lev_contents[0],
	        		lev_contents[1],
	        		lev_contents[2],
	        		Integer.parseInt(lev_contents[3]),//TODO ENSURE U CORRECT THIS FORMAT PROBLEM
	        		//60,
	        		0,
	        		timeStamp());
	        Log.i(TAG,"update event in db" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(this, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else {    
        	try{
        		Long code = new Long (lcontents);
        		String cdate = timeStamp();	        	
	        	db.updatePerson(pID,rID,ptype,code,cdate,pos);
	        	Log.i(TAG,"updated person in db" );
        	} catch (NumberFormatException e){
       		 Toast.makeText(this, "Invalid data format", 
              		Toast.LENGTH_SHORT).show();
        	}        	
        }
        db.close();
    }
    
	private void add_dbdata(int ptype, String [] lev_contents, String lcontents,long lRowID, long pos){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(this); 
        db.open();       
        Log.i(TAG,"add data method" );
        if (ptype == 3){
        	try{
	        db.insertEvent(
	        		lev_contents[0],
	        		lev_contents[1],
	        		lev_contents[2],
	        		Integer.parseInt(lev_contents[3]),//TODO ENSURE U CORRECT THIS FORMAT PROBLEM
	        		//60,
	        		0,
	        		lev_contents[4]);
	        Log.i(TAG,"add Event to db" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(this, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else {    
        	try{
        		Long code = new Long (lcontents);
        		String cdate = timeStamp();	        	
	        	db.insertPerson(lRowID,ptype,code,cdate,pos);	
	        	Log.i(TAG,"add person to db" );
        	} catch (NumberFormatException e){
       		 Toast.makeText(this, "Invalid data format", 
              		Toast.LENGTH_SHORT).show();
        	}
        	
        }
        db.close();
    }
	
	public void event_cancel(long rID){
		//make a method that would cancel all the current status of events	i.e. deleting from the database
		DBAdapter db = new DBAdapter(this); 
        db.open();
        db.deleteEvent(rID);
        db.close();
		EventActivity.this.finish();
	}
	
	public String timeStamp(){
		return (String)android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
	}	
}