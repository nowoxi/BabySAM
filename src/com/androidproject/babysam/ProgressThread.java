package com.androidproject.babysam;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

    public class ProgressThread extends Thread {	
    	
    	// Class constants defining state of the thread
    	private Handler handler;
    	private functions f;
		private long extra_EID;
		private String[] eventDetails;
		private String upload, username;
		private int age;
		Context context;
		
        // Constructor with an argument that specifies Handler on main thread
        // to which messages will be sent by this thread.
        
        ProgressThread(Handler h, Context ctx,long e,String[] s, String u,int a, String user ) {//u is used to bring the url for aries
            handler = h;
            f= new functions(ctx);
            extra_EID = e;
            eventDetails = s;
            upload = u;
            context = ctx;
            age= a;
            username = user;
            //Log.i("BabySAM","send emailrun @@" );
        }
        
        @Override
        public void run() {
            Looper.prepare();
            if(upload.equalsIgnoreCase("Mail"))f.sendEmail(extra_EID, eventDetails, username);
            if(!upload.equalsIgnoreCase("Mail"))f.sendAries(extra_EID,age,upload, username);;
                    
            handler.sendEmptyMessage(0);
            Looper.loop();
        }
    }
