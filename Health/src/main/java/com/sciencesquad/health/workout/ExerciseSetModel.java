package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import org.threeten.bp.Duration;

import io.realm.RealmObject;
import io.realm.annotations.Required;

import java.util.Date;


public class ExerciseSetModel extends RealmObject {

    private int reps;               // number of repetitions
    private Integer weight;         // amount of weight
    private Duration length;        // length of time
    private Double distance;        // distance in miles
    /**
     * Calendar date where this model was created.
     */
    @Required private Date date;


    public ExerciseSetModel(int reps, Integer weight){
        this.reps = reps;
        this.weight = weight;
    }

    public ExerciseSetModel(int reps, Duration length){
        this.reps = reps;
        this.length = length;
    }

    public void setReps(int reps){ this.reps = reps; }

    public void setWeight(Integer weight){ this.weight = weight; }

    public void setLength(Duration length){
        this.length = length;
    }

    public void setDistance(Double distance) { this.distance = distance; }

    public int getReps() { return reps; }

    public Integer getWeight() {
        return weight;
    }

    public Duration getDuration() {
        return length;
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
