package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import com.sciencesquad.health.workout.ExerciseSetModel;
import com.sciencesquad.health.workout.ExerciseTypeModel;

import org.threeten.bp.Duration;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class RoutineModel extends RealmObject {

    private String name;
    private RealmList<ExerciseModel> exercises;
    /**
     * Calendar date where this model was created.
     */
    @Required private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public RealmList<ExerciseModel> getExercises() {
        return exercises;
    }

    public String getName(){
        return name;
    }

    public void setExercises(RealmList<ExerciseModel> exercises){
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
}
