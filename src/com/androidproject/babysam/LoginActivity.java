package com.androidproject.babysam;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Button authbut = (Button) findViewById(R.id.auth_but);
        authbut.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
        // Handle date picking dialog
        	startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        	LoginActivity.this.finish();
        }
        });
        
        
    }
}