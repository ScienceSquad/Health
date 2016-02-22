package com.sciencesquad.health.nutrition;

import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;

/**
 * Realm Model for Nutrition database.
 * Things that are relevant to store for the Nutrition Module go here.
 */
public class NutritionModel extends RealmObject {

	/**
	 * calories taken in on a certain day.
	 */
    // we can set up the primary key to something else if need be.
    @PrimaryKey
    private int calorieIntake;

	/**
	 * Calendar date where this model was created.
	 */
	@NonNull @Required
    private Date date;

	//
	// GENERATED METHODS FOLLOW
	// DO NOT MODIFY -- REALM ONLY
	//

	public int getCalorieIntake() {
		return calorieIntake;
	}

	public void setCalorieIntake(int calorieIntake) {
		this.calorieIntake = calorieIntake;
	}

	@NonNull
	public Date getDate() {
		return date;
	}

	public void setDate(@NonNull Date date) {
		this.date = date;
	}
}
