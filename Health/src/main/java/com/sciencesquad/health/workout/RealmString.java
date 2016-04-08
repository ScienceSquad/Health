package com.sciencesquad.health.workout;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by mrjohnson on 4/7/16.
 */
public class RealmString extends RealmObject {

    private String name;

    /**
     * Calendar date where this model was created.
     */
    @Required
    private Date date;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    /*
    public RealmString(){
        Calendar rightNow = Calendar.getInstance();
        this.setDate(rightNow.getTime());
    }
    */


    public String getName(){
        return name;
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

}
