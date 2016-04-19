package com.sciencesquad.health.prescriptions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.HostActivity;

/**
 * FIXME Deprecate in favor of using the EventBus.
 */
public class PrescriptionAlarmReceiver extends BroadcastReceiver {

	static final String NOTIFICATION_TITLE = "Prescription Reminder";
	static final int SMALL_ICON = R.drawable.ic_menu_manage;
	static final int NOTIFICATION_ID = 0xABABEDAD;

	@Override
	public void onReceive(Context context, Intent intent) {
		String name = intent.getStringExtra(PrescriptionAlarm.PRESCRIPTION_NAME);
		int dosage = intent.getIntExtra(PrescriptionAlarm.PRESCRIPTION_DOSAGE, 0);
		Log.d("PAReceiver", "Name: " + name + " Dosage: " + String.valueOf(dosage));

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setContentTitle(NOTIFICATION_TITLE)
				.setContentText("Don't forget to take " + String.valueOf(dosage) + " " + name + "!")
				.setSmallIcon(SMALL_ICON);

		Intent i = new Intent(context, HostActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, i, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager notifyManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification note = mBuilder.build();

		// Send the notification!
		notifyManager.notify(NOTIFICATION_ID, note);
	}
}
