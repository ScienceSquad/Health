package com.sciencesquad.health.health;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java8.util.stream.StreamSupport;
import rx.Subscription;
import rx.functions.Action1;

import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

// TODO: NEEDS TO BE FLESHED OUT!!
public abstract class Module {
	private static final String TAG = "Module";

	private static int PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;
	private static ForkJoinPool POOL = new ForkJoinPool(PROCESSORS);

	private HashSet<Subscription> _subscriptions = new HashSet<>();

	/**
	 * Returns the concurrent execution service for this Module.
	 *
	 * @return a ForkJoinPool to be used for computationally intensive tasks.
	 */
	protected ForkJoinPool executor() {
		return Module.POOL;
	}

	/**
	 * Publishes any Events to the shared application EventBus.
	 *
	 * @param event the event to publish
	 * @param <E> the type of Event being published
	 */
	public <E extends Event> void publish(@NonNull E event) {
		BridgeApplication.application().ifPresent(app -> {
			app.eventBus().publish(event);
		});
	}

	/**
	 * Subscribes and auto-manage a Subscription to an Event.
	 * Automatically uses the shared application EventBus.
	 *
	 * @implNote Relies on the invocation of finalize() to clean up.
	 *
	 * @param eventClass the type of Event subscribed to
	 * @param handler the action to perform upon notification
	 * @param <E> the type of Event being subscribed to
	 */
	public <E extends Event> void subscribe(@NonNull final Class<E> eventClass,
						@Nullable final Object source, @NonNull final Action1<E> handler) {
		BridgeApplication.application().ifPresent(app -> {
			Subscription sub = app.eventBus().subscribe(eventClass, source, handler);
			this._subscriptions.add(sub);
		});
	}

	/**
	 * Invoked upon garbage collection or deallocation of the Module.
	 * If the current Runtime does not invoke this method automatically,
	 * it MUST be invoked manually, to ensure Subscriptions are dealt with.
	 *
	 * @throws Throwable a generic error
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		StreamSupport.stream(this._subscriptions).forEach(Subscription::unsubscribe);
		this._subscriptions.clear();
	}
}