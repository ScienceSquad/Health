package com.sciencesquad.health.prescriptions;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sciencesquad.health.core.alarm.AlarmModule;

/**
 * Created by andrew on 3/4/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

	public void doSomethingImportant(int alarmId) {
		AlarmModule alarmModule = new AlarmModule();
		alarmModule.handleAlarm(alarmId);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int alarmId = intent.getIntExtra("alarmId", 0);

		if (alarmId > 0) doSomethingImportant(alarmId);
	}
}
