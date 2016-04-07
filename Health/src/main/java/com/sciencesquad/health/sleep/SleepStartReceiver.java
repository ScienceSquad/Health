package com.sciencesquad.health.sleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import com.sciencesquad.health.R;

public class SleepStartReceiver extends BroadcastReceiver {
	private static final String TAG = SleepStartReceiver.class.getSimpleName();

	public static void enforceCalibrationBeforeStartingSleep(final Context context,
															 final Intent service, final Intent activity) {

	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.i(TAG, "Starting sleep...");
		new AsyncTask<Void, Void, Void>() {

			Intent serviceIntent;

			@Override
			protected Void doInBackground(Void... params) {
				final SharedPreferences userPrefs = context.getSharedPreferences(
						SleepMonitoringService.PREFERENCES, 0);
				final float alarmTriggerSensitivity = userPrefs.getFloat(
						context.getString(R.string.pref_alarm_trigger_sensitivity),
						SleepMonitoringService.DEFAULT_ALARM_SENSITIVITY);
				final int sensorDelay = Integer.parseInt(userPrefs.getString(
						context.getString(R.string.pref_sensor_delay), ""
								+ SensorManager.SENSOR_DELAY_NORMAL));
				final boolean useAlarm = userPrefs.getBoolean(
						context.getString(R.string.pref_use_alarm), false);
				final int alarmWindow = Integer.parseInt(userPrefs.getString(
						context.getString(R.string.pref_alarm_window), "-1"));
				final boolean airplaneMode = userPrefs.getBoolean(
						context.getString(R.string.pref_airplane_mode), false);
				final boolean silentMode = userPrefs.getBoolean(
						context.getString(R.string.pref_silent_mode), false);
				final boolean forceScreenOn = userPrefs.getBoolean(
						context.getString(R.string.pref_force_screen), false);

				serviceIntent = new Intent(context, SleepMonitoringService.class);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_ALARM, alarmTriggerSensitivity);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_SENSOR_DELAY, sensorDelay);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_USE_ALARM, useAlarm);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_ALARM_WINDOW, alarmWindow);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_AIRPLANE_MODE, airplaneMode);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_SILENT_MODE, silentMode);
				serviceIntent.putExtra(SleepMonitoringService.EXTRA_FORCE_SCREEN_ON, forceScreenOn);

				Log.i(TAG, "Configuration done!");
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.i(TAG, "Starting service...");
				context.startService(serviceIntent);

				//Intent a = null;//new Intent(context, SleepActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				/*
				final SharedPreferences userPrefs = context.getSharedPreferences(
						SleepMonitoringService.PREFERENCES, Context.MODE_PRIVATE);
				final int prefsVersion = userPrefs.getInt(SleepMonitoringService.PREFERENCES, 0);
				String message = "";
				if (prefsVersion == 0) {
					message = context.getString(R.string.message_not_calibrated);
				} else if (prefsVersion != context.getResources().getInteger(R.integer.prefs_version)) {
					message = context.getString(R.string.message_prefs_not_compatible);
					context.getSharedPreferences(SleepMonitoringService.PREFERENCES, 0).edit().clear().commit();
					//PreferenceManager.setDefaultValues(context, PREFERENCES, 0,
					//		R.xml.settings, true);
				}

				if (message.length() > 0) {
					message += context.getString(R.string.message_recommend_calibration);
					Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				} else if (serviceIntent != null && a != null) {
					context.startService(serviceIntent);
					a.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(a);
				}
				*/
			}

		}.execute();
	}
}
