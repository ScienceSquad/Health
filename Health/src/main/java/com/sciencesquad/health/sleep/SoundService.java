package com.sciencesquad.health.sleep;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.RawRes;
import android.util.Log;
import com.sciencesquad.health.MainActivity;
import com.sciencesquad.health.R;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.util.X;
import java8.util.stream.StreamSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SoundService extends Service {
	private static final String TAG = SoundService.class.getSimpleName();

	/**
	 * Creates a looping MediaPlayer for the given RawRes sound.
	 *
	 * @param res the sound to create a looping player for
	 * @return a MediaPlayer for the RawRes given
	 */
	public static MediaPlayer createPlayer(@RawRes int res) {
		MediaPlayer player = MediaPlayer.create(BaseApplication.application(), res);
		player.setVolume(0.25f, 0.25f);
		player.setLooping(true);
		return player;
	}

	/**
	 * Loads all Sleep-specific RawRes sounds into MediaPlayers.
	 * Each player is encoded in the Map by its name as a String.
	 *
	 * @return a map of all Sleep-specific sounds to MediaPlayers.
	 */
	public static Map<String, MediaPlayer> players() {
		HashMap<String, MediaPlayer> players = new HashMap<>();
		players.put("waves", createPlayer(R.raw.waves));
		players.put("birds", createPlayer(R.raw.birds));
		players.put("crickets", createPlayer(R.raw.crickets));
		players.put("rain", createPlayer(R.raw.rain));
		players.put("thunder", createPlayer(R.raw.thunder));
		players.put("fire", createPlayer(R.raw.fire));
		players.put("wind", createPlayer(R.raw.wind));
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
	private Map<String, MediaPlayer> players = players();

	/**
	 * @see Service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		StreamSupport.stream(players.keySet()).forEach(key -> {
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