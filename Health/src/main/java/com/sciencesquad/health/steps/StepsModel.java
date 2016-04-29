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
    // we can set up the primary key to something else if need be.

    private Date date;

    private int stepCount;

    //
    // GENERATED METHODS FOLLOW
    //

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
