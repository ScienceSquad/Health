package com.sciencesquad.health.prescriptions;

import android.content.Context;
import android.content.Intent;

import com.sciencesquad.health.alarm.AlarmSender;

/**
 * Created by andrew on 3/4/16.
 */
public class PrescriptionAlarm {

	static final String PRESCRIPTION_NAME = "prescriptionName";
	static final String PRESCRIPTION_DOSAGE = "prescriptionDosage";

	public void setAlarm(PrescriptionModel prescriptionModel, Context context) {
		AlarmSender alarm = new AlarmSender();
		alarm.setTimeInMillis(prescriptionModel.getStartDate());
		alarm.setRepeat(true);
		alarm.setInterval(prescriptionModel.getRepeatDuration());


		Intent intent = new Intent(context, PrescriptionAlarmReceiver.class);
		intent.putExtra(PRESCRIPTION_NAME, prescriptionModel.getName());
		intent.putExtra(PRESCRIPTION_DOSAGE, prescriptionModel.getDosage());
		alarm.setAlarm(context, intent);
	}
}
