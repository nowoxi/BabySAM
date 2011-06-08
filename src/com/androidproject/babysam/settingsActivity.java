package com.androidproject.babysam;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class settingsActivity extends babysamActivity {
	
	   //private int en_stscan, en_ofscan, en_evscan;
	   private ListView menuList;
	   private int count = T_SCAN.length;
	   private int [] en_scan = new int [count];

	   
	   //I need to develop the xml to select settings and a method to save to the share preferences
	   //improve code here to use more of if and for loops instead of the repeated sequences
	   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
 
        menuList = (ListView) findViewById(R.id.settings_list);
        String[] menuitems = { getResources().getString(R.string.set_st),
        		getResources().getString(R.string.set_of),
        		getResources().getString(R.string.set_ev),
        		getResources().getString(R.string.set_db)};
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, menuitems);

        menuList.setAdapter(adapt);
        menuList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        menuList.setItemsCanFocus(false);
        LoadPref();
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
        //long id;
        db.insertEvent(
        		"Examination",
        		"NW104",
        		"Electrojumper",
        		60,
        		0,
        		"12:00");
       // Long u = new Long ("20116001325041");
        db.insertPerson(2,2,new Long ("23116001325041"),"12:00",0);
        db.insertPerson(2,1,new Long ("23115001325041"),"12:00",0);
        db.insertPerson(2,1,new Long ("23114001325041"),"12:00",1);
        db.insertPerson(2,1,new Long ("23113001325041"),"12:00",2);
        db.insertPerson(1,1,new Long ("23113001325041"),"12:00",0);
        db.insertPerson(1,1,new Long ("23113001325041"),"12:00",1);
        db.insertPerson(1,1,new Long ("23113001325041"),"12:00",2);
        db.insertPerson(1,2,new Long ("23113001325041"),"12:00",3);
        db.close();
    }
    
    private void delete_db_Tab(){
    	DBAdapter db = new DBAdapter(this); 
        db.open(); 
        db.deleteAllTables();
        db.close();
    }
    //to load all prefences to their variables only used in event
    private void LoadPref(){
	    	eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	    	for (int i = 0; i < count; i++) {
	            en_scan[i] = eventSettings.getInt(T_SCAN[i], 1);
	            this.menuList.setItemChecked(i, true);
	            if (en_scan[i] == 0) this.menuList.setItemChecked(i, false);		        
	        }
    }    
    
    //to save all the preferences only used in settings activity
    private void Save(){
    	//used to save the value of the variables derived from the settings xml and saving it to the current settings
    	for (int i = 0; i < count; i++) {
            en_scan[i] = 0;
            if (this.menuList.isItemChecked(i))en_scan[i] = 1; 
        }
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
       }
    
    private void single_SavePref(String key, int value){
        SharedPreferences.Editor editor = eventSettings.edit();
        editor.putInt(key, value);
        editor.commit();
       }
}
