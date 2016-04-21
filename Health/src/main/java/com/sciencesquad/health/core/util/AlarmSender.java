package com.sciencesquad.health.core.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.alarm.AlarmModule;

/**
 * Created by andrew on 3/3/16.
 * Maintains a date and time and sends an alarm to AlarmManager
 */
public class AlarmSender extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmModule alarmModule = Module.of(AlarmModule.class);
		alarmModule.sendAll();
	}

}
