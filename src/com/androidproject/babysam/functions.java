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
	public long getPersonCode(long ilRowID, int pType) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getStudent(ilRowID);
        if(pType == 2)c = db.getOfficial(ilRowID);
        int codeIDColumn = c.getColumnIndex(db.KEY_CODE) ;
        long LcodeID=0;
		if (c.moveToFirst())LcodeID = c.getLong(codeIDColumn);	        
        db.close();
		return LcodeID;
	}
	
	public long getPersonID(long code, int pType) { //for event activity
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllStudents();
        if(pType == 2)c = db.getAllOfficials();
        int codeColumn = c.getColumnIndex(db.KEY_CODE) ;
        int IDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        long LcodeID=0;
        long lRowID=0;
        if (c.moveToFirst()){
        	do{
				LcodeID = c.getLong(codeColumn);
				if(code == LcodeID) lRowID = c.getLong(IDColumn) ;  
        	}while (c.moveToNext() && code != LcodeID); 
        }      
        db.close();
		return lRowID;
	}
	
	
	public String getPersonName(long ilRowID, int pType, String column) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getStudent(ilRowID);
        if(pType == 2)c = db.getOfficial(ilRowID);
        int nameColumn = c.getColumnIndex(column) ;
        String name = null;
		if (c.moveToFirst())name = c.getString(nameColumn);	        
        db.close();
		return name;
	}
	
	public long getPersonID(int pType, long pos){// used in delete context to find the appropriate row to delete
		long rowID = 0;
		//pos+=1;
		DBAdapter db = new DBAdapter(context);
		db.open();
		Cursor c= null;
		if (pType == 1)c = db.getAllStudents();
		if (pType == 2)c = db.getAllOfficials();
		int IDcolumn = c.getColumnIndex(db.KEY_ROWID);
		if (c.moveToPosition((int) pos)){
			rowID = c.getLong(IDcolumn);
		} else
            Toast.makeText(context, "No Person found at position", Toast.LENGTH_SHORT).show();
		db.close();
		return rowID;
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
        Cursor d = db.getAllStudents();
        int pType;
        long pRowID;
      /* Get the indices of the Columns we will need */        
        int pIDColumn = c.getColumnIndex(db.KEY2_PERSONID);//this will not work again modified to remove error sbut logically wrong
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        int firstColumn = d.getColumnIndex(db.KEY_FIRSTNAME);         
        int lastColumn = d.getColumnIndex(db.KEY_LASTNAME);
        int codeColumn = d.getColumnIndex(db.KEY_CODE);
        Log.i(TAG, " the value for eventID - "+ extra_EID+" c size" + c.getCount());
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	 do {
        		 pType = c.getInt(pTypeColumn);
        		 pRowID = c.getLong(pIDColumn);
        		 
        		 Log.i(TAG,"person extraction " +pRowID+ "THEN ptype is " + pType);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == tpType){   
        			 if (tpType == 1)d = db.getStudent(pRowID);
        			 if (tpType == 2)d = db.getOfficial(pRowID);     	
        			 Log.i(TAG,d.getString(firstColumn)+" "+d.getString(lastColumn)+" "+d.getLong(codeColumn));		 
        			 eventData.add(d.getString(firstColumn)+" "+d.getString(lastColumn)+" "+d.getLong(codeColumn));
        			 
        		}
        		 Log.i(TAG,"person extraction" );
             } while (c.moveToNext());
        } else
            Toast.makeText(context, "No Persons found", 
            		Toast.LENGTH_SHORT).show();
        db.close();
        Log.i(TAG,"person extraction 2" );
    	return eventData;
    }
	
	
	// Used to get all the information of a particular person from the table
	public String [] single_personExtract (long pRowID, int tpType){
		String [] eventData = new String [5];
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = null ;
        if(tpType == 1)c = db.getStudent(pRowID);
        if(tpType == 2)c = db.getOfficial(pRowID);
        
      /* Get the indices of the Columns we will need */        
        int firstColumn = c.getColumnIndex(db.KEY_FIRSTNAME);         
        int lastColumn = c.getColumnIndex(db.KEY_LASTNAME);
        int codeColumn = c.getColumnIndex(db.KEY_CODE);
        int unameColumn = 0;
        int passColumn = 0;
        if(tpType == 2){
	        unameColumn = c.getColumnIndex(db.KEY4_USERNAME);
	        passColumn = c.getColumnIndex(db.KEY4_PASS);
        }
        //Log.i(TAG, " the value for eventID - "+ extra_EID);
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 /* Add current Entry to offeventData and stdeventData. */
        			 eventData[0]=c.getString(firstColumn);
        			 eventData[1]=c.getString(lastColumn);
        			 eventData[2]= Long.toString(c.getLong(codeColumn));
        			 if(tpType == 2){
	        			 eventData[3]=c.getString(unameColumn);
	        			 eventData[4]=c.getString(passColumn);
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

	public boolean deletePerson(long ilRowID, int pType) {
		//create object of DB
    	DBAdapter db = new DBAdapter(context);
		db.open();
		if (pType == 3 )db.deletePerson(ilRowID);
		if (pType == 2 )db.deleteOfficial(ilRowID);
		if (pType == 1 )db.deleteStudent(ilRowID);
		db.close();
		return true;
	}

	//rows in table 2 are completely made unique by the rowID or a combination of eventID, person type and position
	//used to identify selected student via context menu 
	public long getEventPersonID(String bad,long pos, int lpType, long lRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllEventPersons(lRowID);
        int IDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        int presentColumn = c.getColumnIndex(db.KEY2_PRESENT);
        long LID=0,count=0;
        do {
	   		int pType = c.getInt(pTypeColumn);
	   		int present = c.getInt(presentColumn);
	   		/* Add current Entry to offeventData and stdeventData.*/ 
	   		if(pType == lpType && count == pos)LID = c.getLong(IDColumn);    
	   		if(pType == lpType && present == 1 )count++;
        } while (c.moveToNext() && LID == 0); 
        db.close();
		return LID;
	}
	
	//rows in table 2 are completely made unique by the rowid or a combination of eventid, persontype and position
	public long getEventPersonID( long pID, int lpType, long lRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllEventPersons(lRowID);
        int IDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        int pIDColumn = c.getColumnIndex(db.KEY2_PERSONID);
        long LID=0;
        do {
	   		int pType = c.getInt(pTypeColumn);
	   		long dpID = c.getLong(pIDColumn);
	   		/* Add current Entry to offeventData and stdeventData. */
	   		if(pType == lpType && dpID == pID)LID = c.getLong(IDColumn);    
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
	public boolean codeCHECK(long code){ //to check if code exists if it does then return true.
		Log.i(TAG,"checking codes..." );
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllOfficials();
        int codeIDColumn = c.getColumnIndex(db.KEY_CODE) ;
        long LcodeID=0;
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	 do {
        		 Log.i(TAG,"still checking codes..." );
        		 LcodeID = c.getLong(codeIDColumn);
        	     if(code == LcodeID) exist = true ;  
        		
             } while (c.moveToNext() && exist != true);
        }else{
            Toast.makeText(context, "No Officials found", 
            		Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG,"checking codes a finished ..." );
        if ( exist != true){
        	Log.i(TAG,"checking codes b started..." );
	        c = db.getAllStudents();
	        Log.i(TAG,"checking codes dead..." );
	        if (c.moveToFirst()) 
	        	/* Loop through all Results */             	
	        	 do {
	        		 LcodeID = c.getLong(codeIDColumn);
	        		 if(code == LcodeID) exist = true ;
	             } while (c.moveToNext() && exist != true);
	        else
	            Toast.makeText(context, "No Students found", 
	            		Toast.LENGTH_SHORT).show();
        }
        
        db.close();
		return exist;		
	}
	
	public boolean codeCHECK(long code,int pType){ //to check if code exists and in correct table if it does then return true.
		Log.i(TAG,"checking codes..." );
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		int personType =0;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllOfficials();
        int codeIDColumn = c.getColumnIndex(db.KEY_CODE) ;
        long LcodeID=0;
        if (c.moveToFirst()) {
        	/* Loop through all Results */   
        	personType = 2;
        	 do {
        		 Log.i(TAG,"still checking codes..." );
        		 LcodeID = c.getLong(codeIDColumn);
        	     if(code == LcodeID && personType == pType) exist = true ;  
        		
             } while (c.moveToNext() && exist != true);
        }else{
            Toast.makeText(context, "No Officials found", 
            		Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG,"checking codes a finished ..." );
        if ( exist != true){
        	Log.i(TAG,"checking codes b started..." );
	        c = db.getAllStudents();
	        Log.i(TAG,"checking codes dead..." );
	        if (c.moveToFirst()) {
	        	/* Loop through all Results */
	        	personType = 1;
	        	 do {
	        		 LcodeID = c.getLong(codeIDColumn);
	        		 if(code == LcodeID && personType == pType) exist = true ;
	             } while (c.moveToNext() && exist != true);
	        }
	        else
	            Toast.makeText(context, "No Students found", 
	            		Toast.LENGTH_SHORT).show();
        }
        
        db.close();
		return exist;		
	}
	
	public boolean codeCHECK(long code, long lRowID){ //to check if code exists if it does then return true.
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllOfficials();
        int codeIDColumn = c.getColumnIndex(db.KEY_CODE);
        int IDColumn = c.getColumnIndex(db.KEY_ROWID) ;
        
        long LcodeID=0,rowID=0;
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 LcodeID = c.getLong(codeIDColumn);
        		 rowID = c.getLong(IDColumn);
        	     if(code == LcodeID && rowID != lRowID) exist = true ; 
        	     Log.i(TAG,"checking codes..." );
        		
             } while (c.moveToNext() && exist != true);
        else
            Toast.makeText(context, "No Officials found", 
            		Toast.LENGTH_SHORT).show();
        
        if ( exist != true){
	        c = db.getAllStudents();
	        if (c.moveToFirst()) 
	        	/* Loop through all Results */             	
	        	 do {
	        		 LcodeID = c.getLong(codeIDColumn);
	        		 rowID = c.getLong(IDColumn);
	        		 if(code == LcodeID && rowID != lRowID) exist = true ;
	             } while (c.moveToNext()&& exist != true);
	        else
	            Toast.makeText(context, "No Students found", 
	            		Toast.LENGTH_SHORT).show();
        }
        
        db.close();
		return exist;		
	}
	
	
	public boolean eventCHECK(long pID, long leventID, int pType){ //to check if code exists if it does then return true.
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllEventPersons(leventID);
        int pIDColumn = c.getColumnIndex(db.KEY2_PERSONID);
        int eIDColumn = c.getColumnIndex(db.KEY2_EVENTID) ;
        int pTypeColumn = c.getColumnIndex(db.KEY2_PERSONTYPE);
        
        long LpID=0,eventID=0;
        int ptype = 0;
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 LpID = c.getLong(pIDColumn);
        		 eventID = c.getLong(eIDColumn);
        		 ptype = c.getInt(pTypeColumn);
        	     if(pID == LpID && eventID == leventID && ptype == pType) exist = true ; 
        	     Log.i(TAG,"checking codes..." );
        		
             } while (c.moveToNext() && exist != true);
        else
            Toast.makeText(context, "No Persons found for events", Toast.LENGTH_SHORT).show();
        
        db.close();
		return exist;		
	}
	
	
	public void add_dbpersondata(int ptype, long code){//method used when creating a record with only code available
		Log.i(TAG,"add person method codes..." );
			String blank = "";		
			if (ptype == 1)add_dbpersondata(blank,blank,code);
			if (ptype == 2)add_dbpersondata(blank,blank,code,blank,blank);
	}
	public void add_dbpersondata(String fname, String lname,long code){
		if(!codeCHECK(code)){
			DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"add data method" );
	        	try{
			        db.insertStudent(code, lname, fname);
			        Log.i(TAG,"add student to db" );
	        	} catch (NumberFormatException e){
	        		 Toast.makeText(context, context.getResources().getString( R.string.invalid_data), Toast.LENGTH_SHORT).show();
	        	}
	        db.close();
		}else{
            Toast.makeText(context, "Record exists", 
            		Toast.LENGTH_SHORT).show();
            }
		
	}
	
	//public void add_dbpersondata(int ptype, String fname, String lname,long code,String uname, String pass){
	public void add_dbpersondata( String fname, String lname,long code,String uname, String pass){
		if(!codeCHECK(code)){
	    	//---add 2 events and persons---
	    	DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"add data method" );
	        
	        	try{
		        	db.insertOfficial(code, lname, fname, uname, pass);	
		        	Log.i(TAG,"add official to db" );
	        	} catch (NumberFormatException e){
	       		 Toast.makeText(context, "Invalid data format", Toast.LENGTH_SHORT).show();
	        	}
	        	
	       // }
	        db.close();
        }else{
            Toast.makeText(context, "Record exists", Toast.LENGTH_SHORT).show();
            }
    }
	
	
	public void upd_dbpersondata(int ptype, long pID,long code){//method used when updating a record with only code available--can happen with scans only
			//String blank = "";		
			DBAdapter db = new DBAdapter(context); 
			if (ptype == 1)upd_dbpersondata(pID,getPersonName(pID,ptype,db.KEY_FIRSTNAME),getPersonName(pID,ptype,db.KEY_LASTNAME),code);
			if (ptype == 2)upd_dbpersondata(pID,getPersonName(pID,ptype,db.KEY_FIRSTNAME),getPersonName(pID,ptype,db.KEY_LASTNAME),code,
						getPersonName(pID,ptype,db.KEY4_USERNAME),getPersonName(pID,ptype,db.KEY4_PASS));
	}
	
	public void upd_dbpersondata(long pID, String fname, String lname,long code){
		if(!codeCHECK(code,pID)){
	    	//---add 2 events and persons---
	    	DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"update person db method" );
	        	try{
	        		db.updateStudent(pID,code,lname,fname);
		        Log.i(TAG,"update student in db" );
	        	} catch (NumberFormatException e){
	        		 Toast.makeText(context, "Invalid data format", 
	                 		Toast.LENGTH_SHORT).show();
	        	}
	        
	        db.close();
		}else{
            Toast.makeText(context, "Record exists", Toast.LENGTH_SHORT).show();
            }
    }
	
	public void upd_dbpersondata(long pID, String fname, String lname,long code,String uname, String pass){
		if(!codeCHECK(code,pID)){
	    	//---add 2 events and persons---
	    	DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"update person db method" );
	        	try{
		        	db.updateOfficial(pID,code,lname,fname, uname, pass);
		        	Log.i(TAG,"updated official in db" );
	        	} catch (NumberFormatException e){
	       		 Toast.makeText(context, "Invalid data format", 
	              		Toast.LENGTH_SHORT).show();
	        	}        	
	        //}
	        db.close();
		}else{
            Toast.makeText(context, "Record exists", 
            		Toast.LENGTH_SHORT).show();
            }
        
    }
	
	public String timeStamp(){
		return (String)android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
	}


	public void add_dbPerson(int en_stPerson, String contents, String fname, String lname, String uname, String pass) {
		// TODO Auto-generated method stub
		
	}


	public void upd_dbPerson(int en_stPerson, long rowID, String contents, String efname, String elname, String uname, String pass) {
		// TODO Auto-generated method stub
		
	}
}