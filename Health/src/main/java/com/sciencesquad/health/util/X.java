package com.sciencesquad.health.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java8.util.Objects;
import java8.util.function.Consumer;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.function.Supplier;

import java.util.NoSuchElementException;

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, exists() will return true and get() will return the value.
 *
 * Additional methods that depend on the presence or absence of a contained
 * value are provided, such as either() (return a default value if value not
 * present) or let() (execute a block of code if the value is present).
 *
 * This is a value-based class; use of identity-sensitive operations (==),
 * identity hash code, or synchronization) on instances of X may have
 * unpredictable results and should be avoided.
 *
 * @implNote The class name X is intentional; it is much shorter than Optional.
 */
public final class X<T> {

	/**
	 * Common singleton instance for {@code empty()}.
	 */
	private static final X<?> NONE = new X<>();

	/**
	 * If non-null, the value; if null, indicates no value is present
	 */
	private final T value;

	/**
	 * Constructs a NONE instance.
	 */
	private X() {
		this.value = null;
	}

	/**
	 * Constructs a SOME with the given value.
	 */
	private X(@NonNull T value) {
		this.value = Objects.requireNonNull(value);
	}

	/**
	 * Returns an empty Optional instance with no value.
	 *
	 * @implNote do not use == to test against X.empty()!
	 * @param <T> type of the non-existent value
	 * @return an empty Optional
	 */
	@NonNull @SuppressWarnings("unchecked")
	public static<T> X<T> none() {
		return (X<T>)NONE;
	}

	/**
	 * Returns an Optional with the specified present non-null value.
	 *
	 * @param <T> the class of the value
	 * @param value the value to be present, which must be non-null
	 * @return an Optional with the value present
	 * @throws NullPointerException if value is null
	 */
	@NonNull
	public static <T> X<T> some(@NonNull T value) {
		return new X<>(value);
	}

	/**
	 * Returns an Optional describing the specified value, if non-null,
	 * otherwise returns an empty Optional.
	 *
	 * @param <T> the class of the value
	 * @param value the possibly-null value to describe
	 * @return an Optional with a present value if the specified value
	 * is non-null, otherwise an empty Optional
	 */
	@NonNull
	public static <T> X<T> of(@Nullable T value) {
		return value == null ? none() : some(value);
	}

	/**
	 * If a value is present in this Optional, returns the value.
	 *
	 * @return the non-null value held by this Optional
	 * @throws NoSuchElementException if there is no value present
	 */
	@NonNull
	public T get() {
		if (value == null)
			throw new NoSuchElementException("No value present!");
		return value;
	}

	/**
	 * Return true if there is a value present, otherwise false.
	 *
	 * @return true if there is a value present, otherwise false
	 */
	public boolean exists() {
		return value != null;
	}

	/**
	 * If a value is present, invoke the specified consumer with the value,
	 * otherwise do nothing.
	 *
	 * @param consumer block to be executed if a value is present
	 * @throws NullPointerException if value is present and consumer is null
	 */
	@NonNull
	public X<T> let(@NonNull Consumer<? super T> consumer) {
		if (value != null)
			consumer.accept(value);
		return this;
	}

	/**
	 * If no value is present, invoke the specified runnable.
	 *
	 * @param runnable block to be executed if no value is present
	 * @throws NullPointerException if runnable is null
	 */
	@NonNull
	public X<T> or(@NonNull Runnable runnable) {
		if (value == null)
			runnable.run();
		return this;
	}

	/**
	 * If a value is present, and the value matches the given predicate,
	 * return an Optional describing the value, otherwise return an
	 * empty Optional.
	 *
	 * @param predicate a predicate to apply to the value, if present
	 * @return an Optional describing the value of this Optional
	 * if a value is present and the value matches the given predicate,
	 * otherwise an empty Optional
	 * @throws NullPointerException if the predicate is null
	 */
	@NonNull
	public X<T> filter(@NonNull Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		if (!exists())
			return this;
		else return predicate.test(value) ? this : none();
	}

	/**
	 * If a value is present, apply the provided mapping function to it,
	 * and if the result is non-null, return an Optional describing the
	 * result. Otherwise return an empty Optional.
	 *
	 * @param <U> The type of the result of the mapping function
	 * @param mapper a mapping function to apply to the value, if present
	 * @return an Optional describing the result of applying a mapping
	 * function to the value of this Optional, if a value is present,
	 * otherwise an empty Optional
	 * @throws NullPointerException if the mapping function is null
	 */
	@NonNull
	public<U> X<U> map(@NonNull Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		if (!exists())
			return none();
		else return X.of(mapper.apply(value));
	}

	/**
	 * If a value is present, apply the provided Optional-bearing
	 * mapping function to it, return that result, otherwise return an empty
	 * Optional. This method is similar to map(), but the provided mapper is
	 * one whose result is already an Optional, and if invoked, bind() does
	 * not wrap it with an additional Optional.
	 *
	 * @param <U> The type parameter to the Optional returned by mapper
	 * @param mapper a mapping function to apply to the value, if present
	 * @return the result of applying an Optional-bearing mapping
	 * function to the value of this Optional, if a value is present,
	 * otherwise an empty Optional
	 * @throws NullPointerException if mapper is null or returns null
	 */
	@NonNull
	public<U> X<U> bind(@NonNull Function<? super T, X<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (!exists())
			return none();
		else return Objects.requireNonNull(mapper.apply(value));
	}

	/**
	 * Return the value if present, otherwise return otherValue.
	 *
	 * @param otherValue the value to be returned if there is no value
	 * @return the value, if present, otherwise otherValue
	 */
	@Nullable
	public T either(@Nullable T otherValue) {
		return value != null ? value : otherValue;
	}

	/**
	 * Return the value if present, otherwise invoke otherSupplier and return
	 * the result of that invocation.
	 *
	 * @param otherSupplier returns result if no value is present
	 * @return the value if present otherwise the result of otherSupplier.get()
	 * @throws NullPointerException if value is not present and otherSupplier is null
	 */
	@Nullable
	public T either(@NonNull Supplier<? extends T> otherSupplier) {
		return value != null ? value : otherSupplier.get();
	}

	/**
	 * Return the contained value, if present, otherwise throw an exception
	 * to be created by the provided supplier.
	 *
	 * @param <E> type of thrown exception
	 * @param exceptionSupplier supplier for the thrown exception
	 * @return the present value
	 * @throws E if there is no value present
	 * @throws NullPointerException if no value is present and exceptionSupplier is null
	 */
	@Nullable
	public <E extends Throwable> T guard(@NonNull Supplier<? extends E> exceptionSupplier) throws E {
		if (value != null)
			return value;
		else throw exceptionSupplier.get();
	}

	/**
	 * @see Object
	 */
	@Override
	public boolean equals(@NonNull Object obj) {
		return this == obj || (obj instanceof X && Objects.equals(value, ((X<?>)obj).value));
	}

	/**
	 * @see Object
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	/**
	 * @see Object
	 */
	@Override
	public String toString() {
		return value == null ? "Optional.empty" : String.format("Optional[%s]", value);
	}
}
