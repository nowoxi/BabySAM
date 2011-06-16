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
    private int en_stPerson, pEdit, en_stscan, en_ofscan, en_evscan, reScan;//pEdit is used to reflect if edit or rescan has been set by context menu
    private functions f;
    private String contents, blank, scformat, format, efname, elname;
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
	    	    
	    adapt = new ArrayAdapter<String>(this, R.layout.list_item, stdData);        
	    slist.setAdapter(adapt);
	    Log.i(TAG,"List view pupolated" );
    }
    
    private ArrayList<String> studentlistExtract() {
    	ArrayList<String> leventData = new ArrayList<String>();
    	//create object of DB
    	DBAdapter db = new DBAdapter(this);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllStudents();
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
		int scanNew = 0;
		switch(item.getItemId()) { 	
        	case R.id.list_single:
        		scformat = "CODABAR";
	        	scanSet(en_stscan, scformat, scanNew,0);	
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
	  Log.i(TAG,"Context menu" );
	  //long ilRowID = 0;//f.getPersonID(info.id,en_stPerson,RowID);
	  
	  switch (item.getItemId()) {
	  case R.id.event_delete_item:
		  Log.i(TAG,"item id: "+info.id );
	    deleteContext(en_stPerson, info.id);
	    return true;
	  case R.id.event_rescan_item:
		  rescan("CODABAR",en_stPerson,info.id);
		  return true;
	  case R.id.event_edit_item:
		;
		return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}

	private void rescan(String sformat, int pType, long id) {
		// TODO handle context menu rescan
		int en=0;
		stPos = id; //position of row to be edited
		RowID = id +1;
		scanSet(en,sformat,1, RowID);//3rd value set to 1 because it is a rescan
	}

	private void deleteContext(int en_stPerson2, long id) {
		// TODO delete context menu
		RowID = id +1;
		if (f.deletePerson(RowID,en_stPerson2)){
				stdData.remove((int) id);
				adapt.notifyDataSetChanged();
		}		
	}
	
	public void entryDialog (int psEdit, long psRowID){
		// TODO get content for ev_contents as it is done in 
		
		pEdit=psEdit;
		iRowID=psRowID;  //setting the row id of person
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		//final EditText input = new EditText(this);
		//final EditText [] input = { new EditText(this), new EditText(this)};
		//the value for en_stperson might be lost when calling intent please check  
		//Log.i(TAG,"fail 1 " );
	    LayoutInflater inflater = getLayoutInflater();
		final View dialoglayout = inflater.inflate(R.layout.stddialog, (ViewGroup) findViewById(R.id.slayout_root));
	
		alert.setView(dialoglayout);
		EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
				(EditText) dialoglayout.findViewById(R.id.EditText03) };
		
		String [] ev_contents = new String [3];
		if(psEdit == 1){
			ev_contents = f.single_personExtract(psRowID, en_stPerson);
			if (ev_contents  != null)
				for (int i = 0 ; i < eventresult.length; i++)				
					eventresult[i].setText(ev_contents[i]);
			}
		else {
			eventresult[2].setText(contents);
			//Log.i(TAG,"add contents oh "+ contents + " also" + psEdit);
		}
		
		
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 //HERE YOU UPDATE THE DATABASE with  dialog if scanning is set AND NOT INSERT.				
				//you insert values from dialog if scanning is not set
			
				//pRowID = 0;	
				EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
						(EditText) dialoglayout.findViewById(R.id.EditText03) };
				
				String [] ev_contents = new String [3];
				for (int i = 0 ; i < eventresult.length; i++)			
					ev_contents[i]= eventresult[i].getText().toString();
				
					//contents = (String) input.getText().toString();
				try {
					@SuppressWarnings("unused")
					long lcont = new Long(ev_contents[2]);
					enterPerson(ev_contents); //call method that will add the data to both the listview and database
				}catch (NumberFormatException e){
	        		 Toast.makeText(getApplicationContext(), "Invalid data format", 
	                  		Toast.LENGTH_SHORT).show();
	         	}
				Toast.makeText(getApplicationContext(), "content: " + ev_contents[2], Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
		});
		alert.show();	
			
	}
	
	private void enterPerson(String [] len_content) {
		
		int pos = 0 ;//calculating position of item edited
		
		String fname=len_content[0];
		String lname=len_content[1];
		Long code = new Long(len_content[2]);
		
		long pRowID;
		if (en_stscan == 1 || pEdit == 1){ //if en_stscan is enabled scan would have added thats why we are updating only
			//the value of pRowID must be the id of the record added by the scan intent 
			Log.i(TAG,"fail" );
			pRowID = getLastPersonRow(RowID);
			pos = stdData.size();
			Log.i(TAG,"rescanning or editing student" );
			if (pEdit==1){
				pRowID = iRowID;  //set the person rowID to the id to be edited, done at the unset of entrydialog
				pos =(int) stPos ;//value of person to be edited on listview
			}
			
			if (en_stPerson == 1)f.upd_dbpersondata(pRowID, fname, lname, code);
		} else if (en_ofscan == 0){
			if (en_stPerson == 1)f.add_dbpersondata( fname, lname, code);
		}
		
		//this section is for editing the list view	
		String lcontents = fname+" "+lname+" "+code;//TODO set value 
		if (pEdit == 0 )stdData.add(lcontents);
		if (pEdit == 1 ){							
			stdData.remove(pos);
			stdData.add(pos, lcontents);
		}
		adapt.notifyDataSetChanged();
	}
	
	private long getLastPersonRow(long lRowID){ 
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
	
	// the if would contain an or to join the 3 conditions
	//this method is used to load the barcode scanner if option enabled
	private void scanSet(int sett, String scan_format, int scanT, long ilRowID){
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
    		//contents = "";
    		if (scanT == 1){ //to edit the list view correctly on rescan
    			//contents = Long.toString(f.getPersonCode(ilRowID,en_stPerson));
    			entryDialog(1,ilRowID);//method to create the dialog box straight
    		} else{
    			Log.i(TAG,"2 no scan" );
    			entryDialog(0,0);
    		}    		
        }
	}
	
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
                //int pos = 0;
                
                Log.i(TAG, "Array size "+stdData.size() );
                
                try {            
	                Long lcontents = new Long (contents);
	                Log.i(TAG,"contents " + lcontents + " after convertion. also rescan is "+ reScan );
	                if(reScan == 0)f.add_dbpersondata(en_stPerson, lcontents); // using blank as method is expected to be used for both officials and students               
	                if(reScan == 1)f.upd_dbpersondata(en_stPerson, RowID, lcontents);//TODO set rowid from rescan or scanset
	                 
	                
	                Log.i(TAG," after scanning: "  );
	            }catch (NumberFormatException e){
	       		 Toast.makeText(getApplicationContext(), "Invalid data format", 
	                 		Toast.LENGTH_SHORT).show();
	        	}
                if(reScan == 0)entryDialog(0,0);
                if(reScan == 1)entryDialog(1,RowID);
                
                
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Log.i(TAG,"It failed oh" );
            }
        }    	
	}

	
	 //to load all prefences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        en_stscan = eventSettings.getInt(ST_SCAN, 1);
	        en_ofscan = eventSettings.getInt(OF_SCAN, 1);
	        en_evscan = eventSettings.getInt(EV_SCAN, 1);
    }
}