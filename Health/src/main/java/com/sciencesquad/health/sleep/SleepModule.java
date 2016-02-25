package com.sciencesquad.health.sleep;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.events.BaseApplication;
import java8.util.stream.StreamSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SleepModule extends Module {
	public static final String TAG = SleepModule.class.getSimpleName();

	/**
	 * Creates a looping MediaPlayer for the given RawRes sound.
	 *
	 * @param res the sound to create a looping player for
	 * @return a MediaPlayer for the RawRes given
	 */
	public static MediaPlayer playerFor(@RawRes int res) {
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
		players.put("waves", playerFor(R.raw.waves));
		players.put("birds", playerFor(R.raw.birds));
		players.put("crickets", playerFor(R.raw.crickets));
		players.put("rain", playerFor(R.raw.rain));
		players.put("thunder", playerFor(R.raw.thunder));
		players.put("fire", playerFor(R.raw.fire));
		players.put("wind", playerFor(R.raw.wind));
		return players;
	}

	Map<String, MediaPlayer> players = players();

	public SleepModule() {
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
	}
}
