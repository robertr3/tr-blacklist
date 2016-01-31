package com.silentdynamics.student.blacklist;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.silentdynamics.student.blacklist.dummy.DummyContent;

import java.util.List;
import java.util.Random;

public class FindEventsActivity extends FragmentActivity implements OnMapReadyCallback, EventFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener,
        View.OnClickListener {
    private static final String TAG = FindEventsActivity.class.getSimpleName();

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient = null;
    LatLng userPosition = new LatLng(-34, 151);
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationRequest mLocationRequest;

    EventFragment listFragment;

    List<DummyContent.DummyItem> events;
    Button[] btns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_events);
        Log.d(TAG, "find Events open");

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


        // Get the dummy events
        events = DummyContent.ITEMS;
        Log.d(TAG, "1. " + events.get(0).topic);

        // Create the spinner
        Spinner spinner = (Spinner) findViewById(R.id.topics_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.topics_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        if(spinner != null) {
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }

        // Create the TagCloud
        //RelativeLayout tagCloud = (RelativeLayout) findViewById(R.id.topics_cloud);



        //set the properties for button
        /*for(DummyContent.DummyItem event : DummyContent.ITEMS) {
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText(event.topic);
            btnTag.setId(tagID);
            tagID++;

            //add button to the layout
            tagCloud.addView(btnTag);
        }*/

        String[] topics = getResources().getStringArray(R.array.topics_array);
        btns = new Button[topics.length];

        int tagID = 0;

      /*  RelativeLayout layout = (RelativeLayout) findViewById(R.id.topics_cloud);
        if(layout != null) {
            for (Object t : topics) {
                btns[tagID] = new Button(this);
                btns[tagID].setId(tagID);
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
        }*/



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

            // TODO get batterySaferMode true/false
            int random = new Random().nextInt(2);
            if(random == 0) {
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
        Location location = null;

        // Check for permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else {
                //userPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                handleNewLocation(location);
            }
        }
        else {
            Log.d("Permission", " not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        /*//PackageManager pm = this.getPackageManager();
        //int hasPerm = pm.checkPermission(
               // Manifest.permission.ACCESS_FINE_LOCATION,
               // this.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "granted");

        }
        else {
            Log.d("Permission", " not granted");

        }*/
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
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");

        if(mMap != null) {
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        for(DummyContent.DummyItem item : events) {
            latLng = new LatLng(item.lat, item.lng);

            MarkerOptions o = new MarkerOptions()
                    .position(latLng)
                    .title(item.content);

            if(mMap != null) {
                mMap.addMarker(o);
            }
        }
    }

    private void handleNewFilter(String filter) {

        // if fragmant is a map
        if(mMap != null) {
            LatLng latLng;

            // clear all Markers
            mMap.clear();

            // Add new ones according to filter
            for (DummyContent.DummyItem item : events) {
                if (item.topic.equals(filter) || filter.equals("Kein Filter")) {
                    latLng = new LatLng(item.lat, item.lng);

                    MarkerOptions o = new MarkerOptions()
                            .position(latLng)
                            .title(item.content);

                    mMap.addMarker(o);

                }
            }
        }
        // fragment is list
        else {
            listFragment.filter(filter);
        }
    }

/*    // FIXME Erster Button wird übergangen
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
            Log.d(TAG, "ID "+ i +": " + Integer.toString(btns[i].getId()));
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
    } */

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(userPosition).title("Marker at user position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
    }

    public void onFragmentInteraction(String id){

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
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
}
