package com.sciencesquad.health.health.Nutrition;

import android.content.Context;
import android.util.Log;

import com.sciencesquad.health.Module;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

/**
 * Created by danielmiller on 2/13/16.
 *
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class NutritionModule extends Module {

    private static final String TAG = "Nutrition Module";

    RealmNutritionModule nutritionRealm ;

    public NutritionModule(Context context){
        this.nutritionRealm = new RealmNutritionModule(context);
        testNutritionModule();

    }

    protected void testNutritionModule(){
        nutritionRealm.clearRealm();
        nutritionRealm.getNutritionModelList().clear();
        RealmNutritionModel testModel = new RealmNutritionModel();
        testModel.setCalorieIntake(50);
        nutritionRealm.getNutritionModelList().add(testModel);
        nutritionRealm.update();
        nutritionRealm.query();
        RealmQuery<RealmNutritionModel> testQuery = nutritionRealm.getQueryNutrition();

        Log.d(TAG, "Checking initial value");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "testQuery first value:" + testQuery.findAll().first().getCalorieIntake());

        Log.d(TAG, "Adding mass values");
        for (int i = 1 ; i < 12; i++){
            RealmNutritionModel testModelI = new RealmNutritionModel();
            testModelI.setCalorieIntake(i);
            nutritionRealm.getNutritionModelList().add(testModelI);
            nutritionRealm.update();
        }
        Log.d(TAG, "Done adding");
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());
        Log.d(TAG, "Grabbing random value: " + testQuery.findAll().get(4).getCalorieIntake());

        Log.d(TAG, "Setting random value to something different");
        Log.d(TAG, "Length: " + testQuery.findAll().size());
        nutritionRealm.updateRealmNutritionModel(4, 500);
        Log.d(TAG, "Length: " + testQuery.findAll().size());

        Log.d(TAG, "Sanity checks");
        Log.d(TAG, "testQuery length where it's equal to 500: " + testQuery.equalTo("calorieIntake", 500).findAll().size());
        Log.d(TAG, "Grabbing changed value: " + testQuery.equalTo("calorieIntake", 500).findAll().get(0).getCalorieIntake());
        
        Log.d(TAG, "Clearing database");
        nutritionRealm.clearRealm();
        nutritionRealm.getNutritionModelList().clear();
        nutritionRealm.update();
        Log.d(TAG, "testQuery length: " + testQuery.findAll().size());


    }
}
