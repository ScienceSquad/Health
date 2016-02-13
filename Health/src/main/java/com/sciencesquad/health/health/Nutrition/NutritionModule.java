package com.sciencesquad.health.health.Nutrition;

import android.content.Context;

import com.sciencesquad.health.Module;

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

    }
}
