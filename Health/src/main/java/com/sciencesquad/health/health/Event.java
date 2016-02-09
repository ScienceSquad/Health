package com.sciencesquad.health.health;

import android.support.annotation.NonNull;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The Event.
 */
// TODO: Support Auto-Parcellable
public abstract class Event implements Serializable {
	private static final String TAG = "Event";

	public Event() {}

	@NonNull public transient Object source;
	@NonNull
	public Object getSource() {
		return this.source;
	}

	@NonNull
	public static <T extends Event> Event.Builder build(@NonNull final Class<T> eventClass, @NonNull final Object source) {
		return new Event.Builder(eventClass, source);
	}

	@Override @NonNull
	public String toString() {
		return StreamSupport.of(this.getClass().getFields())
				.filter(f -> !Modifier.isStatic(f.getModifiers()))
				.map(f -> {
					try {
						return f.getName() + " = " + f.get(this);
					} catch (IllegalAccessException e) {
						return null;
					}
				})
				.filter(f -> f != null)
				.collect(Collectors.joining(";\n\t", this.getClass().getSimpleName() + " {\n\t", "\n}"));
	}

	public static class Builder {
		private Event _event;

		public <T extends Event> Builder(@NonNull final Class<T> eventClass, @NonNull final Object source) {
			//if (!eventClass.isInstance(eventClass))
			//	throw new AssertionError("eventClass must inherit from Event.");

			try {
				this._event = eventClass.newInstance();
				this._event.source = source;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@NonNull
		public Builder assign(@NonNull final String property, @NonNull final Object value) {
			try {
				Field field = _event.getClass().getDeclaredField(property);
				if (!field.getType().isInstance(value))
					throw new AssertionError("value must be of the same type as property.");
				field.set(_event, value);
			} catch (Exception e) { }
			return this;
		}

		@NonNull
		public Event create() {
			return _event;
		}
	}
}
