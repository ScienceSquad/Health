package com.sciencesquad.health.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sciencesquad.health.R;

/**
 * Created by andrew on 3/2/16.
 */
public class EmergencyNotification {

	public static void
	sendNotification(Context context, CharSequence title, CharSequence content) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setOngoing(true)
				.setContent(new RemoteViews(context.getPackageName(), R.layout.emergency_card))
				.setSmallIcon(R.drawable.ic_menu_manage);

		int mNotificationId = 1;

		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}
}