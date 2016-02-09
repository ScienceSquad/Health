package com.sciencesquad.health.health;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java8.util.Optional;

public class BridgeApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "BridgeApplication";

	public static final class CreateEvent extends Event {
		// EMPTY
	}

	public static final class DestroyEvent extends Event {
		// EMPTY
	}

	public static final class LowMemoryEvent extends Event {
		// EMPTY
	}

	public static final class TrimMemoryEvent extends Event {
		public int level = 0;
	}

	public static final class ConfigurationChangedEvent extends Event {
		public Configuration configuration = null;
	}

	public static final class PreferencesChangedEvent extends Event {
		public String key = null;
	}

	private static BridgeApplication _application = null;

	public static Optional<BridgeApplication> application() {
		return Optional.ofNullable(_application);
	}

	/**
	 * The private application-wide instance of the EventBus.
	 */
	private final EventBus _eventBus = new EventBus();

	/**
	 * The application-wide instance of the EventBus.
	 * @return the EventBus globally available to the application.
	 */
	public EventBus eventBus() {
		return _eventBus;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_application = this;
		this.eventBus().publish(BridgeApplication.CreateEvent
				.from(this).create());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.eventBus().publish(BridgeApplication.DestroyEvent
				.from(this).create());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.eventBus().publish(BridgeApplication.LowMemoryEvent
				.from(this).create());
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		this.eventBus().publish(BridgeApplication.TrimMemoryEvent
				.from(this)
				.assign("level", level)
				.create());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.eventBus().publish(BridgeApplication.ConfigurationChangedEvent
				.from(this)
				.assign("configuration", newConfig)
				.create());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.eventBus().publish(BridgeApplication.PreferencesChangedEvent
				.from(this)
				.assign("key", key)
				.create());
	}
}
