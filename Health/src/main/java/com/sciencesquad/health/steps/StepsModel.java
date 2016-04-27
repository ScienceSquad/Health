package com.sciencesquad.health.steps;

import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;


/**
 * Realm Model for Steps database.
 * Things that are relevant to store for the Steps Module go here.
 */
public class StepsModel extends RealmObject {

    /**
     * Date and step count. Date should always be Primary Key
     */

    private Date date;

    private int stepCount;

	/**
	 * Steps coefficient for overview module
	 */
	private double stepsCoefficient;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public int getStepCount() {
        return this.stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

	public double getStepsCoefficient() { return this.stepsCoefficient; }

	public void setStepsCoefficient(double stepsCoefficient) {
		this.stepsCoefficient = stepsCoefficient; }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
