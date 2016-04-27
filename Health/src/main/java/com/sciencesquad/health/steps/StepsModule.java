package com.sciencesquad.health.steps;

import android.view.View;
import android.widget.TextView;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Coefficient;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;


/**
 * Steps Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */

public class StepsModule extends Module implements Coefficient {
    public static final String TAG = StepsModule.class.getSimpleName();
    private static final String REALMNAME = "steps.realm";

    private RealmContext<StepsModel> stepsRealm;

	/**
	 * Steps coefficient
	 */
	private double stepsCoefficient;

    // Display for steps
    private TextView num_steps;

    // Sensor manager
    //private SensorManager sensorManager;

    // Values to calculate number of steps
    private float prevY;
    private float currY; // YUM!
    private int numSteps;
    private int maxDelay;
    private int counterSteps;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
     * Going to pretend it does not throw an exception for now.
     */
    //public StepsModule() throws Exception {
    public StepsModule() {
        this.stepsRealm = new RealmContext<>();
        this.stepsRealm.init(BaseApp.app(), StepsModel.class, REALMNAME);

        // Initial values
        numSteps = 0;
        counterSteps = 0;
        maxDelay = 0;
    }

    /**
     * Calculates steps coefficient
	 * TODO: Implement!
     * @see Coefficient
     */
    @Override
    public void calculateCoefficient() {

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

    @Override
    public void onStart() {

		stepsCoefficient = 0;

    }

    @Override
    public void onStop() {

    }
}