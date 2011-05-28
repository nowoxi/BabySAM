package com.androidproject.babysam;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	   private String [] ev_contents;
	   
	   
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
		protected void onDestroy() {
			// TODO Auto-generated method stub
			//   alert.dismiss();
			super.onDestroy();
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
	        	event_cancel();	
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
			Log.i(TAG,"2" );
				
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_FORMATS", scan_format);
			intent.putExtra("SCAN_WIDTH", 300 );
			intent.putExtra("SCAN_HEIGHT", 240 );
			startActivityForResult(intent, 0);
    	} else {
               	//method to create the dialog box straight
    		contents = "test";
    		entryDialog();
        }
	}
	
	public void entryDialog (){
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		//TODO-the value for en_stperson might be lost when calling intent please check  
	    
		if (en_stPerson == 3){
			LayoutInflater inflater = getLayoutInflater();
			View dialoglayout = inflater.inflate(R.layout.session, (ViewGroup) findViewById(R.id.layout_root3));
			alert.setView(dialoglayout);
			EditText [] eventresult = { (EditText) dialoglayout.findViewById(R.id.EditText01), (EditText) dialoglayout.findViewById(R.id.EditText02),
					(EditText) dialoglayout.findViewById(R.id.EditText03), (EditText) dialoglayout.findViewById(R.id.EditText04) };
			//split_content();
			ev_contents = contents.split(content_delimiter);
			for (int i = 0 ; i < ev_contents.length; i++){
				eventresult[i].setText(ev_contents[i]);
			}
            
		} else {
		//	final EditText input = new EditText(this);
			alert.setView(input);
			input.setText(contents);			
			
		}
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 
				saveData();
				Toast.makeText(getApplicationContext(), "content: " + contents, Toast.LENGTH_SHORT).show();
				if (en_stPerson == 3){
					TextView [] text = {(TextView) findViewById(R.id.textView1),(TextView) findViewById(R.id.textView2),(TextView) findViewById(R.id.TextView02),
			    	    	(TextView) findViewById(R.id.TextView01)};
					for (int i = 0; i < 4 ; i++){
			    		text[i].setText(ev_contents[i]);
			    	}
				}else {
					contents = (String) input.getText().toString();
					if (en_stPerson == 1){
						stdeventData.add(contents);
						std_adapt.notifyDataSetChanged();
					} else if (en_stPerson == 2){
						offeventData.add(contents);
						off_adapt.notifyDataSetChanged();
					} 
				} 
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
		
	}
	
	public void saveData(){
		//TODO - update the database with the value from content
		// bear in mind if official or student. so ensure en_stperson= 3 is never entered into db
		if (en_stPerson == 3){
			
		}else {
			//final EditText input = new EditText(this);
			//contents = (String) input.getText().toString(); 
		    
		}
	}
    
	public void event_cancel(){
		// TODO - make a method that would cancel all the current status of events	i.e. deleting from the database
	}
	
	public void split_content (){
		
	}
	//junk code
	//String value = input.getText().toString().trim();
}