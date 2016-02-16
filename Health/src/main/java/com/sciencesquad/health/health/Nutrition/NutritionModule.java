package com.sciencesquad.health.health.Nutrition;

import android.content.Context;
import android.util.Log;

import com.sciencesquad.health.Module;

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
        RealmList<RealmNutritionModel> testModelList = nutritionRealm.getNutritionModelList();
        testModelList.get(0).setCalorieIntake(50);
        nutritionRealm.update();
        nutritionRealm.query();
        RealmQuery<RealmNutritionModel> testQuery = nutritionRealm.getQueryNutrition();

        Log.d(TAG, "Checking initial value");
        System.out.println("testQuery length: " + testQuery.findAll().size());
        System.out.println("testQuery first value:" + testQuery.findAll().first().getCalorieIntake());

        for (int i = 1 ; i < 11; i++){
            testModelList.add(new RealmNutritionModel());
            testModelList.get(i).setCalorieIntake(i);
        }

        nutritionRealm.update();

        Log.d(TAG, "Adding a lot of values.");
        System.out.println("testQuery length: " + testQuery.findAll().size());

    }
}
