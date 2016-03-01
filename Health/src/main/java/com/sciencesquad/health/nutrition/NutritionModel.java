package com.sciencesquad.health.nutrition;

import io.realm.annotations.PrimaryKey;
import android.support.annotation.NonNull;
import io.realm.RealmObject;
import org.threeten.bp.LocalDateTime;

/**
 * Realm Model for Nutrition database.
 * Things that are relevant to store for the Nutrition Module go here.
 */
public class NutritionModel extends RealmObject {

	/**
	 * Calories taken in on a certain day.
	 */
	private int calorieIntake;
	private boolean hadCaffeine;

	/**
	 * Calendar date where this model was created.
	 */
	@PrimaryKey
	private LocalDateTime date;

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

	public void setHadCaffeine(boolean caffeineCondition){
		this.hadCaffeine = caffeineCondition;
	}

	public boolean getHadCaffeine(){
		return this.hadCaffeine;
	}

	@NonNull
	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(@NonNull LocalDateTime date) {
		this.date = date;
	}
}
