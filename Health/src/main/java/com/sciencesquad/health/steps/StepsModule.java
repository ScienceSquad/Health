package com.sciencesquad.health.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;


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

    // Values to calculate number of steps
    private float prevY;
    private float currY; // YUM!
    private int numSteps;
    private int maxDelay;
    private int counterSteps;

    // SeekBar fields
    private SeekBar seekBar;
    private int threshold;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
     */
    public StepsModule() throws Exception {
        this.stepsRealm = new RealmContext<>();
        this.stepsRealm.init(BaseApplication.application(), StepsModel.class, "steps.realm");
        onCreate();
        registerEventListener(maxDelay);

    }

    /**
     * Registers a listener for the Sensor to pick up User's steps.
     * @param maxdelay
     */

    private void registerEventListener(int maxdelay) {
        // BEGIN_INCLUDE(register)

        // Keep track of state so that the correct sensor type and batch delay can be set up when
        // the app is restored (for example on screen rotation).
        maxDelay = maxdelay;
        counterSteps = 0;

        // Get the default sensor for the sensor type from the SenorManager

        SensorManager sensorManager = (SensorManager) BaseApplication.application().getSystemService(Context.SENSOR_SERVICE);
        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Register the listener for this sensor in batch mode.
        // If the max delay is 0, events will be delivered in continuous mode without batching.
        final boolean sensorWorking = sensorManager.registerListener(
                sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, maxDelay);
        // END_INCLUDE(register)

        if (!sensorWorking){
            // something fucked up
            Log.e(TAG, "Sensor could not be initialized");
        }
        else {
            Log.d(TAG, "Counting enabled");
        }

    }


    /**
     * Event handler for StepCounter events.
     * It will log the steps as it picks up events.
     */

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        //
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Empty for now.
            if (counterSteps < 1) {
                // initial value
                counterSteps = (int) event.values[0];
            }

            // Calculate steps taken based on first counter value received.
            numSteps= (int) event.values[0] - counterSteps;
            Log.d(TAG, "Sensor picked up steps. Current step count: " + numSteps);

        }

        //
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Empty for the rest of time.
        }
    };

    /**
     * Initialisation helper of the module.
     * This is a very rough sketch of it as of now.
     */
    protected void onCreate() {
        // commented out to prevent null pointer exception
        /*setContentView(R.layout.steps_layout);

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
        tvSensitive.setText(String.valueOf(threshold));*/

        // Initial values
        prevY = 0;
        currY = 0;
        numSteps = 0;
        counterSteps = 0;
        maxDelay = 0;
    }

    // Self Explanatory
    public void resetSteps(View v) {
        numSteps = 0;
        counterSteps = 0;
        tvSteps.setText(String.valueOf(numSteps));
    }

    private SensorManager getSystemService(String string) {
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        return sm;
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
        // what does this do exactly?
        view = view;
    }

    private View findViewById(int button) {
        //int resID = getResources();
        //View button;
        return null;//button;
    }

}
