package com.sciencesquad.health.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.DataEmptyEvent;
import com.sciencesquad.health.data.DataFailureEvent;
import com.sciencesquad.health.data.DataUpdateEvent;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.steps.StepsModel;

import io.realm.RealmQuery;


/**
 * Steps Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */

public class StepsModule extends Module {
    private static final String TAG = StepsModule.class.getSimpleName();
    private static final String REALMNAME = "steps.realm";

    private RealmContext<StepsModel> stepsRealm;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
     */
    public StepsModule() throws Exception {
        this.stepsRealm = new RealmContext<>();
        this.stepsRealm.init(BaseApplication.application(), StepsModel.class, "steps.realm");

        try {
            this.testStepsModule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void testStepsModule() throws Exception {

    }

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

    private SensorManager getSystemService(String string) {
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        return sm;
    }

    // Enable accelerometer and register listener
    private void enableAccelerometerListening() {
        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
    }

    private SeekBar.OnSeekBarChangeListener seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
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

    private void setContentView(int view) {
        view = view;
    }

    private View findViewById(int button) {
        //int resID = getResources();
        //View button;
        return null;//button;
    }

    protected void onCreate(Bundle savedInstanceState) {

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

    // Self Explanatory
    public void resetSteps(View v) {
        numSteps = 0;
        tvSteps.setText(String.valueOf(numSteps));
    }

}
