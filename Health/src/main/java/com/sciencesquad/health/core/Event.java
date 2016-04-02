package com.sciencesquad.health.core;

import org.immutables.builder.Builder;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Event interface from which all events must derive.
 */
public interface Event {

	/**
	 * The object source sending this Event.
	 *
	 * @return the source of the Event
	 */
	@Builder.Parameter Object source();

	/**
	 * The EventType annotation provides, in conjunction
	 * with the @Immutable or @Modifiable annotation, a simple
	 * value class with the following pattern:
	 *
	 * ```
	 * @Immutable @EventType
	 * public interface Update extends Event {}
	 * ```
	 *
	 * Usage:
	 * `Event e = UpdateEvent.from(this).create();`
	 *
	 * It also pre-computes a hash value, and generates
	 * implementations of equals() and toString().
	 * Note that the source object is a required parameter.
	 *
	 * If the @Modifiable annotation is used, the
	 * generated value class would be MutableUpdateEvent.
	 * Both @Immutable and @Modifiable can be used together.
	 */
	@Target({ElementType.PACKAGE, ElementType.TYPE})
	@Retention(RetentionPolicy.CLASS)
	@Value.Style(
			get = {"is*", "get*"},
			typeImmutable = "*Event",
			typeModifiable = "Mutable*Event",
			of = "from",
			builder = "from",
			from = "fromCopy",
			build = "create",
			strictBuilder = true,
			defaults = @Immutable(copy = false, prehash = true)
	) @interface EventType {}
}
