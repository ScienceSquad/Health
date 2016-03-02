package com.sciencesquad.health.sleep;

import android.util.Pair;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.DataContext;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.nutrition.NutritionModel;

/**
 * Sleep Module
 */
public class SleepModule extends Module {
	public static final String TAG = SleepModule.class.getSimpleName();
	static { Module.registerModule(SleepModule.class); }

	private DataContext<NutritionModel> dataContext;

	public void init() {
		this.dataContext = new RealmContext<>();
		this.dataContext.init(BaseApplication.application(), NutritionModel.class, "nutrition.realm");

		// FIXME: Put this in a unit test or hook it up to the UI.
		//SoundService.startSoundService();
	}

	@Override
	public Pair<String, Integer> identifier() {
		return new Pair<>("Sleep", R.drawable.ic_music_circle);
	}
}
