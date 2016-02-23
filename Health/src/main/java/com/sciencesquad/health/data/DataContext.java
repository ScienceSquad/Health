package com.sciencesquad.health.data;

import android.content.Context;

/**
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public interface DataContext {
    void init(Context context, String identifier);
    void update();
    void query();
}