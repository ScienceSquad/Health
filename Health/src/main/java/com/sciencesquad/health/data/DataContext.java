package com.sciencesquad.health.data;

import android.content.Context;
import com.sciencesquad.health.events.Event;
import org.immutables.value.Value;

import java.util.Collection;

/**
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public interface DataContext<E> extends Collection<E>, AutoCloseable {

	/**
	 * Event for clearing a DataContext (aka the database has been wiped).
	 */
	@Value.Immutable @Event.EventType
	interface DataEmpty extends Event {
		String realmName();
	}

	/**
	 * Event for updating a DataContext in any abstract way.
	 */
	@Value.Immutable @Event.EventType
	interface DataUpdate extends Event {
		String key();
	}

	/**
	 * Event in case of a data failure.
	 */
	@Value.Immutable @Event.EventType
	interface DataFailure extends Event {
		String operation();
	}

	/**
	 * Initialize the context.
	 *
	 * @param context the Android Context for any file access needed
	 * @param modelClass the class type of the Model used
	 * @param identifier the identifier, if needed for the Model
	 */
    void init(Context context, Class<E> modelClass, String identifier);
}