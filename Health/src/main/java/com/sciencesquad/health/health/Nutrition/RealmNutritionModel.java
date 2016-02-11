package com.sciencesquad.health.health.Nutrition;


import io.realm.RealmObject;

/**
 * Created by danielmiller on 2/11/16.
 *
 * Realm Model for Nutrition database.
 * Things that are relevant to store for Nutrition go here.
 */
public class RealmNutritionModel extends RealmObject {
    private int calorieIntake;

    public void setCalorieIntake(int calorieIntake){
        this.calorieIntake = calorieIntake;
    }
}
