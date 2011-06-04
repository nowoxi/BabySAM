package com.androidproject.babysam;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

    public class ProgressThread extends Thread {	
    	
    	// Class constants defining state of the thread
    	Handler handler;
        functions f;
		private long extra_EID;
		private String[] eventDetails;
		private ArrayList<String> offeventData;
		private ArrayList<String> stdeventData;
       
        // Constructor with an argument that specifies Handler on main thread
        // to which messages will be sent by this thread.
        
        ProgressThread(Handler h, Context ctx,long e,String[] s, ArrayList<String> o, ArrayList<String> st ) {
            handler = h;
            f= new functions(ctx);
            extra_EID = e;
            eventDetails = s;
            offeventData = o;
            stdeventData = st;
            
        }
        
        @Override
        public void run() {
        	Looper.prepare();
        	Log.i("BabySAM","send emailrun " );
            f.sendEmail(extra_EID, eventDetails, offeventData, stdeventData);
            Log.i("BabySAM","send emailrun 2" );
            handler.sendEmptyMessage(0);
        }
    }
