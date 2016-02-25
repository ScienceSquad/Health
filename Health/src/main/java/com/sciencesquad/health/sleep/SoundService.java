package com.sciencesquad.health.sleep;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
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

/**
 *
 */
// TODO: Use PARTIAL_WAKE_LOCK.
public class SoundService extends Service {
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
		X.of(BaseApplication.application()).let(app -> {
			Intent startIntent = new Intent(app, SoundService.class);
			app.startService(startIntent);
		}).or(() -> {
			Log.d(TAG, "SoundService could not be started.");
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
	private Map<String, MediaPlayer> players = defaultPlayers();

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
		this.showNotification();
		return Service.START_STICKY;
	}

	/**
	 * @see Service
	 */
	public IBinder onBind(Intent intent) {
		// Unimplemented.
		return null;
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

		NotificationManager m = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		m.cancel(332);
	}

	/**
	 * @see Service
	 */
	@Override
	public void onLowMemory() {
		// Unimplemented.
	}

	private void showNotification() {
		PendingIntent pending = PendingIntent.getActivity(this, 3, new Intent(this, MainActivity.class), 0);
		Notification n = new Notification.Builder(this)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_menu_manage)
				.setContentTitle("Playing sounds...")
				.setContentText("Tap to stop playing sounds.")
				.setContentIntent(pending)
				.setAutoCancel(true)
				.setOngoing(true)
				.build();
		NotificationManager m = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		m.notify(332, n);
	}
}