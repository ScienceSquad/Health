package com.sciencesquad.health.health;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The abstract Event class from which all events must derive.
 * This class provides the basis for Event building and manipulation
 * through dynamic lookup at runtime.
 */
public abstract class Event implements Cloneable, Serializable {
	private static final String TAG = "Event";

	/**
	 * The object source sending this Event.
	 */
	@NonNull public transient Object source;

	/**
	 * Clones the Event object as it implements Cloneable.
	 *
	 * @return a deep-copy clone of the current Event
	 * @throws CloneNotSupportedException if the clone failed
	 */
	@NonNull
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Convenience method to create a copy of an Event object.
	 * Note that every subclass MUST implicitly support Cloneable.
	 *
	 * @implNote any members of an Event subclass should resolve any
	 * cyclic dependencies on the origin Event object in the `clone()`
	 * method independently of this implementation.
	 *
	 * @return a copy of this Event object, or null if clone() failed
	 */
	@Nullable
	public Event copy() {
		try {
			return (Event)this.clone();
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Creates an `Event.Builder` for the given Event class and source object.
	 *
	 * @param eventClass the class of Event to build
	 * @param source the object origin of the Event
	 * @param <T> a subclass of Event
	 * @return an `Event.Builder` for the provided eventClass
	 */
	@NonNull
	public static <T extends Event> Event.Builder build(@NonNull final Class<T> eventClass, @NonNull final Object source) {
		return new Event.Builder(eventClass, source);
	}

	/**
	 * Returns a string representation of any Event subclass, by dynamic
	 * lookup of public non-static fields and organizing them.
	 *
	 * @return A string representation of any Event subclass.
	 */
	@Override @NonNull
	public String toString() {
		return StreamSupport.of(this.getClass().getFields())
				.filter(f -> !Modifier.isStatic(f.getModifiers()))
				.map(f -> {
					try {
						return f.getName() + " = " + f.get(this).toString() + ";\n";
					} catch (IllegalAccessException e) {
						return null;
					}
				})
				.filter(f -> f != null)
				.collect(Collectors.joining("\t", this.getClass().getSimpleName() + " {\n\t", "}"));
	}

	/**
	 * Implements the Builder pattern for the Event class and
	 * any of its subclasses through dynamic runtime lookup.
	 */
	public static final class Builder {

		/**
		 * The underlying Event subclass.
		 */
		// FIXME: Should be a ConcurrentHashMap<String, Object> and dynamically created.
		private Event _event;

		/**
		 * Creates an `Event.Builder` for the provided eventClass, with the initial
		 * object source.
		 *
		 * @param eventClass the class of Event to build
		 * @param source the object origin of the Event
		 * @param <T> a subclass of Event
		 */
		public <T extends Event> Builder(@NonNull final Class<T> eventClass, @NonNull final Object source) {
			if (!Event.class.isAssignableFrom(eventClass))
				throw new AssertionError("eventClass must inherit from Event.");

			try {
				this._event = eventClass.newInstance();
				this._event.source = source;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Assigns the value to a public member field of name property.
		 *
		 * @implNote supports boxed value type classes through dynamic lookup
		 *
		 * @param property the property or field name to assign to
		 * @param value the value to assign the property to
		 * @return the Builder itself
		 */
		@NonNull
		public Builder assign(@NonNull final String property, @Nullable final Object value) {
			try {
				Field field = _event.getClass().getDeclaredField(property);
				field.set(_event, value);

				// FIXME: Always assert the field is assignable!
				// Use Apache Commons's ClassUtils for this.
				//if (!field.getType().isAssignableFrom(value.getClass()))
				//	throw new AssertionError("value must be of the same type as property.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		/**
		 * Returns a fully configured Event based on the provided Event class,
		 * object source, and any or no property assignments.
		 *
		 * @return a fully configured Event
		 */
		@NonNull
		public Event create() {
			return _event;
		}
	}
}
