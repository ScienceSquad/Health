package com.sciencesquad.health.health;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class BridgeApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "BridgeApplication";

	public interface ApplicationEvent extends EventBus.Event {
		// intentionally empty
	}

	public final class ApplicationCreateEvent implements ApplicationEvent {
		// intentionally empty
	}

	public final class ApplicationDestroyEvent implements ApplicationEvent {
		// intentionally empty
	}

	public final class ApplicationLowMemoryEvent implements ApplicationEvent {
		// intentionally empty
	}

	public final class ApplicationTrimMemoryEvent implements ApplicationEvent {
		int level = 0;
		public ApplicationTrimMemoryEvent(int level) {
			this.level = level;
		}
	}

	public final class ApplicationConfigurationChangedEvent implements ApplicationEvent {
		Configuration configuration = null;
		public ApplicationConfigurationChangedEvent(Configuration configuration) {
			this.configuration = configuration;
		}
	}

	public final class ApplicationPreferencesChangedEvent implements ApplicationEvent {
		String key = null;
		public ApplicationPreferencesChangedEvent(String key) {
			this.key = key;
		}
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
		this.eventBus().publish(new ApplicationCreateEvent());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.eventBus().publish(new ApplicationDestroyEvent());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.eventBus().publish(new ApplicationLowMemoryEvent());
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		this.eventBus().publish(new ApplicationTrimMemoryEvent(level));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.eventBus().publish(new ApplicationConfigurationChangedEvent(newConfig));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.eventBus().publish(new ApplicationPreferencesChangedEvent(key));
	}
}
