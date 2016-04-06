package com.sciencesquad.health.core;

import android.content.Context;

import java.util.Collection;

/**
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public interface DataContext<E> extends Collection<E>, AutoCloseable {

	/**
	 * Initialize the context.
	 *
	 * @param context the Android Context for any file access needed
	 * @param modelClass the class type of the Model used
	 * @param identifier the identifier, if needed for the Model
	 */
    void init(Context context, Class<E> modelClass, String identifier);
}