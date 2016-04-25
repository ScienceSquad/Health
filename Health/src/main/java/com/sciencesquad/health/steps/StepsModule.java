package com.sciencesquad.health.steps;

import android.content.Context;
import android.databinding.ObservableField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

/**
 * Steps Module
 */
public class StepsModule extends Module implements SensorEventListener {
    public static final String TAG = StepsModule.class.getSimpleName();
    private static final String REALMNAME = "steps.realm";

    private RealmContext<StepsModel> stepsRealm;

    // Values to calculate number of steps
    private float prevY;
    private float currY; // YUM!
    private int numSteps;
    private int maxDelay;
    private int counterSteps;

    // The public bindings for the StepsFragment
    public ObservableField<String> numberSteps = new ObservableField<>("");
    public ObservableField<String> strideLength = new ObservableField<>("");
    public ObservableField<String> averageSpeed = new ObservableField<>("");

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
     * Going to pretend it does not throw an exception for now.
     */
    @Override
    public void onStart() {
        this.stepsRealm = new RealmContext<>();
        this.stepsRealm.init(BaseApp.app(), StepsModel.class, REALMNAME);

        // Initial values
        this.numSteps = 0;
        this.counterSteps = 0;
        this.maxDelay = 0;

        // Initiate step counter
        registerEventListener(getMaxDelay());
    }

    @Override
    public void onStop() {

    }

    public int getNumSteps() {
        return numSteps;
    }
    public void setNumSteps(int ns) {
        numSteps = ns;
    }

    public int getMaxDelay() {
        return maxDelay;
    }
    public void setMaxDelay(int md) {
        maxDelay = md;
    }

    public int getCounterSteps() {
        return counterSteps;
    }
    public void setCounterSteps(int cs) {
        counterSteps = cs;
    }

    public void writeStepsToRealm() {
        StepsModel model = new StepsModel();
        model.setStepCount(numSteps);
        model.setDate(DateTimeUtils.toDate(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        stepsRealm.add(model);
    }

    public void resetSteps(View v) {
        numSteps = 0;
        counterSteps = 0;
        //writeStepsToRealm();
    }

    /**
     * Registers a listener for the Sensor to pick up User's steps.
     * @param maxdelay
     */
    private void registerEventListener(int maxdelay) {
        // Keep track of state so that the correct sensor type and batch delay can be set up when
        // the app is restored (for example on screen rotation).
        maxDelay = maxdelay;
        counterSteps = 0;

        // Get the default sensor for the sensor type from the SenorManager
        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        SensorManager sensorManager = (SensorManager) BaseApp.app().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Register the listener for this sensor in batch mode.
        // If the max delay is 0, events will be delivered in continuous mode without batching.
        boolean q = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL, maxDelay);

        // Error cases!
        if (!q)
            Log.e(TAG, "Sensor could not be initialized");
        else Log.d(TAG, "Counting enabled");
    }

    /**
     * Update the visual bindings based on sensor data received.
     */
    private void updateBindings() {
        this.numberSteps.set("" + numSteps);
        this.strideLength.set((Math.round((0.415 * 1.8796)*100d)/100d) + "m");
        this.averageSpeed.set(strideLength + "m/s");
    }

    /**
     * @see SensorManager
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (counterSteps < 1) { // initial value
            counterSteps = (int) event.values[0];
            setCounterSteps(counterSteps);
        }

        // Calculate steps taken based on first counter value received.
        numSteps = (int) event.values[0] - counterSteps;
        this.updateBindings();
    }

    /**
     * @see SensorManager
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Does nothing.
    }
}