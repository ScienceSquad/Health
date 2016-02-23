package com.sciencesquad.health.Nutrition;

import com.sciencesquad.health.core.Module;

/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class NutritionModule extends Module {
    private static final String TAG = NutritionModule.class.getSimpleName();

    /*private RealmNutritionModule nutritionRealm;

    /**
     * Constructs the module itself.

    public NutritionModule() {
        this.nutritionRealm = new RealmNutritionModule();
        testNutritionModule();
    }

    /**
     * Unit testing method for this module.
     * Also used to test Realm capabilities/ integration is correct.

    protected void testNutritionModule() {
        nutritionRealm.clearRealm();
        nutritionRealm.getRealmList().clear();
        NutritionModel testModel = new NutritionModel();
        testModel.setCalorieIntake(50);
        Calendar rightNow = Calendar.getInstance();
        testModel.setDate(rightNow.getTime());
        nutritionRealm.getRealmList().add(testModel);
        nutritionRealm.update();
        nutritionRealm.query();
        RealmQuery<NutritionModel> testQuery = nutritionRealm.getQueryNotation();

        Log.d(TAG, "Checking initial value");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "testQuery first value:" + testQuery.findAll().first().getCalorieIntake());

        Log.d(TAG, "Adding mass values");
        for (int i = 1 ; i < 12; i++){
            NutritionModel testModelI = new NutritionModel();
            testModelI.setCalorieIntake(i);
            testModelI.setDate(rightNow.getTime());
            nutritionRealm.getRealmList().add(testModelI);
            nutritionRealm.update();
        }
        Log.d(TAG, "Done adding");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "Grabbing random value: " + testQuery.findAll().get(4).getCalorieIntake());

        Log.d(TAG, "Setting random value to something different");
        Log.d(TAG, "Length: " + testQuery.findAll().size());
        nutritionRealm.updateRealmModel(4, 500);
        Log.d(TAG, "Length: " + testQuery.findAll().size());

        Log.d(TAG, "Sanity checks");
        Log.d(TAG, "testQuery length where it's equal to 500: " + testQuery.equalTo("calorieIntake", 500).findAll().size());
        Log.d(TAG, "Grabbing changed value: " + testQuery.equalTo("calorieIntake", 500).findAll().get(0).getCalorieIntake());
        
        Log.d(TAG, "Clearing database");
        nutritionRealm.clearRealm();
        nutritionRealm.getRealmList().clear();
        nutritionRealm.update();
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());

        nutritionRealm.closeRealm();
    }*/
}
