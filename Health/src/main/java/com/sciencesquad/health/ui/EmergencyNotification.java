package com.sciencesquad.health.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by andrew on 3/2/16.
 */
public class EmergencyNotification {

	public static void
	sendNotification(Context context, CharSequence title, CharSequence content) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setOngoing(true)
				.setContentTitle(title)
				.setContentText(content);

		int mNotificationId = 1;

		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}
}