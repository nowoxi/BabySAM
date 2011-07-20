package com.androidproject.babysam;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class settingsActivity extends babysamActivity {
	
	   private ListView menuList;
	   private int count = T_SCAN.length;
	   private int [] en_scan = new int [count];
	   private long EventID;
	   private String uri;
	   private functions f;
	   ArrayAdapter<String> adapt;
	   
	   //I need to develop the xml to select settings and a method to save to the share preferences
	   //improve code here to use more of if and for loops instead of the repeated sequences
	   
	   /* TODO 
	    * add setting for adding users to the database for test purposes. Add setting on scanning list and individual users
	    */
	   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        f = new functions(this);
        
        menuList = (ListView) findViewById(R.id.settings_list);
        
        
        uri = "";
        String ariesMenuItemTemp = getResources().getString(R.string.set_aries)+"\n"+uri;
        String[] menuitems = { getResources().getString(R.string.set_st),
        		getResources().getString(R.string.set_of),
        		getResources().getString(R.string.set_ev),
        		getResources().getString(R.string.set_url),
        		getResources().getString(R.string.set_db),
        		ariesMenuItemTemp };
        
        adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, menuitems);
        
        menuList.setAdapter(adapt);
        menuList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        menuList.setItemsCanFocus(false);
        LoadPref();
        //uri = "http://lexican.com.ng/babysam/upload.php";
        final String ariesMenuItem = getResources().getString(R.string.set_aries)+"\n"+uri;
        menuitems[5]= ariesMenuItem;
        adapt.notifyDataSetChanged();
        
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
		    	TextView textView = (TextView) itemClicked;
		    	String strText = textView.getText().toString();
		    	
		    	if (strText.equalsIgnoreCase(ariesMenuItem)) {
		    		Context context = settingsActivity.this;
		    		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		    		final EditText input = new EditText(context);
		    		alert.setView(input);			
					input.setText(uri);
					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String uriTemp  = (String) input.getText().toString();
							String httpheader = "http://",httptail = "upload.php";
							if (!uriTemp.startsWith(httpheader)){//add header if missing.. any other thing used should be self corrected correct
								uriTemp= httpheader.concat(uriTemp);
							}
							if (!uriTemp.endsWith(httptail)){//add upload.php if missing.. any other thing used should correct
								if (uriTemp.endsWith("/")){
									uriTemp= uriTemp.concat(httptail);
								}else {
									uriTemp=uriTemp+"/"+httptail;
								}
							}
							try {
								@SuppressWarnings("unused")
								URL uriTest = new URL (uriTemp);
								uri = uriTemp;
								adapt.notifyDataSetChanged();
							} catch (MalformedURLException e) {
								e.printStackTrace();
								Toast.makeText(getApplicationContext(), "Invalid URL", 
				                  		Toast.LENGTH_SHORT).show();
							}
							
							Toast.makeText(getApplicationContext(), "URL: " + uri, Toast.LENGTH_SHORT).show();
						}
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.cancel();
								}
							});
					alert.show();		
				}
		    	
		    	
		    	} 
		    }
    	);
    }        
    
    @Override
	protected void onPause() {	
		super.onPause();
		Save();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.settingsoptions, menu);
	    return true;	    
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	//this section will run the barcode scanner as an intent if student barcode scanning is enabled
    	//Log.i(TAG, R.id.add_data+" options: "+item.getItemId());
		switch(item.getItemId()) {        	
        	case R.id.set_save:
	        	settingsActivity.this.finish();
	        return true;
	        
        	case R.id.set_clear:
	        	clear();
    	    return true;
    	    
        	case R.id.set_All:
	        	setAll();
    	    return true;  
    	    
        	case R.id.add_data:
        		//Log.i(TAG, "add to db 1");
        		add_dbdata();
    	    return true;  
    	    
        	case R.id.delete_alldb:
        		delete_db_Tab();
    	    return true;  
		}
		return super.onOptionsItemSelected(item);
    }
    
    private void add_dbdata(){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(this); 
        db.open();        
        db.insertEvent(
        		"Examination",
        		"NW104",
        		"Electrojumper",
        		60,
        		0,
        		"12:00");
        Long u = new Long ("20116001325041");
        Random randomGenerator = new Random();
        for (int i = 0; i<3;i++)db.insertStudent(u + randomGenerator.nextInt(1000), getRandomfName(), getRandomlName());        
        for (int i = 0; i<1;i++){
        	String name = getRandomlName();
        	db.insertOfficial(u + randomGenerator.nextInt(1000), getRandomfName(), name, name,"pass");        
        }
      //db.insertEventPerson(eventid, ptype, pID, timestamp, position, present, list)
        int j = randomGenerator.nextInt(2);
        int k = randomGenerator.nextInt(2);
        for (int i = 0; i<3;i++)db.insertEventPerson(EventID, 1, (i+1)*EventID, f.timeStamp(), randomGenerator.nextInt(2), randomGenerator.nextInt(2));
        Boolean t = db.insertEventPerson(EventID, 2, EventID, f.timeStamp(), 1, 1);
        Log.i(TAG," "+t+" then: "+j+k );
        Log.i(TAG," "+EventID );
        //for (int i = 0; i<3;i++)db.insertEventPerson(2, 1, i+1, f.timeStamp(), i, 0, 1);
        //db.insertEventPerson(2, 2, 1, f.timeStamp(), 0, 0, 1);
        
        
        db.close();
        EventID += 1;
        Log.i(TAG, "add to db");
    }
    
    private void delete_db_Tab(){
    	DBAdapter db = new DBAdapter(this); 
        db.open(); 
        EventID = 1;
        single_SavePref(NEW_SET[0], EventID);
        db.deleteAllTables();
        db.close();
    }
    
    //to load all prefences to their variables only used in event
    private void LoadPref(){// this is used to load all the current settings on start of activity
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	    	for (int i = 0; i < count; i++) {
	            en_scan[i] = eventSettings.getInt(T_SCAN[i], 1);
	            this.menuList.setItemChecked(i, true);
	            if (en_scan[i] == 0) this.menuList.setItemChecked(i, false);	
	        }
	    	uri = eventSettings.getString(NEW_SET[1], "http://lexican.com.ng/babysam/upload.php");
	    	EventID = eventSettings.getLong(NEW_SET[0], 1);
    }    
    
    //to save all the preferences only used in settings activity
    private void Save(){
    	//used to save the value of the variables derived from the settings xml and saving it to the current settings
    	for (int i = 0; i < count; i++) {
            en_scan[i] = 0;
            if (this.menuList.isItemChecked(i))en_scan[i] = 1; 
        }
    	
    	//URI AND EVENT ID NOT NECESSARY TO BE SET HERE
    	SavePref();
    	Toast.makeText(getApplicationContext(), R.string.settings_save, Toast.LENGTH_SHORT).show();
    }
    
    private void setAll(){
    	int count = this.menuList.getAdapter().getCount();
    	for (int i = 0; i < count; i++) {
            this.menuList.setItemChecked(i, true);
        }
    }
    
    private void clear(){
    	int count = this.menuList.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            this.menuList.setItemChecked(i, false);
        }
    }    
    	
    private void SavePref(){
        for (int i = 0; i < count; i++) {
        	single_SavePref(T_SCAN[i], en_scan[i]);
        }
        single_SavePref(NEW_SET[0], EventID);
        single_SavePref(NEW_SET[1], uri);
        //Log.i(TAG, uri+" test");
       }
    
    private void single_SavePref(String key, int value){
        SharedPreferences.Editor editor = eventSettings.edit();
        editor.putInt(key, value);
        editor.commit();
       }
    private void single_SavePref(String key, String value){
        SharedPreferences.Editor editor = eventSettings.edit();
        editor.putString(key, value);
        editor.commit();
       }
    private void single_SavePref(String key, long value){
        SharedPreferences.Editor editor = eventSettings.edit();
        editor.putLong(key, value);
        editor.commit();
       }
    
    public String getRandomfName(){
		Random r1 = new Random();		
		String[] names = new String[]{
			"Tom","Jacob","Jake",
			"Ethan","Jonathan","Tyler","Samuel","Nicholas","Angel",
			"Jayden","Nathan","Elijah","Christian","Gabriel","Benjamin",
			"Emma","Aiden","Ryan","James","Abigail","Logan","John",
			"Daniel","Alexander","Isabella","Anthony","William","Christopher","Matthew","Emily","Madison",
			"Rob","Ava","Olivia","Andrew","Joseph","David","Sophia","Noah",
			"Justin",};		
		
		int indexF = r1.nextInt(names.length - 1);
		return names[indexF];
	}
    
    public String getRandomlName(){
		Random r2 = new Random();
		String[] lNames = new String[]{
			"Smith","Johnson","Williams","Jones","Brown","Davis","Miller","Wilson","Moore",
			"Taylor","Anderson","Thomas","Jackson","White","Harris","Martin","Thompson","Garcia",
			"Martinez","Robinson","Clark","Lewis","Lee","Walker","Hall","Allen","Young",
			"King","Wright","Hill","Scott","Green","Adams","Baker","Carter","Turner","Tolulope"
		};
		int indexL = r2.nextInt(lNames.length - 1);
		
		return lNames[indexL];
	}
}
