package com.androidproject.babysam;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class functions 
{
	private final Context context;
	public static final String TAG = "BabySAM";
	String filePath;
	
	
	public functions(Context ctx) 
    {
        this.context = ctx;        
    }
	
	
	//TODO find out who using the getpersoncode... i think its the edit probably then modify to get data from the student list or officials list tables
	// as required
	public long getPersonCode(long ilRowID, int pType) {//iRowID is variable for rowid of table 3 or 4 ie person ID
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getStudent(ilRowID);
        if(pType == 2)c = db.getOfficial(ilRowID);
        int codeIDColumn = c.getColumnIndex(DBAdapter.KEY_CODE) ;
        long LcodeID=0;
		if (c.moveToFirst())LcodeID = c.getLong(codeIDColumn);
		c.close();
        db.close();
		return LcodeID;
	}
	public long getEventPersonIDValue(long eventID) {//used to get the personid value of aperaticular person used in events when editting
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getPerson(eventID);
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID) ;
        long pID=0;
		if (c.moveToFirst())pID = c.getLong(pIDColumn);	 
		c.close();
        db.close();
		return pID;
	}
	
	public long getPersonID(long code, int pType) { //for event activity
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllStudents();
        if(pType == 2)c = db.getAllOfficials();
        int codeColumn = c.getColumnIndex(DBAdapter.KEY_CODE) ;
        int IDColumn = c.getColumnIndex(DBAdapter.KEY_ROWID) ;
        long LcodeID=0;
        long lRowID=0;
        if (c.moveToFirst()){
        	do{
				LcodeID = c.getLong(codeColumn);
				if(code == LcodeID) lRowID = c.getLong(IDColumn) ;  
        	}while (c.moveToNext() && code != LcodeID); 
        }
        c.close();
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
		c.close();
        db.close();
		return name;
	}
	
	public long getPersonID(int pType, long pos){// used in delete context to find the appropriate row to delete. used in lists
		long rowID = 0;
		//pos+=1;
		DBAdapter db = new DBAdapter(context);
		db.open();
		Cursor c= null;
		if (pType == 1)c = db.getAllStudents();
		if (pType == 2)c = db.getAllOfficials();
		int IDcolumn = c.getColumnIndex(DBAdapter.KEY_ROWID);
		if (c.moveToPosition((int) pos)){
			rowID = c.getLong(IDcolumn);
		} else
            Toast.makeText(context, "No Person found at position", Toast.LENGTH_SHORT).show();
		c.close();
		db.close();
		return rowID;
	}
	
	public String getWelcomeMessage(String uname){// used in delete context to find the appropriate row to delete. used in lists
		String username = null, welcome = "Somethings Wrong";
		if (uname != null){
				DBAdapter db = new DBAdapter(context);
				db.open();
				Cursor c= db.getAllOfficials();
				int unameColumn = c.getColumnIndex(DBAdapter.KEY4_USERNAME);
				int fnameColumn = c.getColumnIndex(DBAdapter.KEY_FIRSTNAME);
				int lnameColumn = c.getColumnIndex(DBAdapter.KEY_LASTNAME);
				if (c.moveToFirst()){
					do{
						username = c.getString(unameColumn);
						if (uname.equalsIgnoreCase(username)) welcome = "Welcome, "+ c.getString(fnameColumn) +" "+  c.getString(lnameColumn) ;
					}while (c.moveToNext() && uname != username);
				} else{
					Toast.makeText(context, "No Person found at position", Toast.LENGTH_SHORT).show();
				}
				c.close();
				db.close();
		}
		return welcome;
	}
	
	public String [] eventExtract (long extra_EID){
		String [] eventDetails = new String [5];
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getEvent(extra_EID);
        /* Get the indices of the Columns we will need */        
        int eventTypeColumn = c.getColumnIndex(DBAdapter.KEY1_EVENTTYPE);
        int venueColumn = c.getColumnIndex(DBAdapter.KEY1_VENUE);
        int courseColumn = c.getColumnIndex(DBAdapter.KEY1_COURSE);
        int durColumn = c.getColumnIndex(DBAdapter.KEY1_DURATION);
        int timeColumn = c.getColumnIndex(DBAdapter.KEY_TIMESTAMP);
        
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
        c.close();
        db.close();
    	return eventDetails;
    }	
	//this method to extract the rows
	public ArrayList<String> personExtract (long extra_EID, int tpType){
		ArrayList<String> eventData = new ArrayList<String>();
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEventPersons(extra_EID);
        Cursor d = db.getAllStudents();
        int pType,present, list;
        long pRowID;
        
      /* Get the indices of the Columns we will need */        
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID);//this will not work again modified to remove error sbut logically wrong
        int pTypeColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONTYPE);
        int preColumn = c.getColumnIndex(DBAdapter.KEY2_PRESENT);
        int listColumn = c.getColumnIndex(DBAdapter.KEY2_LIST);
        int firstColumn = d.getColumnIndex(DBAdapter.KEY_FIRSTNAME);         
        int lastColumn = d.getColumnIndex(DBAdapter.KEY_LASTNAME);
        int codeColumn = d.getColumnIndex(DBAdapter.KEY_CODE);
        Log.i(TAG, " the value for eventID - "+ extra_EID+" c size" + c.getCount());
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	 do {
        		 String SPresent = "", SList = "";
        		 pType = c.getInt(pTypeColumn);
        		 pRowID = c.getLong(pIDColumn);
        		 present = c.getInt(preColumn);
        		 list = c.getInt(listColumn);
        		 
        		// Log.i(TAG,"person extraction " +pRowID+ "THEN ptype is " + pType);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == tpType){   
        			 if (tpType == 1)d = db.getStudent(pRowID);
        			 if (tpType == 2)d = db.getOfficial(pRowID);  
        			 if (present == 0)SPresent = "AB";
        			 if (list == 0)SList = "**";
        			 String data= d.getString(firstColumn)+" "+d.getString(lastColumn)+" "+d.getLong(codeColumn)+" "+SList+" "+SPresent;
        			 Log.i(TAG,data);		 
        			 eventData.add(data);
        			 
        		}
        		// Log.i(TAG,"person extraction" );
             } while (c.moveToNext());
        } else
            Toast.makeText(context, "No Persons found", 
            		Toast.LENGTH_SHORT).show();
        c.close();
        db.close();
        Log.i(TAG,"person extraction" );
    	return eventData;
    }
	
	//this method to extract the rows
	public ArrayList<String> evenedit_personExtract (long extra_EID, int tpType){
		ArrayList<String> eventData = new ArrayList<String>();
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEventPersons(extra_EID);
        Cursor d = db.getAllStudents();
        int pType,list;
		long present;
        long pRowID;
        
      /* Get the indices of the Columns we will need */        
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID);//this will not work again modified to remove error sbut logically wrong
        int pTypeColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONTYPE);
        int preColumn = c.getColumnIndex(DBAdapter.KEY2_PRESENT);
        int listColumn = c.getColumnIndex(DBAdapter.KEY2_LIST);
        int firstColumn = d.getColumnIndex(DBAdapter.KEY_FIRSTNAME);         
        int lastColumn = d.getColumnIndex(DBAdapter.KEY_LASTNAME);
        int codeColumn = d.getColumnIndex(DBAdapter.KEY_CODE);
        Log.i(TAG, " the value for eventID - "+ extra_EID+" c size" + c.getCount());
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	 do {
        		 String SList = "";
        		 pType = c.getInt(pTypeColumn);
        		 pRowID = c.getLong(pIDColumn);
        		 present = c.getLong(preColumn);
        		 list = c.getInt(listColumn);
        		 
        		 Log.i(TAG,"person extraction " +pRowID+ "THEN ptype is " + pType);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == tpType){   
        			 if (tpType == 1)d = db.getStudent(pRowID);
        			 if (tpType == 2)d = db.getOfficial(pRowID);  
        			 String  data = null;
        			 if (list == 0)SList = "**";
					 if (present == 1) data= d.getString(firstColumn)+" "+d.getString(lastColumn)+" "+d.getLong(codeColumn)+" "+SList;
        			 Log.i(TAG," "+data);
        			 eventCHECK(pRowID,extra_EID,tpType);
        			 if(data!=null)eventData.add(data);
        		}
        		// Log.i(TAG,"person extraction" );
             } while (c.moveToNext());
        } else
            Toast.makeText(context, "No Persons found", 
            		Toast.LENGTH_SHORT).show();
        c.close();
        db.close();
        Log.i(TAG,"person extraction 2" );
    	return eventData;
    }
	
	
	public ArrayList<String []> aries_personExtract (long extra_EID, int tpType){
		ArrayList<String []> eventData = new ArrayList<String []>();
		//ArrayList <object> Data = new ArrayList <>();
      //create object of DB
    	DBAdapter db = new DBAdapter(context);
        
      //---get all events---
        db.open();
        Cursor c = db.getAllEventPersons(extra_EID);
        Cursor d = db.getAllStudents();
        int pType;
        long pRowID;
      /* Get the indices of the Columns we will need */        
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID);//this will not work again modified to remove error sbut logically wrong
        int pTypeColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONTYPE);
        int firstColumn = d.getColumnIndex(DBAdapter.KEY_FIRSTNAME);         
        int lastColumn = d.getColumnIndex(DBAdapter.KEY_LASTNAME);
        int codeColumn = d.getColumnIndex(DBAdapter.KEY_CODE);
        int presentColumn = c.getColumnIndex(DBAdapter.KEY2_PRESENT);
        int listColumn = c.getColumnIndex(DBAdapter.KEY2_LIST);
        int timestampColumn = c.getColumnIndex(DBAdapter.KEY_TIMESTAMP);
        
        Log.i(TAG, " the value for eventID - "+ extra_EID+" c size" + c.getCount());
        //String [] test;
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	 do {
        		 pType = c.getInt(pTypeColumn);
        		 pRowID = c.getLong(pIDColumn);
        		 
        		 Log.i(TAG," ARIES person extraction " +pRowID+ "THEN ptype is " + pType);
        		 /* Add current Entry to offeventData and stdeventData. */
        		 if(pType == tpType){   
        			 if (tpType == 1)d = db.getStudent(pRowID);
        			 if (tpType == 2)d = db.getOfficial(pRowID);     
        			 Log.i(TAG,d.getString(firstColumn)+" "+d.getString(lastColumn)+" "+d.getLong(codeColumn));
        			 String [] test = new String [6];
        			 test[0] = Long.toString(d.getLong(codeColumn));
        			 test[1] = Integer.toString(c.getInt(presentColumn));
        			 test[2] = Integer.toString(c.getInt(listColumn));
        			 test[3] = c.getString(timestampColumn);
        			 test[4] = d.getString(firstColumn);
        			 test[5] = d.getString(lastColumn);//Log.i(TAG," Lastname: "+test[5]);
        			 eventData.add(test);
        		 }
        		// Log.i(TAG,"Aries person extraction" );
             } while (c.moveToNext());
        } else{
            Toast.makeText(context, "No Persons found", 
            		Toast.LENGTH_SHORT).show();
            }
        c.close();
        db.close();
        Log.i(TAG,"Aries person extraction End" );
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
        int firstColumn = c.getColumnIndex(DBAdapter.KEY_FIRSTNAME);         
        int lastColumn = c.getColumnIndex(DBAdapter.KEY_LASTNAME);
        int codeColumn = c.getColumnIndex(DBAdapter.KEY_CODE);
        int unameColumn = 0;
        int passColumn = 0;
        if(tpType == 2){
	        unameColumn = c.getColumnIndex(DBAdapter.KEY4_USERNAME);
	        passColumn = c.getColumnIndex(DBAdapter.KEY4_PASS);
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
        c.close();
        db.close();
    	return eventData;
    }
	
	public void sendEmail(long rID, String [] ev_contents, String Username ){
		ArrayList<String[]> offeventData = aries_personExtract(rID, 2);
		ArrayList<String[]> stdeventData = aries_personExtract(rID, 1);
		Mail m = new Mail("babysam.proj@gmail.com", "babysamproj"); 
		String Body = mailBody(ev_contents, offeventData, stdeventData);
		String Subject = mailSubject(ev_contents);
		Log.i(TAG,"send mail during" );
        String[] toArr = {"nowoxi@gmail.com", "babysam.proj@gmail.com", Username+"@aston.ac.uk"}; 
        m.setTo(toArr); 
        m.setFrom("nowoxi@gmail.com"); 
        m.setSubject(Subject); 
        m.setBody("Email body.\n "+ Body); 
       
        Log.i(TAG,"start sending" );
        try { 
         // TODO
        	String savedPath = saveasFile(rID, Username);
        	m.addAttachment(savedPath); 	
        	Log.i(TAG,"Sending" );
	          if(m.send()) { 	        	
	              Toast.makeText( context, "Email was sent successfully.", Toast.LENGTH_SHORT).show(); 
	              deleteTempXML(savedPath);
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
	
	private String mailBody(String [] ev_contents, ArrayList<String[]> offeventData, ArrayList<String[]> stdeventData){		
		String Body = "Event: "+ev_contents[0] + "\n Venue: "+ev_contents[1]+"\n"
		+" Course: "+ev_contents[2]+"\n Duration: "+ev_contents[3]+"\n \nOfficials\n";
		
		
		for (int i=0;i<offeventData.size();i++) {
			String SPresent="",SList="";
			String [] Data = offeventData.get(i);
			if (Integer.parseInt(Data[1]) == 0)SPresent = "AB";
			if (Integer.parseInt(Data[2]) == 0)SList = "**";
			 //String data= d.getString(firstColumn)+" "+d.getString(lastColumn)+" "+d.getLong(codeColumn)+" "+SList+" "+SPresent
			Body += " "+Data[4]+" "+Data[5]+" "+Data[0]+" "+SList+" "+SPresent+" \n";		
		}
		Body = Body+"\n \nStudents\n";
		for (int i=0;i<stdeventData.size();i++) {
			String SPresent="",SList="";
			String [] Data = stdeventData.get(i);
			if (Integer.parseInt(Data[1]) == 0)SPresent = "AB";
			if (Integer.parseInt(Data[2]) == 0)SList = "**";
			Body += " "+Data[4]+" "+Data[5]+" "+Data[0]+" "+SList+" "+SPresent+" \n";	
		}
		return Body;
	}
	
	
	public void sendAries(long lRowID, int age, String Uri, String Username){// age is used to check if more than one copy of an event can be sent to the server 0 - yes and 1 - no 
		//usually new event or from history (if history - 1 if new event - 0)
		/* This method does the following
		 * 1. creates xml of session
		 * 2. uploads the xml and retrieves response
		 * 3. deletes created xml and informs if any errors
		 * 4. checks if upload has been done before and will not upload if it has 
		 */
		
		String fileName;
		//registration check
		int ariesReg=1;
		if ( age == 1) ariesReg = getEventReg(lRowID);
		Log.i(TAG, "ariesReg "+ariesReg);
		
		if(age == 0 || ariesReg == 0){
			//xml creation
			fileName = saveasFile(lRowID,Username);
			String encrypted_fileName = encryptFile(fileName);
			if(uploadFile(Uri,encrypted_fileName))ariesReg(lRowID);//if able to upload change aries in dB to 1
		}
		if (ariesReg == 1){
			Toast.makeText(context, "Each event can only be registered once per server", Toast.LENGTH_LONG).show();
			Log.i(TAG, "Each event can only be registered once per server");
		}
	}

	private Boolean uploadFile(String Uri,String fileName) {
		HttpFileUpload upload = new HttpFileUpload(fileName, Uri);
		Boolean status = false;
		//Here the file created should be deleted if all is well and a Toast showing success should be displayed
		// else the file is left and a message displaying the server error message and not sucessful sent.
		int serverResponse = upload.getServerResponseCode();
		String serverMessage = upload.getServerResponseMessage();
		
		if (serverResponse == 200 && serverMessage.equals("OK")){
			File file = new File(fileName);
			if(!file.delete())Toast.makeText(context, "Error deleting saved Dectypted copy of session. Delete manually from "+fileName, Toast.LENGTH_LONG).show();
			file = new File(fileName);
			status = true;
			Toast.makeText(context, "Session uploaded to Server", Toast.LENGTH_SHORT).show();
		}else {
			Toast.makeText(context, "Error uploading Session. A copy of the data has been save to "+fileName, Toast.LENGTH_LONG).show();
			Log.e(TAG, serverResponse+" "+serverMessage);
		}
		return status;
	}


	private int getEventReg(long lRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getEvent(lRowID);
        int ariesregIDColumn = c.getColumnIndex(DBAdapter.KEY1_ARIES) ;
        int ariesReg=0;
		if (c.moveToFirst())ariesReg = c.getInt(ariesregIDColumn);	  
		c.close();
        db.close();
		return ariesReg;
	}


	private void ariesReg(long lRowID) {
		DBAdapter db = new DBAdapter(context);
    	db.open();
    	db.ariesupdate(lRowID);
    	db.close();
	}
	
	
	public boolean deletePerson(long ilRowID, int pType) {
		//create object of DB
    	DBAdapter db = new DBAdapter(context);
		db.open();
		if (pType == 3 ){
			//db.deletePerson(ilRowID);
			long present = 0;
			db.updateEventPerson(ilRowID,timeStamp() , present);
		}
		if (pType == 2 )db.deleteOfficial(ilRowID);
		if (pType == 1 )db.deleteStudent(ilRowID);
		db.close();
		return true;
	}

	//rows in table 2 are completely made unique by the rowID or a combination of eventID, person type and position
	//used to identify selected student via context menu 
	public long getEventPersonID(String bad,long pos, int lpType, long lRowID, ArrayList<String> listData) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Long code = getPersonID(listData.get((int) pos));
        Long pID = getPersonID(code,lpType);
        Cursor c = db.getAllEventPersons(lRowID);
        int IDColumn = c.getColumnIndex(DBAdapter.KEY_ROWID) ;
        int pTypeColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONTYPE);
        //int presentColumn = c.getColumnIndex(DBAdapter.KEY2_PRESENT);
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID);
        long LID=0,count=0;
        c.moveToFirst();
        do {
	   		int pType = c.getInt(pTypeColumn);
	   		//int present = c.getInt(presentColumn);
	   		int dpID = c.getInt(pIDColumn);
	   		/* Add current Entry to offeventData and stdeventData.*/ 
	   		if(pType == lpType && pID == dpID)LID = c.getLong(IDColumn);    
	   		if(pType == lpType )count++;
	   		Log.d(TAG,"pEventID: "+c.getLong(IDColumn)+" count:"+count);
        } while (c.moveToNext() && LID == 0); 
        c.close();
        db.close();
        Log.d(TAG,"c count:"+c.getCount()+" "+count);
		return LID;
	}
	
	private Long getPersonID(String data) {
		String[] datasplit = data.split(" ");
		long numdata = 0;
		try{
			numdata = new Long(datasplit[2]);
		}catch (Exception e){
			try{
				numdata = new Long(datasplit[3]);
			}catch (Exception er){
				Log.e(TAG, "Error getting nmber",er);
			}
		}
		return numdata;
	}


	//rows in table 2 are completely made unique by the rowid or a combination of eventid, persontype and position
	public long getEventPersonID( long pID, int lpType, long lRowID) {
		DBAdapter db = new DBAdapter(context);
		//---get person---
        db.open();
        Cursor c = db.getAllEventPersons(lRowID);
        int IDColumn = c.getColumnIndex(DBAdapter.KEY_ROWID) ;
        int pTypeColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONTYPE);
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID);
        long LID=0;
        do {
	   		int pType = c.getInt(pTypeColumn);
	   		long dpID = c.getLong(pIDColumn);
	   		/* Add current Entry to offeventData and stdeventData. */
	   		if(pType == lpType && dpID == pID)LID = c.getLong(IDColumn);    
        } while (c.moveToNext()); 
        c.close();
        db.close();
		return LID;
	}

	public boolean codeCHECK(long code){ //to check if code exists if it does then return true.
		Log.d(TAG,"checking codes(if exists in persons)..." );
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllOfficials();
        int codeIDColumn = c.getColumnIndex(DBAdapter.KEY_CODE) ;
        long LcodeID=0;
        if (c.moveToFirst()) {
        	/* Loop through all Results */             	
        	 do {
        		 //Log.i(TAG,"still checking codes..." );
        		 LcodeID = c.getLong(codeIDColumn);
        	     if(code == LcodeID) exist = true ;  
        		
             } while (c.moveToNext() && exist != true);
        }else{
            Toast.makeText(context, "No Officials found", 
            		Toast.LENGTH_SHORT).show();
        }
        c.close();
        //Log.i(TAG,"checking codes a finished ..." );
        if ( exist != true){
        	//Log.i(TAG,"checking codes b started..." );
	        c = db.getAllStudents();
	        Log.v(TAG,"checking codes ..." );
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
        c.close();
        db.close();
		return exist;		
	}
	
	public boolean codeCHECK(long code,int pType){ //to check if code exists and in correct table if it does then return true.
		Log.d(TAG,"checking codes(if correct table)..." );
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		int personType =0;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllOfficials();
        int codeIDColumn = c.getColumnIndex(DBAdapter.KEY_CODE) ;
        long LcodeID=0;
        if (c.moveToFirst()) {
        	/* Loop through all Results */   
        	personType = 2;
        	 do {
        		 Log.v(TAG,"still checking codes..." );
        		 LcodeID = c.getLong(codeIDColumn);
        	     if(code == LcodeID && personType == pType) exist = true ;  
        		
             } while (c.moveToNext() && exist != true);
        }else{
            Toast.makeText(context, "No Officials found", 
            		Toast.LENGTH_SHORT).show();
        }
        c.close();
        Log.v(TAG,"checking codes a finished ..." );
        if ( exist != true){
        	Log.v(TAG,"checking codes b started..." );
	        c = db.getAllStudents();
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
        c.close();
        db.close();
		return exist;		
	}
	
	public boolean codeCHECK(long code, long lRowID){ //to check if code exists if it does then return true.
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllOfficials();
        int codeIDColumn = c.getColumnIndex(DBAdapter.KEY_CODE);
        int IDColumn = c.getColumnIndex(DBAdapter.KEY_ROWID) ;
        
        long LcodeID=0,rowID=0;
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 LcodeID = c.getLong(codeIDColumn);
        		 rowID = c.getLong(IDColumn);
        	     if(code == LcodeID && rowID != lRowID) exist = true ; 
        	     //Log.i(TAG,"checking codes..." );
        		
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
        c.close();
        db.close();
		return exist;		
	}
	
	
	public long [] eventCHECK(long pID, long leventID, int pType){ //to check if code exists in attendance list if it does then return true.
		DBAdapter db = new DBAdapter(context);
		boolean exist = false;
		long [] pos = {0,0,0,0};// 0 - count, 1 - present, 2 - list
		//boolean stdexist = false;
        db.open();
        Cursor c = db.getAllEventPersons(leventID);
        int pIDColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONID);
        //int eIDColumn = c.getColumnIndex(DBAdapter.KEY2_EVENTID) ;
        int pTypeColumn = c.getColumnIndex(DBAdapter.KEY2_PERSONTYPE);
        int presentColumn = c.getColumnIndex(DBAdapter.KEY2_PRESENT);
        int listColumn = c.getColumnIndex(DBAdapter.KEY2_LIST);
        
        long LpID=0,count =0, present=0;
        int ptype = 0;
        if (c.moveToFirst()) 
        	/* Loop through all Results */             	
        	 do {
        		 LpID = c.getLong(pIDColumn);
        		 //eventID = c.getLong(eIDColumn);
        		 ptype = c.getInt(pTypeColumn);
        		 if (ptype == pType){
        			 count++;
        			 present =c.getLong(presentColumn);
        			 if (present == 1)pos[3]++;
        			 if(pID == LpID){
	        	    	 //exist = true;
	        	    	 pos[0] = count;
	        	    	 pos[1] = present;
	        	    	 pos[2] = c.getLong(listColumn);
        			 }
        		 }
        	     //Log.i(TAG,"checking codes..." );
             } while (c.moveToNext() && pos[0] == 0);//exist != true);
       // else
         //   Toast.makeText(context, "No Persons found for events", Toast.LENGTH_SHORT).show();
        c.close();
        db.close();
        //if (exist)pos[0] = count;
        Log.i(TAG,"checking codes..."+exist+pos[0]+pos[1]+pos[2] );
		return pos;		
	}
	
	
	public boolean add_dbpersondata(int ptype, long code){//method used when creating a record with only code available
		Log.i(TAG,"add person method codes..." );
		boolean exist = true;
			String blank = "";		
			if (ptype == 1)exist = add_dbpersondata(blank,blank,code);
			if (ptype == 2)exist = add_dbpersondata(blank,blank,code,blank,blank);
			return exist;
	}
	
	public boolean add_dbpersondata(String fname, String lname,long code){
		boolean exist = true;
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
	        exist = false;
		}else{
            Toast.makeText(context, "Record exists", 
            		Toast.LENGTH_SHORT).show();
            }
		return exist;
	}
	
	//public void add_dbpersondata(int ptype, String fname, String lname,long code,String uname, String pass){
	public boolean add_dbpersondata( String fname, String lname,long code,String uname, String pass){
		if(!codeCHECK(code)){
	    	//---add 2 events and persons---
	    	DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"add data method" );
	        
	        	try{
		        	db.insertOfficial(code, lname, fname, uname, pass);	
		        	Log.d(TAG,"add official to db" );
	        	} catch (NumberFormatException e){
	       		 Toast.makeText(context, "Invalid data format", Toast.LENGTH_SHORT).show();
	        	}
	        	
	       // }
	        db.close();
	        return false;
        }else{
            Toast.makeText(context, "Record exists", Toast.LENGTH_SHORT).show();
            return true;
            }
    }
	
	
	public boolean upd_dbpersondata(int ptype, long pID,long code){//method used when updating a record with only code available--can happen with scans only
			//String blank = "";		
			//DBAdapter db = new DBAdapter(context); 
		boolean exist = true;
			if (ptype == 1)exist = upd_dbpersondata(pID,getPersonName(pID,ptype,DBAdapter.KEY_FIRSTNAME),getPersonName(pID,ptype,DBAdapter.KEY_LASTNAME),code);
			if (ptype == 2)exist = upd_dbpersondata(pID,getPersonName(pID,ptype,DBAdapter.KEY_FIRSTNAME),getPersonName(pID,ptype,DBAdapter.KEY_LASTNAME),code,
						getPersonName(pID,ptype,DBAdapter.KEY4_USERNAME),getPersonName(pID,ptype,DBAdapter.KEY4_PASS));
			return exist;
	}
	
	public boolean upd_dbpersondata(long pID, String fname, String lname,long code){
		if(!codeCHECK(code,pID)){
	    	//---add 2 events and persons---
	    	DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"update person db method" );
	        	try{
	        		db.updateStudent(pID,code,lname,fname);
		        Log.d(TAG,"update student in db" );
	        	} catch (NumberFormatException e){
	        		 Toast.makeText(context, "Invalid data format", 
	                 		Toast.LENGTH_SHORT).show();
	        	}
	        
	        db.close();
	        return false;
		}else{
            Toast.makeText(context, "Record exists", Toast.LENGTH_SHORT).show();
            return true;
		}
    }
	
	public boolean upd_dbpersondata(long pID, String fname, String lname,long code,String uname, String pass){
		if(!codeCHECK(code,pID)){
	    	//---add 2 events and persons---
	    	DBAdapter db = new DBAdapter(context); 
	        db.open();       
	        Log.i(TAG,"update person db method" );
	        	try{
		        	db.updateOfficial(pID,code,lname,fname, uname, pass);
		        	Log.d(TAG,"updated official in db" );
	        	} catch (NumberFormatException e){
	       		 Toast.makeText(context, "Invalid data format", 
	              		Toast.LENGTH_SHORT).show();
	        	}        	
	        //}
	        db.close();
	        return false;
		}else{
            Toast.makeText(context, "Record exists", 
            		Toast.LENGTH_SHORT).show();
            return true;
            }
        
    }
	
	public String timeStamp(){
		return (String)android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
	}
	
	public String saveasFile(long lRowID,String username) {
		ArrayList<String[]> officalData = aries_personExtract(lRowID, 2);
		ArrayList<String[]> studentData = aries_personExtract(lRowID, 1);
		String [] eventDetails = eventExtract(lRowID);
		
		File newxmlfile = makeFile(lRowID,eventDetails);
		
		String eventTag = "EventDetails";
		String studentTag = "Student";
		String officialTag = "Official";
		String rootTag = "Event";
		String userTag = "username";
		Log.d(TAG, "save file ");

        try{
                newxmlfile.createNewFile();
        }catch(IOException e){
                Log.e(TAG, "exception in createNewFile() method");
        }
        
        //we have to bind the new file with a FileOutputStream
        FileOutputStream fileos = null;        
        try{
                fileos = new FileOutputStream(newxmlfile);
        }catch(FileNotFoundException e){
                Log.e(TAG, "can't create FileOutputStream");
        }
        
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        try {
        	Log.d(TAG, "save file in the try bracket");
                //we set the FileOutputStream as output for the serializer, using UTF-8 encoding
                        serializer.setOutput(fileos, "UTF-8");
                        //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
                        serializer.startDocument(null, Boolean.valueOf(true));
                        //set indentation option
                        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                        //start a tag called "root"
                        serializer.startTag(null, rootTag);
                        //i indent code just to have a view similar to xml-tree
                        //building xml section for eventdetails
	                        serializer.startTag(null, userTag);
	                        	serializer.text(username);
	                        serializer.endTag(null, userTag);
                        
                        		serializer.startTag(null, eventTag);
			                        //set an attribute called "attribute" with a "value" for <eventdetails>
			                        serializer.attribute(null, "Event_Type", eventDetails[0]);
			                        serializer.attribute(null, "Venue", eventDetails[1]);
			                        serializer.attribute(null, "Course", eventDetails[2]);
			                        serializer.attribute(null, "Duration", eventDetails[3]);
			                        serializer.attribute(null, "Time_Stamp", eventDetails[4]);
		                        serializer.endTag(null, eventTag);
		                        
                                //building xml section for officials
                                for (int i = 0; i < officalData.size();i++){
                                	serializer.startTag(null, officialTag);
                                    	//set an attribute called "attribute" with a "value" for <child2>
                                	serializer.attribute(null, "Time_Stamp", officalData.get(i)[3]);
                                    	serializer.attribute(null, "First_Name", officalData.get(i)[5]);
                                    	serializer.attribute(null, "Last_Name", officalData.get(i)[4]);
                                    	serializer.attribute(null, "Code", officalData.get(i)[0]);
                                    	serializer.attribute(null, "Present", officalData.get(i)[1]);
                                    	serializer.attribute(null, "List", officalData.get(i)[2]);
                                    serializer.endTag(null, officialTag);
                                }
                               // Log.i(TAG, "save file 2");
                                //building xml section for student
                                for (int i = 0; i < studentData.size();i++){
                                	serializer.startTag(null, studentTag);
                                    	//set an attribute called "attribute" with a "value" for <child2>
                                    	serializer.attribute(null, "Time_Stamp", studentData.get(i)[3]);
                                    	serializer.attribute(null, "List", studentData.get(i)[2]);
                                    	serializer.attribute(null, "Present", studentData.get(i)[1]);
                                    	serializer.attribute(null, "Code", studentData.get(i)[0]);
                                    	serializer.attribute(null, "Last_Name", studentData.get(i)[4]);
                                    	serializer.attribute(null, "First_Name", studentData.get(i)[5]);
                                    serializer.endTag(null, studentTag);
                                 //  Log.i(TAG, "save file: "+ studentData.get(i)[5]);
                                }
                        serializer.endTag(null, rootTag);
                        serializer.endDocument();//Log.i(TAG, "save file 3");
                        //write xml data into the FileOutputStream
                        serializer.flush();
                        //finally we close the file stream
                        fileos.close();
                       
                        Toast.makeText(context, "file has been created on SD card", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                        Log.e(TAG,"error occurred while creating xml file");
                }
		return getFilePath();
	}


	private File makeFile(long lRowID, String[] eventDetails) {
		//String fileName ="test.xml";
		File dirPath = new File(Environment.getExternalStorageDirectory()+"/BabySAM/"+eventDetails[0]+"/");
		// have the object build the directory structure, if needed.
		dirPath.mkdirs();
		String fileName = lRowID+"_"+eventDetails[0]+"_"+eventDetails[1]+"_"+eventDetails[2]+".xml";
		File newxmlfile = new File(dirPath,fileName);
		
		int count = 1; //variable used to change filename
		while (newxmlfile.exists()){
			fileName = lRowID+"_"+eventDetails[0]+"_"+eventDetails[1]+"_"+eventDetails[2]+"_"+count+".xml";
			count++;
			newxmlfile = new File(dirPath,fileName);
		}
		setFilePath(newxmlfile);
		return newxmlfile;
	}
	
	public String encryptFile(String savedPath){
		//break the filename path 
		String [] pathNname = getPATHnNAME(savedPath);
		String ciphertext = getCryptoFileName(pathNname);
		try {
		    // Generate a temporary key. In practice, you would save this key.
		    // See also Encrypting with DES Using a Pass Phrase.
		    //SecretKey key = KeyGenerator.getInstance("DES").generateKey();
			String passPhrase = "babySAM";

		    // Create encrypter/decrypter class
		    DesEncrypter encrypter = new DesEncrypter(passPhrase);

		    // Encrypt
		    encrypter.encrypt(new FileInputStream(savedPath),
		        new FileOutputStream(ciphertext));

		   /* // Decrypt
		    encrypter.decrypt(new FileInputStream(ciphertext),
		        new FileOutputStream(pathNname[0]+"cleartext2"));*/
		    
		    deleteTempXML(savedPath);
			Toast.makeText(context, " Server registration completed Successfully ", Toast.LENGTH_SHORT).show();
			
		} catch (Exception e) {
		}
		return ciphertext;
	}
	
	private void deleteTempXML(String savedPath) {
		File file = new File(savedPath);
	    if(!file.delete())Toast.makeText(context, "Error deleting saved Encrypted copy of session. Delete manually from "+savedPath, Toast.LENGTH_LONG).show();		
	}


	private String getCryptoFileName(String [] pathNname){
		return pathNname[0]+"cryp-"+pathNname[1];
	}
	
	/*
	 * 
	 * encrypted file send to send file already
	 * 
	 * private String getCryptoFileName(String savedPath){
		String [] pathNname = getPATHnNAME(savedPath);
		return pathNname[0]+"cryp-"+pathNname[1];
	}*/

	private String [] getPATHnNAME(String savePath){
		//method will be used to split the saved Path into path and filename
		String [] splitPath = savePath.split("\\/");
		String path = "";//, name = null;
		int count = splitPath.length-1;
		for (int i = 0; i < count;i++)path+=splitPath[i]+"/";
		Log.v(TAG, path+"  "+count+"  "+splitPath[count]);
		return new String []{path,splitPath[count]};
	}

	private void setFilePath(File newxmlfile) {
		this.filePath = newxmlfile.toString();
		Log.i(TAG, filePath);
	}
	
	public String getFilePath(){
		return filePath;
	}


	public ArrayList<String> getAllUsernames() {
		ArrayList<String> usernames = new ArrayList <String>();
		DBAdapter db = new DBAdapter (context);
		db.open();
		Cursor c = db.getAllOfficials();
		int unameColumn = c.getColumnIndex(DBAdapter.KEY4_USERNAME) ;
		if (c.moveToFirst()){
			do{
				usernames.add(c.getString(unameColumn));	        
			}while (c.moveToNext());
		}
		c.close();
        db.close();
		return usernames;
	}
	
}