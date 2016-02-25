package com.sciencesquad.health.sleep;

import android.animation.ValueAnimator;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import com.sciencesquad.health.MainActivity;
import com.sciencesquad.health.R;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.util.X;
import java8.util.stream.StreamSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SoundService extends IntentService {
	private static final String TAG = SoundService.class.getSimpleName();

	/**
	 * Loads all Sleep-specific RawRes sounds into MediaPlayers.
	 * Each player is encoded in the Map by its name as a String.
	 *
	 * @return a map of all Sleep-specific sounds to MediaPlayers.
	 */
	public static Map<String, MediaPlayer> defaultPlayers() {
		HashMap<String, MediaPlayer> players = new HashMap<>();
		X.of(BaseApplication.application()).let(app -> {
			players.put("waves", MediaPlayer.create(app, R.raw.waves));
			players.put("birds", MediaPlayer.create(app, R.raw.birds));
			players.put("crickets", MediaPlayer.create(app, R.raw.crickets));
			players.put("rain", MediaPlayer.create(app, R.raw.rain));
			players.put("thunder", MediaPlayer.create(app, R.raw.thunder));
			players.put("fire", MediaPlayer.create(app, R.raw.fire));
			players.put("wind", MediaPlayer.create(app, R.raw.wind));
		});
		return players;
	}

	/**
	 * Convenience method to start the SoundService.
	 */
	public static void startSoundService(boolean foreground) {
		final String action = foreground ?	SoundService.ACTION_FOREGROUND:
											SoundService.ACTION_BACKGROUND;

		X.of(BaseApplication.application()).let(app -> {
			Intent startIntent = new Intent(app, SoundService.class);
			startIntent.setAction(action);
			app.startService(startIntent);
		}).or(() -> {
			Log.d(TAG, "SoundService could not be started with " + action + ".");
		});
	}

	/**
	 * Convenience method to stop the SoundService.
	 */
	public static void stopSoundService() {
		X.of(BaseApplication.application()).let(app -> {
			Intent stopIntent = new Intent(app, SoundService.class);
			app.stopService(stopIntent);
		}).or(() -> {
			Log.d(TAG, "SoundService could not be stopped.");
		});
	}

	/**
	 *
	 */
	public static final String ACTION_FOREGROUND = "ACTION_FOREGROUND";

	/**
	 *
	 */
	public static final String ACTION_BACKGROUND = "ACTION_BACKGROUND";

	/**
	 *
	 */
	private Map<String, MediaPlayer> players = defaultPlayers();

	/**
	 * @see IntentService
	 */
	public SoundService() {
		super("Default");
	}

	/**
	 * @see IntentService
	 */
	public SoundService(String name) {
		super(name);
	}

	/**
	 * @see Service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		StreamSupport.stream(players.keySet()).forEach(key -> {
			players.get(key).setLooping(true);
			players.get(key).start();

			// Slowly "animate" value to the new one.
			float vol = (float)Math.random();
			ValueAnimator animator = ValueAnimator.ofFloat(0.25f, vol);
			animator.setDuration(TimeUnit.SECONDS.toMillis(3L));
			animator.setRepeatCount(ValueAnimator.INFINITE);
			animator.setRepeatMode(ValueAnimator.REVERSE);
			animator.addUpdateListener(animation -> {
				float value = (Float)(animation.getAnimatedValue());
				players.get(key).setVolume(value, value);
			});
			animator.start();
		});
		return 1;
	}

	/**
	 * @see Service
	 */
	public IBinder onBind(Intent intent) {
		// Unimplemented.
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}

	/**
	 * @see Service
	 */
	@Override
	public void onDestroy() {
		StreamSupport.stream(players.keySet()).forEach(key -> {
			players.get(key).stop();
			players.get(key).release();
		});
	}

	/**
	 * @see Service
	 */
	@Override
	public void onLowMemory() {
		// Unimplemented.
	}

	private Notification foregroundNotification() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent pending = PendingIntent.getActivity(this, 3, intent, 0);
		return new Notification.Builder(this)
				.setContentTitle("SoundService active.")
				.setContentIntent(pending)
				.build();
	}
}