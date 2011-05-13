package com.androidproject.babysam;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class EventActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.eventoptions, menu);
	    menu.findItem(R.id.event_help).setIntent( new Intent(this, HelpActivity.class));
	    menu.findItem(R.id.event_addse).setIntent( new Intent(this, SessionActivity.class));
	    menu.findItem(R.id.event_addof).setIntent( new Intent(this, OfficalsActivity.class));
	    menu.findItem(R.id.event_addst).setIntent( new Intent(this, StudentsActivity.class));
	    // TODO - make a method that would save all the current status of events	    
	    menu.findItem(R.id.event_save).setIntent( new Intent(this, HelpActivity.class));
	    // TODO - make a method that would cancel all the current status of events	    
	    menu.findItem(R.id.event_cancel).setIntent( new Intent(this, MenuActivity.class));
	    
	    return true;
    }
}