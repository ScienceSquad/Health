package com.sciencesquad.health.prescriptions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sciencesquad.health.alarm.AlarmModel;
import com.sciencesquad.health.alarm.AlarmModule;
import com.sciencesquad.health.core.HostActivity;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;

/**
 * Created by andrew on 3/4/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

	static final String NOTIFICATION_TITLE = "Prescription Reminder";
	static final int SMALL_ICON = R.drawable.ic_menu_manage;
	static final int NOTIFICATION_ID = 0xABABEDAD;



	public void doSomethingImportant(String name, int dosage) {
		Context context = BaseApp.app();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setContentTitle(NOTIFICATION_TITLE)
				.setContentText("Don't forget to take " + String.valueOf(dosage) + " " + name + "!")
				.setSmallIcon(SMALL_ICON);

		Intent intent = new Intent(context, HostActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager notifyManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification note = mBuilder.build();

		// Send the notification!
		notifyManager.notify(NOTIFICATION_ID, note);

	}

	@Override
	public void onReceive(Context context, Intent intent) {

		// Placeholders :(
		String name = "Prescription name";
		int dosage = 0;

		int alarmId = intent.getIntExtra("alarmId", 0);
		if (alarmId == 0) {
			return;
		}
		AlarmModule alarmModule = new AlarmModule();
		AlarmModel alarm = alarmModule.getAlarmById(alarmId);

		Log.d("PAReceiver", "Name: " + name + " Dosage: " + String.valueOf(dosage));

		doSomethingImportant(name, dosage);
	}
}
