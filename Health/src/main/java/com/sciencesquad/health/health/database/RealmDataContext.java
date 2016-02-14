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
 */
public abstract class RealmDataContext extends AbstractDataContext {

    private static final String TAG = "Realm Database Context";

    private String realmName;
    private Context realmContext;


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
