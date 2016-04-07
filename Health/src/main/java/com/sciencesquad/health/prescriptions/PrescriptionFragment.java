package com.sciencesquad.health.prescriptions;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.databinding.FragmentPrescriptionBinding;

import java.util.ArrayList;

/**
 * Created by andrew on 4/7/16.
 */
public class PrescriptionFragment extends BaseFragment {
	public static final String TAG = PrescriptionFragment.class.getSimpleName();

	public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

		private ArrayList<String> mPrescriptions;

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

			public void setContent(String item) {
			}
		}

		// Provide a suitable constructor (depends on the kind of dataset)
		public ListAdapter(ArrayList<String> myDataset) {
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

	private void setPrescriptionAlarm() {
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

			PrescriptionAlarm.setAlarm(prescriptionModel, getActivity());
		}
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
		ArrayList<String> stringArrayList = new ArrayList<String>();
		int num_alarms = 10;
		for (int i = 0; i < num_alarms; i++) {
			stringArrayList.add("blah");
		}
		ListAdapter listAdapter = new ListAdapter(stringArrayList);
		xml().alarmList.setAdapter(listAdapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		xml().alarmList.setLayoutManager(new LinearLayoutManager(getActivity()));
		updateAlarmList();

		Drawable emergency = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alert);
		emergency.setTint(Color.WHITE);
		Drawable alarms = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alarm);
		alarms.setTint(Color.WHITE);

		xml().fabEmergency.setImageDrawable(emergency);
		xml().fabEmergency.setOnClickListener(view2 -> {
			sendEmergencyNotification();
		});

		xml().fabAlarms.setImageDrawable(alarms);
		xml().fabAlarms.setOnClickListener(view2 -> {
			setPrescriptionAlarm();
		});
	}
}
