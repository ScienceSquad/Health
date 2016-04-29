package com.sciencesquad.health.workout;


import io.realm.annotations.Required;
import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.Required;
import java.util.Date;



public class ExerciseSetModel extends RealmObject{

    private Integer reps;               // number of repetitions
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

    public Integer getReps() { return reps; }

    public Integer getWeight() {
        return weight;
    }

    public Long getDuration() {
        return duration;
    }

    public Double getDistance() { return distance; }

    public ExerciseSetModel(int reps, int weight){
        this.reps = reps;
        this.weight = weight;
        this.duration = (long)0;
        this.distance = 0.0;
    }

    public ExerciseSetModel(){
        this.reps = 0;
        this.weight = 0;
        this.duration = (long)0;
        this.distance = 0.0;
    }

    @Override
    public String toString(){
        String str = "Reps: " + reps + "     Weight: " + weight;
        return str;
    }

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
