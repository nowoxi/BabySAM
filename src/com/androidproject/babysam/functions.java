package com.androidproject.babysam;


import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class functions 
{
	private final Context context;
	public static final String TAG = "BabySAM";
	
	
	public functions(Context ctx) 
    {
        this.context = ctx;        
    }
	public String [] eventExtract (long extra_EID){
		String [] eventDetails = new String [5];
    	//create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getEvent(extra_EID);
        /* Get the indices of the Columns we will need */
        //int timeColumn = c.getColumnIndex("timestamp");         
        int eventTypeColumn = c.getColumnIndex("evType");
        int venueColumn = c.getColumnIndex("venue");
        int courseColumn = c.getColumnIndex("course");
        int durColumn = c.getColumnIndex("duration");
        int timeColumn = c.getColumnIndex("timestamp");
       // int rowIDColumn = c.getColumnIndex("_id") ;
        
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	// do {
        		 /* Add current Entry to meventData. */
                 eventDetails[0] = c.getString(eventTypeColumn);
                 eventDetails[1] = c.getString(venueColumn);
                 eventDetails[2] = c.getString(courseColumn);
                 eventDetails[3] = c.getString(durColumn);
                 eventDetails[4] = c.getString(timeColumn);
                 
            // } while (c.moveToNext());
        } else {
            Toast.makeText(context, "No Event found", 
            		Toast.LENGTH_SHORT).show();
        }
        db.close();
    	return eventDetails;
    }
	
	
	public ArrayList<String> personExtract (long extra_EID, int tpType){
		ArrayList<String> eventData = new ArrayList<String>();
    	//create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEventPersons(extra_EID);
        /* Get the indices of the Columns we will need */
        //int timeColumn = c.getColumnIndex("timestamp"); 
        int codeColumn = c.getColumnIndex("code");
        int pTypeColumn = c.getColumnIndex("pType");
        //int rowIDColumn = c.getColumnIndex("_id") ;
        Log.i(TAG, " the value for eventID - "+ extra_EID);
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 int pType = c.getInt(pTypeColumn);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == tpType){
        			 eventData.add("  "+c.getLong(codeColumn));
                 //ofRowID.add(c.getLong(rowIDColumn));
        		 }
                 
                 
             } while (c.moveToNext());
        else
            Toast.makeText(context, "No Persons found", 
            		Toast.LENGTH_SHORT).show();
        db.close();
    	return eventData;
    }
	
	public void sendEmail(long rID, String [] ev_contents, ArrayList<String> offeventData, ArrayList<String> stdeventData){
		Mail m = new Mail("babysam.proj@gmail.com", "babysamproj"); 
		String Body = mailBody(ev_contents, offeventData, stdeventData);
		String Subject = mailSubject(ev_contents);
		Log.i(TAG,"send mail during" );
        String[] toArr = {"nowoxi@gmail.com", "babysam.proj@gmail.com"}; 
        m.setTo(toArr); 
        m.setFrom("nowoxi@gmail.com"); 
        m.setSubject(Subject); 
        m.setBody("Email body.\n "+ Body); 
       
        Log.i(TAG,"start sending" );
        try { 
         // TODO m.addAttachment("/sdcard/filelocation"); 	
        	Log.i(TAG,"Sending" );
	          if(m.send()) { 	        	
	              Toast.makeText( context, "Email was sent successfully.", Toast.LENGTH_SHORT).show(); 
	          } else { 
	              Toast.makeText( context, "Email was not sent.", Toast.LENGTH_SHORT).show(); 
	          } 
	          
        } catch(Exception e) { 
          //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
        	Log.e(TAG, "Could not send email", e); 
        }        
	}
	
	private String mailSubject(String[] ev_contents) {
		// TODO Auto-generated method stub		
		@SuppressWarnings("unused")
		String Subject;
		return  Subject = " Attendance for "+ev_contents[0] +" on "+ev_contents[4];
	}
	
	private String mailBody(String [] ev_contents, ArrayList<String> offeventData, ArrayList<String> stdeventData){
		
		String Body = "Event: "+ev_contents[0] + "\n Venue: "+ev_contents[1]+"\n"
		+" Course: "+ev_contents[2]+"\n Duration: "+ev_contents[3]+"\n \nOfficials\n";
		
		for (int i=0;i<offeventData.size();i++) { 
			Body += " "+offeventData.get(i)+" \n";
		}
		
		Body = Body+"\n \nStudents\n";
		for (int i=0;i<stdeventData.size();i++) { 
			Body += " "+stdeventData.get(i)+" \n";
		}
		return Body;
	}
	
	public void sendAries(){
		
	}
	
	
	 // dialog.dismiss();
	// ProgressDialog dialog = ProgressDialog.show(context, "",
    // 			"Please wait for few seconds...", true);
	/*public final Handler handler = new Handler() {
        public void handleMessage(Message msg, ProgressThread progThread, ProgressDialog progDialog) {
        	// Get the current value of the variable total from the message data
        	// and update the progress bar.
            int total = msg.getData().getInt("total");
            progDialog.setProgress(total);
            if (total <= 0){
                //dismissDialog();
                progThread.setState(ProgressThread.DONE);
            }
        }
	};*/
}