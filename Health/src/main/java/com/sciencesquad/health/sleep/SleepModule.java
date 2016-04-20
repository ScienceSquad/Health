package com.sciencesquad.health.sleep;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.EventBus;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import java8.util.stream.StreamSupport;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Sleep Module
 */
public class SleepModule extends Module {
	public static final String TAG = SleepModule.class.getSimpleName();

	/**
	 * Map between integers to days of the week.
	 */
	public static final String[] week_days = new String[] {
			"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"
	};

	/**
	 * Map between integers to actual WAV file names.
	 */
	public static final String[] wav_map = new String[] {
			"waves", "birds", "crickets", "rain",
			"thunder", "fire", "wind"
	};

	/**
	 * Map between integers to actual WAV resource IDs.
	 */
	private static final int[] res_map = new int[] {
			R.raw.waves, R.raw.birds, R.raw.crickets, R.raw.rain,
			R.raw.thunder, R.raw.fire, R.raw.wind
	};

	/**
	 * Loads all Sleep-specific RawRes sounds into MediaPlayers.
	 * Each player is encoded in the Map by its name as a String.
	 *
	 * @return a map of all Sleep-specific sounds to MediaPlayers.
	 */
	private MediaPlayer getPlayer(int pos) {
		if (pos < 0 || pos >= wav_map.length)
			return null;

		final MediaPlayer p = this.players.get(wav_map[pos]);
		if (p != null)
			return p;

		final MediaPlayer player = MediaPlayer.create(app(), res_map[pos]);
		player.setWakeMode(app(), PowerManager.PARTIAL_WAKE_LOCK);
		player.setLooping(true);

		this.players.put(wav_map[pos], player);
		return player;
	}

	public static String nameForPosition(int pos) {
		if (pos < 0 || pos >= wav_map.length)
			return "other";
		return wav_map[pos];
	}

	public String timeForDayOfWeek(int pos) {
		if (pos < 0 || pos >= week_days.length)
			return "???";
		return alarms[pos].format(DateTimeFormatter.ofPattern("h:mm a"));
	}

	public String dayOfWeek(int pos) {
		if (pos < 0 || pos >= week_days.length)
			return "???";
		return DayOfWeek.of(pos + 1).getDisplayName(TextStyle.SHORT, Locale.getDefault());
	}

	/**
	 * The SleepModule-specific DataContext for storing sleep info.
	 */
	private DataContext<SleepDataModel> dataContext;

	/**
	 * The private group of `MediaPlayer`s used by the SoundService.
	 * A set of `ValueAnimator`s is used to meander volume levels.
	 */
	private Map<String, MediaPlayer> players = new HashMap<>();

	/**
	 * A quick cache of the alarm times per day of week.
	 */
	/*package*/ LocalTime[] alarms = new LocalTime[] {
			LocalTime.of(9, 00), LocalTime.of(6, 30), LocalTime.of(8, 30),
			LocalTime.of(6, 30), LocalTime.of(6, 00), LocalTime.of(6, 30),
			LocalTime.of(9, 30),
	};

	/**
	 * @see Module
	 */
	@Override
	public void onStart() {

		// Initialize and acquire the Sleep database.
		this.dataContext = new RealmContext<>();
		this.dataContext.init(app(), SleepDataModel.class, "sleep.realm");

		// Prepare to stop sleep sounds if needed.
		track(bus().subscribe("StopSleepSoundsEvent", null, ev -> {
			Log.i(TAG, "Turning off sleep sounds...");
			this.stopSleepSounds();
		}));

		// ----
		// TODO:
		// ----

		// Prepare to handle a wake-up alarm if needed.
		track(bus().subscribe("SleepWakeAlarmEvent", null, ev -> {
			Log.i(TAG, "Waking up to an alarm...");
			app().display("Good morning!", true);
			app().vibrate(3000);
		}));
	}

	/**
	 * @see Module
	 */
	@Override
	public void onStop() {
		this.stopSleepSounds();

		// Release all MediaPlayers.
		StreamSupport.stream(this.players.keySet()).forEach(key -> {
			this.players.get(key).stop();
			this.players.get(key).release();
		});
	}

	/**
	 * Begin the sleep sounds and initialize the MediaPlayers.
	 */
	protected Notification sleepSoundsNote() {
		Intent intent = EventBus.intentForEvent(app(), "StopSleepSoundsEvent");
		PendingIntent pending = PendingIntent.getBroadcast(app(), 0, intent, 0);
		return new Notification.Builder(app())
				.setSmallIcon(R.drawable.ic_music_circle)
				.setContentTitle("Playing sleep sounds...")
				.setContentText("Tap to stop playing sounds.")
				.setContentIntent(pending)
				.setDeleteIntent(pending)
				.setAutoCancel(true)
				.setOngoing(true)
				.build();
	}

	/**
	 * Stop the sleep sounds and release the MediaPlayers.
	 */
	protected void stopSleepSounds() {

		// Stop, release, and cancel!
		StreamSupport.stream(this.players.keySet()).forEach(key -> {
			this.players.get(key).pause();
			//this.players.get(key).release();
		});

		// Notify the world of what we did.
		app().cancel("SleepSounds");
		bus().publish("StoppedSleepSoundsEvent", this);
	}

	/**
	 * Set an individual MediaPlayer's volume for Sleep Sounds.
	 *
	 * @param position the position of the MediaPlayer
	 * @param cycle the percentage cycle for its volume
	 */
	public void setPlayerVolume(int position, int cycle) {

		// Ensure we have a player to set, otherwise bail.
		if (position < 0 || position >= wav_map.length) return;
		MediaPlayer player = this.getPlayer(position);
		if (player == null) return;

		// Set the volume and ensure the player is
		// either started or paused, depending on cycle.
		player.setVolume(cycle / 4.0f, cycle / 4.0f);
		if (cycle > 0 && !player.isPlaying())
			player.start();
		else if (cycle == 0 && player.isPlaying())
			player.pause();

		// If we have at least one playing sound, show a notification.
		long current = StreamSupport
				.stream(this.players.entrySet())
				.map(Map.Entry::getValue)
				.filter(MediaPlayer::isPlaying)
				.count();
		if (current > 0)
			app().notify("SleepSounds", sleepSoundsNote());
		else app().cancel("SleepSounds");
	}
}
