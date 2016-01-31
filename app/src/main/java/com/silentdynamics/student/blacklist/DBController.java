package com.silentdynamics.student.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Student on 27.01.2016.
 */
public class DBController  extends SQLiteOpenHelper {
    private static final String TAG = DBController.class.getSimpleName();
    public static final String DATABASE_NAME = "BlacklistEvents.db";

    public DBController(Context applicationcontext) {
        super(applicationcontext, DATABASE_NAME, null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE " + EventsContract.EventsEntry.TABLE_NAME + " (" + EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID +
                " INTEGER PRIMARY KEY," + EventsContract.EventsEntry.COLUMN_NAME_NAME + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_TYPE + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_TOPIC + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_TIMESTART + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_TIMEEND + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_LOCATION + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_PRIVACY + " TINYINT," +
                EventsContract.EventsEntry.COLUMN_NAME_USERNAME + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_UPDATE + " TEXT)";
        database.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS EventsContract.EventsEntry.TABLE_NAME";
        database.execSQL(query);
        onCreate(database);
    }
    /**
     * Inserts Event into SQLite DB
     * @param queryValues
     */
    public void insertEvent(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventsContract.EventsEntry.COLUMN_NAME_NAME, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_NAME));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TYPE, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TYPE));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TOPIC, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TOPIC));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TIMEEND, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TIMEEND));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_LOCATION));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_USERNAME));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_UPDATE, "no");
        database.insert(EventsContract.EventsEntry.TABLE_NAME, EventsContract.EventsEntry.COLUMN_NAME_TOPIC, values);
        database.close();
        Log.d(TAG, "inseide insertEvent" + queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_USERNAME));
    }

    /**
     * Delets Event from SQLite DB
     */
    public void deleteEvent() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.isOpen();
        Log.d(TAG, "insede deleteEvent");
        Cursor cursor = database.query(EventsContract.EventsEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            String rowId = cursor.getString(cursor.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID));
            Log.d(TAG, "row: " + rowId);
            database.delete(EventsContract.EventsEntry.TABLE_NAME, EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID +
                    "=?",new String[]{rowId});
        }
        database.close();
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllEvents() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsEntry.TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID, cursor.getString(0));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_NAME, cursor.getString(1));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_TYPE, cursor.getString(2));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_TOPIC, cursor.getString(3));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART, cursor.getString(4));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_TIMEEND, cursor.getString(5));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, cursor.getString(6));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, cursor.getString(7));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, cursor.getString(8));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsEntry.TABLE_NAME +
                " where " + EventsContract.EventsEntry.COLUMN_NAME_UPDATE + " = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID, cursor.getString(0));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_NAME, cursor.getString(1));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync neededn";
        }
        return msg;
    }

    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsEntry.TABLE_NAME + " where " +
                EventsContract.EventsEntry.COLUMN_NAME_UPDATE + " = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    /**
     * Update Sync status against each User ID
     * @param id
     * @param status
     */
    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update " + EventsContract.EventsEntry.TABLE_NAME + " set " +
                EventsContract.EventsEntry.COLUMN_NAME_UPDATE + " = '"+ status +"' where " +
                EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID + " ="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
}