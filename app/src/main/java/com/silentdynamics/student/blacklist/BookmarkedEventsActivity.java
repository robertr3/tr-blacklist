package com.silentdynamics.student.blacklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class BookmarkedEventsActivity extends AppCompatActivity {
    private static final String TAG = BookmarkedEventsActivity.class.getSimpleName();
    //DB Class to perform DB related operations
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ArrayList<HashMap<String, String>> eventList =  controller.getBookmarkedEvents();
        for (int i = 0; i < eventList.size(); i++){
            Log.d(TAG, "eventList " + i + ": " + eventList.get(i));
        }

        //
        if(eventList.size()!=0){
            //Set the Event Array list in ListView
            BookmarklistAdapter eventAdapter = new BookmarklistAdapter(eventList, this);

            ListAdapter adapter = new SimpleAdapter( BookmarkedEventsActivity.this,eventList, R.layout.view_bookmarked_entry, new String[] { "id","name","topic1"}, new int[] {R.id.eventId, R.id.eventName, R.id.eventTopic});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(eventAdapter);
            //Display Sync status of SQLite DB
            Toast.makeText(getApplicationContext(), controller.getSyncStatus(), Toast.LENGTH_LONG).show();
        }
    }


    public void navigateToHomeActivity(View view){
        Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
