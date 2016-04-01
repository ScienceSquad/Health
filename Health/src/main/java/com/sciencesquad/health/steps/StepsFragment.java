package com.sciencesquad.health.steps;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

/**
 * Below are packages that may or may not need to be used
 */
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * Above are packages that may or may not need to be used
 */

import android.app.Fragment;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.ui.Stopwatch;

import org.threeten.bp.Duration;

/**
 * This is the only way I know how to do this as of right now. Should this be in my Model instead?
 * Will write more in a few.
 */
public class StepsFragment extends Fragment implements SensorEventListener {

    private StepsModule stepsModule;
    private Stopwatch stopwatch;
    private SensorManager sensorManager;
    //private SensorEventListener sensorEventListener;
    int numSteps;
    int maxDelay;
    int counterSteps;
    double strideLength;
    private Button reset_steps;
    private TextView num_steps;
    private TextView stride_length;
    private TextView avg_speed;
    private TextView elapsed_time;
    boolean activityRunning;
    private static final String TAG = StepsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stepsModule = new StepsModule();
        numSteps = stepsModule.getNumSteps();
        counterSteps = stepsModule.getCounterSteps();
        stopwatch = new Stopwatch();

        registerEventListener(stepsModule.getMaxDelay());

        num_steps = (TextView) getView().findViewById(R.id.num_steps);
        num_steps.setText(String.valueOf(numSteps));

        strideLength = Math.round((0.415 * 1.8796)*100d)/100d;

        stride_length = (TextView) getView().findViewById(R.id.stride_length);
        stride_length.setText(String.valueOf(strideLength) + "m");

        elapsed_time = (TextView) getView().findViewById(R.id.elapsed_time);
        stopwatch.start();
        stopwatch.setInterval(51);
        stopwatch.setOnTimeChange(new Runnable() {
            public void run() {
                long millisElapsed = stopwatch.getMillisElapsed(true);
                Duration duration = stopwatch.getDurationForMode();
                elapsed_time.setText(stopwatch.getPrettyTime(duration, false) + "."
                        + stopwatch.getMilliString(duration) + "s");
            }
        });

        avg_speed = (TextView) getView().findViewById(R.id.avg_speed);
        avg_speed.setText(String.valueOf(strideLength) + "m/s");

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Spaghett!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // This IS used -- see content_steps.xml
        Button reset_steps = (Button) getView().findViewById(R.id.buttonReset);
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
        sensorManager = (SensorManager) BaseApp.app().getSystemService(Context.SENSOR_SERVICE);

        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Register the listener for this sensor in batch mode.
        // If the max delay is 0, events will be delivered in continuous mode without batching.
        final boolean sensorWorking = sensorManager.registerListener(
                sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, maxDelay);

        if (!sensorWorking) {
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
            if (counterSteps < 1) {
                // initial value
                counterSteps = (int) event.values[0];
                stepsModule.setCounterSteps(counterSteps);
            }
            // Calculate steps taken based on first counter value received.
            numSteps = (int) event.values[0] - counterSteps;
            num_steps.setText(String.valueOf(numSteps));
            stepsModule.setNumSteps(numSteps);
            Log.d(TAG, "Sensor picked up steps. Current step count: " + numSteps);
        }

        //
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Empty for the rest of time.
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getActivity(), "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
        // sensorManager.unregisterListener(this);
    }


    // Resets the steps
    public void resetSteps(View v) {
        stepsModule.resetSteps(v);
        numSteps = stepsModule.getNumSteps();
        stopwatch.reset();
        stopwatch.start();
        num_steps.setText(String.valueOf(numSteps));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (counterSteps < 1) {
            // initial value
            counterSteps = (int) event.values[0];
            stepsModule.setCounterSteps(counterSteps);
        }
        // Calculate steps taken based on first counter value received.
        numSteps = (int) event.values[0] - counterSteps;
        num_steps.setText(String.valueOf(numSteps));
        stepsModule.setNumSteps(numSteps);
        Log.d(TAG, "Sensor picked up steps. Current step count: " + numSteps);
    }

    //
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Empty for the rest of time.
    }
}