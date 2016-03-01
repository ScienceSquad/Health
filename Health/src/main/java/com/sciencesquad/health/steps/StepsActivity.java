package com.sciencesquad.health.steps;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Below are packages that may or may not need to be used
 */
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
/**
 * Above are packages that may or may not need to be used
 */

import com.sciencesquad.health.R;
import com.sciencesquad.health.events.BaseActivity;

/**
 * This is the only way I know how to do this as of right now. Should this be in my Model instead?
 * Will write more in a few.
 */
public class StepsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Display fields for accelerometer
    private TextView tvX;
    private TextView tvY;
    private TextView tvZ;

    // Display field for sensitivity
    private TextView tvSensitive;

    // Display for steps
    private TextView tvSteps;

    // Reset button
    private Button buttonReset;

    // Sensor manager
    private SensorManager sensorManager;
    private float acceleration;

    // Values to calculate number of steps
    private float prevY;
    private float currY; // YUM!
    private int numSteps;

    // SeekBar fields
    private SeekBar seekBar;
    private int threshold;

    // Event handler for accelerometer events
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        //
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Gather values from accelerometer
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Fetch current y
            currY = y;

            // Measure if a step is taken
            if (Math.abs(currY - prevY) > threshold) {
                numSteps++;
                tvSteps.setText(String.valueOf(numSteps));
            }

            // Display the values
            tvX.setText(String.valueOf(x));
            tvY.setText(String.valueOf(y));
            tvZ.setText(String.valueOf(z));

            // Store previous y
            prevY = y;
        }

        //
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Empty
        }
    };

    // Enable accelerometer and register listener
    private void enableAccelerometerListening() {
        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
    }

    private OnSeekBarChangeListener seekBarListener =
            new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Change threshold
                    threshold = seekBar.getProgress();
                    // Write to text view
                    tvSensitive.setText(String.valueOf(threshold));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO
                }
            };

    @Override
    protected void OnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Attach objects to XML view
        tvX = (TextView) findViewById(R.id.tvX);
        tvY = (TextView) findViewById(R.id.tvY);
        tvZ = (TextView) findViewById(R.id.tvZ);

        // Attach step and sensitive view objects to XML
        tvSteps = (TextView) findViewById(R.id.tvSteps);
        tvSensitive = (TextView) findViewById(R.id.tvSensitive);

        // Attach reset button to XML
        buttonReset = (Button) findViewById(R.id.buttonReset);

        // Attach the seekBar to XML
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        // Set the values on the seekBar, threshold and threshold display
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        threshold = 10;
        tvSensitive.setText(String.valueOf(threshold));

        // Initial values
        prevY = 0;
        currY = 0;
        numSteps = 0;

        // Initialize acceleration values
        acceleration = 0.00f;

        // Enable the listener
        enableAccelerometerListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if present
        getMenuInflater().inflate(R.layout.activity_steps, menu);
        return true;
    }

    // Self Explanatory
    public void resetSteps(View v) {
        numSteps = 0;
        tvSteps.setText(String.valueOf(numSteps));
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_run) {
            // Handle the run action
        } else if (id == R.id.nav_sleep) {

        } else if (id == R.id.nav_steps) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
