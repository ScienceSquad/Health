package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;



import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

import java.util.Date;
import java.util.HashSet;




public class ExerciseTypeModel extends RealmObject {

    private String name;
    private String category;    // Strength or Cardio
    private String target;          // Part of body
    private Double maxDistance;
    private Integer maxWeight;
    private long maxDuration;

    @Ignore
    private ExerciseKind enumb;


    /**
     * Calendar date where this model was created.
     */
    @Required private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //
    public void setEnumb(ExerciseKind kind) {
        setCategory(kind.toString());
    }

    public ExerciseKind getEnumb() {
        return ExerciseKind.valueOf(getCategory());
    }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public String getTarget() { return target; }

    public Integer getMaxWeight(){
        return maxWeight;
    }

    public long getMaxDuration(){
        return maxDuration;
    }

    public Double getMaxDistance() { return  maxDistance; }

    public void setName(String name) { this.name = name; }

    public void setCategory(String category) { this.category = category; }

    public void setTarget(String target) { this.target = target; }

    public void setMaxWeight(Integer newMax){ maxWeight = newMax; }

    public void setMaxDuration(long newMax){
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