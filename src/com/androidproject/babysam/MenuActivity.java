package com.androidproject.babysam;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MenuActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        ListView menuList = (ListView) findViewById(R.id.menu_list);
        
        String[] menuitems = { getResources().getString(R.string.menu_item_event),
        		getResources().getString(R.string.menu_item_old),
        		getResources().getString(R.string.menu_item_facerec),
        		getResources().getString(R.string.menu_item_olist),
        		getResources().getString(R.string.menu_item_slist),
        		getResources().getString(R.string.menu_item_set),
        		getResources().getString(R.string.menu_item_help),
        		getResources().getString(R.string.menu_item_logout)};
        
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,
        		R.layout.menu_item, menuitems);
        
        menuList.setAdapter(adapt);
        eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        TextView welcomeView = (TextView) findViewById(R.id.userView);
        functions f = new functions(this);
        welcomeView.setText(f.getWelcomeMessage(eventSettings.getString(USER, "")));
        Log.i(TAG, eventSettings.getString(USER, ""));
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
		    	TextView textView = (TextView) itemClicked;
		    	String strText = textView.getText().toString();
		    	if (strText.equalsIgnoreCase(getResources().getString(R.string.menu_item_event))) {
		    	// Launch the Events Activity
		    		startActivity(new Intent(MenuActivity.this, EventActivity.class));
		    	} else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_help))) {
		    			// Launch the Help Activity
		    			startActivity(new Intent(MenuActivity.this,HelpActivity.class));
		    	} else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_old))) {
		    			// Launch the old session Activity
		    			startActivity(new Intent(MenuActivity.this,	OldActivity.class));
		    			
		    	} else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_facerec))) {
		    			// Launch the facerec Activity
		    			startActivity(new Intent(MenuActivity.this, FaceActivity.class));
		    	} else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_set))) {
	    			// Launch the old session Activity
	    			startActivity(new Intent(MenuActivity.this,	settingsActivity.class));
		    	}else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_olist))) {
	    			// Launch the facerec Activity
	    			startActivity(new Intent(MenuActivity.this, olistActivity.class));
		    	} else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_slist))) {
	    			// Launch the old session Activity
	    			startActivity(new Intent(MenuActivity.this,	slistActivity.class));
		    	} else if (strText.equalsIgnoreCase(getResources().getString( R.string.menu_item_logout))) {
	    			// Launch the old session Activity
		    		single_SavePref(USER,"");
	    			startActivity(new Intent(MenuActivity.this,	LoginActivity.class));
	    			MenuActivity.this.finish();
		    	}
		    }
    	});
        
    }
    
    private void single_SavePref(String key, String value){
        SharedPreferences.Editor editor = eventSettings.edit();
        editor.putString(key, value);
        editor.commit();
       }

}