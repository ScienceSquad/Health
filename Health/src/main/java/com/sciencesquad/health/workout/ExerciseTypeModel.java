package com.sciencesquad.health.workout;

import android.app.ListActivity;
import android.support.annotation.NonNull;



import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class ExerciseTypeModel {

    private String name;
    private String category;    // Strength or Cardio
    private String target;          // Part of body
    private Double maxDistance;
    private Integer maxWeight;
    private long maxDuration;

    public List<List<ExerciseSetModel>> instances = new ArrayList<>();

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

    @Override
    public String toString(){
        return getName();
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public ExerciseTypeModel(String name, String category, String target){
        setName(name);
        setCategory(category);
        setTarget(target);
        setMaxDistance(0.0);
        setMaxDuration((long) 0);
        setMaxWeight(0);
        Calendar rightNow = Calendar.getInstance();
        setDate(rightNow.getTime());
    }
}