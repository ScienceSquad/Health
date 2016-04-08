package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by mrjohnson on 4/8/16.
 */
public class WorkoutScheduleModel extends RealmObject{


    /**
     * Calendar date where this model was created.
     */
    @Required
    private Date date;
    private Date startDate;
    private Boolean sunday;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private RealmList<RealmString> routineRotation;



    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public Boolean getSunday(){
        return this.sunday;
    }

    public void setSunday(Boolean isWorkoutDay){
        this.sunday = isWorkoutDay;
    }

    public Boolean getMonday(){
        return this.monday;
    }

    public void setMonday(Boolean isWorkoutDay){
        this.monday = isWorkoutDay;
    }

    public Boolean getTuesday(){
        return this.tuesday;
    }

    public void setTuesday(Boolean isWorkoutDay){
        this.tuesday = isWorkoutDay;
    }

    public Boolean getWednesday(){
        return this.wednesday;
    }

    public void setWednesday(Boolean isWorkoutDay){
        this.wednesday = isWorkoutDay;
    }

    public Boolean getThursday(){
        return this.thursday;
    }

    public void setThursday(Boolean isWorkoutDay){
        this.thursday = isWorkoutDay;
    }

    public Boolean getFriday(){
        return this.friday;
    }

    public void setFriday(Boolean isWorkoutDay){
        this.friday = isWorkoutDay;
    }

    public Boolean getSaturday(){
        return this.saturday;
    }

    public void setSaturday(Boolean isWorkoutDay){
        this.saturday = isWorkoutDay;
    }


    public RealmList<RealmString> getRoutineRotation(){
        return this.routineRotation;
    }

    public void setRoutineRotation(RealmList<RealmString> routineRotation){
        this.routineRotation = routineRotation;
    }

    public Date getStartDate(){
        return this.startDate;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }


    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

}