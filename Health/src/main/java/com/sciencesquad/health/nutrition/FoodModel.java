package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by danielmiller on 4/14/16.
 */
public class FoodModel extends RealmObject implements Comparable<FoodModel>{
    public static final String TAG = FoodModel.class.getSimpleName();

    @PrimaryKey
    private String name;

    private float calories;

    public FoodModel(){
        this.name = "";
        this.calories = 0;
    }

    public FoodModel(String name, float calories){
        this.name = name;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    @Override
    public int compareTo(FoodModel another) {
        return Math.round(this.calories - another.getCalories());
    }
}
