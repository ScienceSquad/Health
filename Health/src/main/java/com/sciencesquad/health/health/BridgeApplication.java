package com.sciencesquad.health.health;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java8.util.Optional;

/**
 * The BridgeApplication connects the monolithic Android Application
 * to the EventBus mechanism by broadcasting its lifecycle as Events.
 * It also connects to the SharedPreferences singleton and broadcasts
 * any preference key changes as an Event as well.
 */
public class BridgeApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = "BridgeApplication";

	/**
	 * The Application was created.
	 */
	public static final class CreateEvent extends Event {
		// EMPTY
	}

	/**
	 * The Application was destroyed.
	 */
	public static final class DestroyEvent extends Event {
		// EMPTY
	}

	/**
	 * The Application was given a low-memory warning.
	 */
	public static final class LowMemoryEvent extends Event {
		// EMPTY
	}

	/**
	 * The Application's memory level was trimmed.
	 */
	public static final class TrimMemoryEvent extends Event {

		/**
		 * The memory level to which the Application was trimmed.
		 */
		public int level = 0;
	}

	/**
	 * The Application configuration changed.
	 */
	public static final class ConfigurationChangedEvent extends Event {

		/**
		 * The new configuration for which the Application was updated.
		 */
		public Configuration configuration = null;
	}

	/**
	 * The Application preferences were updated.
	 */
	public static final class PreferencesChangedEvent extends Event {

		/**
		 * The key of the preference entry that was changed.
		 * @implNote this should be used when accessing the preference entry
		 * from SharedPreferences.
		 */
		public String key = null;
	}

	/**
	 * The private application-wide BridgeApplication singleton instance.
	 */
	private static BridgeApplication _application = null;

	/**
	 * The private application-wide instance of the EventBus.
	 */
	private final EventBus _eventBus = new EventBus();

	/**
	 * Returns the global shared BridgeApplication.
	 *
	 * @apiNote if called from within a Service not attached to this Application,
	 * this method will return null.
	 *
	 * @return an Optional containing the BridgeApplication singleton.
	 */
	public static Optional<BridgeApplication> application() {
		return Optional.ofNullable(_application);
	}

	/**
	 * Returns the application-wide instance of the EventBus.
	 *
	 * @return the EventBus globally available to the application.
	 */
	public EventBus eventBus() {
		return _eventBus;
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		_application = this;
		this.eventBus().publish(Event.build(CreateEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		this.eventBus().publish(Event.build(DestroyEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.eventBus().publish(Event.build(LowMemoryEvent.class, this)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		this.eventBus().publish(Event.build(TrimMemoryEvent.class, this)
				.assign("level", level)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.eventBus().publish(Event.build(ConfigurationChangedEvent.class, this)
				.assign("configuration", newConfig)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.eventBus().publish(Event.build(PreferencesChangedEvent.class, this)
				.assign("key", key)
				.create());
	}
}
