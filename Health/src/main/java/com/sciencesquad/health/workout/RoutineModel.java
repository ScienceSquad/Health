package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;


public class RoutineModel extends RealmObject {
    @PrimaryKey
    private String name;
    private RealmList<RealmString> exercises;
    /**
     * Calendar date where this model was created.
     */
    @Required
    private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public RealmList<RealmString> getExercises() {
        return exercises;
    }

    public String getName(){
        return name;
    }

    public void setExercises(RealmList<RealmString> exercises){
        this.exercises = exercises;
    }

    public void setName(String name) {
        this.name = name;
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
        return getName();
    }

    /*
    public RoutineModel(String name){
        this.name = name;
        exercises = new ArrayList<ExerciseModel>();
    }

    public RoutineModel(){

    }
    */
}
