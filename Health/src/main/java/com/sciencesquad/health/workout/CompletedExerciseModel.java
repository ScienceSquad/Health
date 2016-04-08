package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import com.sciencesquad.health.workout.ExerciseSetModel;
import com.sciencesquad.health.workout.ExerciseTypeModel;

import org.threeten.bp.Duration;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class CompletedExerciseModel extends RealmObject {

    @PrimaryKey
    private String exerciseName;
    private RealmList<ExerciseSetModel> sets;
    /**
     * Calendar date where this model was created.
     */
    @Required private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public RealmList<ExerciseSetModel> getSets() {
        return sets;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setSets(RealmList<ExerciseSetModel> sets){
        this.sets = sets;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    /**
    *   Returns one-rep-max from this exercise instance
    *
     */
    public Integer get1RMax(){
        Integer maxWeight = 0;
        Integer nextWeight = 0;
        Iterator<ExerciseSetModel> setModelIterator = sets.iterator();
        while(setModelIterator.hasNext()) {
            if((nextWeight = setModelIterator.next().getWeight()) > maxWeight){
                maxWeight = nextWeight;
            }
        }
        return maxWeight;
    }


    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
