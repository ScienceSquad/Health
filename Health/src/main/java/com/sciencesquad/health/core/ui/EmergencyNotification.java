package com.sciencesquad.health.core.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sciencesquad.health.R;

/**
 * Created by andrew on 3/2/16.
 */
public class EmergencyNotification {

	// 9 SAD DADS
	// 95 A.D.D. ADS
	private static final int notificationId = 0x95ADDAD5;
	private static Runnable onNotificationSend;
	private static boolean sent = false;
	private static PendingIntent resultPendingIntent;

	private static NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static void sendNotification(Context context, CharSequence title, CharSequence content) {
		if (sent) return;

		// Create RemoteView for standard notification
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.emergency_card);

		// Set title and content of notification
		rv.setTextViewText(R.id.emc_title, title);
		rv.setTextViewText(R.id.emc_content, content);

		// Create builder for notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setOngoing(true)
				.setContent(rv)
				.setSmallIcon(R.drawable.ic_alert);

		if (resultPendingIntent != null)
			mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager notifyManager = getNotificationManager(context);

		Notification note = mBuilder.build();

		// Create RemoteView for expanded notification
		RemoteViews rv_lg = new RemoteViews(context.getPackageName(), R.layout.emergency_card_lg);

		// Set title and content of expanded notification
		rv_lg.setTextViewText(R.id.emc_title_lg, title);
		rv_lg.setTextViewText(R.id.emc_content_lg, content);

		note.bigContentView = rv_lg;

		// Send the notification!
		notifyManager.notify(notificationId, note);

		if (onNotificationSend != null)
			onNotificationSend.run();

		sent = true;
	}

	public static void closeNotification(Context context) {
		if (!sent) return;

		NotificationManager notifyManager = getNotificationManager(context);
		notifyManager.cancel(notificationId);

		sent = false;
	}

	public static void setOnNotificationSend(Runnable onNotificationSend) {
		EmergencyNotification.onNotificationSend = onNotificationSend;
	}

	public static void setResultPendingIntent(PendingIntent resultPendingIntent) {
		EmergencyNotification.resultPendingIntent = resultPendingIntent;
	}

	public static boolean hasSent() {
		return sent;
	}
}