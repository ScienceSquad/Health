package com.sciencesquad.health.run;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
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
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.core.util.TTSManager;
import com.sciencesquad.health.databinding.FragmentRunBinding;

import java.util.ArrayList;
import java.util.List;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static java.lang.System.currentTimeMillis;


public class RunFragment extends BaseFragment implements
        ConnectionCallbacks,  OnConnectionFailedListener, LocationListener {
    public static final String TAG = RunFragment.class.getSimpleName();

    @Override
    protected Configuration getConfiguration() {
        return new Configuration(
                TAG, "RunFragment", R.drawable.ic_fitness_center_24dp,
                R.style.AppTheme_Run, R.layout.fragment_run
        );
    }

    // Our generated binding class is different...
    @Override @SuppressWarnings("unchecked")
    protected FragmentRunBinding xml() {
        return super.xml();
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int REQUEST_LOCATION_PERMISSION = 8;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView myTextViewCalories = null;
    private TextView myTextViewDistance = null;
    private TextView myTextViewSpeed = null;

    private FloatingActionButton fabMarker;
    private FloatingActionButton fabStop;
    private Button runStartButton;

	List<LatLng> pointsLatLng = new ArrayList<>();
	List<Long> timeStamps = new ArrayList<>();
	List<Double> distances = new ArrayList<>();
	static double totalDistance = 0;
	static double totalCalories = 0;
	LatLng lastLoc = null;

	boolean firstLoc = true; // used to ensure that only one starting marker is created.
    boolean isRunStarted = false; // only starts on button press.
	Marker currentPos = null; // used to display current position
	Circle accuracyCircle = null;
    float currentAcc;

    MarkerOptions currentPosOptions = new MarkerOptions()
            .position(new LatLng(40,40))
            .title("Current Position");

    CircleOptions accuracyCircleOptions = new CircleOptions()
            .center(new LatLng(40,40))
            .radius((double) currentAcc)
            .strokeColor(0xaf00bfff)
            .fillColor(0x3f00bfff);

    PolylineOptions polylineoptions = new PolylineOptions()
            .width(8)
            .color(Color.BLUE);

    //int split = 800; // split distance in meters (NORMAL SPLIT)
    int split = 5; // TEST SPLIT
    int splitNumber = 1; // number of times user has traveled split distance

    private TTSManager ttsManager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TextToSpeech Initialization
        ttsManager = new TTSManager();
        ttsManager.init(getActivity());


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

        // Setup the Toolbar
        xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

        StaticPagerAdapter.install(xml().pager);
        xml().tabs.setupWithViewPager(xml().pager);

        // Create text views
        this.myTextViewCalories = (TextView) view.findViewById(R.id.textView_Calories);
        this.myTextViewDistance = (TextView) view.findViewById(R.id.textView_Distance);
        this.myTextViewSpeed = (TextView) view.findViewById(R.id.textView_Speed);

        fabMarker = xml().runFab;
        fabStop = xml().endRunFab;

        runStartButton = xml().buttonStartRun;

        fabMarker.setOnClickListener(v -> {
            //Links to CreateRouteFragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            new CreateRouteFragment().open(transaction, R.id.drawer_layout).commit();
        });

        fabStop.setOnClickListener(v -> {
            //TODO: Stop Run

            //TODO: Save run to Realm Object

        });

        runStartButton.setOnClickListener(v -> {
            buttonStartRunClicked();
        });
    }

    public void buttonStartRunClicked() {
        try {
            isRunStarted = true;
            startRun(lastLoc);
            //Makes start button disappear on click
            xml().buttonStartRun.setVisibility(View.GONE);
        } catch(Exception e) {
            //Log the error
            Log.e(TAG, "No Location Data Received");
        } catch(Error e2) {
            Log.e(TAG, "Error: " + e2.toString());
        }
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
        currentAcc = location.getAccuracy();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        // Sets the minimum distance needed to trigger a change in location
        // Based on GPS accuracy: the returned value from getAccuracy() is the 1sigma value of radius.
        //float minDistResolution = currentAcc/2; //NORMAL RESOLUTION
        float minDistResolution = currentAcc/8; //TEST RESOLUTION


        if (lastLoc==null) {
            lastLoc = latLng;
            currentPos = mMap.addMarker(currentPosOptions);
            accuracyCircle = mMap.addCircle(accuracyCircleOptions);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));


        accuracyCircle.setCenter(latLng);
        accuracyCircle.setRadius((double) currentAcc);

        if (!isRunStarted) return; // stops method if button has not been pressed.

        if (computeDistanceBetween(lastLoc,latLng)<minDistResolution)
            return; //stops running the method if distance is inconsequential.

        lastLoc = latLng;

        double speed = 0;
        pointsLatLng.add(latLng);
        timeStamps.add(currentTimeMillis());
        if (timeStamps.size()>2) {
            double distanceDiff = computeDistanceBetween(pointsLatLng.get(timeStamps.size() - 2), latLng);
            distances.add(distanceDiff);
            totalDistance = totalDistance + distanceDiff;
            double timeDiff = (timeStamps.get(timeStamps.size()-1)-timeStamps.get(timeStamps.size()-2))/1000; //time difference in seconds
            speed = distanceDiff/timeDiff; //calculates the speed since the last location update
            totalCalories = totalCalories + calorieBurn(speed,timeDiff,weightKG);
            this.myTextViewCalories.setText("Cal. Burned: " +
                    String.format("%.1f",totalCalories));
            this.myTextViewDistance.setText("Distance: " +
                    String.format("%.1f",totalDistance) + " m");
            this.myTextViewSpeed.setText("Pace: " +
                    String.format("%.1f", speed) + " m/s");
        }

        // TextToSpeech - Split Data
        if (totalDistance>split*splitNumber) {
            String textToSpeech = "Distance traveled, " + String.format("%.0f",totalDistance) +
                    " meters. Current pace is " + String.format("%.1f",speed) + "meters per second";
            this.ttsManager.initQueue(textToSpeech);
            splitNumber = splitNumber + 1;
        }

        currentPos.setPosition(latLng);

        mMap.addPolyline((polylineoptions)
                .addAll(pointsLatLng));
    }

    public void startRun(LatLng latLng) {
        if (firstLoc && isRunStarted) {
            newStartingMarker(mMap, latLng);

            pointsLatLng.add(latLng);
            timeStamps.add(currentTimeMillis());

            // TTS
            String textToSpeak = "Activity Started. I will let you know how you're doing every half mile. Enjoy your run!";
            this.ttsManager.initQueue(textToSpeak);

            firstLoc = false;
        }
    }

    public static void newStartingMarker(GoogleMap mMap, LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Starting Place");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    // DEFAULT WEIGHT (To be changed later)
    double weightKG = 70;

    public static double calorieBurn(double speed, double timeDiff, double weightKG) {
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

        return METS * weightKG * timeDiff/3600;
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
