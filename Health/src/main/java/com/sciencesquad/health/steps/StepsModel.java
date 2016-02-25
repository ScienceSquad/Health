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
     * Steps walked on a given day.
     */
    // we can set up the primary key to something else if need be.
    @PrimaryKey
    private int stepCount;

    /**
     * Calendar date where this model was created.
     */
    @NonNull @Required
    private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
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
