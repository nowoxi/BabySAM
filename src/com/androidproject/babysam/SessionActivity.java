package com.androidproject.babysam;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class SessionActivity extends babysamActivity {
    /** Called when the activity is first created. */
	
	   private String contents;
	   private String format;
	    
	    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.session_options, menu);
	    menu.findItem(R.id.session_help).setIntent( new Intent(this, HelpActivity.class));
	  //  menu.findItem(R.id.session_scan).
	    return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	//private static final int m = INSERT_ID + 1;
    	Log.i(TAG,"1" );
        switch(item.getItemId()) {        	
            case R.id.session_scan:
            	Log.i(TAG,"2" );
            	   Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                   intent.setPackage("com.google.zxing.client.android");
                   intent.putExtra("SCAN_FORMATS", "QR_CODE");
                   startActivityForResult(intent, 0);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	//super.onActivityResult(requestCode, resultCode, intent);
    	Log.i(TAG,"to check that onactivity result happens" );
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Log.i(TAG,"requestCode = "+requestCode+" / resultCode = " +resultCode );
                Log.i(TAG,"Format = "+format+" / Contents = " +contents );
                EditText zxingresult = (EditText) findViewById(R.id.EditText01);
                zxingresult.setText(contents);
                


            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Log.i(TAG,"It failed oh" );
            }
        }
	}
	
}