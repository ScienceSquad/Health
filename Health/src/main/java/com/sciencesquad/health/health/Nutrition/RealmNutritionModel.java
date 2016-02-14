package com.sciencesquad.health.health.Nutrition;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by danielmiller on 2/11/16.
 *
 * Realm Model for Nutrition database.
 * Things that are relevant to store for Nutrition go here.
 */
public class RealmNutritionModel extends RealmObject {

    private static final String TAG = "Nutrition Realm Object";

    // we can set up the primary key to something else if need be.
    @PrimaryKey
    private int calorieIntake;

    public RealmNutritionModel(){
        this.calorieIntake = 0;
    }

    public int getCalorieIntake(){ return calorieIntake; }

    public void setCalorieIntake(int calorieIntake){
        this.calorieIntake = calorieIntake;
    }
}
