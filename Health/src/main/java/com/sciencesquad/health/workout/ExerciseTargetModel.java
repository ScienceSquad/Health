package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by mrjohnson on 4/8/16.
 */
public class ExerciseTargetModel extends RealmObject {
    @PrimaryKey
    private String target;

    /**
     * Calendar date where this model was created.
     */
    @Required
    private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public String getTarget(){
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return getTarget();
    }
}
