package com.sciencesquad.health.core;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sciencesquad.health.core.EventBus.Entry;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The BaseApp connects the monolithic Android Application
 * to the EventBus mechanism by broadcasting its lifecycle as Events.
 * It also connects to the SharedPreferences singleton and broadcasts
 * any preference key changes as an Event as well.
 */
public class BaseApp extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = BaseApp.class.getSimpleName();
	private static final int MOD = 0xDEADBEEF;

	/**
	 * The private app-wide BaseApp singleton instance.
	 */
	private static BaseApp _application = null;

	/**
	 * The private app-wide instance of the EventBus.
	 */
	private EventBus _eventBus;

	/**
	 * The set of Application-wide Wakelocks.
	 */
	private static Map<String, PowerManager.WakeLock> locks = new ConcurrentHashMap<>();

	/**
	 * Returns the global shared BaseApp.
	 *
	 * @apiNote if called from within a Service not attached to this Application,
	 * this method will return null.
	 *
	 * @return an Optional containing the BaseApp singleton.
	 */
	@NonNull
	public static BaseApp app() {
		return _application;
	}

	/**
	 * Returns the app-wide instance of the EventBus.
	 *
	 * @return the EventBus globally available to the app.
	 */
	@NonNull
	public EventBus eventBus() {
		if (_eventBus == null)
			_eventBus = new EventBus(this);
		return _eventBus;
	}

	/**
	 * Convenience method to access vibration functions.
	 *
	 * @see Vibrator
	 * @param milliseconds the vibration duration
	 */
	public void vibrate(long milliseconds) {
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(milliseconds);
	}

	/**
	 * Convenience method to access vibration functions.
	 *
	 * @see Vibrator
	 * @param pattern the vibration pattern
	 * @param repeat the number of times to repeat the pattern
	 */
	public void vibrate(long[] pattern, int repeat) {
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(pattern, repeat);
	}

	/**
	 * Convenience method to display a Toast message.
	 *
	 * @see Toast
	 * @param text the text to display on screen
	 * @param longer whether to display the message for a longer time
	 */
	public void display(@NonNull String text, boolean longer) {
		Toast.makeText(this, text, longer ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}

	/**
	 * Convenience method to acquire a managed WakeLock by ID.
	 *
	 * @see PowerManager
	 * @param id the unique WakeLock ID
	 * @param flags flags passed to PowerManager to create a WakeLock
	 * @param autorelease the number of milliseconds to autorelease within
	 */
	public void acquireWakelock(@NonNull String id, int flags, int autorelease) {
		final PowerManager power = (PowerManager)getSystemService(Context.POWER_SERVICE);
		final PowerManager.WakeLock lock = power.newWakeLock(flags, id);
		lock.setReferenceCounted(false);

		// Manage our non-reference counted lock and acquire it.
		locks.put(id, lock);
		if (autorelease <= 0)
			lock.acquire();
		else lock.acquire(autorelease);
	}

	/**
	 * Convenience method to release a managed WakeLock by ID.
	 *
	 * @see PowerManager
	 * @param id the unique WakeLock ID
	 */
	public void releaseWakelock(@NonNull String id) {
		final PowerManager.WakeLock wakeLock = locks.get(id);
		try {
			if (wakeLock != null)
				wakeLock.release();
		} catch (Exception ignored) {
		} finally {
			locks.remove(id);
		}
	}

	/**
	 * Support for sending a system/app Notification.
	 *
	 * @param name the notification's unique name
	 * @param notification the notification to send
	 */
	public void notify(@NonNull String name, @NonNull Notification notification) {
		NotificationManager n = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		n.notify(name, MOD, notification);
	}

	/**
	 * Support for cancelling a system/app Notification.
	 *
	 * @param name the notification's unique name
	 */
	public void cancel(@NonNull String name) {
		NotificationManager n = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		n.cancel(name, MOD);
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

		// Begin the background service.
		_application = this;
		startService(new Intent(this, HostService.class));

		// Application-specific configuration.
		RealmConfiguration defaultConfig = new RealmConfiguration.Builder(getBaseContext())
				.name("default.health.realm")
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(defaultConfig);
		AndroidThreeTen.init(this);

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
		this.eventBus().publish("AppConfigurationChangedEvent", this, new Entry("configuration", newConfig.toString()));
	}

	/**
	 * Overridden to provide Application lifecycle Events to the EventBus.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.eventBus().publish("AppPreferencesChangedEvent", this, new Entry("key", key));
	}
}
