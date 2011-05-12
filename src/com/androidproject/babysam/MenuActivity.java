package com.androidproject.babysam;




import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends babysamActivity {
    /** Called when the activity is first created. */
	private String contents;
	private String format;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG,"In on create" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        Button zxingbut = (Button) findViewById(R.id.zxing_but);
        
        zxingbut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_FORMATS", "CODABAR");
                startActivityForResult(intent, 0);
            }
        });


        
    }
    
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
                TextView zxingresult = (TextView) findViewById(R.id.zxing_result);
                zxingresult.setText(contents);
                


            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	Log.i(TAG,"It failed oh" );
            }
        }
    }
}