package com.sciencesquad.health.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import com.sciencesquad.health.core.util.X;
import java8.util.function.Consumer;

import java.lang.ref.WeakReference;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.UUID;

/**
 * The EventBus that publishes all Events and notifies any subscribers
 * of the Event types they wish to listen to.
 */
public class EventBus {
	private static final String TAG = EventBus.class.getSimpleName();

	/**
	 *
	 */
	public static final class Entry extends SimpleImmutableEntry<String, Object> {
		public Entry(String theKey, Object theValue) {
			super(theKey, theValue);
		}
	}

	private final LocalBroadcastManager _lbm = LocalBroadcastManager.getInstance(BaseApp.app());
	private final HashMap<UUID, WeakReference<Object>> _registry = new HashMap<>();

	/**
	 * Publishes an Event to all subscribers interested that are listening.
	 *
	 * @param name
	 * @param source
	 */
	public void publish(@NonNull String name, @Nullable final Object source) {
		this.publish(name, source, (HashMap<String, Object>) null);
	}

	/**
	 * Publishes an Event to all subscribers interested that are listening.
	 *
	 * @param name
	 * @param source
	 */
	public void publish(@NonNull String name, @Nullable final Object source,
						@NonNull SimpleImmutableEntry<String, Object>... entries) {
		Intent intent = new Intent(name);
		X.of(source).let(s -> {
			UUID id = UUID.randomUUID();
			_registry.put(id, new WeakReference<>(source));
			intent.putExtra("registry", id);
		});
		X.of(entries).let(d -> {
			HashMap<String, Object> map = new HashMap<>();
			for (SimpleImmutableEntry<String, Object> e : entries)
				map.put(e.getKey(), e.getValue());
			intent.putExtra("data", map);
		});
		this._lbm.sendBroadcast(intent);
	}

	/**
	 * Publishes an Event to all subscribers interested that are listening.
	 *
	 * @param name
	 * @param source
	 */
	public void publish(@NonNull String name, @Nullable final Object source,
						@Nullable HashMap<String, Object> data) {
		Intent intent = new Intent(name);
		X.of(source).let(s -> {
			UUID id = UUID.randomUUID();
			_registry.put(id, new WeakReference<>(source));
			intent.putExtra("registry", id);
		});
		X.of(data).let(d -> intent.putExtra("data", d));
		this._lbm.sendBroadcast(intent);
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
	 * @param name the class of Event to listen for
	 * @param source the object source to specifically listen to events from, or null
	 * @param handler the action handler to be executed
	 * @return a Subscription that may be cancelled later.
	 */
	@NonNull
	@SuppressWarnings("unchecked")
	public BroadcastReceiver subscribe(@NonNull final String name, @Nullable final Object source,
							@NonNull final Consumer<HashMap<String, Object>> handler) {
		final WeakReference<Object> ref = new WeakReference<>(source);
		final BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				HashMap<String, Object> data = (HashMap<String, Object>)intent.getSerializableExtra("data");
				X.of(intent.getSerializableExtra("registry")).let(r -> {
					WeakReference<Object> registry = _registry.get(r);
					if (ref.get() == null || ref.get().equals(registry.get()))
						handler.accept(data);
				}).or(() -> handler.accept(data));
			}
		};
		this._lbm.registerReceiver(receiver, new IntentFilter(name));
		return receiver;
	}

	/**
	 * Unsubscribe from the Event originally being listened to.
	 *
	 * @implNote equivalent to calling `subscription.unsubscribe()`
	 * @param receiver the subscription to cancel
	 */
	public void unsubscribe(@NonNull BroadcastReceiver receiver) {
		this._lbm.unregisterReceiver(receiver);
	}
}