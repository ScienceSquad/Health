package com.sciencesquad.health.sleep;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;
import com.sciencesquad.health.core.BaseApp;

public class SleepWakeUpReceiver extends WakefulBroadcastReceiver {
	private static final String TAG = SleepWakeUpReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Woke up!");
		Toast.makeText(context, "Woke up!", Toast.LENGTH_LONG).show();
		BaseApp.app().vibrate(3000);
	}
}
