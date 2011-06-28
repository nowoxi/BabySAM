package com.androidproject.babysam;


import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
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
	
	//TODO you need to scan event student lists. Compare them to the students on the list and add which ever is missing ( i am thinking
	/*of having the mixed method as described which would probably have a size constraints and a large class style). 
	 * after list is checked then get the row id's of the students in the list and use it to populate the event table. 
	 * Use the student names to populate the list view. 
	 * Remove the edit options, the option 
	 * Change the add officials option also as you changed the student list option, so when you click add student or official it should
	 * do the new activities described above 
	 * Makes sure that students aren't added to the event list twice or event the student or officials list
	 */
	
	   
	// the en_stscan should eventually become a share preference that would determine if information is scanned or not
	   // en_stscan - student scan , scformat - scanformat to use , format - format of scanned code
	   // en_ofscan - Official scan, contents - data in scanned code
	   // en_evsxan - event scan
	   private int en_stscan, en_ofscan, en_evscan, en_stPerson, pEdit, reScan; 
	   private String format, scformat, contents, content_delimiter,FirstName, LastName;
	   private String [] ev_contents = new String [5];
	   private long RowID, pRowID, stateID, iRowID, stPos, offPos, rPos;
	   private functions f;
	   private ProgressDialog dialog;
	   private boolean correctTable;//used to contrl if the list view should be updated after checking if added person belongs to correct table
	   
	   
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
	  long ilRowID = f.getEventPersonID(info.id,en_stPerson,RowID);
	  
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
		// you need to set values for ptype and rpos from db
		int en=0;
		if (Person == 1)en = en_stscan;
		if (Person == 2)en = en_ofscan;
		  scanSet(en,scformat,1, ilRowID);//3rd value set to 1 because it is a rescan		
	}

	private void deleteContext(int pType, int pos, long ilRowID) {		
		//  i am hoping u do this by fetching all persons then adding the Row ID of persons
		//  that have positions higher than deleted position and reducing by one then update them
		f.updatePos(pType,pos,RowID);//   you need to update the pos of all persons after the deleted position
		if (f.deletePerson(ilRowID,3)){
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
		// get the row id for a person that is of type 2 and event .... and pos....
		
		// ilRowID is the row id of the person to be edited 
		// get last row id of previous session then add it to the contextmenu +1
		
		Log.i(TAG,"the row id of selected: "+ilRowID);
		if (en_stPerson == 2) offPos = pos;
		if (en_stPerson == 1) stPos = pos;
		contents = Long.toString(f.getPersonCode(ilRowID,0));		
		entryDialog(1,ilRowID);		
	}

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
        } else if (requestCode == 1){
        	if (resultCode == RESULT_OK){
        		long pID = intent.getLongExtra("PersonID", 0);
        		Log.i(TAG,"the new person ID is "+ pID );
        		if(pID != 0)attdList(pID, RowID, en_stPerson, timeStamp(), rPos);
        		
        		String lData = listview_Format(pID,en_stPerson);
    			//this section is for editing the list view	
        		if (en_stPerson == 1){
        			int pos = stdeventData.size()-1;
    				stdeventData.remove(pos);
    				stdeventData.add(lData);
    				std_adapt.notifyDataSetChanged();
        		}
        		else if (en_stPerson == 2){
        			int pos = offeventData.size()-1;
    				offeventData.remove(pos);
    				offeventData.add(lData);
    				off_adapt.notifyDataSetChanged();
        		}
        		
        	}else if (resultCode == RESULT_CANCELED) {
        		// Handle cancel
        		Log.i(TAG,"Saving single person failed oh" );
        	}
            	
        }   	
	}

	private String[] setevContent(String contents2, int p, String delimit) {		
		String[] sev_contents= new String [4];
		if (p==3)sev_contents = contents2.split(delimit);
		String[] tev_contents= new String [5];
		for (int i = 0 ; i < sev_contents.length; i++)				
			tev_contents[i]=sev_contents[i];
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
        		//check if exists in any persons list
        		//if so get row id---else create(ask for details) and then get row id
        		//check if exist in attendance list 
        		//if so change present to 1 and list to 1 ---else add to list and change present to 1 list to 0
        		//
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
        	case R.id.student_list:
        		//check if each exists in student list or official
        		//yes - dont add move to next,get rowid
        		//no - add move to next, get rowid
        		//add row id to event table with list set to 1 n present to 0
        		
	        	importAttendance();        		
            return true;
        	case R.id.official_list:
	        	;        		
            return true;
        }
		
		return super.onOptionsItemSelected(item);
	}
    
	private void importAttendance() {
		// TODO Auto-generated method stub
		// Retrieve XML
	    XmlResourceParser eventxml = getResources().getXml(R.xml.list);
	    try {
	    	processData(eventxml);
	    } catch (Exception e) {
            Log.e(TAG, "Failed to load Events", e);
        }
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
    		contents = "";
    		if (scanT == 1){ //to edit the list view correctly on rescan
    			contents = Long.toString(f.getPersonCode(ilRowID,0));
    			entryDialog(1,ilRowID);//method to create the dialog box straight
    		} else{
    			Log.i(TAG,"2 no scan" );
    			entryDialog(0,0);
    		}    		
        }
	}
	
	public void sendAries(){
		
	}
		
	
	// this will change from entering values to checking the list if exist and changing present to 1. Also it would
	//check if in event list
	// yes get row id change persent to 1 and list to 1
	// no check students and official list if exist
	//    yes get rowid add to event list set list to 0 and present to 1
	//    no add to list and get row id and add to eventlist set list to 0 and present to 1
	//    
	//
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
					
					for (int i = 0 ; i < eventresult.length; i++)			
						ev_contents[i]= eventresult[i].getText().toString();				
					ev_contents[4]= timeStamp();
					//update data if data was scanned, insert data if other wise
					Log.i(TAG,"on click ok for event dialog " );
					if (en_evscan == 1)	upd_dbdata(en_stPerson, pRowID,RowID, ev_contents, contents,eventPos);
					 else if (en_evscan == 0) add_dbdata(en_stPerson, ev_contents, contents, RowID,eventPos);
					
					TextView [] text = {(TextView) findViewById(R.id.textView1),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView02),
			    	    	(TextView) findViewById(R.id.TextView01)};
					for (int i = 0; i < text.length ; i++)
			    		text[i].setText(ev_contents[i]);
			    		
					stateID=1;		//variable used to control the correct session to load on rotation
				}else {
					contents = (String) input.getText().toString();
					try {
						@SuppressWarnings("unused")
						long lcont = new Long(contents);
						enterPerson(contents); //call method that will add the data to both the listview and database
					}catch (NumberFormatException e){
		        		 Toast.makeText(getApplicationContext(), "Invalid data format", 
		                  		Toast.LENGTH_SHORT).show();
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
	
	//TODO do not add to list if present set to 0
	private void enterPerson(String lcontents){
		String lData;
		int pos = 0 ;//calculating position of item edited 
		//this section adds to the database from the text box and also populates the list view
		if (en_stPerson == 1){						
			if (en_stscan == 1 || pEdit == 1){ //if en_stscan is enabled scan would have added thats why we are updating only
				//the value of pRowID must be the id of the record added by the scan intent 
				Log.i(TAG,"fail" );
				pRowID = getLastPersonRow(RowID);
				pos = stdeventData.size();
				Log.i(TAG,"rescanning or editing student" );
				if (pEdit==1){
					pRowID = iRowID;  //set the person rowID to the id to be edited, done at the unset of entrydialog
					pos =(int) stPos ;//value of person to be edited
				}
				upd_dbdata(en_stPerson, pRowID,RowID, ev_contents, lcontents,pos);
			} else if (en_ofscan == 0){
				stPos = stdeventData.size();
				add_dbdata(en_stPerson, ev_contents, lcontents, RowID, stPos);
			}
			
			if (correctTable){//TODO also consider if present set to 1
				lData = listview_Format(lcontents,en_stPerson);
				//this section is for editing the list view						
				if (pEdit == 0 )stdeventData.add(lData);
				if (pEdit == 1 ){							
					stdeventData.remove(pos);
					stdeventData.add(pos, lData);
				}
				std_adapt.notifyDataSetChanged();
			}
		} else if (en_stPerson == 2){
			if (en_ofscan == 1|| pEdit == 1){//update info if scan enable or editing 
				pRowID = getLastPersonRow(RowID);
				pos = offeventData.size();
				if (pEdit==1){
					pRowID = iRowID;  //set the person rowID to the id to be edited
					pos = (int) offPos ;//value of person to be edited
				}
				upd_dbdata(en_stPerson, pRowID,RowID, ev_contents, lcontents,pos);
			} else if (en_ofscan == 0){
				offPos = offeventData.size();
				add_dbdata(en_stPerson, ev_contents, lcontents, RowID, offPos);
			}
			if (correctTable){
				lData = listview_Format(lcontents,en_stPerson);
				if (pEdit == 0 )offeventData.add(lData);
				if (pEdit == 1 ){
					;//calculating position of item edited 
					offeventData.remove(pos);
					offeventData.add(pos, lData);
				}
			}
			off_adapt.notifyDataSetChanged();
		} 
	}
	
	
	//formating the string to be used for the list view
	private String listview_Format(String lcontents, int pType) {
		long code = new Long (lcontents);
		long lpRowID = f.getPersonID( code, pType);
		Log.i(TAG," list Row ID : "+ lpRowID );
		String [] data = f.single_personExtract(lpRowID, pType);
		return data[0]+" "+data[1]+" "+data[2];
	}
	private String listview_Format(long lpRowID, int pType) {//  if person rowid is know
		Log.i(TAG," list Row ID : "+ lpRowID );
		String [] data = f.single_personExtract(lpRowID, pType);
		return data[0]+" "+data[1]+" "+data[2];
	}

	//if i try to use a method that returns a value it would always try to recalculate 
	// the next row id which would make the programming wrong as this activity would increase the 
	// the rows during operation
	public long getLastEventRow(){
		DBAdapter db = new DBAdapter(this);
        
	      //---get all events---
	        db.open();
	        Cursor c = db.getAllEvents();
	        int rowIDColumn = c.getColumnIndex(db.KEY_ROWID) ;
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
	
	public long getLastEventLastPersonRow(long lRowID){ //should be called getlasteventlastpersonrow--not used
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
	        int rowIDColumn = c.getColumnIndex(db.KEY_ROWID) ;
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
        int rowIDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        long LRowID=0;
		if (c.moveToLast()) LRowID = c.getLong(rowIDColumn);//	Log.i(TAG,"test " );        
        db.close();
	    //    Log.i(TAG,lRowID+ " Last Person Row ID : "+ LRowID+"got info from "+ r );	
		return LRowID;
	}
	
	
	//TODO this should change as we are not going to be using code any more in event tables
	//decide if you are leaving this as i
	private void upd_dbdata(int ptype,long pID, long rID, String [] lev_contents, String lcontents,long pos){
    	//---add 2 events and persons---
		int sourceList = 0;
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
	        		0,
	        		lev_contents[4]);
	        Log.i(TAG,"update event in db" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(this, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else {    
        	try{
        		Long code = new Long (lcontents);
        		String cdate = timeStamp();	        	
	        	// db.updatePerson(pID,rID,ptype,code,cdate,pos);
        		populate_table2(sourceList,ptype,lcontents,rID,pos);
        		
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
		int sourceList = 0;
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
	        		0,
	        		lev_contents[4]);
	        Log.i(TAG,"add Event to db. The last row now is "+ getLastEventRow() );
        	} catch (NumberFormatException e){
        		 Toast.makeText(this, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else {    
        	try{
        		@SuppressWarnings("unused")
				Long code = new Long (lcontents);
        		//String cdate = timeStamp();
        		//  db.insertPerson(lRowID,ptype,code,cdate,pos, code, code);
        		populate_table2(sourceList,ptype,lcontents,lRowID,pos);
	        	Log.i(TAG,"add person to db" );
        	} catch (NumberFormatException e){
       		 Toast.makeText(this, "Invalid data format", 
              		Toast.LENGTH_SHORT).show();
        	}
        	
        }
        db.close();
        
    }
	
	private void populate_table2(int sourceList,int ptype, String lcontents, long lRowID, long pos) {
		Long code = new Long (lcontents);
		String cdate = timeStamp();
		long pID = 0;
		String blank =" ";
    	
 		//check if exists in any persons list
		//if so get row id---else create(ask for details) and then get row id
		if(f.codeCHECK(code)){
			if(f.codeCHECK(code,ptype)){//check if code exists in correct table
				pID = f.getPersonID(code, ptype);
				if(sourceList == 0)attdList(pID,lRowID, ptype, cdate, pID);
				if(sourceList == 1)add_attdList(pID, lRowID, ptype, cdate, stPos);
				correctTable = true;
			}else {
				Log.i(TAG,"wrong row "+ ptype );
				correctTable = false;
				Toast.makeText(this, "Adding person to wrong group", Toast.LENGTH_SHORT).show();
			}
		}else if(sourceList == 1){
			//TODO this is used to save none existent students on a list to the students list and
			// same for officials also
			if(ptype == 1)f.add_dbpersondata(FirstName, LastName, code);
			if(ptype == 2)f.add_dbpersondata(FirstName, LastName, code, blank, blank);
		} else{// create person, to set pID
		
			//I thank God for this solution that occurred to me
			/* create a method that returns a long
			 * in the method create an intent to load student list or official list and go straight to the add student or official panel
			 * get the ID of the student or official and return it to the row with the intent 
			 */
			Class<?> cls= olistActivity.class;
			if(en_stPerson == 1)cls = slistActivity.class;
			Intent intent = new Intent(EventActivity.this,cls);	
			//intent.putExtra("code", code);
			intent.putExtra("code", lcontents);
			startActivityForResult(intent,1);
			rPos= pos;
			//pID = 0; //  create record n return pid please
		}
		
	}

		//check if exist in attendance list 
		//if so change present to 1 and list to 1 ---else add to list and change present to 1 list to 0
	private void attdList(long pID, long lRowID, int ptype, String cdate, long pos) {
		long present = 1, list = 0;
		DBAdapter db = new DBAdapter(this); 
        db.open(); 
		if(f.eventCHECK(pID, lRowID)){
			long epID=f.getEventPersonID(pos, ptype, lRowID);
			list = 1;
			db.updateEventPerson(epID, lRowID, ptype, pID, cdate, pos, present, list);//TODO when updating i might only want it to update a few detais not all
		}else{
			list = 0;
			db.insertEventPerson(lRowID, ptype, pID, cdate, pos, present, list);
		}
		db.close();
		
	}
	private void add_attdList(long pID, long lRowID, int ptype, String cdate, long pos) {
		int list = 1;
		int present= 0;
		DBAdapter db = new DBAdapter(this); 
        db.open(); 
			db.insertEventPerson(lRowID, ptype, pID, cdate, pos, present, list);
		db.close();
		
	}
	private void processData( XmlResourceParser list) throws XmlPullParserException,IOException {
		//no need for data to show in list view until studnet present
		
		int doceventType = -1;
		boolean bFoundEvents = false;  
	    String lcontents;
	    int sourceList=1;
		// Find Event records from XML
		while (doceventType != XmlResourceParser.END_DOCUMENT) {
		    if (doceventType == XmlResourceParser.START_TAG) {		    	
		        // Get the name of the tag (eg scores or score)
		    	
		        String strName = list.getName();
		        if (strName.equals("student")) {
		            bFoundEvents = true;
		            en_stPerson = 1;	           
		        }
		        if (strName.equals("official")) {
		            bFoundEvents = true;
		            en_stPerson = 2;	           
		        }
		        if (bFoundEvents == true) {
			      //extracting information from XML
			        FirstName = list.getAttributeValue(null, "fname");
		            LastName = list.getAttributeValue(null, "lname");
		            lcontents = list.getAttributeValue(null, "code");
		        
			        try{
		        		@SuppressWarnings("unused")
						Long code = new Long (lcontents);
						//String cdate = timeStamp();
		        		//  db.insertPerson(lRowID,ptype,code,cdate,pos, code, code);
		        		populate_table2(sourceList,en_stPerson,lcontents,RowID,stPos);
			        	Log.i(TAG,"add person to db" );
		        	} catch (NumberFormatException e){
		       		 Toast.makeText(this, "Invalid data format", 
		              		Toast.LENGTH_SHORT).show();
		        	}
		        }
		    }
		    doceventType = list.next();
		}
		
		// Handle no events available
		if (bFoundEvents == false) {
			String data = getResources().getString(R.string.no_data);
			Toast.makeText(this, data, 
              		Toast.LENGTH_SHORT).show();
		}	
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