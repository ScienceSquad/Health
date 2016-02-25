package com.sciencesquad.health.sleep;

import com.sciencesquad.health.core.Module;

public class SleepModule extends Module {
	public static final String TAG = SleepModule.class.getSimpleName();

	public SleepModule() {
		SoundService.startSoundService(true);
	}
}
