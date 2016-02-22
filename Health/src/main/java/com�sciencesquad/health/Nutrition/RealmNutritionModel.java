package com.sciencesquad.health.nutrition;

import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

/**
 * Realm Model for Nutrition database.
 * Things that are relevant to store for the Nutrition Module go here.
 */
public class RealmNutritionModel extends RealmObject {
    private static final String TAG = RealmNutritionModel.class.getSimpleName();

    /**
     * Nutrition Model data summary.
     * - calorieIntake: calories taken in on a certain day.
     * - date: Calendar date where this model was created.
     *  Note: Particularly useful when compiling data in history.
     */

    // we can set up the primary key to something else if need be.
    @PrimaryKey
    private int calorieIntake;

    // other relevant data
    @NonNull
    private Date date;

    public RealmNutritionModel(){
        this.calorieIntake = 0;
        Calendar rightNow = Calendar.getInstance();
        this.date = rightNow.getTime();
    }

    public int getCalorieIntake(){ return calorieIntake; }

    public void setCalorieIntake(int calorieIntake){ this.calorieIntake = calorieIntake; }


    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

}
