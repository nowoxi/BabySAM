package com.androidproject.babysam;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Toast;

public class slistActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
    private ArrayList<String> stdData = new ArrayList<String>();
    private ListView slist;
    
    private long RowID, stPos, iRowID;
    private int en_stPerson, pEdit, en_stscan,  reScan, fromIntent;//pEdit is used to reflect if edit or rescan has been set by context menu
    private functions f;
    private String contents, scformat, format;//,  blank,efname, elname;
    private ArrayAdapter<String> adapt;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setupViews();
	}
    
    private void setupViews() {
    	setContentView(R.layout.slist);
        
        LoadPref();
        f = new functions(this);
        en_stPerson =1;
        fromIntent=0;
        
        registerForContextMenu(findViewById(R.id.slistView));
      //Retrieve listview
	    slist = (ListView) findViewById(R.id.slistView);    
	    
	    stdData = studentlistExtract();
	    	    
	    adapt = new ArrayAdapter<String>(this, R.layout.list_item, stdData);        
	    slist.setAdapter(adapt);
	    Log.d(TAG,"List view pupolated" );
	    
	  //get the event id from the intent that was passed
        Intent intent = getIntent();
        if ( intent != null){
        	if (intent.hasExtra("code")){
        		contents= intent.getStringExtra("code");
        		fromIntent=1;
        		entryDialog(0,0);
        	}
        }
	    
	}

	private ArrayList<String> studentlistExtract() {
    	ArrayList<String> leventData = new ArrayList<String>();
    	//create object of DB
    	DBAdapter db = new DBAdapter(this);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllStudents();
        /* Get the indices of the Columns we will need */
        int firstColumn = c.getColumnIndex(DBAdapter.KEY_FIRSTNAME);         
        int lastColumn = c.getColumnIndex(DBAdapter.KEY_LASTNAME);
        int codeColumn = c.getColumnIndex(DBAdapter.KEY_CODE);
        
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
        		Toast.makeText(this, getResources().getString(R.string.unnecessary_func), Toast.LENGTH_SHORT).show();
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
	  Log.i(TAG,"Context menu "+info.id );
	  RowID = f.getPersonID(en_stPerson, info.id);
	  switch (item.getItemId()) {
	  case R.id.event_delete_item:
		  Log.i(TAG,"item id: "+info.id );
	    deleteContext(en_stPerson, info.id);
	    return true;
	  case R.id.event_rescan_item:
		  rescan("CODABAR",en_stPerson,info.id);
		  return true;
	  case R.id.event_edit_item2:
		  stPos = info.id;
		  entryDialog(1,RowID);
		return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}

	private void rescan(String sformat, int pType, long id) {
		//  handle context menu rescan
		stPos = id; //position of row to be edited
		scanSet(en_stscan,sformat,1, RowID);//3rd value set to 1 because it is a rescan
		//RowID has been set from the oncontextitem selected method
	}

	private void deleteContext(int pType, long id) {
		//  delete context menu
		Log.i(TAG,"RowID "+ RowID );
		if (RowID != 0){
			if (f.deletePerson(RowID,pType)){
					stdData.remove((int) id);
					adapt.notifyDataSetChanged();
			}		
		}
	}
	
	public void entryDialog (int psEdit, long psRowID){
		// get content for ev_contents as it is done in - am not suer what this means any more 
		
		pEdit=psEdit;
		iRowID=psRowID;  //setting the row id of person
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
		}
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 //HERE YOU UPDATE THE DATABASE with  dialog if scanning is set AND NOT INSERT.				
				//you insert values from dialog if scanning is not set
			
				EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
						(EditText) dialoglayout.findViewById(R.id.EditText03) };
				
				String [] ev_contents = new String [3];
				for (int i = 0 ; i < eventresult.length; i++)			
					ev_contents[i]= eventresult[i].getText().toString();
				
					//contents = (String) input.getText().toString();
				boolean pass;// variable to know if try failed or not
				try {
					@SuppressWarnings("unused")
					long lcont = new Long(ev_contents[2]);
					enterPerson(ev_contents); //call method that will add the data to both the listview and database
					pass = true;
				}catch (NumberFormatException e){
	        		 Toast.makeText(getApplicationContext(), "Invalid data format", 
	                  		Toast.LENGTH_SHORT).show();
	        		 pass = false;
	         	}
				// checking if loaded from intent and returning
				if (fromIntent==1){
					Intent intent = new Intent();
					if(pass){
						intent.putExtra("PersonID", f.getPersonID(new Long(ev_contents[2]), en_stPerson));
						setResult(RESULT_OK, intent);
					}else{
						setResult(RESULT_CANCELED, intent);
					}
					//slistActivity.this.
					finish();
				}
				Toast.makeText(getApplicationContext(), "content: " + ev_contents[2], Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						// checking if loaded from intent and returning
						if (fromIntent==1){
							finish();
						}
					}
		});
		alert.show();	
			
	}
	
	private void enterPerson(String [] len_content) {
		int pos = 0 ;//calculating position of item edited
		boolean exist = true;
		String fname=len_content[0];
		String lname=len_content[1];
		Long code = new Long(len_content[2]);
		
		long pRowID;
		if (en_stscan == 1 || pEdit == 1){ //if en_stscan is enabled scan would have added thats why we are updating only
			//the value of pRowID must be the id of the record added by the scan intent 
			pRowID = getLastPersonRow(RowID);
			Log.i(TAG,"rescanning or editing student " );
			if (pEdit==1){
				pRowID = iRowID;  //set the person rowID to the id to be edited, done at the unset of entrydialog
				pos =(int) stPos ;//value of person to be edited on listview
			}
			exist = false;//for update is should be allowed to edit list view always
			 f.upd_dbpersondata(pRowID, fname, lname, code);
		} else if (en_stscan == 0){
			exist = f.add_dbpersondata( fname, lname, code);
		}
		
		//this section is for editing the list view	
		if (!exist){
			String lcontents = fname+" "+lname+" "+code; 
			if (pEdit == 0 )stdData.add(lcontents);
			if (pEdit == 1 ){							
				stdData.remove(pos);
				stdData.add(pos, lcontents);
			}
		}
		adapt.notifyDataSetChanged();
	}
	
	private long getLastPersonRow(long lRowID){ 
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllStudents();
        int rowIDColumn = c.getColumnIndex(DBAdapter.KEY_ROWID) ;
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
			Log.d(TAG," Code scaning initiated" );				
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_FORMATS", scan_format);
			intent.putExtra("SCAN_WIDTH", 310 );
			intent.putExtra("SCAN_HEIGHT", 240 );
			startActivityForResult(intent, 0);
    	} else {               	
    		if (scanT == 1){ //to edit the list view correctly on rescan
    			entryDialog(1,ilRowID);//method to create the dialog box straight
    		} else{
    			Log.i(TAG,"Scanning Disabled" );
    			entryDialog(0,0);
    		}    		
        }
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	//super.onActivityResult(requestCode, resultCode, intent);
		// it will not come here if the scan option isn't enabled
		
    	//Log.i(TAG,"to check that onactivity result happens" );
    	
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Log.d(TAG,"requestCode = "+requestCode+" / resultCode = " +resultCode );
                Log.d(TAG,"Format = "+format+" / Contents = " +contents );
                
                try {            
	                Long lcontents = new Long (contents);
	                Log.d(TAG,"contents " + lcontents + " after convertion. also rescan is "+ reScan );
	                if(reScan == 0)f.add_dbpersondata(en_stPerson, lcontents); // using blank as method is expected to be used for both officials and students               
	                if(reScan == 1)f.upd_dbpersondata(en_stPerson, RowID, lcontents);// set rowid from rescan or scanset
	                 
	                //Log.i(TAG," after scanning: "  );
	            }catch (NumberFormatException e){
	       		 Toast.makeText(getApplicationContext(), "Invalid data format", 
	                 		Toast.LENGTH_SHORT).show();
	        	}
                if(reScan == 0)entryDialog(0,0);
                if(reScan == 1)entryDialog(1,RowID);
                
                
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Toast.makeText(this, getResources().getString(R.string.scanfail), Toast.LENGTH_SHORT).show();
            	Log.e(TAG,getResources().getString(R.string.scanfail) );
            }
        }    	
	}

	
	 //to load all prefences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	        en_stscan = eventSettings.getInt(ST_SCAN, 1);
    }
}