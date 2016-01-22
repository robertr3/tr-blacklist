package com.silentdynamics.student.blacklist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class CreateEventActivity extends AppCompatActivity {

    // Name Edit View Object
    EditText nameE;
    // Email Edit View Object
    EditText typeE;
    // Passwprd Edit View Object
    EditText topic1E;
    // Passwprd Edit View Object
    EditText startTimeE;
    // Passwprd Edit View Object
    EditText endTimeE;
    // Passwprd Edit View Object
    EditText locationE;
    // Passwprd Edit View Object
    Switch privacyE;
    TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nameE = (EditText)findViewById(R.id.registerEvent);
        typeE = (EditText)findViewById(R.id.registerType);
        topic1E = (EditText)findViewById(R.id.chooseTopic1);
        startTimeE = (EditText)findViewById(R.id.choosetimeS);
        endTimeE = (EditText)findViewById(R.id.choosetimeE);
        locationE = (EditText)findViewById(R.id.location);
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
    }

    /**
     * Method gets triggered when Create button is clicked
     *
     * @param view
     */
    public void registerEvent(View view){
        // Get NAme ET control value
        String name = nameE.getText().toString();
        // Get type ET control value
        String type = typeE.getText().toString();
        // Get topic1 ET control value
        String topic1 = topic1E.getText().toString();
        // Get startTime ET control value
        String startTime = startTimeE.getText().toString();
        // Get endTime ET control value
        String endTime = endTimeE.getText().toString();
        // Get location ET control value
        String location = locationE.getText().toString();
        // Get privacy Switch value
        Boolean privacy = privacyE.isChecked();
        // Get username
        String username = Utility.getUsername();
        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        // When Name Edit View, Email Edit View and Password Edit View have values other than Null
        if(Utility.isNotNull(name) && Utility.isNotNull(type) && Utility.isNotNull(topic1) && Utility.isNotNull(startTime) && Utility.isNotNull(location)){
                // Put Http parameter name with value of Name Edit View control
                params.put("name", name);
                // Put Http parameter type with value of type Edit View control
                params.put("type", type);
                // Put Http parameter topic1 with value of Password Edit View control
                params.put("topic1", topic1);
                params.put("timeStart", startTime);
            if (Utility.isNotNull(endTime)){
                params.put("timeEnd", endTime);
            }
            else {
                params.put("timeEnd", "not defiened");
            }
                params.put("location", location);
                params.put("privacy", privacy);
                params.put("username", username);
                // Invoke RESTful Web Service with Http parameters
                invokeWS(params);
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
        client.get("http://" + getResources().getString(R.string.servicePort) + "/blacklistService/register/doregister",params ,new AsyncHttpResponseHandler() {
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
                    if(obj.getBoolean("status")){
                        // Display successfully registered message using Toast
                        Toast.makeText(getApplicationContext(), "You successfully created an event!", Toast.LENGTH_LONG).show();
                    }
                    // Else display error message
                    else{
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
            public void onFailure(int statusCode,Header[] headers, byte[] content, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
