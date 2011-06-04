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
	/*
	 * 
	 * public static final String KEY_ROWID = "_id";
	 * public static final String KEY_ISBN = "isbn";
	 * public static final String KEY_TITLE = "title";
	 * public static final String KEY_PUBLISHER = "publisher";    
	 * private static final String TAG = "DBAdapter";
	 * private static final String DATABASE_NAME = "books";
	 * private static final String DATABASE_TABLE = "titles";
    */
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIMESTAMP = "timestamp";
	
	public static final String KEY1_EVENTTYPE = "evType";
	public static final String KEY1_VENUE = "venue";
	public static final String KEY1_COURSE = "course";
	public static final String KEY1_DURATION = "duration";
	//public static final String KEY1_TIMESTAMP = "timestamp";
	public static final String KEY1_ARIES = "aries";
	
	public static final String KEY2_EVENTID = "eventid";
	public static final String KEY2_PERSONTYPE = "pType";
	public static final String KEY2_CODE = "code";
	//public static final String KEY2_TIMESTAMP = "timestamp";
	
	private static final String DBTAG = "DBAdapter";
	private static final String DATABASE_NAME = "babySAM";
	private static final String DATABASE_TABLE1 = "session";
	private static final String DATABASE_TABLE2 = "person";
	private static final int DATABASE_VERSION = 1;

    /* 
     * private static final String DATABASE_CREATE =
     *         "create table titles (_id integer primary key autoincrement, "
     *                 + "isbn text not null, title text not null, " 
     *                         + "publisher text not null);";
     *                                 */
	
	private static final String DATABASE_T1_CREATE =
        "create table "+DATABASE_TABLE1+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY1_EVENTTYPE + " text not null, "+KEY1_VENUE+" text not null,"+KEY1_COURSE+" text not null, " 
        + KEY1_DURATION +" integer not null, "+KEY_TIMESTAMP +" text not null, "+KEY1_ARIES+" integer not null);";
	
	private static final String DATABASE_T2_CREATE =
		"create table "+DATABASE_TABLE2+" ("+KEY_ROWID+" integer primary key autoincrement, "
        + KEY2_EVENTID +" integer not null, "+KEY2_PERSONTYPE +" integer not null, "+KEY2_CODE+" integer not null, " 
        + KEY_TIMESTAMP+" text not null);";
	
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                              int newVersion) 
        {
            Log.w(DBTAG, "Upgrading database from version " + oldVersion 
                  + " to "
                  + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE1);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE2);
            onCreate(db);
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
    
  //---insert a person into the database---
    public long insertPerson(long eventid, int ptype, long code, String timestamp) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY2_EVENTID, eventid);
        initialValues.put(KEY2_PERSONTYPE, ptype);
        initialValues.put(KEY2_CODE, code);
        initialValues.put(KEY_TIMESTAMP, timestamp);
        return db.insert(DATABASE_TABLE2, null, initialValues);
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
        
    }
    
    //---deletes a particular person---
    public boolean deletePerson(long rowId) 
    {
        return db.delete(DATABASE_TABLE2, KEY_ROWID + "=" + rowId, null) > 0;
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
                KEY2_CODE,
                KEY_TIMESTAMP}, 
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
                        KEY2_CODE,
                        KEY_TIMESTAMP}, 
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
    
    //---updates a person---
    public boolean updatePerson(long rowId,long eventid, int ptype, long code, String timestamp) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY2_EVENTID, eventid);
        args.put(KEY2_PERSONTYPE, ptype);
        args.put(KEY2_CODE, code);
        args.put(KEY_TIMESTAMP, timestamp);
        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}