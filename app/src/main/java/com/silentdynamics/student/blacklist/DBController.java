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
                EventsContract.EventsEntry.COLUMN_NAME_LOCATION + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_PRIVACY + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_USERNAME + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_UPDATE + " TEXT," +
                EventsContract.EventsEntry.COLUMN_NAME_CACHED + " TEXT)";
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
        Log.d(TAG, "inseide insertEvent" + queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_USERNAME));

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventsContract.EventsEntry.COLUMN_NAME_NAME, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_NAME));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TYPE, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TYPE));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TOPIC, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TOPIC));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_LOCATION));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, queryValues.get(EventsContract.EventsEntry.COLUMN_NAME_USERNAME));
        values.put(EventsContract.EventsEntry.COLUMN_NAME_UPDATE, "no");
        values.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, "false");
        database.insert(EventsContract.EventsEntry.TABLE_NAME, EventsContract.EventsEntry.COLUMN_NAME_TOPIC, values);
        database.close();
    }

    /**
     * Delets first Event from SQLite DB
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
     * Delets specified Event from SQLite DB
     */
    public void deleteEvent(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        Log.d(TAG, "insede deleteSpecificEvent: " + id);
        database.delete(EventsContract.EventsEntry.TABLE_NAME, EventsContract.EventsEntry.COLUMN_NAME_ENTRY_ID + "=" + id, null);
    }

    /**
     * Changes bookmarkstatus for given Event
     */
    public void switchBookmark(String id) {
        Log.d(TAG, "insede switchBookmark: " + id);
        String privacy = "";
        String selectQuery = "SELECT privacy FROM " + EventsContract.EventsEntry.TABLE_NAME + " WHERE id = '" + id + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                privacy = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        ContentValues cv = new ContentValues();
        if (privacy.equals("false")) {
            Log.d(TAG, "inside false");
            cv.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, "true");
        }
        else {
            Log.d(TAG, "inside true");
            cv.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, "false");
        }
        Log.d(TAG, "privacy: " + privacy);
        database.update(EventsContract.EventsEntry.TABLE_NAME, cv, "id=" + id, null);
    }

    /**
     * Changes cachedstatus for given Event
     */
    public void switchCached(String id) {
        Log.d(TAG, "insede switchCached: " + id);
        String cached = "";
        String selectQuery = "SELECT " + EventsContract.EventsEntry.COLUMN_NAME_CACHED + " FROM " + EventsContract.EventsEntry.TABLE_NAME + " WHERE id = '" + id + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                cached = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        ContentValues cv = new ContentValues();
        if (cached.equals("false")) {
            cv.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, "true");
        }
        else {
            cv.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, "false");
        }
        Log.d(TAG, "cached: " + cached);
        database.update(EventsContract.EventsEntry.TABLE_NAME, cv, "id=" + id, null);
    }

    /**
     * Changes cachedstatus for given Event to true
     */
    public void putCached(String id) {
        Log.d(TAG, "insede putCached: " + id);
        String cached = "";
        String selectQuery = "SELECT " + EventsContract.EventsEntry.COLUMN_NAME_CACHED + " FROM " + EventsContract.EventsEntry.TABLE_NAME + " WHERE id = '" + id + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                cached = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        ContentValues cv = new ContentValues();
        if (cached.equals("false")) {
            cv.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, "true");
            database.update(EventsContract.EventsEntry.TABLE_NAME, cv, "id=" + id, null);
        }
        else {
            Log.d(TAG, "allready cached! " + cached);
        }
    }

    /**
     * Changes cachedstatus for given Event to false
     */
    public void removeCached(String id) {
        Log.d(TAG, "insede removeCached: " + id);
        String cached = "";
        String selectQuery = "SELECT " + EventsContract.EventsEntry.COLUMN_NAME_CACHED + " FROM " + EventsContract.EventsEntry.TABLE_NAME + " WHERE id = '" + id + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                cached = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        ContentValues cv = new ContentValues();
        if (cached.equals("true")) {
            cv.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, "false");
            database.update(EventsContract.EventsEntry.TABLE_NAME, cv, "id=" + id, null);
        }
        else {
            Log.d(TAG, "allready not cached! " + cached);
        }
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
                map.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, cursor.getString(5));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, cursor.getString(6));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, cursor.getString(7));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, cursor.getString(9));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    /**
     * Get list of bookmarkedEvents as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getBookmarkedEvents() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsEntry.TABLE_NAME + " WHERE privacy = 'true'";
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
                map.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, cursor.getString(5));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, cursor.getString(6));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, cursor.getString(7));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, cursor.getString(9));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    /**
     * Get list of cachedEvents as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getCachedEvents() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + EventsContract.EventsEntry.TABLE_NAME + " WHERE cached = 'true'";
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
                map.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, cursor.getString(5));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, cursor.getString(6));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, cursor.getString(7));
                map.put(EventsContract.EventsEntry.COLUMN_NAME_CACHED, cursor.getString(9));
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