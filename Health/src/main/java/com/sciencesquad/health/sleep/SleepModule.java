package com.sciencesquad.health.sleep;

import android.util.Log;
import android.util.Pair;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

import java.util.Calendar;

/**
 * Sleep Module
 */
public class SleepModule extends Module {
	public static final String TAG = SleepModule.class.getSimpleName();

	private DataContext<SleepDataModel> dataContext;

	public void init() {
		Log.i(TAG, "SleepModule initializing...");

		this.dataContext = new RealmContext<>();
		this.dataContext.init(BaseApp.app(), SleepDataModel.class, "sleep.realm");

		SleepMonitoringService.startMonitoringService();
	}

	public void setTileCycle(int tile, int cycle) {
		if (tile >= SoundService.wav_map.length) return;
		String name = SoundService.wav_map[tile];
		Log.i(TAG, "Sound \"" + name + "\" updated to volume " + (cycle / 4.0f) * 100.0f + ".");
	}

	public static Calendar get() {
		Calendar reference = Calendar.getInstance();
		reference.add(Calendar.DATE, 1);
		reference.set(Calendar.HOUR_OF_DAY, 8);
		reference.set(Calendar.MINUTE, 30);

		Calendar c = Calendar.getInstance();
		Calendar d = Calendar.getInstance();
		do {
			c.roll(Calendar.MINUTE, 90);
		} while (c.before(reference));
		return c;
	}

	@Override
	public Pair<String, Integer> identifier() {
		return new Pair<>("Sleep", R.drawable.ic_music_circle);
	}
}
