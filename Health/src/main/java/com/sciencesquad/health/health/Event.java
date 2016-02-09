package com.sciencesquad.health.health;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * The Event.
 */
public abstract class Event {
	@NonNull public Object source;

	public Event() {}

	public Object getSource() {
		return this.source;
	}

	public static Event.Builder from(@NonNull Object source) {
		Class currentClass = new Object(){}.getClass().getEnclosingClass();
		return Event.Builder.from(currentClass, source);
	}

	public static class Builder {
		private Event _event;
		private Builder() {}

		public static <T extends Event> Builder from(@NonNull Class<T> eventClass, @NonNull Object source) {
			if (!eventClass.isInstance(eventClass))
				throw new AssertionError("eventClass must inherit from Event.");

			try {
				Builder builder = new Builder();
				builder._event = eventClass.newInstance();
				builder._event.source = source;
				return builder;
			} catch (Exception e) {
				return null;
			}
		}

		public Builder assign(String property, Object value) {
			try {
				Field field = _event.getClass().getDeclaredField(property);
				if (!field.getType().isInstance(value))
					throw new AssertionError("value must be of the same type as property.");
				field.set(_event, value);
			} catch (Exception e) { }
			return this;
		}

		public Event create() {
			return _event;
		}
	}
}
