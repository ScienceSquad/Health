package com.sciencesquad.health.sleep;

import android.util.Log;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.util.Dispatcher;
import com.sciencesquad.health.core.util.X;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

		// Run the sleep monitor for a minute.
		SleepMonitoringService.startMonitoringService();
		Dispatcher.BACKGROUND.run(() -> {
			SleepMonitoringService.stopMonitoringService();
		}, 1L, TimeUnit.MINUTES);

		// Broadcast an event to say that we started up.
		X.of(BaseApp.app()).map(BaseApp::eventBus).let(bus -> {
			bus.subscribe("SleepWakeAlarmEvent", null, ev -> {
				Log.i(TAG, "Woke up!");
				BaseApp.app().display("Woke up!", true);
				BaseApp.app().vibrate(3000);
			});
		});
	}

	public void setTileCycle(int tile, int cycle) {
		if (tile >= SoundService.wav_map.length) return;
		String name = SoundService.wav_map[tile];
		if (!SoundService.isSoundServiceActive())
			SoundService.startSoundService();
		else SoundService.stopSoundService();
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
}
