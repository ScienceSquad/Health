package com.sciencesquad.health.health.database;

import io.realm.Realm;

/**
 * Created by danielmiller on 2/11/16.
 *
 * This is an abstract database context specifically for a Realm database.
 * Everything a Realm Database must do goes here.
 */
public abstract class RealmDataBaseContext extends AbstractDataBaseContext {

    private static final String TAG = "Realm Database Context";

    private String realmName;

    public void setRealmName(String filename){
        this.realmName = filename;
    }

}
