package com.sciencesquad.health.run;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.databinding.FragmentCreateRouteBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;


public class CreateRouteFragment extends BaseFragment implements
        ConnectionCallbacks,  OnConnectionFailedListener, OnMarkerDragListener, LocationListener {
    public static final String TAG = CreateRouteFragment.class.getSimpleName();

    @Override
    protected BaseFragment.Configuration getConfiguration() {
        return new Configuration(
                TAG, "CreateRouteFragment", R.drawable.ic_fitness_center_24dp,
                R.style.AppTheme_Run, R.layout.fragment_create_route
        );
    }

    // Our generated binding class is different...
    @Override @SuppressWarnings("unchecked")
    protected FragmentCreateRouteBinding xml() {
        return super.xml();
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int REQUEST_LOCATION_PERMISSION = 8;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Button newMarkerButton;
    private FloatingActionButton fabSave;
    private FloatingActionButton fabBack;
    private static TextView textViewDistance;

    private static double totalDistance = 0;

    private Polyline polyline;

    Realm realm = Realm.getDefaultInstance();

    List<LatLng> pointsLatLng = new ArrayList<>();
    private static List<Double> distances = new ArrayList<>();
    //static double totalDistance = 0;
    private LatLng latLng;

    boolean firstLoc = true; // used to ensure that only one starting marker is created.
    boolean firstMarker = true;

    PolylineOptions polylineoptions = new PolylineOptions()
            .width(8)
            .color(Color.BLUE);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the Toolbar
        xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

        textViewDistance = xml().textViewRouteDistance;

        newMarkerButton = xml().buttonNewMarker;
        fabBack = xml().backRouteFab;
        fabSave = xml().saveRouteFab;

        newMarkerButton.setOnClickListener(v -> {
            buttonClicked();
        });

        fabBack.setOnClickListener(v -> {
            //Links to RunFragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            new RunFragment().open(transaction, R.id.drawer_layout).commit();
        });

        fabSave.setOnClickListener(v -> {
            saveRouteToRealm();
            //TODO: Saved to Realm Confirmation Dialog Box
        });

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

    public void saveRouteToRealm() {
        realm.beginTransaction();
        CreatedRouteModel route = new CreatedRouteModel();
        route.setDistance(totalDistance);
        //TODO: Save route information
        realm.commitTransaction();
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
                        mMap.setOnMarkerDragListener(this);
                    });
        }
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        latLng = new LatLng(currentLatitude, currentLongitude);

        // Disconnects Location Services once location is found with sufficient accuracy.
        double accThreshold = 25; // Accuracy threshold in meters
        if (location.getAccuracy()<=accThreshold) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        // Creates starting marker
        if (firstLoc) {
            newStartingMarker(mMap, latLng);
            pointsLatLng.add(latLng);
            firstLoc = false;
        }
    }

    public void newStartingMarker(GoogleMap mMap, LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Starting Place");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    public void newMarker(GoogleMap mMap, LatLng lastLatLng) {
        latLng = new LatLng(lastLatLng.latitude, lastLatLng.longitude+0.001);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true);
        mMap.addMarker(options);
        pointsLatLng.add(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        if (firstMarker) {
            polyline = mMap.addPolyline(polylineoptions.addAll(pointsLatLng));
            firstMarker = false;
        } else {
            polyline.setPoints(pointsLatLng);
        }
    }

    // Button push triggers new marker (dragable)
    public void buttonClicked() {
        try {
            newMarker(mMap, pointsLatLng.get(pointsLatLng.size() - 1));
            distanceCalculate(latLng, pointsLatLng);
        } catch(ArrayIndexOutOfBoundsException e) {
            //Log the error
            Log.e(TAG, "No Location Data Received");
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        // Nothing, but necessary to have this here.
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        int id = Integer.valueOf(marker.getId().substring(1));
        LatLng dragPosition = marker.getPosition();
        pointsLatLng.set(id, dragPosition);
        if (!firstMarker) {
            polyline.setPoints(pointsLatLng);
            distanceCalculate(dragPosition, pointsLatLng);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        /*
        int id = Integer.valueOf(marker.getId().substring(1));
        LatLng dragPosition = marker.getPosition();
        pointsLatLng.set(id, dragPosition);
        if (!firstMarker) polyline.setPoints(pointsLatLng);
        */
    }

    //TODO: Adds location information for marker to location list

    //TODO: Calculate distance between new marker and previous

    // Calculates distance and adds to the distances list
    public static double distanceCalculate(LatLng latLng, List<LatLng> pointsLatLng) {
        totalDistance = 0;
        for (int i = 1; i<pointsLatLng.size(); i++) {
            totalDistance = totalDistance + computeDistanceBetween(pointsLatLng.get(i-1),
                    pointsLatLng.get(i));
        }
        textViewDistance.setText("Distance: " + String.format("%.1f", totalDistance) + " m");
        return totalDistance;
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