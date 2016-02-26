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
import android.widget.Toast;
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
	private Map<String, ValueAnimator> animators = new HashMap<>();

	/**
	 * @see Service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Handle STOP first, and pass-through to START.
		// If intent is null, the Service was just restarted.
		if (intent != null && "STOP".equals(intent.getAction())) {
			Toast.makeText(this, "Stopping sounds...", Toast.LENGTH_SHORT).show();
			stopSelfResult(startId);
			return Service.START_REDELIVER_INTENT;
		}

		StreamSupport.stream(players.keySet()).forEach(key -> {
			this.players.get(key).setLooping(true);
			this.players.get(key).start();

			// Install an animator to slowly interpolate value to the new one.
			float vol = (float)Math.random();
			ValueAnimator animator = ValueAnimator.ofFloat(0.25f, vol);
			animator.setDuration(TimeUnit.SECONDS.toMillis(3L));
			animator.setRepeatCount(ValueAnimator.INFINITE);
			animator.setRepeatMode(ValueAnimator.REVERSE);
			animator.addUpdateListener(animation -> {
				float value = (Float)(animation.getAnimatedValue());
				players.get(key).setVolume(value, value);
			});

			this.animators.put(key, animator);
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
		StreamSupport.stream(this.players.keySet()).forEach(key -> {
			this.players.get(key).stop();
			this.players.get(key).release();
			this.animators.get(key).cancel();
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

	/**
	 * Creates the ongoing notification that can stop this service.
	 */
	// TODO: Fix the PendingIntent to actually stop the service. :(
	private void showNotification() {

		// Create the STOP intent first.
		Intent intent = new Intent(this, SoundService.class);
		intent.setAction("STOP");
		PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);

		// Create the notification as we like it.
		Notification n = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_menu_manage)
				.setContentTitle("Playing sounds...")
				.setContentText("Tap to stop playing sounds.")
				.setContentIntent(pending)
				.setDeleteIntent(pending)
				.setAutoCancel(true)
				.setOngoing(true)
				.build();

		// Send it on its way.
		NotificationManager m = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		m.notify(332, n);
	}
}