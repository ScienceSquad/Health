package com.sciencesquad.health.prescriptions;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.sciencesquad.health.R;
import com.sciencesquad.health.alarm.AlarmModel;
import com.sciencesquad.health.alarm.AlarmModule;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.AlarmSender;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentPrescriptionBinding;

import java.util.Calendar;

import io.realm.RealmResults;

/**
 * Created by andrew on 4/7/16.
 */
public class PrescriptionFragment extends BaseFragment {
	public static final String TAG = PrescriptionFragment.class.getSimpleName();

	private PrescriptionModule prescriptionModule;
	private AlarmModule alarmModule;

	public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

		private RealmResults<PrescriptionModel> mPrescriptions;

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		public class ViewHolder extends RecyclerView.ViewHolder {
			// each data item is just a string in this case
			public LinearLayout mLinearLayout;
			public ViewHolder(LinearLayout v) {
				super(v);
				mLinearLayout = v;
			}

			public void setContent(PrescriptionModel item) {
				String name = item.getName();
				int dosage = item.getDosage();

				AlarmModel alarm = alarmModule.getAlarmById(item.getAlarmID());

				alarmModule.setTimeInMillis(alarm.getTime());

				TextView alarmTime = (TextView) mLinearLayout.findViewById(R.id.alarm_time);
				TextView alarmPeriod = (TextView) mLinearLayout.findViewById(R.id.alarm_time_period);
				TextView alarmDosage = (TextView) mLinearLayout.findViewById(R.id.alarm_dosage);
				RelativeLayout removeButton = (RelativeLayout) mLinearLayout.findViewById(R.id.alarm_remove);
				removeButton.setOnClickListener((view) -> {
					Log.d(TAG, "Should remove alarm");
					removeAlarm(item);
					updateAlarmList();
				});
				alarmTime.setText(alarmModule.getPrettyTime());
				alarmPeriod.setText(alarmModule.getTimePeriod());
				alarmDosage.setText(String.valueOf(dosage) + " " + name + " every day");
			}
		}

		// Provide a suitable constructor (depends on the kind of dataset)
		public ListAdapter(RealmResults<PrescriptionModel> myDataset) {
			mPrescriptions = myDataset;
		}

		// Create new views (invoked by the layout manager)
		@Override
		public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
														 int viewType) {
			// create a new view
			LinearLayout v = (LinearLayout) getInflater()
					.inflate(R.layout.prescription_alarm_list_item, parent, false);
			// set the view's size, margins, paddings and layout parameters


			ViewHolder vh = new ViewHolder(v);
			return vh;
		}

		// Replace the contents of a view (invoked by the layout manager)
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			// - get element from your dataset at this position
			// - replace the contents of the view with that element
			holder.setContent(mPrescriptions.get(position));

		}

		// Return the size of your dataset (invoked by the layout manager)
		@Override
		public int getItemCount() {
			return mPrescriptions.size();
		}
	}

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

	public void scrollAlarmListToBottom() {
		xml().alarmList.scrollToPosition(xml().alarmList.getAdapter().getItemCount() - 1);
	}

	private void removeAlarm(PrescriptionModel item) {
		prescriptionModule.removePrescription(item);
	}

	private void setPrescriptionAlarm(View view) {
			/* PrescriptionAlarmReceiver receiver = new PrescriptionAlarmReceiver();
			receiver.doSomethingImportant("Tylenol", 5); */
		View alarmDialog = getInflater().inflate(R.layout.fragment_prescription_alarm_dialog, null);
		new MaterialStyledDialog(getActivity())
				.setCustomView(alarmDialog)
				.withDialogAnimation(true, Duration.FAST)
				.setCancelable(false)
				.setPositive("Accept",
						(dialog, which) -> {
							Log.d(TAG,"Accepted!");
							EditText nameInput = (EditText) alarmDialog.findViewById(R.id.prescription_name);
							EditText dosageInput = (EditText) alarmDialog.findViewById(R.id.prescription_dosage);
							String name = nameInput.getText().toString();
							String dosageString = dosageInput.getText().toString();
							int dosage = 0;
							if ((name.length() <= 0) || (dosageString.length() <= 0)) {
								Snackbar.make(view, "Invalid input", Snackbar.LENGTH_LONG)
										.setAction("Action", null).show();
								return;
							}
							else {
								dosage = Integer.parseInt(dosageString);
							}
							prescriptionModule.setName(name);
							prescriptionModule.setDosage(dosage);
							// Set alarm data
							alarmModule.setTimeInMillis(System.currentTimeMillis());
							// Tie alarm to prescription
							prescriptionModule.setAlarmID(alarmModule.add().getAlarmId());
							prescriptionModule.addPrescription();
							updateAlarmList();
							scrollAlarmListToBottom();
						})
				.setNegative("Decline",
						(dialog, which) -> Log.d(TAG,"Declined!"))
				.show();
	}

	private void sendEmergencyNotification() {
		if (EmergencyNotification.hasSent()) {
			EmergencyNotification.closeNotification(getActivity());
		}
		else {
			EmergencyNotification.sendNotification(getActivity(), "THIS IS AN EMERGENCY",
					"This is actually a pretty big deal, you should be concerned.");
		}
	}

	public void updateAlarmList() {

		RealmResults<PrescriptionModel> prescriptions = prescriptionModule.getPrescriptions();
		ListAdapter listAdapter = new ListAdapter(prescriptions);
		xml().alarmList.setAdapter(listAdapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		prescriptionModule = new PrescriptionModule();
		alarmModule = new AlarmModule();

		prescriptionModule.clearAllPrescriptions();

		// Set Prescription data
		prescriptionModule.setName("foolenol")
				.setDosage(5);

		// Set alarm data
		alarmModule.setTimeInMillis(System.currentTimeMillis())
				.setRepeatInterval(AlarmModule.RepeatInterval.DAILY);

		// Tie alarm to prescription
		prescriptionModule.setAlarmID(alarmModule.add().getAlarmId());

		prescriptionModule.addPrescription();

		// Set Prescription data
		prescriptionModule.setName("barlisil");
		prescriptionModule.setDosage(3);

		// Set alarm data
		alarmModule.setTimeInMillis(System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR)
				.setRepeatInterval(AlarmModule.RepeatInterval.DAILY);

		// Tie alarm to prescription
		prescriptionModule.setAlarmID(alarmModule.add().getAlarmId());

		prescriptionModule.addPrescription();

		xml().alarmList.setLayoutManager(new LinearLayoutManager(getActivity()));
		updateAlarmList();

		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		Drawable emergency = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alert);
		emergency.setTint(Color.WHITE);
		Drawable alarms = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alarm);
		alarms.setTint(Color.WHITE);

		xml().fabEmergency.setImageDrawable(emergency);
		xml().fabEmergency.setOnClickListener(view2 -> {
			sendEmergencyNotification();
		});

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);

		xml().fabAlarms.setImageDrawable(alarms);
		xml().fabAlarms.setOnClickListener(view2 -> {
			setPrescriptionAlarm(view2);
		});
	}
}
