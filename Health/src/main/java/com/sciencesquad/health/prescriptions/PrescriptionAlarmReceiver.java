package com.sciencesquad.health.prescriptions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.sciencesquad.health.MainActivity;
import com.sciencesquad.health.R;
import com.sciencesquad.health.events.BaseApplication;

/**
 * Created by andrew on 3/4/16.
 */
public class PrescriptionAlarmReceiver extends BroadcastReceiver {

	static final String NOTIFICATION_TITLE = "Prescription Reminder";
	static final int SMALL_ICON = R.drawable.ic_menu_manage;
	static final int NOTIFICATION_ID = 0xABABEDAD;



	public void doSomethingImportant(String name, int dosage) {
		Context context = BaseApplication.application();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setContentTitle(NOTIFICATION_TITLE)
				.setContentText("Don't forget to take " + String.valueOf(dosage) + " " + name + "!")
				.setSmallIcon(SMALL_ICON);

		Intent intent = new Intent(context, MainActivity.class);
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

		String name = intent.getStringExtra(PrescriptionAlarm.PRESCRIPTION_NAME);
		int dosage = intent.getIntExtra(PrescriptionAlarm.PRESCRIPTION_DOSAGE, 0);

		Log.d("PAReceiver", "Name: " + name + " Dosage: " + String.valueOf(dosage));

		doSomethingImportant(name, dosage);
	}
}
