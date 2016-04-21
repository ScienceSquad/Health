package com.sciencesquad.health.steps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.Stopwatch;
import com.sciencesquad.health.databinding.FragmentStepsBinding;

import org.threeten.bp.Duration;

/**
 * Below are packages that may or may not need to be used
 * <p>
 * Above are packages that may or may not need to be used
 */
/**
 * Above are packages that may or may not need to be used
 */

/**
 * This is the only way I know how to do this as of right now. Should this be in my Model instead?
 * Will write more in a few.
 */
public class StepsFragment extends BaseFragment implements SensorEventListener {
	public static final String TAG = StepsFragment.class.getSimpleName();

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

	private FloatingActionButton fab;
	private FloatingActionButton fab2;
	private FloatingActionButton fab3;
	private Boolean isFabOpen = false;
	private Animation fab_open;
	private Animation fab_close;
	private Animation rotate_forward;
	private Animation rotate_backward;
	private Animation rotation;
	private Animation pause_morph;
	private Animation play_morph;

	@Override
	protected BaseFragment.Configuration getConfiguration() {
		String stepsTag = StepsModule.TAG; // instantiates the Module...
		return new BaseFragment.Configuration(
				TAG, "Steps", R.drawable.ic_menu_steps,
				R.style.AppTheme_Steps, R.layout.fragment_steps
		);
	}

	// Our generated binding class is different...
	@Override @SuppressWarnings("unchecked")
	protected FragmentStepsBinding xml() {
		return super.xml();
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

		Drawable plus = ContextCompat.getDrawable(getActivity(), R.drawable.ic_plus);
		plus.setTint(Color.DKGRAY);
		Drawable reset = ContextCompat.getDrawable(getActivity(), R.drawable.ic_reset);
		reset.setTint(Color.DKGRAY);
		Drawable pause = ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause);
		pause.setTint(Color.DKGRAY);

		// Setup the Toolbar
		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		// Create text views
        num_steps = (TextView) view.findViewById(R.id.num_steps);
        stride_length = (TextView) view.findViewById(R.id.stride_length);
        elapsed_time = (TextView) view.findViewById(R.id.elapsed_time);
        avg_speed = (TextView) view.findViewById(R.id.avg_speed);

		// Create stuff needed to count steps
        stepsModule = new StepsModule();
        numSteps = stepsModule.getNumSteps();
        counterSteps = stepsModule.getCounterSteps();
        stopwatch = new Stopwatch();

		// Initiate step counter
        registerEventListener(stepsModule.getMaxDelay());

		// Populate
        num_steps.setText(String.valueOf(numSteps));
        strideLength = Math.round((0.415 * 1.8796)*100d)/100d;
        stride_length.setText(String.valueOf(strideLength) + "m");

		// Begin stopwatch
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

        avg_speed.setText(String.valueOf(strideLength) + "m/s");

		// Animate fabs
		fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.fab_open);
		fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.fab_close);
		rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.rotate_forward);
		rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.rotate_backward);
		rotation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.rotation);

		fab = xml().stepsFab;
		fab.setImageDrawable(plus);
		fab2 = xml().buttonReset;
		fab2.setImageDrawable(reset);
		fab2.setOnClickListener(this::resetSteps);
		fab2.hide();
		fab3 = xml().stepsFab3;
		fab3.setImageDrawable(pause);
		fab3.hide();

		fab.setOnClickListener(v -> {
			animateFab();
		});

		fab2.setOnClickListener(v -> {
			fab2.startAnimation(rotation);
		});

		fab3.setOnClickListener(v -> {
			fab3.startAnimation(rotation);
		});
    }

    /**
     * Registers a listener for the Sensor to pick up User's steps.
     * @param maxdelay
     */
    private void registerEventListener(int maxdelay) {
        // BEGIN_INCLUDE(start)

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
            BaseApp.app().display("Count sensor not available!", true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
        // if you stop the last listener, the hardware will stop detecting step events
        // sensorManager.unregisterListener(this);
    }


	/**
	 *
	 * @param v
	 */
	public void resetSteps(View v) {
        stepsModule.resetSteps(v);
        numSteps = stepsModule.getNumSteps();
        stopwatch.reset();
        stopwatch.start();
        num_steps.setText(String.valueOf(numSteps));
    }

	/**
	 *
	 * @param event
	 */
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

	/**
	 *
	 * @param sensor
	 * @param accuracy
	 */
	@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Empty for the rest of time.
    }

	/**
	 * Animations for when the overviewFab is pressed
	 */
	public void animateFab() {
		if (!isFabOpen) {
			fab.startAnimation(rotate_forward);
			fab2.startAnimation(fab_open);
			fab3.startAnimation(fab_open);
			fab2.show();
			fab3.show();
			fab2.setClickable(true);
			fab3.setClickable(true);
			isFabOpen = true;
		} else {
			fab.startAnimation(rotate_backward);
			fab2.startAnimation(fab_close);
			fab3.startAnimation(fab_close);
			fab2.hide();
			fab3.hide();
			fab2.setClickable(false);
			fab3.setClickable(false);
			isFabOpen = false;
		}
	}
}