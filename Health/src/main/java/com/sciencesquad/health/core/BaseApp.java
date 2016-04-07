package com.sciencesquad.health.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sciencesquad.health.core.EventBus.Entry;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * The BaseApp connects the monolithic Android Application
 * to the EventBus mechanism by broadcasting its lifecycle as Events.
 * It also connects to the SharedPreferences singleton and broadcasts
 * any preference key changes as an Event as well.
 */
public class BaseApp extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = BaseApp.class.getSimpleName();

	/**
	 * The private app-wide BaseApp singleton instance.
	 */
	private static BaseApp _application = null;

	/**
	 * The private app-wide instance of the EventBus.
	 */
	private final EventBus _eventBus = new EventBus();

	/**
	 * Returns the global shared BaseApp.
	 *
	 * @apiNote if called from within a Service not attached to this Application,
	 * this method will return null.
	 *
	 * @return an Optional containing the BaseApp singleton.
	 */
	@Nullable
	public static BaseApp app() {
		return _application;
	}

	/**
	 * Returns the app-wide instance of the EventBus.
	 *
	 * @return the EventBus globally available to the app.
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
		// gives timezone data.
		AndroidThreeTen.init(this);

		_application = this;
		this.eventBus().publish("AppCreateEvent", this);
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		this.eventBus().publish("AppDestroyEvent", this);
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.eventBus().publish("AppLowMemoryEvent", this);
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		this.eventBus().publish("AppTrimMemoryEvent", this);
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.eventBus().publish("AppConfigurationChangedEvent", this, new Entry("configuration", newConfig));
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.eventBus().publish("AppPreferencesChangedEvent", this, new Entry("key", key));
	}
}
