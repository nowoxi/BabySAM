package com.androidproject.babysam;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class SessionActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.session_options, menu);
	    menu.findItem(R.id.help_menu_item).setIntent(
	    new Intent(this, HelpActivity.class));
	    return true;
    }
}