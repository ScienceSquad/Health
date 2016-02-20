package com.sciencesquad.health.health.database;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

/**
 * Created by danielmiller on 2/11/16.
 *
 * This is an abstract database context specifically for a Realm database.
 * Everything a Realm Database must do goes here.
 *
 * Things must be implemented.
 * - Everything from AbstractDataContext
 * - Clearing a realm.
 * - Updating a single Realm model.
 * - Closing a realm.
 * - Returning a String representation of the Primary key in a Realm.
 *
 */
public abstract class RealmDataContext extends AbstractDataContext {

    private static final String TAG = "Realm Database Context";

    private String realmName;
    private Context realmContext;

    public abstract void clearRealm();
    public abstract void updateRealmModel(int index, int newKey);
    public abstract void closeRealm();
    public abstract String returnRealmKey();

    // GETTERS AND SETTERS
    public void setRealmName(String filename){
        this.realmName = filename;
    }
    public String getRealmName(){ return this.realmName; }

    public void setRealmContext(Context context){
        this.realmContext = context;
    }

    public Context getRealmContext(){
        return this.realmContext;
    }

}
