package com.sciencesquad.health.health;

import java8.util.stream.StreamSupport;
import rx.Subscription;
import rx.functions.Action1;

import java.util.HashSet;

public abstract class Module {

	private static final String TAG = "Module";

	private HashSet<Subscription> _subscriptions = new HashSet<>();

	/**
	 * Helper to publish any events needed.
	 *
	 * @param event
	 * @param <E>
	 */
	public <E extends EventBus.Event> void publish(E event) {
		BridgeApplication.application().ifPresent(app -> {
			app.eventBus().publish(event);
		});
	}

	/**
	 * Helper to subscribe and auto-manage any Subscriptions.
	 * Relies on the invocation of finalize() to clean up.
	 *
	 * @param eventClass
	 * @param handler
	 * @param <E>
	 */
	public <E extends EventBus.Event> void subscribe(final Class<E> eventClass, Action1<E> handler) {
		BridgeApplication.application().ifPresent(app -> {
			Subscription sub = app.eventBus().subscribe(eventClass, handler);
			this._subscriptions.add(sub);
		});
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		StreamSupport.stream(this._subscriptions).forEach(Subscription::unsubscribe);
		this._subscriptions.clear();
	}

	// things go here.
}