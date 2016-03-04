package com.sciencesquad.health.prescriptions;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sciencesquad.health.alarm.AlarmSender;
import com.sciencesquad.health.events.BaseApplication;

/**
 * Created by andrew on 3/4/16.
 */
public class PrescriptionAlarm {


	static final String prefix = "com.sciencesquad.health.prescriptions";
	static final String PRESCRIPTION_NAME = prefix + ".name";
	static final String PRESCRIPTION_DOSAGE = prefix + ".dosage";

	public static void setAlarm(PrescriptionModel prescriptionModel, Context context) {
		AlarmSender alarm = new AlarmSender();
		alarm.setTimeInMillis(prescriptionModel.getStartDate());
		Log.d("PrescripAlarm", alarm.getFieldString(AlarmSender.HOUR_OF_DAY, true) + ":" + alarm.getFieldString(AlarmSender.MINUTE, true));
		// alarm.setRepeat(true);
		// alarm.setInterval(prescriptionModel.getRepeatDuration());


		Intent intent = new Intent(BaseApplication.application(), PrescriptionAlarmReceiver.class);
		intent.putExtra(PRESCRIPTION_NAME, prescriptionModel.getName());
		intent.putExtra(PRESCRIPTION_DOSAGE, prescriptionModel.getDosage());
		alarm.setAlarm(BaseApplication.application(), intent);
	}
}
