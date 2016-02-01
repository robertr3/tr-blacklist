package com.silentdynamics.student.blacklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //DB Class to perform DB related operations
    DBController controller = new DBController(this);
    //Progress Dialog Object
    ProgressDialog prgDialog;
   // private Button eventsButton;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoCreateEventActivity(view);
            }
        });
        //eventsButton = (Button) findViewById(R.id.findEventsButton);
        //eventsButton.setOnClickListener(this);

        /*
        *Eventlist related operations
        *Get Event records from local composeJSONfromSQLiteSQLite DB
        *
        * */

        ArrayList<HashMap<String, String>> eventList =  controller.getAllEvents();
        for (int i = 0; i < eventList.size(); i++){
            Log.d(TAG, "eventList " + i + ": " +eventList.get(i));
        }

        //
        if(eventList.size()!=0){
            //Set the Event Array list in ListView
            EventlistAdapter eventAdapter = new EventlistAdapter(eventList, this);

            ListAdapter adapter = new SimpleAdapter( MainActivity.this,eventList, R.layout.view_event_entry, new String[] { "id","name","topic1"}, new int[] {R.id.eventId, R.id.eventName, R.id.eventTopic});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(eventAdapter);
            //Display Sync status of SQLite DB
            Toast.makeText(getApplicationContext(), controller.getSyncStatus(), Toast.LENGTH_LONG).show();
        }

        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);

        // register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        BatteryLevelReceiver receiver = new BatteryLevelReceiver();
        registerReceiver(receiver, filter);

        // Timer to check for current events
        Message msg = new Message();
        Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if(msg.arg1==1)
                {
                    //Print Toast or open dialog
                }
                return false;
            }
        });

        Timer updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            public static final String PREFS_NAME = "MyPrefsFile";
            ArrayList<String> alreadyNotified = new ArrayList<>();

            public void run() {
                try {

                    SharedPreferences settings = MainActivity.this.getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();

                    SimpleDateFormat format = new SimpleDateFormat("HH-mm");

                    Date systemTime = new Date();
                    String myDate = format.format(systemTime);
                    systemTime = format.parse(myDate);

                    Date eventTime = new Date();

                    ArrayList<HashMap<String, String>> eventList =  controller.getAllEvents();
                    for (int i = 0; i < eventList.size(); i++){
                        eventTime  = format.parse(eventList.get(i).get("timestart"));
                        long mills = eventTime.getTime() - systemTime.getTime();
                        int Hours = (int) (mills / (1000 * 60 * 60));
                        int Mins = (int) (mills / (1000 * 60)) % 60;
                        String diff = Hours + ":" + Mins; // updated value every1 second
                        Log.d(TAG, "i: " + diff);
                        if(Hours == 0){
                            if(alreadyNotified.contains(eventList.get(i).get("name")) == false) {
                                alreadyNotified.add(eventList.get(i).get("name"));
                                editor.putString("CurrentEvent", eventList.get(i).get("name"));
                                editor.commit();
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences settings = getSharedPreferences(BatteryLevelReceiver.PREFS_NAME, 0);
                                        if (settings.contains("BatterySafe")) {
                                            String event = settings.getString("CurrentEvent", "");
                                            Toast.makeText(MainActivity.this, event + " faengt bald an", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }


                    long mills = systemTime.getTime() - eventTime.getTime();
                    Log.v("Data1", "" + systemTime.getTime());
                    Log.v("Data2", "" + eventTime.getTime());
                    int Hours = (int) (mills / (1000 * 60 * 60));
                    int Mins = (int) (mills / (1000 * 60)) % 60;

                    String diff = Hours + ":" + Mins; // updated value every1 second
                    //txtCurrentTime.setText(diff);
                    Log.d(TAG, diff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, 0, 2000);
    }

    public void notifyTime(String eventName) {
        Toast.makeText(this, eventName + " f�ngt bald an", Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.refresh) {
            //Sync SQLite DB data to remote MySQL DB
            syncSQLiteMySQLDB();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void syncSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> eventList =  controller.getAllEvents();
        if(eventList.size()!=0){
            if(controller.dbSyncCount() != 0){
                prgDialog.show();
                params.put("usersJSON", controller.composeJSONfromSQLite());
                client.post("http://" + getResources().getString(R.string.servicePort) + "/sqlitemysqlsync/insertuser.php",params ,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        System.out.println(response);
                        prgDialog.hide();
                        try {
                            JSONArray arr = null;
                            try {
                                arr = new JSONArray(new String(response, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            System.out.println(arr.length());
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                System.out.println(obj.get("id"));
                                System.out.println(obj.get("status"));
                                controller.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                            }
                            Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] content, Throwable error) {
                        // TODO Auto-generated method stub
                        prgDialog.hide();
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadDataTask().execute(url);
        } else {
            Log.e("Connect", "Couldn't connect");
        }
    }

    /**
     * Method gets triggered when Register button is clicked
     *
     * @param view
     */
    public void navigatetoCreateEventActivity(View view){
        Intent createEventIntent = new Intent(getApplicationContext(),CreateEventActivity.class);
        createEventIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(createEventIntent);
    }

    public void navigateToFindEventsActivity(View view){
        Log.d(TAG, "find Events");
        Intent findEventIntent = new Intent(getApplicationContext(),FindEventsActivity.class);
        findEventIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(findEventIntent);
    }

    public void navigateToBookmarkedEventsActivity(View view){
        Intent bookmarkedEventIntent = new Intent(getApplicationContext(),BookmarkedEventsActivity.class);
        bookmarkedEventIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(bookmarkedEventIntent);
    }

    public void deleteFirstEvent(View view){
        controller.deleteEvent();
        ArrayList<HashMap<String, String>> eventList =  controller.getAllEvents();
        Log.d(TAG, "eventList: " + eventList.size());
        for (int i = 0; i < eventList.size(); i++){
            Log.d(TAG, "eventList " + i + ": " +eventList.get(i));
        }

        //
        if(eventList.size()!=0){
            //Set the Event Array list in ListView
            ListAdapter adapter = new SimpleAdapter( MainActivity.this,eventList, R.layout.view_event_entry, new String[] { "id","name","topic1"}, new int[] {R.id.eventId, R.id.eventName, R.id.eventTopic});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(adapter);
            //Display Sync status of SQLite DB
            Toast.makeText(getApplicationContext(), controller.getSyncStatus(), Toast.LENGTH_LONG).show();
        }
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}
