package com.androidproject.babysam;

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
		private String upload;
		
        // Constructor with an argument that specifies Handler on main thread
        // to which messages will be sent by this thread.
        
        ProgressThread(Handler h, Context ctx,long e,String[] s, String u ) {//u is used to bring the url for aries
            handler = h;
            f= new functions(ctx);
            extra_EID = e;
            eventDetails = s;
            upload = u;
        }
        
        @Override
        public void run() {
        	Looper.prepare();
        	Log.i("BabySAM","send emailrun " );
            if(upload.equalsIgnoreCase("Mail"))f.sendEmail(extra_EID, eventDetails);
            if(!upload.equalsIgnoreCase("Mail"))f.sendAries(extra_EID,0,upload);;
            Log.i("BabySAM","send emailrun 2" );
            handler.sendEmptyMessage(0);
        }
    }
