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
 * Created by Student on 01.02.2016.
 */
public class DBCacheController  extends SQLiteOpenHelper {
    private static final String TAG = DBCacheController.class.getSimpleName();
    public static final String DATABASE_NAME = "BlacklistCache.db";

    public DBCacheController(Context applicationcontext) {
        super(applicationcontext, DATABASE_NAME, null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE " + EventsContract.EventsCached.TABLE_NAME + " (" + EventsContract.EventsCached.COLUMN_NAME_CACHED_ID +
                " INTEGER PRIMARY KEY," + EventsContract.EventsCached.COLUMN_NAME_ENTRY_ID + " TEXT," +
                EventsContract.EventsCached.COLUMN_NAME_UPDATE + " TEXT)" +
                EventsContract.EventsCached.COLUMN_NAME_CACHE + " TEXT,";
        database.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS EventsContract.EventsCached.TABLE_NAME";
        database.execSQL(query);
        onCreate(database);
    }
    /**
     * Inserts Event into SQLite DB
     * @param queryValues
     */
    public void insertCachedEvent(HashMap<String, String> queryValues) {
        Log.d(TAG, "inseide insertCachedEvent");

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventsContract.EventsCached.COLUMN_NAME_ENTRY_ID, queryValues.get(EventsContract.EventsCached.COLUMN_NAME_ENTRY_ID));
        values.put(EventsContract.EventsCached.COLUMN_NAME_UPDATE, queryValues.get(EventsContract.EventsCached.COLUMN_NAME_UPDATE));
        values.put(EventsContract.EventsCached.COLUMN_NAME_CACHE, "true");
        database.insert(EventsContract.EventsCached.TABLE_NAME, null, values);
        database.close();
    }

    /**
     * Delets specified Event from SQLite DB
     */
    public void deleteEvent(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        Log.d(TAG, "insede deleteSpecificCachedEvent: " + id);
        database.delete(EventsContract.EventsCached.TABLE_NAME, EventsContract.EventsCached.COLUMN_NAME_CACHED_ID + "=" + id, null);
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllCachedEvents() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsCached.TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(EventsContract.EventsCached.COLUMN_NAME_CACHED_ID, cursor.getString(0));
                map.put(EventsContract.EventsCached.COLUMN_NAME_ENTRY_ID, cursor.getString(1));
                map.put(EventsContract.EventsCached.COLUMN_NAME_UPDATE, cursor.getString(2));
                map.put(EventsContract.EventsCached.COLUMN_NAME_CACHE, cursor.getString(3));
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
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsCached.TABLE_NAME +
                " where " + EventsContract.EventsCached.COLUMN_NAME_UPDATE + " = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(EventsContract.EventsCached.COLUMN_NAME_CACHED_ID, cursor.getString(0));
                map.put(EventsContract.EventsCached.COLUMN_NAME_ENTRY_ID, cursor.getString(1));
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
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsCached.TABLE_NAME + " where " +
                EventsContract.EventsCached.COLUMN_NAME_UPDATE + " = '"+"no"+"'";
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
        String updateQuery = "Update " + EventsContract.EventsCached.TABLE_NAME + " set " +
                EventsContract.EventsCached.COLUMN_NAME_UPDATE + " = '"+ status +"' where " +
                EventsContract.EventsCached.COLUMN_NAME_ENTRY_ID + " ="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
}