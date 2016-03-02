package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import org.threeten.bp.Duration;

import io.realm.RealmObject;
import io.realm.annotations.Required;

import java.util.Date;
import java.util.HashSet;


enum ExerciseKind {
    STRENGTH, CARDIO
}

public class ExerciseTypeModel extends RealmObject {

    private String name;
    private ExerciseKind kind;       // Strength or Cardio
    private String category;          // Part of body
    private Double maxDistance;
    private Integer maxWeight;
    private Duration maxDuration;

    /**
     * Calendar date where this model was created.
     */
    @Required private Date date;

    ExerciseTypeModel(String name, ExerciseKind kind, String category){
        this.name = name;
        this.kind = kind;
        this.category = category;
        this.maxDistance = 0.0;
        this.maxWeight = 0;
    }

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public String getName() {
        return name;
    }

    public ExerciseKind getType() {
        return kind;
    }

    public String getCategory() {
        return category;
    }

    public Integer get1RMax(){
        return maxWeight;
    }

    public Duration getMaxDuration(){
        return maxDuration;
    }

    public Double getMaxDistance() { return  maxDistance; }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ExerciseKind kind) {
        this.kind = kind;
    }

    public void set1RMax(int newMax){
        maxWeight = newMax;
    }

    public void setMaxDuration(Duration newMax){
        maxDuration = newMax;
    }

    public void setMaxDistance(Double newMax) { maxDistance = newMax; }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}