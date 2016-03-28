package com.sciencesquad.health.activity;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sciencesquad.health.R;

import java.util.ArrayList;
import java.util.List;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.mapLibraryName;


public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private final static int REQUEST_LOCATION_PERMISSION = 8;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView myTextViewCalories = null;
    private TextView myTextViewDistance = null;
    private TextView myTextViewSpeed = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps); //old file
        setContentView(R.layout.maplayout); //new file

        //NEW CODE
        this.myTextViewCalories = (TextView)findViewById(R.id.textView_Calories);
        this.myTextViewDistance = (TextView)findViewById(R.id.textView_Distance);
        this.myTextViewSpeed = (TextView)findViewById(R.id.textView_Speed);

        //END CODE
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
                ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(googleMap -> {
                        mMap = googleMap;
                        setUpMap();
                    });

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Could probably delete this method.
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    List<LatLng> pointsLatLng = new ArrayList<>();
    List<Long> timeStamps = new ArrayList<>();
    List<Double> distances = new ArrayList<>();
    static double totalDistance = 0;
    static double totalCalories = 0;
    LatLng lastLoc = null;


    boolean firstLoc = true; // used to ensure that only one starting marker is created.
    Marker currentPos = null; // used to display current position


    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        // Sets the minimum distance needed to trigger a change in location
        // Based on GPS accuracy: the returned value from getAccuracy() is the 1sigma value of radius.
        float minDistResolution = location.getAccuracy()/2; //NORMAL RESOLUTION


        if (lastLoc==null) {
            lastLoc = latLng;
        }

        // Creates a marker at the starting point.

        if (firstLoc) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("Starting Place");
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

            MarkerOptions currentPosOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Current Position");
            currentPos = mMap.addMarker(currentPosOptions);

            pointsLatLng.add(latLng);
            timeStamps.add(currentTimeMillis());

            firstLoc = false;
        }

        if (computeDistanceBetween(lastLoc,latLng)<minDistResolution) {
            return; //stops running the method if distance is inconsequential.
        }
        lastLoc = latLng;

        pointsLatLng.add(latLng);
        timeStamps.add(currentTimeMillis());
        if (timeStamps.size()>2) {
            double distanceDiff = computeDistanceBetween(pointsLatLng.get(timeStamps.size() - 2), latLng);
            distances.add(distanceDiff);
            totalDistance = totalDistance + distanceDiff;
            Log.i(TAG, "Distance traveled" + String.valueOf(totalDistance));
            double timeDiff = (timeStamps.get(timeStamps.size()-1)-timeStamps.get(timeStamps.size()-2))/1000; //time difference in seconds
            double speed = distanceDiff/timeDiff; //calculates the speed since the last location update
            totalCalories = totalCalories + calorieBurn(speed,timeDiff,weightKG);
            //NEW CODE
            this.myTextViewCalories.setText("Calories Burned: " + totalCalories);
            this.myTextViewDistance.setText("Distance: " + totalDistance);
            this.myTextViewSpeed.setText("Pace: " + speed);
            //END CODE
            Log.i(TAG, "Burned: " + String.valueOf(totalCalories));
            Log.i(TAG, "Time since last location update:" + String.valueOf(timeDiff));
        }



        currentPos.setPosition(latLng);

        PolylineOptions polylineoptions = new PolylineOptions()
                .width(8)
                .color(Color.BLUE);

        mMap.addPolyline((polylineoptions)
                .addAll(pointsLatLng));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    // DEFAULT WEIGHT (To be changed later)
    double weightKG = 70;

    public double calorieBurn(double speed, double timeDiff, double weightKG) {
        // METS List (Metric for the exertion for each exercise)
        double METS = 0;
        double speedMPH = speed*2.23694; //converting speed from m/s to MPH
        if (speedMPH>1)METS = 2.0;
        if (speedMPH>2)METS=2.8;
        if (speedMPH>3)METS=4.5;
        if (speedMPH>4)METS=6;
        if (speedMPH>5)METS=8.3;
        if (speedMPH>5.2)METS=9;
        if (speedMPH>6)METS=9.8;
        if (speedMPH>6.7)METS=10.5;
        if (speedMPH>7)METS=11;
        if (speedMPH>7.5)METS=11.5;
        if (speedMPH>8)METS=11.8;
        if (speedMPH>8.6)METS=12.3;
        if (speedMPH>9)METS=12.8;
        if (speedMPH>10)METS=14.5;
        if (speedMPH>11)METS=16;
        if (speedMPH>12)METS=19;
        if (speedMPH>13)METS=19.8;
        if (speedMPH>14)METS=23;
        if (speedMPH>15)METS=25;
        if (speedMPH>20)METS=0;

        double calories = METS * weightKG * timeDiff/3600;
        return calories;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                //This if statement may be unnecessary.
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Do what you wanted to do with the permissions
                } else {
                    // Do something for when permission is denied by the user
                }
            }
            default:
                return;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }
}