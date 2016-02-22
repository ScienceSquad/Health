package com.sciencesquad.health.steps;

import android.util.Log;
import com.sciencesquad.health.Module;
import io.realm.RealmQuery;

/**
 * Created by colin on 2/19/16.
 */

/**
 * Steps Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */

public class StepsModule extends Module {
    private static final String TAG = StepsModule.class.getSimpleName();

    private RealmStepsModule stepsRealm;

    /**
     * Constructs the module itself.
     */
    public StepsModule() {
        this.stepsRealm = new RealmStepsModule();
        testStepsModule();
    }

    /**
     * Unit testing method for this module.
     * Also used to test Realm capabilities/ integration is correct.
     */

    // THIS IS (PROBABLY) WRONG! I am just setting up the ground work

    protected void testStepsModule() {
        stepsRealm.clearRealm();
        stepsRealm.getRealmList().clear();
        StepsModel testModel = new StepsModel();
        testModel.setStepCount(50);
        stepsRealm.getRealmList().add(testModel);
        stepsRealm.update();
        stepsRealm.query();
        RealmQuery<StepsModel> testQuery = stepsRealm.getQuerySteps();

        Log.d(TAG, "Checking initial value");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "testQuery first value:" + testQuery.findAll().first().getStepCount());

        Log.d(TAG, "Adding mass values");
        for (int i = 1 ; i < 12; i++){
            StepsModel testModelI = new StepsModel();
            testModelI.setStepCount(i);
            stepsRealm.getRealmList().add(testModelI);
            stepsRealm.update();
        }
        Log.d(TAG, "Done adding");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "Grabbing random value: " + testQuery.findAll().get(4).getStepCount());

        Log.d(TAG, "Setting random value to something different");
        Log.d(TAG, "Length: " + testQuery.findAll().size());
        stepsRealm.updateRealmModel(4, 500);
        Log.d(TAG, "Length: " + testQuery.findAll().size());

        Log.d(TAG, "Sanity checks");
        Log.d(TAG, "testQuery length where it's equal to 500: " + testQuery.equalTo("calorieIntake", 500).findAll().size());
        Log.d(TAG, "Grabbing changed value: " + testQuery.equalTo("calorieIntake", 500).findAll().get(0).getStepCount());

        Log.d(TAG, "Clearing database");
        stepsRealm.clearRealm();
        stepsRealm.getRealmList().clear();
        stepsRealm.update();
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());

        stepsRealm.closeRealm();
    }
}
