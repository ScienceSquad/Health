package com.sciencesquad.health.nutrition;

import android.content.Context;
import android.util.Log;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import io.realm.RealmQuery;

import java.util.Calendar;

/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class NutritionModule extends Module {
    private static final String TAG = NutritionModule.class.getSimpleName();

    private RealmContext<NutritionModel> nutritionRealm;

    /**
     * Constructs the module itself.
    */

    // OVERLOADING

    public NutritionModule() throws Exception{
        Log.d(TAG, "Constructing Nutrition Module");
        this.nutritionRealm = new RealmContext<>();
        this.nutritionRealm.init(BaseApplication.application(), NutritionModel.class, "nutrition.realm");
    }

    public NutritionModule(Context context, String id) throws Exception {
        Log.d(TAG, "Constructing Nutrition Module");
        this.nutritionRealm = new RealmContext<>();
		this.nutritionRealm.init(context, NutritionModel.class, id);
    }

    /**
     * Unit testing method for this module.
     * Also used to test Realm capabilities/ integration is correct.
    */
    public boolean testNutritionModule() throws Exception {
        nutritionRealm.clear();
        NutritionModel testModel = new NutritionModel();
        testModel.setCalorieIntake(50);
        Calendar rightNow = Calendar.getInstance();
        testModel.setDate(rightNow.getTime());
        nutritionRealm.add(testModel);
        RealmQuery<NutritionModel> testQuery = nutritionRealm.query();

        Log.d(TAG, "Checking initial value");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "testQuery first value:" + testQuery.findAll().first().getCalorieIntake());

        Log.d(TAG, "Adding mass values");
        for (int i = 1 ; i < 12; i++){
            NutritionModel testModelI = new NutritionModel();
            testModelI.setCalorieIntake(i);
            testModelI.setDate(rightNow.getTime());
            nutritionRealm.add(testModelI);
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
