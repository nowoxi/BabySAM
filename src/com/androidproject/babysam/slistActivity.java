package com.androidproject.babysam;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class slistActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
    private ArrayList<String> stdData = new ArrayList<String>();
    private ListView slist;
    
    private long RowID, stPos, iRowID;
    private int en_stPerson, pEdit, en_stscan, en_ofscan, en_evscan;//pEdit is used to reflect if edit or rescan has been set by context menu
    private functions f;
    private String contents, blank;
    private ArrayAdapter<String> adapt;
    private String [] en_content;// TODO assign value to it from the entry dialog method
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slist);
        
        LoadPref();
        f = new functions(this);
        blank = " ";
        en_stPerson =1;
        
        registerForContextMenu(findViewById(R.id.slistView));
      //Retrieve listview
	    slist = (ListView) findViewById(R.id.slistView);    
	    
	    stdData = studentlistExtract();
	    	    
	    adapt = new ArrayAdapter<String>(this, R.layout.menu_item, stdData);        
	    slist.setAdapter(adapt);
	    Log.i(TAG,"List view pupolated" );
    }
    
    private ArrayList<String> studentlistExtract() {
    	ArrayList<String> leventData = new ArrayList<String>();
    	//create object of DB
    	DBAdapter db = new DBAdapter(this);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEvents();
        /* Get the indices of the Columns we will need */
        int firstColumn = c.getColumnIndex(db.KEY_FIRSTNAME);         
        int lastColumn = c.getColumnIndex(db.KEY_LASTNAME);
        int codeColumn = c.getColumnIndex(db.KEY_CODE);
        
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 /* Add current Entry to meventData. */
                 leventData.add(c.getString(firstColumn)+" "+c.getString(lastColumn)+" "+c.getString(codeColumn));
             } while (c.moveToNext());
        else
            Toast.makeText(this, "No Students found", 
            		Toast.LENGTH_SHORT).show();
        db.close();
    	return leventData;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.list_options, menu);
	    menu.findItem(R.id.help_menu_item).setIntent( new Intent(this, HelpActivity.class));
	    return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	//this section will run the barcode scanner as an intent if student barcode scanning is enabled
		switch(item.getItemId()) { 	
        	case R.id.list_single:
	        	;	
            return true;
        	case R.id.list_multiple:
        		;
            return true;
        }
		return super.onOptionsItemSelected(item);
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		  super.onCreateContextMenu(menu, v, menuInfo);
		  MenuInflater inflater = getMenuInflater();	  
		  inflater.inflate(R.menu.event_context2, menu);
		}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  long ilRowID = f.getPersonID(info.id,en_stPerson,RowID);
	  
	  switch (item.getItemId()) {
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
	  default:
	    return super.onContextItemSelected(item);
	  }
	}

	private void rescan(String string, int en_stPerson2, long ilRowID) {
		// TODO handle context menu rescan
		
	}

	private void deleteContext(int en_stPerson2, int id, long ilRowID) {
		// TODO delete context menu
		
	}
	
	public void entryDialog (int psEdit, long psRowID){
		// TODO get content for ev_contents as it is done in 
		
		pEdit=psEdit;
		iRowID=psRowID;  //setting the row id of person
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		//final EditText [] input = { new EditText(this), new EditText(this)};
		//the value for en_stperson might be lost when calling intent please check  
		//Log.i(TAG,"fail 1 " );
	    LayoutInflater inflater = getLayoutInflater();
			final View dialoglayout = inflater.inflate(R.layout.session, (ViewGroup) findViewById(R.id.layout_root3));
		
			alert.setView(input);			
			input.setText(contents);	
		
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 //HERE YOU UPDATE THE DATABASE with  dialog if scanning is set AND NOT INSERT.				
				//you insert values from dialog if scanning is not set
			
				//pRowID = 0;				
					contents = (String) input.getText().toString();
					try {
						@SuppressWarnings("unused")
						long lcont = new Long(contents);
						enterPerson(contents); //call method that will add the data to both the listview and database
					}catch (NumberFormatException e){
		        		 Toast.makeText(getApplicationContext(), "Invalid data format", 
		                  		Toast.LENGTH_SHORT).show();
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
	
	private void enterPerson(String lcontents) {
		
		int pos = 0 ;//calculating position of item edited
		String uname= blank, pass = blank;
		if (en_stPerson == 2){
			uname = en_content[3];
			pass = en_content[4];
		}
		
		String fname=en_content[0];
		String lname=en_content[1];
		Long code = new Long(en_content[2]);
		
		long pRowID;
		if (en_stscan == 1 || pEdit == 1){ //if en_stscan is enabled scan would have added thats why we are updating only
			//the value of pRowID must be the id of the record added by the scan intent 
			Log.i(TAG,"fail" );
			pRowID = getLastPersonRow(RowID);
			pos = stdData.size();
			Log.i(TAG,"rescanning or editing student" );
			if (pEdit==1){
				pRowID = iRowID;  //set the person rowID to the id to be edited, done at the unset of entrydialog
				pos =(int) stPos ;//value of person to be edited
			}
			
			f.upd_dbpersondata(en_stPerson, pRowID, fname, lname, code, uname, pass);
		} else if (en_ofscan == 0){
			f.add_dbpersondata(en_stPerson, fname, lname, code, uname, pass);
		}
		
		//this section is for editing the list view						
		if (pEdit == 0 )stdData.add(lcontents);
		if (pEdit == 1 ){							
			stdData.remove(pos);
			stdData.add(pos, lcontents);
		}
		adapt.notifyDataSetChanged();
	}
	
	public long getLastPersonRow(long lRowID){ 
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllStudents();
		if (en_stPerson == 2) c = db.getAllOfficials();
        int rowIDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        long LRowID=0;
		if (c.moveToLast()) LRowID = c.getLong(rowIDColumn);//	Log.i(TAG,"test " );        
        db.close();
	    //    Log.i(TAG,lRowID+ " Last Person Row ID : "+ LRowID+"got info from "+ r );	
		return LRowID;
	}
	
	 //to load all prefences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        en_stscan = eventSettings.getInt(ST_SCAN, 1);
	        en_ofscan = eventSettings.getInt(OF_SCAN, 1);
	        en_evscan = eventSettings.getInt(EV_SCAN, 1);
    }
}