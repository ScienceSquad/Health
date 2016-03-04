package com.sciencesquad.health.prescriptions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andrew on 3/4/16.
 */
public class PrescriptionAlarmReceiver extends BroadcastReceiver {

	public void doSomethingImportant(String name, int dosage) {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String name = intent.getStringExtra(PrescriptionAlarm.PRESCRIPTION_NAME);
		int dosage = intent.getIntExtra(PrescriptionAlarm.PRESCRIPTION_DOSAGE, 0);

		doSomethingImportant(name, dosage);
	}
}
