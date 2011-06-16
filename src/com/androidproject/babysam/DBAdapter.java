package com.androidproject.babysam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter 
{
	//TODO create methods to return each of the row names
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_CODE = "code";
	
	public static final String KEY1_EVENTTYPE = "evType";
	public static final String KEY1_VENUE = "venue";
	public static final String KEY1_COURSE = "course";
	public static final String KEY1_DURATION = "duration";
	public static final String KEY1_ARIES = "aries";
	
	public static final String KEY2_EVENTID = "eventid";
	public static final String KEY2_PERSONTYPE = "pType";
	public static final String KEY2_PERSONID = "pID";
	public static final String KEY2_POSITION = "position";
	public static final String KEY2_PRESENT = "present";
	public static final String KEY2_LIST = "list";
	
	public static final String KEY_LASTNAME = "lastname";
	public static final String KEY_FIRSTNAME = "fistname";
	
	public static final String KEY4_USERNAME = "username";
	public static final String KEY4_PASS = "pass";
	
	private static final String DBTAG = "DBAdapter";
	private static final String DATABASE_NAME = "babySAM";
	private static final String DATABASE_TABLE1 = "session";
	private static final String DATABASE_TABLE2 = "person";
	private static final String DATABASE_TABLE3 = "student_list";
	private static final String DATABASE_TABLE4 = "officials_list";
	private static final int DATABASE_VERSION = 3;
	
	private static final String DATABASE_T1_CREATE =
        "create table "+DATABASE_TABLE1+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY1_EVENTTYPE + " text not null, "+KEY1_VENUE+" text not null,"+KEY1_COURSE+" text not null, " 
        + KEY1_DURATION +" integer not null, "+KEY_TIMESTAMP +" text not null, "+KEY1_ARIES+" integer not null);";
	
	private static final String DATABASE_T2_CREATE =
		"create table "+DATABASE_TABLE2+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY2_EVENTID +" integer not null, "+KEY2_PERSONTYPE +" integer not null, "+KEY2_PERSONID+" integer not null, " 
        + KEY_TIMESTAMP+" text not null, "+KEY2_POSITION+" integer not null, "+KEY2_PRESENT+" integer not null," 
        + KEY2_LIST +" integer not null);";
	
	private static final String DATABASE_T3_CREATE =
		"create table "+ DATABASE_TABLE3 +" (" + KEY_ROWID + " integer primary key autoincrement, "
        + KEY_CODE +" integer not null, " + KEY_LASTNAME +" text not null, "+ KEY_FIRSTNAME+" text not null);";
	
	private static final String DATABASE_T4_CREATE =
		"create table "+DATABASE_TABLE4+" ("+KEY_ROWID+" integer primary key autoincrement, "
		 + KEY_CODE +" integer not null, " + KEY_LASTNAME +" text not null, "+ KEY_FIRSTNAME+" text not null," 
		 + KEY4_USERNAME +" text not null, " + KEY4_PASS +" text not null);";
	
    private final Context context; 
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_T1_CREATE);
            db.execSQL(DATABASE_T2_CREATE);
            db.execSQL(DATABASE_T3_CREATE);
            db.execSQL(DATABASE_T4_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                              int newVersion) 
        {
            Log.w(DBTAG, "Upgrading database from version " + oldVersion 
                  + " to "
                  + newVersion + ", which will destroy all old data");
            destroyDB(db);
            onCreate(db);
        }

		private void destroyDB(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE1);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE2);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE3);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE4);			
		}
    }
  //...
    //...

   //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert a Event into the database---
    public long insertEvent(String event, String venue, String course, int duration, int aries, String timestamp) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY1_EVENTTYPE, event);
        initialValues.put(KEY1_VENUE, venue);
        initialValues.put(KEY1_COURSE, course);
        initialValues.put(KEY1_DURATION, duration);
        initialValues.put(KEY1_ARIES, aries);
        initialValues.put(KEY_TIMESTAMP, timestamp);
        return db.insert(DATABASE_TABLE1, null, initialValues);
    }
    
  //---insert a person for event into the database---
    public long insertEventPerson(long eventid, int ptype, long pID, String timestamp, long position, long present, long list) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY2_EVENTID, eventid);
        initialValues.put(KEY2_PERSONTYPE, ptype);
        initialValues.put(KEY2_PERSONID, pID);
        initialValues.put(KEY_TIMESTAMP, timestamp);
        initialValues.put(KEY2_POSITION, position);
        initialValues.put(KEY2_PRESENT, present);
        initialValues.put(KEY2_LIST, list);
        return db.insert(DATABASE_TABLE2, null, initialValues);
    }

  //---insert a Student in an event---
    public boolean insertStudent(long code, String lname, String fname) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CODE, code);
        initialValues.put(KEY_LASTNAME, lname);
        initialValues.put(KEY_FIRSTNAME, fname);       
        return db.insert(DATABASE_TABLE3, null, initialValues) > 0;
    }
    
  //---insert a official in an event---
    public boolean insertOfficial(long code, String lname, String fname, String uname, String pass) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CODE, code);
        initialValues.put(KEY_LASTNAME, lname);
        initialValues.put(KEY_FIRSTNAME, fname);
        initialValues.put(KEY4_USERNAME, uname);
        initialValues.put(KEY4_PASS, pass);        
        return db.insert(DATABASE_TABLE4, null, initialValues) > 0;
    }
    
    //---deletes a particular event---
    public boolean deleteEvent(long rowId) 
    {   //when deleting event delete all associated persons also
        db.delete(DATABASE_TABLE2, KEY2_EVENTID + "=" + rowId, null);
        return db.delete(DATABASE_TABLE1, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
  //---deletes a particular event---
    public void deleteAllTables() 
    {   //when deleting event delete all associated persons also
    	db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE1);
    	db.execSQL(DATABASE_T1_CREATE);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE2);    	
        db.execSQL(DATABASE_T2_CREATE);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE3);
    	db.execSQL(DATABASE_T3_CREATE);
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE4);    	
        db.execSQL(DATABASE_T4_CREATE);
        
    }
    
    //---deletes a particular person---
    public boolean deletePerson(long rowId) 
    {
        return db.delete(DATABASE_TABLE2, KEY_ROWID + "=" + rowId, null) > 0;
    }
  //---deletes a particular person---
    public boolean deleteStudent(long rowId) 
    {
        return db.delete(DATABASE_TABLE3, KEY_ROWID + "=" + rowId, null) > 0;
    }
  //---deletes a particular person---
    public boolean deleteOfficial(long rowId) 
    {
        return db.delete(DATABASE_TABLE4, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the events---
    public Cursor getAllEvents() 
    {
        return db.query(DATABASE_TABLE1, new String[] {
        		KEY_ROWID, 
        		KEY1_EVENTTYPE,
        		KEY1_VENUE,
                KEY1_COURSE,
                KEY1_DURATION,
                KEY_TIMESTAMP,
                KEY1_ARIES}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }
    
    //---retrieves all the persons for particular event---
    public Cursor getAllEventPersons(long eID)throws SQLException 
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE2, new String[] {
        		KEY_ROWID, 
        		KEY2_EVENTID,
        		KEY2_PERSONTYPE,
                KEY2_PERSONID,
                KEY_TIMESTAMP,
                KEY2_POSITION,
                KEY2_PRESENT,
                KEY2_LIST}, 
                KEY2_EVENTID+"="+eID, 
                null, 
                null, 
                null, 
                null,
                null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
  //---retrieves all students---
    public Cursor getAllStudents() 
    {
        return db.query(DATABASE_TABLE3, new String[] {
        		KEY_ROWID, 
        		KEY_CODE,
        		KEY_LASTNAME,
                KEY_FIRSTNAME}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }
    
  //---retrieves all officials---
    public Cursor getAllOfficials() 
    {
        return db.query(DATABASE_TABLE4, new String[] {
        		KEY_ROWID, 
        		KEY_CODE,
        		KEY_LASTNAME,
                KEY_FIRSTNAME,
                KEY4_USERNAME,
                KEY4_PASS}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }

    //---retrieves a particular event---
    public Cursor getEvent(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE1, new String[] {
                		KEY_ROWID,
                		KEY1_EVENTTYPE, 
                		KEY1_VENUE,
                		KEY1_COURSE,
                		KEY1_DURATION,
                        KEY_TIMESTAMP,
                        KEY1_ARIES}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
  //---retrieves a particular person---
    public Cursor getPerson(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE2, new String[] {
                		KEY_ROWID, 
                		KEY2_EVENTID,
                		KEY2_PERSONTYPE,
                        KEY2_PERSONID,
                        KEY_TIMESTAMP,
                        KEY2_POSITION,
                        KEY2_PRESENT,
                        KEY2_LIST},  
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
  //---retrieves a particular student---
    public Cursor getStudent(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE3, new String[] {
                		KEY_ROWID, 
                		KEY_CODE,
                		KEY_LASTNAME,
                        KEY_FIRSTNAME}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
  //---retrieves a particular official---
    public Cursor getOfficial(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE4, new String[] {
                		KEY_ROWID, 
                		KEY_CODE,
                		KEY_LASTNAME,
                        KEY_FIRSTNAME,
                        KEY4_USERNAME,
                        KEY4_PASS},
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates an event---
    public boolean updateEvent(long rowId,String event, String venue, String course, int duration, int aries, String timestamp) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY1_EVENTTYPE, event);
        args.put(KEY1_VENUE, venue);
        args.put(KEY1_COURSE, course);
        args.put(KEY1_DURATION, duration);
        args.put(KEY1_ARIES, aries);
        args.put(KEY_TIMESTAMP, timestamp);
        return db.update(DATABASE_TABLE1, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    //---updates a person in an event---
    public boolean updatePerson(long rowId,long eventid, int ptype, long pID, String timestamp, long position, long present, long list) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY2_EVENTID, eventid);
        args.put(KEY2_PERSONTYPE, ptype);
        args.put(KEY2_PERSONID, pID);
        args.put(KEY_TIMESTAMP, timestamp);
        args.put(KEY2_POSITION, position);
        args.put(KEY2_PRESENT, present);
        args.put(KEY2_LIST, list);
        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
  //---updates a Student ---
    public boolean updateStudent(long rowId,long code, String lname, String fname) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_CODE, code);
        args.put(KEY_LASTNAME, lname);
        args.put(KEY_FIRSTNAME, fname);                
        return db.update(DATABASE_TABLE3, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
  //---updates a official ---
    public boolean updateOfficial(long rowId,long code, String lname, String fname, String uname, String pass) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_CODE, code);
        args.put(KEY_LASTNAME, lname);
        args.put(KEY_FIRSTNAME, fname);
        args.put(KEY4_USERNAME, uname);
        args.put(KEY4_PASS, pass);        
        return db.update(DATABASE_TABLE4, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    

	public Cursor getAllPersons() {
		return db.query(DATABASE_TABLE2, new String[] {
				KEY_ROWID, 
        		KEY2_EVENTID,
        		KEY2_PERSONTYPE,
                KEY2_PERSONID,
                KEY_TIMESTAMP,
                KEY2_POSITION,
                KEY2_PRESENT,
                KEY2_LIST},    
                null, 
                null, 
                null, 
                null,
                null);
	}

	public boolean posChange(long rowID, long position) {
		 ContentValues args = new ContentValues();
	        args.put(KEY2_POSITION, position);
	        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowID, null) > 0;
	}

}