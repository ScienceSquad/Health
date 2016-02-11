package com.sciencesquad.health.health.database;


import android.content.Context;

/**
 * Created by danielmiller on 2/11/16.
 *
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public abstract class AbstractDataContext {

    private static final String TAG = "Abstract Database Context";

    public abstract void init(Context context);
    public abstract void update();
    public abstract void query();
}