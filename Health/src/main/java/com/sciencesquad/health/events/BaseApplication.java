package com.sciencesquad.health.events;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import com.sciencesquad.health.events.Event.EventType;
import org.immutables.value.Value.Immutable;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * The BaseApplication connects the monolithic Android Application
 * to the EventBus mechanism by broadcasting its lifecycle as Events.
 * It also connects to the SharedPreferences singleton and broadcasts
 * any preference key changes as an Event as well.
 */
public class BaseApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = BaseApplication.class.getSimpleName();

	/**
	 * The Application was created.
	 */
	@Immutable @EventType
	public interface AppCreate extends Event {
		// EMPTY
	}

	/**
	 * The Application was destroyed.
	 */
	@Immutable @EventType
	public interface AppDestroy extends Event {
		// EMPTY
	}

	/**
	 * The Application was given a low-memory warning.
	 */
	@Immutable @EventType
	public interface AppLowMemory extends Event {
		// EMPTY
	}

	/**
	 * The Application's memory level was trimmed.
	 */
	@Immutable @EventType
	public interface AppTrimMemory extends Event {

		/**
		 * The memory level to which the Application was trimmed.
		 */
		int level();
	}

	/**
	 * The Application configuration changed.
	 */
	@Immutable @EventType
	public interface AppConfigurationChanged extends Event {

		/**
		 * The new configuration for which the Application was updated.
		 */
		Configuration configuration();
	}

	/**
	 * The Application preferences were updated.
	 */
	@Immutable @EventType
	public interface AppPreferencesChanged extends Event {

		/**
		 * The key of the preference entry that was changed.
		 * @implNote this should be used when accessing the preference entry
		 * from SharedPreferences.
		 */
		String key();
	}

	/**
	 * The private application-wide BaseApplication singleton instance.
	 */
	private static BaseApplication _application = null;

	/**
	 * The private application-wide instance of the EventBus.
	 */
	private final EventBus _eventBus = new EventBus();

	/**
	 * Returns the global shared BaseApplication.
	 *
	 * @apiNote if called from within a Service not attached to this Application,
	 * this method will return null.
	 *
	 * @return an Optional containing the BaseApplication singleton.
	 */
	@Nullable
	public static BaseApplication application() {
		return _application;
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
	 * "Setting a default configuration in your custom Application class,
	 * will ensure that it is available in the rest of your code."
	 * - Realm Library 0.87.4
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		RealmConfiguration defaultConfig = new RealmConfiguration.Builder(getBaseContext())
				.name("default.health.realm")
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(defaultConfig);

		_application = this;
		this.eventBus().publish(AppCreateEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		this.eventBus().publish(AppDestroyEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.eventBus().publish(AppLowMemoryEvent.from(this)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		this.eventBus().publish(AppTrimMemoryEvent.from(this)
				.level(level)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.eventBus().publish(AppConfigurationChangedEvent.from(this)
				.configuration(newConfig)
				.create());
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.eventBus().publish(AppPreferencesChangedEvent.from(this)
				.key(key)
				.create());
	}
}
