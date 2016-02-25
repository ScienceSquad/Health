package com.sciencesquad.health.data;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sciencesquad.health.events.Event;
import org.immutables.value.Value;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * An abstract class for databases. They must do the following
 *  - Initialize the database
 *  - Update the database
 *  - Query the database
 */
public abstract class DataContext<E> extends AbstractCollection<E> implements AutoCloseable {

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

    public abstract void init(Context context, Class clazz, String identifier);

	public abstract boolean add(E object);

	public abstract boolean contains(Object object);

	@NonNull
	public abstract Iterator<E> iterator();

	public abstract boolean remove(Object object);

	public abstract int size();

	public abstract void close() throws Exception;



	/**
	 *
     */
	public abstract void update();
}