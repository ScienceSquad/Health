package com.sciencesquad.health.sleep;

import android.util.Log;
import android.util.Pair;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

/**
 * Sleep Module
 */
public class SleepModule extends Module {
	public static final String TAG = SleepModule.class.getSimpleName();
	static { Module.registerModule(SleepModule.class); }

	private DataContext<SleepDataModel> dataContext;

	public void init() {
		this.dataContext = new RealmContext<>();
		this.dataContext.init(BaseApp.app(), SleepDataModel.class, "sleep.realm");

		Log.i(TAG, "SleepModule initializing...");

		SleepMonitoringService.startMonitoringService();
		//BaseApp.app().sendBroadcast(new Intent(BaseApp.app(), SleepStartReceiver.class));
	}

	@Override
	public Pair<String, Integer> identifier() {
		return new Pair<>("Sleep", R.drawable.ic_music_circle);
	}
}
