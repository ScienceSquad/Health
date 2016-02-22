package com.sciencesquad.health.data;

/**
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public interface DataContext {
    void init();
    void update();
    void query();
}