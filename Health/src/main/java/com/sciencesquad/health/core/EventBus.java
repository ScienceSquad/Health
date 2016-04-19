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

import java.io.Serializable;
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
	 * An Entry for a Key-Value pair to be used in HashMap construction.
	 */
	public static final class Entry extends SimpleImmutableEntry<String, Serializable> {
		public Entry(String theKey, Serializable theValue) {
			super(theKey, theValue);
		}
	}

	/**
	 * The EventBusBroadcastReceiverBridge is designed to receive any
	 * global Broadcasts and bridge them into the local Broadcast system
	 * and wrap them into an event for automatic/easy consumption.
	 *
	 * It is advised that an Intent have some earmarking so a user of the
	 * GenericBroadcastEvent knows which Intent is theirs.
	 */
	public static final class BroadcastReceiverBridge extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String event = intent.getAction() == null ?
					"GenericBroadcastEvent" : intent.getAction();
			final HashMap<String, Serializable> data = (HashMap)intent.getSerializableExtra("data");
			BaseApp.app().eventBus().publish(event, null, data);
		}
	}

	/**
	 * Convenience method to create an Intent from Event descriptors.
	 *
	 * @param context if provided, a global Intent will be made
	 * @param entries values to include in the Event's Intent
	 * @return an Intent for the Event parameters
	 */
	public static Intent intentForEvent(Context context, @NonNull String name,
										@Nullable SimpleImmutableEntry<String, Serializable>... entries) {
		if (entries == null)
			return intentForEvent(context, name, (HashMap)null);

		HashMap<String, Serializable> map = new HashMap<>();
		for (SimpleImmutableEntry<String, Serializable> e : entries)
			map.put(e.getKey(), e.getValue());
		return intentForEvent(context, name, map);
	}

	/**
	 * Convenience method to create an Intent from Event descriptors.
	 *
	 * @param context if provided, a global Intent will be made
	 * @param data values to include in the Event's Intent
	 * @return an Intent for the Event parameters
	 */
	public static Intent intentForEvent(Context context, @NonNull String name,
										@Nullable HashMap<String, Serializable> data) {
		final Intent i = context == null ? new Intent() : new Intent(context, BroadcastReceiverBridge.class);
		i.setAction(name);
		X.of(data).let(d -> {
			for (String key : data.keySet())
				i.putExtra(key, data.get(key));

			i.putExtra("data", d);
		});
		return i;
	}

	private final LocalBroadcastManager _lbm;
	private final HashMap<UUID, WeakReference<Object>> _registry = new HashMap<>();

	public EventBus(Context ctx) {
		_lbm = LocalBroadcastManager.getInstance(ctx);
	}

	/**
	 * Publishes an Event to all subscribers interested that are listening.
	 *
	 * @param name
	 * @param source
	 */
	public void publish(@NonNull String name, @Nullable final Object source) {
		this.publish(name, source, (HashMap<String, Serializable>) null);
	}

	/**
	 * Publishes an Event to all subscribers interested that are listening.
	 *
	 * @param name
	 * @param source
	 */
	public void publish(@NonNull String name, @Nullable final Object source,
						@Nullable SimpleImmutableEntry<String, Serializable>... entries) {
		Intent intent = new Intent(name);
		X.of(source).let(s -> {
			UUID id = UUID.randomUUID();
			_registry.put(id, new WeakReference<>(source));
			intent.putExtra("registry", id);
		});
		X.of(entries).let(d -> {
			HashMap<String, Serializable> map = new HashMap<>();
			for (SimpleImmutableEntry<String, Serializable> e : entries)
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
						@Nullable HashMap<String, Serializable> data) {
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
							@NonNull final Consumer<HashMap<String, Serializable>> handler) {
		final WeakReference<Object> ref = new WeakReference<>(source);
		final BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				HashMap<String, Serializable> data = (HashMap<String, Serializable>)intent.getSerializableExtra("data");
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