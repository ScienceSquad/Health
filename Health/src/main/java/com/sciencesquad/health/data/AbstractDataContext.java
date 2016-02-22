package com.sciencesquad.health.data;

/**
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public abstract class AbstractDataContext {
    private static final String TAG = AbstractDataContext.class.getSimpleName();

    public abstract void init();
    public abstract void update();
    public abstract void query();
}