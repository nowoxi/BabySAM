package com.androidproject.babysam;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends babysamActivity {
    /** Called when the activity is first created. */
	private EditText uname;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Button authbut = (Button) findViewById(R.id.auth_but);
        authbut.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        // Handle date picking dialog
	        	uname = (EditText)findViewById(R.id.authEText);
	        	String user = uname.getText().toString();
	        	checkUsername(user);
	        }
        });
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == 1){
			 if (resultCode == RESULT_OK) {
				 String user = intent.getStringExtra("username");
				 single_SavePref(USER,user);
				 startActivity(new Intent(LoginActivity.this, MenuActivity.class));
	    		 LoginActivity.this.finish();
			 } else if (resultCode == RESULT_CANCELED) {
	                // Handle cancel
				 Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_message),Toast.LENGTH_LONG).show();
	            	Log.w(TAG,getResources().getString(R.string.login_message) );
	         }
		}
	}

    private void checkUsername(String user) {
		if ( user.equalsIgnoreCase(null)||user.equalsIgnoreCase("")||user.length() < 3){
    		Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_message),Toast.LENGTH_LONG).show();
    	}else {
    		functions f = new functions(getApplicationContext());
    		ArrayList<String> unames = f.getAllUsernames();
    		Boolean check = false;
    		int i = 0;
    		while (i < unames.size() && !check){
        		if (user.equalsIgnoreCase(unames.get(i))) check = true;
        		i++; 
    		}
    		if (!check){
    			Class<?> cls= olistActivity.class;
    			Intent intent = new Intent(LoginActivity.this,cls);	
    			intent.putExtra("username", user);
    			startActivityForResult(intent,1);
    		}else{
    			uname.setText("");
        		single_SavePref(USER,user);
    			startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        		LoginActivity.this.finish();
    		}
    	}
	}
	private void single_SavePref(String key, String value){
		eventSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = eventSettings.edit();
        editor.putString(key, value);
        editor.commit();
       }
}