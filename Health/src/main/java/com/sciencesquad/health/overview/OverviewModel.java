package com.sciencesquad.health.overview;


import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;

import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;


/**
 * Realm Model for Overview database.
 * Things that are relevant to store for the Overview Module go here.
 * TODO: Improve upon this garbage
 */
public class OverviewModel extends RealmObject {

    /**
     * Date and step count. Date should always be Primary Key
     */
    // we can set up the primary key to something else if need be.

    private Date date;

    private int number;

    //
    // GENERATED METHODS FOLLOW
    // DO NOT MODIFY -- REALM ONLY
    //

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }
}
