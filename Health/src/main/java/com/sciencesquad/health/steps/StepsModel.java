package com.sciencesquad.health.steps;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;

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
     * Date this event occured
     */
    private Date date;

	/**
	 * When the steps interval began
	 */
	private Date start; // LocalTime

	/**
	 * When the steps interval ended
	 */
	private Date end; // LocalTime

	/**
	 * Number of steps recorded in event
	 */
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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
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
