package com.sciencesquad.health.health;

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

	/**
	 * The Event.
	 */
	public interface Event {
		// nothing here intentionally
	}

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
		return _bus
				.filter(event -> event.getClass().equals(eventClass))
				.map(obj -> (E)obj)
				.subscribe(handler);
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