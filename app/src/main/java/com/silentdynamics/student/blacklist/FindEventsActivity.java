package com.silentdynamics.student.blacklist;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.silentdynamics.student.blacklist.dummy.DummyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindEventsActivity extends FragmentActivity implements OnMapReadyCallback, EventFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener,
        View.OnClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = FindEventsActivity.class.getSimpleName();

    boolean batterySaferMode;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient = null;
    LatLng userPosition;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationRequest mLocationRequest;

    EventFragment listFragment;

    List<DummyContent.DummyItem> events;
    ArrayList<HashMap<String, String>> eventList;
    DBController dbc = new DBController(this);
    //private Map<Marker, DummyContent.DummyItem> markerMap = new HashMap<>();
    private Map<Marker, HashMap<String, String>> markerMap = new HashMap<>();
    Button[] btns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_events);

        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        userPosition = new LatLng(51.0431384, 13.7667263);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(BatteryLevelReceiver.PREFS_NAME, 0);
        if(settings.contains("BatterySafe")) {
            batterySaferMode = settings.getBoolean("BatterySafe", false);
            Toast.makeText(this, "BatterySafe: " + batterySaferMode, Toast.LENGTH_SHORT).show();
        }


        // Get the dummy events
        events = DummyContent.ITEMS;
        eventList =  dbc.getAllEvents();

        // Create the spinner
        Spinner spinner = (Spinner) findViewById(R.id.topics_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.topics_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        if(spinner != null) {
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }

        // Create the TagCloud
        Collection<String> allTopics = new ArrayList<String>();
        allTopics.addAll(Arrays.asList(getResources().getStringArray(R.array.topics_array)));
        allTopics.addAll(Arrays.asList(getResources().getStringArray(R.array.topicsMovie_array)));

        String[] topics = allTopics.toArray(new String[] {});
        //String[] topics = getResources().getStringArray(R.array.topics_array);
        btns = new Button[topics.length];



        int tagID = 0;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.topics_cloud);
        if(layout != null) {
            // Because of the error with teh first button. Only a workaround
            // FIXME
            btns[0] = new Button(this);
            btns[0].setId(0);
            btns[0].setText("");
            btns[0].setOnClickListener(this);
            btns[0].setStateListAnimator(null);
            layout.addView(btns[0]);

            for (Object t : topics) {
                btns[tagID] = new Button(this);
                btns[tagID].setId(tagID + 1);
                btns[tagID].setText(t.toString());
                btns[tagID].setOnClickListener(this);
                layout.addView(btns[tagID]);    //add button into the layout dynamically
                tagID++;
            }
            layout.post(new Runnable() {    //post a Runnable that call reLayout to layout object
                @Override
                public void run() {
                    reLayout();
                }
            });
        }



        //
        // Fragments
        //
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            if(checkIfOnline() == false) {
                // Create a new Fragment to be placed in the activity layout
                listFragment = new EventFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                listFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, listFragment).commit();
            }
            else {
                SupportMapFragment mapFragment = new SupportMapFragment();
                mapFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mapFragment).commit();
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if(mMap != null) {
            onMapReady(mMap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
           if(checkPositionPermission() == true) {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (loc == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else {
                userPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(this, "UserPos: " + userPosition, Toast.LENGTH_SHORT).show();
            }
            if(mMap != null) {
                updateMap();
            }
        }
    }

    private void updateMap() {
        if(batterySaferMode == false && checkPositionPermission() == true) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
    }

    private boolean checkPositionPermission() {
        // Check for permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    private boolean checkIfOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        }
        return ni.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Do stuff

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void handleNewLocation(Location location) {
        /*Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        /*MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");*/

        /*if(mMap != null) {
            //mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }*/
    }

    private void handleNewFilter(String filter) {

        // if fragmant is a map
        if(mMap != null) {
            LatLng latLng;

            // clear all Markers
            mMap.clear();
            markerMap.clear();

            // Add new ones according to filter
            //for (DummyContent.DummyItem item : events) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
            for (int i = 0; i < eventList.size(); i++){
                if (eventList.get(i).get("topic1").equals(filter) || filter.equals("Kein Filter")) {
                    String locName = eventList.get(i).get("location");
                    try {
                    addresses = geocoder.getFromLocationName(locName, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                    MarkerOptions o = new MarkerOptions()
                            .position(latLng)
                            .title(eventList.get(i).get("name"));

                    Marker marker = mMap.addMarker(o);
                    markerMap.put(marker,eventList.get(i));
                }
            }
        }
        // fragment is list
        else {
            listFragment.filter(filter);
        }
    }

    // FIXME Erster Button wird übergangen
    @TargetApi(17)
    protected void reLayout() {
        int totalWidth;
        int curWidth;
        int layoutPadX;
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.topics_cloud);
        int w = layout.getMeasuredWidth();  //get width of current layout
        totalWidth = 0;
        //layoutPadX = layout.getPaddingLeft() + layout.getPaddingRight();
        //w = w - layoutPadX;
        Button upBtn = null, leftBtn = btns[0];
        for (int i = 0; i < btns.length; i++) {
            //create a layout parameter first
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            curWidth = btns[i].getMeasuredWidth(); //get the width, beware of the caller site
            if (i > 0) {
                lp.addRule(RelativeLayout.END_OF, btns[i - 1].getId()); //add END_OF property by default.
                if (totalWidth + curWidth > w) {	//check if need to wrap
                    upBtn = leftBtn;
                    leftBtn = btns[i];
                    totalWidth = curWidth;
                    lp.removeRule(RelativeLayout.END_OF);   //remove the END_OF for wrap case
                } else {
                    totalWidth += curWidth;
                }
                if (upBtn != null)  //add below property for none-first "line"
                    lp.addRule(RelativeLayout.BELOW, upBtn.getId() + 1);
            } else {
                totalWidth += curWidth;
            }
            btns[i].setLayoutParams(lp);	//set layout parameter for button
        }
    }

    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        markerMap.clear();
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        LatLng latLng;

        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        for (int i = 0; i < eventList.size(); i++){
                String locName = eventList.get(i).get("location");
                try {
                    addresses = geocoder.getFromLocationName(locName, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                MarkerOptions o = new MarkerOptions()
                        .position(latLng)
                        .title(eventList.get(i).get("name"));

                Marker marker = mMap.addMarker(o);
                markerMap.put(marker,eventList.get(i));
        }

        // new GeocoderTask().execute("Comeniusstraße Dresden, Deutschland");
    }

    public void onFragmentInteraction(String id){

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        handleNewFilter(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        Button b = (Button)v;
        String buttonText = b.getText().toString();
        handleNewFilter(buttonText);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        dbc.switchBookmark(markerMap.get(marker).get("id"));
        Toast.makeText(getBaseContext(), markerMap.get(marker).get("name") + " gespeichert", Toast.LENGTH_LONG).show();
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            TextView tvTopic = ((TextView)myContentsView.findViewById(R.id.topic));
            TextView tvTime = ((TextView)myContentsView.findViewById(R.id.time));

            tvTitle.setText(marker.getTitle());
            tvTitle.setTextSize(15);

            HashMap<String, String> event = markerMap.get(marker);

            tvTopic.setText("~" + event.get("topic1"));
            tvTime.setText(event.get(EventsContract.EventsEntry.COLUMN_NAME_TIMESTART) + " Uhr");
            tvTime.setTextColor(Color.GRAY);

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    public class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            //googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                Log.d("Adresse", latLng.toString());
            }
        }
    }
}
