package com.sciencesquad.health.nutrition;

import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.Required;
import java.util.Date;

/**
 * Realm Model for Nutrition database.
 * Things that are relevant to store for the Nutrition Module go here.
 */
public class NutritionModel extends RealmObject {
	/**
	 * Data that is relevant to store for Nutrition Purposes.
	 */
	private float calorieIntake;
	private boolean hadCaffeine;

	private NutrientModel nutrientModel;
	private VitaminModel vitaminModel;
	private MineralModel mineralModel;

	/**
	 * Calendar date where this model was created.
	 */
	@Required
	private Date date;
	private String dateString;

	//
	// GENERATED METHODS FOLLOW
	// DO NOT MODIFY -- REALM ONLY
	//

	public float getCalorieIntake() {
		return calorieIntake;
	}

	public void setCalorieIntake(float calorieIntake) {
		this.calorieIntake = calorieIntake;
	}

	@NonNull
	public Date getDate() {
		return date;
	}

	public void setDate(@NonNull Date date) {
		this.date = date;
	}

	public boolean isHadCaffeine() {
		return hadCaffeine;
	}

	public void setHadCaffeine(boolean hadCaffeine) {
		this.hadCaffeine = hadCaffeine;
	}

	@NonNull
	public MineralModel getMineralModel() {
		return mineralModel;
	}

	public void setMineralModel(@NonNull MineralModel mineralModel) {
		this.mineralModel = mineralModel;
	}

	@NonNull
	public VitaminModel getVitaminModel() {
		return vitaminModel;
	}


	public void setVitaminModel(@NonNull VitaminModel vitaminModel) {
		this.vitaminModel = vitaminModel;
	}

	@NonNull
	public NutrientModel getNutrientModel() {
		return nutrientModel;
	}

	public void setNutrientModel(@NonNull NutrientModel nutrientModel) {
		this.nutrientModel = nutrientModel;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
}
