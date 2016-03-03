package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.Required;

import java.util.Date;


public class ExerciseSetModel extends RealmObject {

    private int reps;               // number of repetitions
    private Integer weight;         // amount of weight
    private Long duration;          // length of time
    private Double distance;        // distance in miles
    /**
     * Calendar date where this model was created.
     */
    @Required private Date date;

    public void setReps(int reps){ this.reps = reps; }

    public void setWeight(Integer weight){ this.weight = weight; }

    public void setDuration(Long duration){ this.duration = duration; }

    public void setDistance(Double distance) { this.distance = distance; }

    public int getReps() { return reps; }

    public Integer getWeight() {
        return weight;
    }

    public Long getDuration() {
        return duration;
    }

    public Double getDistance() { return distance; }

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
