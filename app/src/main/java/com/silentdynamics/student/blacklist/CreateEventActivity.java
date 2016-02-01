package com.silentdynamics.student.blacklist;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class CreateEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = CreateEventActivity.class.getSimpleName();

    // Name Edit View Object
    EditText nameE;
    // Email Edit View Object
    Spinner typeSpinner;
    // Passwprd Edit View Object
    Spinner topicSpinner;
    // Passwprd Edit View Object
    static TextView timeTextView;
    // Passwprd Edit View Object
    String locationE;
    // Passwprd Edit View Object
    Switch privacyE;
    TextView errorMsg;

    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> topicMDAdapter;
    ArrayAdapter<CharSequence> topicMovieAdapter;

    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nameE = (EditText)findViewById(R.id.registerEvent);
        timeTextView = (TextView)findViewById(R.id.timeValue);
        privacyE = (Switch)findViewById(R.id.privacy);
        errorMsg = (TextView)findViewById(R.id.registerEvent_error);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*
        * List of types
        * */
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        typeSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
            // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.typeCreate_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);

        /*
        * List of topics
        * */
        topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
         topicMDAdapter = ArrayAdapter.createFromResource(this,
                R.array.topicsCreateMD_array, android.R.layout.simple_spinner_item);
        topicMovieAdapter = ArrayAdapter.createFromResource(this,
                R.array.topicsCreateMovie_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        topicMDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicMovieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        topicSpinner.setAdapter(topicMDAdapter);

        /*
        * The PlaceSelectionListener handles returning a place in response to the user's selection.
        * */
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                locationE = (String) place.getAddress();
                Log.i(TAG, "Place: " + locationE);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        /*
        * The TimeSelectionListener handels returning the picked time
        * */
        timeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                newFragment.show(ft, "timePicker");
            }
        });
    }
    /**
     * Method gets triggered when Create button is clicked
     *
     * @param view
     */
    public void registerEvent(View view){
        /*Operations for central MySQL db */
        // Get NAme ET control value
        String name = nameE.getText().toString();
        // Get type ET control value
        String type = (String) typeSpinner.getSelectedItem();
        // Get topic1 ET control value
        String topic1 = (String) topicSpinner.getSelectedItem();
        // Get startTime ET control value
        String startTime = (String) timeTextView.getText();
        // Get privacy Switch value
      //  String privacy = String.valueOf(privacyE.isChecked());
        String privacy = "false";
        Log.d(TAG, "privacy: " + privacy);
        // Get username
        String username = Utility.getUsername();
        if (username == null){username = "TestUser";}
        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        HashMap<String, String> queryValues = new HashMap<String, String>();

        // When Name Edit View, Email Edit View and Password Edit View have values other than Null
        if(Utility.isNotNull(name) && Utility.isNotNull(type) && Utility.isNotNull(topic1) && Utility.isNotNull(startTime) && Utility.isNotNull(locationE)){
                // Put Http parameter name with value of Name Edit View control
                params.put("name", name);
                // Put Http parameter type with value of type Edit View control
                params.put("type", type);
                // Put Http parameter topic1 with value of Password Edit View control
                params.put("topic1", topic1);
                params.put("timestart", startTime);
                params.put("location", locationE);
                params.put("privacy", privacy);
                params.put("username", username);

            /*Operations for local SQLite db */
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_NAME, name);
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_TYPE, type);
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_TOPIC, topic1);
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART, startTime);
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, locationE);
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_PRIVACY, privacy);
            queryValues.put(EventsContract.EventsEntry.COLUMN_NAME_USERNAME, username);
            controller.insertEvent(queryValues);
            // Invoke RESTful Web Service with Http parameters
            invokeWS(params);
            this.callHomeActivity(view);
        }
        // When any of the Edit View control left blank
        else{
            Toast.makeText(getApplicationContext(), "Please fill the form", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://" + getResources().getString(R.string.servicePort) + "/blacklistService/registerevent/doregisterevent", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    // JSON Object
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(new String(response, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        // Display successfully registered message using Toast
                        Toast.makeText(getApplicationContext(), "You successfully created an event!", Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else {
                        errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] content, Throwable error) {
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        newFragment.show(ft, "timePicker");
    }

    /**
     * Navigate to Home Screen
     * @param view
     */
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(objIntent);
    }

    /**
     * Called when Cancel button is clicked
     * @param view
     */
    public void cancelAddEvent(View view) {
        this.callHomeActivity(view);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(typeSpinner)){
            topicSpinner.setEnabled(true);
            if(typeSpinner.getSelectedItem().equals("Meet and Drink"))
            {
                ArrayAdapter<CharSequence> c1 = ArrayAdapter.createFromResource(this,
                        R.array.topicsCreateMD_array, android.R.layout.simple_spinner_item);
                c1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                topicSpinner.setAdapter(c1);
            }
            else  if(typeSpinner.getSelectedItem().equals("Movies"))
            {
                ArrayAdapter<CharSequence> c2 = ArrayAdapter.createFromResource(this,
                        R.array.topicsCreateMovie_array, android.R.layout.simple_spinner_item);
                c2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                topicSpinner.setAdapter(c2);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeTextView.setText(hourOfDay + "-" + minute);
        }
    }
}


