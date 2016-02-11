package com.sciencesquad.health.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.lang.ref.WeakReference;

/**
 * The EventBus that publishes all Events and notifies any subscribers
 * of the Event types they wish to listen to.
 */
public class EventBus {
	private static final String TAG = "EventBus";

	/*
	// In case we switch away from RxJava:
	public interface Subscription {
		void unsubscribe();
		boolean isUnsubscribed();
	}
	*/

	/**
	 * The internal RxJava PublishSubject representation of the EventBus.
	 */
	private final Subject<Event, Event> _bus = new SerializedSubject<>(PublishSubject.create());

	/**
	 * Publishes an Event to all subscribers interested that are listening.
	 *
	 * @param event the event to be published
	 * @param <E> the event class conforming to the Event interface
	 */
	public <E extends Event> void publish(@NonNull E event) {
		_bus.onNext(event);
	}

	/**
	 * Subscribe to notifications of a given Event type, and execute a handler upon
	 * a publisher publishing a notification.
	 *
	 * @implNote if the source is null, all events of eventClass's type will be
	 * subscribed to; this holds true if source eventually becomes null.
	 * @implNote the `source` object is weakly referenced during the subscription
	 *
	 * @apiNote if `this` is strongly referred to within the handler, it runs the
	 * risk of the object not properly being deallocated.
	 *
	 * @param eventClass the class of Event to listen for
	 * @param source the object source to specifically listen to events from, or null
	 * @param handler the action handler to be executed
	 * @param <E> the event class conforming to the Event interface
	 * @return a Subscription that may be cancelled later.
	 */
	@NonNull
	@SuppressWarnings("unchecked")
	public <E extends Event> Subscription subscribe(@NonNull final Class<E> eventClass,
						@Nullable final Object source, @NonNull final Action1<E> handler) {
		/* TODO: Ensure all object references in Events are weak! */
		final WeakReference<Object> ref = new WeakReference<>(source);
		return _bus
				.filter(event -> event.getClass().equals(eventClass))
				.filter(event -> (ref.get() == null) || (event.source().equals(ref.get())))
				.map(obj -> (E)obj)
				.subscribe(handler);
	}

	/**
	 * Unsubscribe from the Event originally being listened to.
	 *
	 * @implNote equivalent to calling `subscription.unsubscribe()`
	 * @param subscription the subscription to cancel
	 */
	public void unsubscribe(@NonNull Subscription subscription) {
		subscription.unsubscribe();
	}

	/**
	 * Convert the EventBus to a raw RxJava Observable.
	 * This may be desired in cases the EventBus class's simplicity hinders
	 * the user from attaining a desired subscriber or publisher effect.
	 *
	 * @return an Observable representation of this EventBus.
	 */
	@NonNull
	public Observable<Event> asObservable() {
		return _bus;
	}
}