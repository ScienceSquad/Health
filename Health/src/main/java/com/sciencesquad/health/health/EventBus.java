package com.sciencesquad.health.health;

import android.util.Log;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * The EventBus.
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
	 * Publishes an Event to all subscribers interested in listening for it.
	 *
	 * @param event the event to be published
	 * @param <E> the event class conforming to the Event interface
	 */
	public <E extends Event> void publish(E event) {
		Log.d(TAG, "Got " + event);
		_bus.onNext(event);
	}

	/**
	 * Subscribe to notifications of a given Event type, and execute a handler upon
	 * a publisher publishing a notification.
	 *
	 * @param eventClass the class of Event to listen for
	 * @param handler the action handler to be executed
	 * @param <E> the event class conforming to the Event interface
	 * @return a Subscription that may be cancelled later.
	 */
	public <E extends Event> Subscription subscribe(final Class<E> eventClass, Action1<E> handler) {
		Log.d(TAG, "Subbing " + eventClass);
		return _bus
				.filter(event -> event.getClass().equals(eventClass))
				.map(obj -> (E)obj)
				.subscribe(handler);
	}

	/**
	 * Unsubscribe from the Event originally being listened to.
	 *
	 * @param subscription the subscription to cancel
	 */
	public void unsubscribe(Subscription subscription) {
		subscription.unsubscribe();
	}

	/**
	 * Convert the EventBus to a raw Rx Observable.
	 *
	 * @return an Observable representation of this EventBus.
	 */
	public Observable<Event> asObservable() {
		return _bus;
	}
}