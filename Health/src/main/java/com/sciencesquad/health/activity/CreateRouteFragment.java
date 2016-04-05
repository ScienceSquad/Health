package com.sciencesquad.health.activity;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sciencesquad.health.R;

import java.util.ArrayList;
import java.util.List;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;


public class CreateRouteFragment extends Fragment implements
        ConnectionCallbacks,  OnConnectionFailedListener, LocationListener {
    public static final String TAG = CreateRouteFragment.class.getSimpleName();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int REQUEST_LOCATION_PERMISSION = 8;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView myTextViewDistance = null;

    List<LatLng> pointsLatLng = new ArrayList<>();
    List<Double> distances = new ArrayList<>();
    static double totalDistance = 0;
    LatLng lastLoc = null;

    boolean firstLoc = true; // used to ensure that only one starting marker is created.
    Marker currentPos = null; // used to display current position
    Circle accuracyCircle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_route, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.myTextViewDistance = (TextView) view.findViewById(R.id.textView_Distance);

        setUpMapIfNeeded();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)        // 2 seconds, in milliseconds
                .setFastestInterval(500); // Half second, in milliseconds
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(googleMap -> {
                        mMap = googleMap;
                    });

        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        // Sets the minimum distance needed to trigger a change in location
        // Based on GPS accuracy: the returned value from getAccuracy() is the 1sigma value of radius.
        float minDistResolution = location.getAccuracy()/2; //NORMAL RESOLUTION
        //float minDistResolution = location.getAccuracy()/20; //TEST RESOLUTION


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

            CircleOptions accuracyCircleOptions = new CircleOptions()
                    .center(latLng)
                    .radius((double) location.getAccuracy())
                    .strokeColor(0xaf00bfff)
                    .fillColor(0x3f00bfff);
            currentPos = mMap.addMarker(currentPosOptions);
            accuracyCircle = mMap.addCircle(accuracyCircleOptions);

            pointsLatLng.add(latLng);

            firstLoc = false;
        }

        if (computeDistanceBetween(lastLoc,latLng)<minDistResolution)
            return; //stops running the method if distance is inconsequential.

        lastLoc = latLng;

        pointsLatLng.add(latLng);
        if (pointsLatLng.size()>2) {
            double distanceDiff = computeDistanceBetween(pointsLatLng.get(pointsLatLng.size() - 2), latLng);
            distances.add(distanceDiff);
            totalDistance = totalDistance + distanceDiff;
            this.myTextViewDistance.setText("Distance: " +
                    String.format("%.1f",totalDistance) + " m");
        }



        currentPos.setPosition(latLng);
        accuracyCircle.setCenter(latLng);
        accuracyCircle.setRadius((double)location.getAccuracy());


        PolylineOptions polylineoptions = new PolylineOptions()
                .width(8)
                .color(Color.BLUE);

        mMap.addPolyline((polylineoptions)
                .addAll(pointsLatLng));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
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
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
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