package com.sciencesquad.health.prescriptions;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.transition.Visibility;
import android.view.View;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.databinding.FragmentPrescriptionBinding;

/**
 * Created by andrew on 4/7/16.
 */
public class PrescriptionFragment extends BaseFragment {
	public static final String TAG = PrescriptionFragment.class.getSimpleName();

	private boolean alarmSet = false;
	AlarmDialog dialog;

	private Runnable createRunnable(AlarmDialog dialog, Context context, Intent intent) {
		return () -> dialog.setAlarm(context, intent);
	}

	public Configuration getConfiguration() {
		return new Configuration(TAG, "Prescription",
				R.drawable.ic_alarm, R.style.AppTheme_Nutrition,
				R.layout.fragment_prescription);
	}

	@Override @SuppressWarnings("unchecked")
	protected FragmentPrescriptionBinding xml() { return super.xml(); }

	@Override
	public void onSetupTransition() {
		this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
		this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Drawable emergency = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alert);
		emergency.setTint(Color.WHITE);
		Drawable alarms = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alarm);
		alarms.setTint(Color.WHITE);

		xml().fabEmergency.setImageDrawable(emergency);
		xml().fabEmergency.setOnClickListener(view2 -> {
			if (EmergencyNotification.hasSent()) {
				EmergencyNotification.closeNotification(getActivity());
			}
			else {
				EmergencyNotification.sendNotification(getActivity(), "THIS IS AN EMERGENCY",
						"This is actually a pretty big deal, you should be concerned.");
			}
		});

		xml().fabAlarms.setImageDrawable(alarms);
		xml().fabAlarms.setOnClickListener(view2 -> {
			/* PrescriptionAlarmReceiver receiver = new PrescriptionAlarmReceiver();
			receiver.doSomethingImportant("Tylenol", 5); */
			if (dialog == null) {
				dialog = new AlarmDialog(getActivity());
			}
			if (!dialog.isSet()) {
				dialog.callDatePicker();
			}
			else {
				AlarmSender alarm = dialog.getAlarm();

				alarm.setInterval(AlarmManager.INTERVAL_DAY);

				PrescriptionModel prescriptionModel = new PrescriptionModel();

				prescriptionModel.setDosage(5);
				prescriptionModel.setName("Tylenol");
				prescriptionModel.setRepeatDuration(alarm.getRepeatInterval());
				prescriptionModel.setStartDate(alarm.getTimeInMillis());

				PrescriptionAlarm.setAlarm(prescriptionModel, view2.getContext());
			}
		});
	}
}
