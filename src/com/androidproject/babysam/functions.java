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
	
	
	//TODO find out who using the getpersoncode... i think its the edit probably then modify to get data from the student list or officials list tables
	// as required
	public long getPersonCode(long ilRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getPerson(ilRowID);
        int codeIDColumn = c.getColumnIndex(db.KEY_CODE) ;//this will not work again modified to remove error but logically wrong
        long LcodeID=0;
		if (c.moveToFirst())LcodeID = c.getLong(codeIDColumn);	        
        db.close();
		return LcodeID;
	}
	
	public String [] eventExtract (long extra_EID){
		String [] eventDetails = new String [5];
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getEvent(extra_EID);
        /* Get the indices of the Columns we will need */        
        int eventTypeColumn = c.getColumnIndex(db.KEY1_EVENTTYPE);
        int venueColumn = c.getColumnIndex(db.KEY1_VENUE);
        int courseColumn = c.getColumnIndex(db.KEY1_COURSE);
        int durColumn = c.getColumnIndex(db.KEY1_DURATION);
        int timeColumn = c.getColumnIndex(db.KEY_TIMESTAMP);
        
        if (c.moveToFirst()) {
        	/* Loop through all Results */  
        		 /* Add current Entry to meventData. */
                 eventDetails[0] = c.getString(eventTypeColumn);
                 eventDetails[1] = c.getString(venueColumn);
                 eventDetails[2] = c.getString(courseColumn);
                 eventDetails[3] = c.getString(durColumn);
                 eventDetails[4] = c.getString(timeColumn);            
        } else {
            Toast.makeText(context, "No Event found", 
            		Toast.LENGTH_SHORT).show();
        }
        db.close();
    	return eventDetails;
    }	
	//TODO change this method to extract the rows
	public ArrayList<String> personExtract (long extra_EID, int tpType){
		ArrayList<String> eventData = new ArrayList<String>();
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEventPersons(extra_EID);
        
      /* Get the indices of the Columns we will need */        
        int codeColumn = c.getColumnIndex(db.KEY_CODE);//this will not work again modified to remove error sbut logically wrong
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        //Log.i(TAG, " the value for eventID - "+ extra_EID);
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 int pType = c.getInt(pTypeColumn);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == tpType){
        			 eventData.add("  "+c.getLong(codeColumn));
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
		return " Attendance for "+ev_contents[0] +" on "+ev_contents[4];
	}
	
	private String mailBody(String [] ev_contents, ArrayList<String> offeventData, ArrayList<String> stdeventData){		
		String Body = "Event: "+ev_contents[0] + "\n Venue: "+ev_contents[1]+"\n"
		+" Course: "+ev_contents[2]+"\n Duration: "+ev_contents[3]+"\n \nOfficials\n";
		
		for (int i=0;i<offeventData.size();i++) Body += " "+offeventData.get(i)+" \n";		
		Body = Body+"\n \nStudents\n";
		for (int i=0;i<stdeventData.size();i++) Body += " "+stdeventData.get(i)+" \n";
		return Body;
	}
	
	public void sendAries(){
		
	}

	public boolean deletePerson(long ilRowID) {
		//create object of DB
    	DBAdapter db = new DBAdapter(context);
		db.open();
		db.deletePerson(ilRowID);
		db.close();
		return true;
	}

	//rows in table 2 are completely made unique by the rowid or a combination of eventid, persontype and position
	public long getPersonID(long pos, int lpType, long lRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllEventPersons(lRowID);
        int IDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        int posColumn = c.getColumnIndex(db.KEY2_POSITION);
        long LID=0;
        do {
	   		int pType = c.getInt(pTypeColumn);
	   		long dpos = c.getLong(posColumn);
	   		/* Add current Entry to offeventData and stdeventData. */
	   		if(pType == lpType && dpos == pos)LID = c.getLong(IDColumn);    
        } while (c.moveToNext()); 
        db.close();
		return LID;
	}

	public void updatePos(int lpType, int pos, long lRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllEventPersons(lRowID);
        int IDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        int posColumn = c.getColumnIndex(db.KEY2_POSITION);
        do {
	   		int pType = c.getInt(pTypeColumn);
	   		long dpos = c.getLong(posColumn);
	   		long rowID = c.getLong(IDColumn);
	   		/* Add current Entry to offeventData and stdeventData. */
	   		if(pType == lpType && dpos > pos){
	   			dpos-=1;
	   			 try{
	   				 db.posChange(rowID,dpos);
	   			 } catch (Exception e) {
	 	            Log.e(TAG, "Failed to update persion poistion", e);
	 	        }
	   		}
        } while (c.moveToNext()); 
        db.close();
	}
	
	public void add_dbpersondata(int ptype, String fname, String lname,long code,String uname, String pass){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(context); 
        db.open();       
        Log.i(TAG,"add data method" );
        if (ptype == 1){
        	try{
		        db.insertStudent(code, lname, fname);
		        Log.i(TAG,"add student to db" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(context, context.getResources().getString( R.string.invalid_data), Toast.LENGTH_SHORT).show();
        	}
        } else if (ptype == 2){    
        	try{
	        	db.insertOfficial(code, lname, fname, uname, pass);	
	        	Log.i(TAG,"add official to db" );
        	} catch (NumberFormatException e){
       		 Toast.makeText(context, "Invalid data format", Toast.LENGTH_SHORT).show();
        	}
        	
        }
        db.close();
    }
	
	public void upd_dbpersondata(int ptype,long pID, String fname, String lname,long code,String uname, String pass){
    	//---add 2 events and persons---
    	DBAdapter db = new DBAdapter(context); 
        db.open();       
        Log.i(TAG,"update person db method" );
        if (ptype == 1){
        	try{
        		db.updateStudent(pID,code,lname,fname);
	        Log.i(TAG,"update student in db" );
        	} catch (NumberFormatException e){
        		 Toast.makeText(context, "Invalid data format", 
                 		Toast.LENGTH_SHORT).show();
        	}
        } else if (ptype == 2){    
        	try{
	        	db.updateOfficial(pID,code,lname,fname, uname, pass);
	        	Log.i(TAG,"updated official in db" );
        	} catch (NumberFormatException e){
       		 Toast.makeText(context, "Invalid data format", 
              		Toast.LENGTH_SHORT).show();
        	}        	
        }
        db.close();
    }
	
	public String timeStamp(){
		return (String)android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
	}
}