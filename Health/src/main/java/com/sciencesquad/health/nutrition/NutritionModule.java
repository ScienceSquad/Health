package com.sciencesquad.health.nutrition;

import android.util.Log;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.DataEmptyEvent;
import com.sciencesquad.health.data.DataFailureEvent;
import com.sciencesquad.health.data.DataUpdateEvent;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;

import org.threeten.bp.LocalDateTime;

import io.realm.RealmQuery;


/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class NutritionModule extends Module {
    private static final String TAG = NutritionModule.class.getSimpleName();
    private static final String REALMNAME = "nutrition.realm";

    private int calorieIntake;
    private boolean hadCaffeine;

    private RealmContext<NutritionModel> nutritionRealm;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
    */
    public NutritionModule() throws Exception {
        this.nutritionRealm = new RealmContext<>();
        this.nutritionRealm.init(BaseApplication.application(), NutritionModel.class, "nutrition.realm");

        this.subscribe(DataEmptyEvent.class, null, (DataEmptyEvent dataEmptyEvent) -> Log.d(TAG, "Some realm was empty."));
        this.subscribe(DataFailureEvent.class, this, (DataFailureEvent dataFailureEvent1) -> {
            Log.d(TAG, "Nutrition realm failed in Realm Transaction!");

        });
        this.subscribe(DataFailureEvent.class, null, (DataFailureEvent dataFailureEvent) -> {
            Log.d(TAG, "Data failed somewhere.");

        });
        this.subscribe(DataUpdateEvent.class, null, (DataUpdateEvent dataUpdateEvent) -> {
            Log.d(TAG, "There was an update to a realm.");

            // maybe use the key as the realm name?
            if (dataUpdateEvent.key().equals(REALMNAME)){
                Log.d(TAG, "Ignoring " + this.getClass().getSimpleName() + "'s own data update");
            }
            else {
                // do something about it.
            }
        });


        try {
            this.testNutritionModule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unit testing method for this module.
     * Also used to test Realm capabilities/ integration is correct.
    */
    protected void testNutritionModule() throws Exception {
        nutritionRealm.clear();
        NutritionModel testModel = new NutritionModel();
        testModel.setHadCaffeine(false);
        testModel.setCalorieIntake(50);
        testModel.setDate(LocalDateTime.now());
        nutritionRealm.add(testModel);
        RealmQuery<NutritionModel> testQuery = nutritionRealm.query();

        Log.d(TAG, "Checking initial value");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "testQuery first value:" + testQuery.findAll().first().getCalorieIntake());

        Log.d(TAG, "Adding mass values");
        boolean testCaffeine = false;
        for (int i = 1 ; i < 12; i++){
            NutritionModel testModelI = new NutritionModel();
            testModelI.setHadCaffeine(testCaffeine);
            testModelI.setCalorieIntake(i);
            testModelI.setDate(LocalDateTime.now());
            nutritionRealm.add(testModelI);
            testCaffeine = !testCaffeine;
        }
        Log.d(TAG, "Done adding");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "Grabbing random value: " + testQuery.findAll().get(4).getCalorieIntake());

        Log.d(TAG, "Setting random value to something different");
        Log.d(TAG, "Length: " + testQuery.findAll().size());
        nutritionRealm.updateRealmModel(4, d -> d.setCalorieIntake(500));
        Log.d(TAG, "Length: " + testQuery.findAll().size());

        Log.d(TAG, "Sanity checks");
        Log.d(TAG, "testQuery length where it's equal to 500: " + testQuery.equalTo("calorieIntake", 500).findAll().size());
        Log.d(TAG, "Grabbing changed value: " + testQuery.equalTo("calorieIntake", 500).findAll().get(0).getCalorieIntake());
        
        Log.d(TAG, "Clearing database");
        nutritionRealm.clear();
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());

        try {
            nutritionRealm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHadCaffeine(boolean hadCaffeine) {
        this.hadCaffeine = hadCaffeine;
    }

    public boolean isHadCaffeine() {
        return hadCaffeine;
    }

    public int getCalorieIntake() {
        return calorieIntake;
    }

    public void setCalorieIntake(int calorieIntake) {
        this.calorieIntake = calorieIntake;
    }
}
