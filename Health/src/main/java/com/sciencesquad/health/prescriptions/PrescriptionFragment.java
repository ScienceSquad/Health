package com.sciencesquad.health.prescriptions;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
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
import android.widget.*;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.alarm.AlarmModel;
import com.sciencesquad.health.core.alarm.AlarmModule;
import com.sciencesquad.health.core.ui.BottomSheet;
import com.sciencesquad.health.core.ui.EmergencyNotification;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.ui.Stopwatch;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentPrescriptionBinding;
import com.sciencesquad.health.nutrition.NutritionFragment;
import com.sciencesquad.health.overview.OverviewFragment;
import com.sciencesquad.health.overview.SuggestionModule;
import com.sciencesquad.health.run.RunFragment;
import com.sciencesquad.health.sleep.SleepFragment;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.workout.WorkoutFragment;
import io.realm.RealmResults;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;

/**
 * Created by andrew on 4/7/16.
 */
public class PrescriptionFragment extends BaseFragment {
	public static final String TAG = PrescriptionFragment.class.getSimpleName();

	private PrescriptionModule prescriptionModule;
	private AlarmModule alarmModule;

	public class IntervalSelected implements AdapterView.OnItemSelectedListener {

		private int alarmId;

		public IntervalSelected(int alarmId) {
			this.alarmId = alarmId;
		}

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			alarmModule.setRepeatInterval(this.alarmId, position);
		}

		public void onNothingSelected(AdapterView<?> parent) { }
	}


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

				int alarmId = item.getAlarmID();
				AlarmModel alarm = alarmModule.getAlarmById(alarmId);

				alarmModule.setTimeInMillis(alarm.getTime());

				View.OnClickListener timeListener = (view) -> {
					AlarmDialog dialog = new AlarmDialog();
					dialog.callTimePicker(alarmId, () -> updateAlarmList(), getActivity(), false);
				};

				TextView alarmTime = (TextView) mLinearLayout.findViewById(R.id.alarm_time);
				alarmTime.setOnClickListener(timeListener);

				TextView alarmPeriod = (TextView) mLinearLayout.findViewById(R.id.alarm_time_period);
				alarmPeriod.setOnClickListener(timeListener);

				TextView alarmDosage = (TextView) mLinearLayout.findViewById(R.id.alarm_dosage);
				alarmDosage.setOnClickListener((view) -> setPrescriptionAlarm(view, item));

				Spinner spinner = (Spinner) mLinearLayout.findViewById(R.id.alarm_repeat);
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
						R.array.repeat_types, android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);
				spinner.setSelection(alarm.getRepeatInterval());
				spinner.setOnItemSelectedListener(new IntervalSelected(alarmId));


				FrameLayout removeButton = (FrameLayout) mLinearLayout.findViewById(R.id.alarm_remove);
				Switch toggleSwitch = (Switch) mLinearLayout.findViewById(R.id.alarm_switch);
				toggleSwitch.setChecked(alarm.getActive());
				removeButton.setOnClickListener((view) -> {
					Log.d(TAG, "Should remove alarm");
					removeAlarm(item);
					updateAlarmList();
				});
				toggleSwitch.setOnClickListener((view) -> {
					Log.d(TAG, "Should switch alarm");
					alarmModule.setActive(alarm, toggleSwitch.isChecked());
				});
				alarmTime.setText(alarmModule.getPrettyTime());
				alarmPeriod.setText(alarmModule.getTimePeriod());
				alarmDosage.setText(String.valueOf(dosage) + " " + name);
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

	private void setPrescriptionAlarm(View view, PrescriptionModel prescription) {
		String currentName = "";
		String currentDosage = "";
		if (prescription != null) {
			currentName = prescription.getName();
			currentDosage = String.valueOf(prescription.getDosage());
		}

		BottomSheet dialog = new BottomSheet(getActivity());

		dialog.addTextInput("prescription_name", "Name", currentName)
				.addTextInput("prescription_dosage", "Dosage", currentDosage)
				.addAction("save", "Save", R.drawable.ic_done)
				.setOnAction("save", () -> {
					String name = dialog.getTextInputValue("prescription_name");
					String dosageString = dialog.getTextInputValue("prescription_dosage");
					int dosage = 0;
					if ((name.length() <= 0) || (dosageString.length() <= 0)) {
						Snackbar.make(view, "Invalid input", Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
						return;
					}
					else {
						dosage = Integer.parseInt(dosageString);
					}
					if (prescription == null) {
						prescriptionModule.setName(name);
						prescriptionModule.setDosage(dosage);
						// Set alarm data
						alarmModule.setTimeInMillis(System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR);
						// Tie alarm to prescription
						prescriptionModule.setAlarmID(alarmModule.add().getAlarmId());
						prescriptionModule.addPrescription((random() * 100) % 2 > 0); // random
					}
					else {
						prescriptionModule.setName(prescription, name);
						prescriptionModule.setDosage(prescription, dosage);
					}
					updateAlarmList();
				})
				.addAction("cancel", "Cancel", R.drawable.places_ic_clear)
				.setOnAction("cancel", () -> Log.d(TAG, "Prescription canceled!"))
				.show();
	}

	private void setPrescriptionAlarm(View view) {
		setPrescriptionAlarm(view, null);
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

	private void updateTime() {
		String hours = xml().countdownHours.getText().toString();
		String minutes = xml().countdownMinutes.getText().toString();
		String seconds = xml().countdownSeconds.getText().toString();
		Stopwatch stopwatch = xml().stopwatch.getStopwatch();
		stopwatch.setMillis(0);
		try {
			if (hours.length() > 0)
				stopwatch.addTime(Integer.parseInt(hours), TimeUnit.HOURS);
			if (minutes.length() > 0)
				stopwatch.addTime(Integer.parseInt(minutes), TimeUnit.MINUTES);
			if (seconds.length() > 0)
				stopwatch.addTime(Integer.parseInt(seconds), TimeUnit.SECONDS);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		xml().stopwatch.postInvalidate();
	}

	public void openAssociatedModule(SuggestionModule.AssociatedModule module) {
		FragmentTransaction transaction;
		switch (module) {
			case NUTRITION:
				transaction = getFragmentManager().beginTransaction();
				new NutritionFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case OVERVIEW:
				transaction = getFragmentManager().beginTransaction();
				new OverviewFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case PRESCRIPTIONS:
				transaction = getFragmentManager().beginTransaction();
				new PrescriptionFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case RUN:
				transaction = getFragmentManager().beginTransaction();
				new RunFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case SLEEP:
				transaction = getFragmentManager().beginTransaction();
				new SleepFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case STEPS:
				transaction = getFragmentManager().beginTransaction();
				new StepsFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case WORKOUT:
				transaction = getFragmentManager().beginTransaction();
				new WorkoutFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			default:
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		prescriptionModule = Module.of(PrescriptionModule.class);
		alarmModule = Module.of(AlarmModule.class);

		prescriptionModule.clearAllPrescriptions();

		// Set Prescription data
		prescriptionModule.setName("foolenol")
				.setDosage(5);

		// Set alarm data
		// Send alarm 5 seconds from now
		alarmModule.setTimeInMillis(System.currentTimeMillis() + (1000 * 5))
				.setRepeatInterval(AlarmModule.RepeatInterval.DAILY)
				.setNumDays(4);

		// Tie alarm to prescription
		prescriptionModule.setAlarmID(alarmModule.add().getAlarmId());

		prescriptionModule.addPrescription(true);

		// Set Prescription data
		prescriptionModule.setName("barlisil");
		prescriptionModule.setDosage(3);

		// Set alarm data
		// Send alarm 15 seconds from now
		alarmModule.setTimeInMillis(System.currentTimeMillis() + (1000 * 10))
				.setRepeatInterval(AlarmModule.RepeatInterval.DAILY);


		// Tie alarm to prescription
		prescriptionModule.setAlarmID(alarmModule.add().getAlarmId());

		prescriptionModule.addPrescription(false);

		xml().alarmList.setLayoutManager(new LinearLayoutManager(getActivity()));
		updateAlarmList();

		xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

		Drawable emergency = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alert);
		emergency.setTint(Color.WHITE);
		Drawable alarms = ContextCompat.getDrawable(getActivity(), R.drawable.ic_alarm);
		alarms.setTint(Color.WHITE);

		/*
		xml().fabEmergency.setImageDrawable(emergency);
		xml().fabEmergency.setOnClickListener(view2 -> {
			sendEmergencyNotification();
		});
		*/

		StaticPagerAdapter.install(xml().pager);
		xml().tabs.setupWithViewPager(xml().pager);

		xml().fabAlarms.setImageDrawable(alarms);
		xml().fabAlarms.setOnClickListener(view2 -> {
			setPrescriptionAlarm(view2);
		});

		Stopwatch stopwatch = xml().stopwatch.getStopwatch();
		TextView typeText = xml().typeText;
		if (stopwatch.getMode() == Stopwatch.WatchMode.DOWN)
			typeText.setText("Down");
		else
			typeText.setText("Up");

		xml().stopwatchType.setChecked(stopwatch.getMode() == Stopwatch.WatchMode.DOWN);

		xml().stopwatchType.setOnClickListener((view2) -> {
			Log.d(TAG, "STOPWATCH - pause");
			stopwatch.pause();
			CompoundButton button = xml().stopwatchType;
			Log.d(TAG, "STOPWATCH - reset");
			Log.d(TAG, "STOPWATCH - setmode");
			if (button.isChecked()) {
				stopwatch.setMode(Stopwatch.WatchMode.DOWN);
				updateTime();
				xml().typeText.setText("Down");
			}
			else {
				stopwatch.setMode(Stopwatch.WatchMode.UP);
				xml().typeText.setText("Up");
			}
			Log.d(TAG, "STOPWATCH - successful setMode");
			stopwatch.reset();
			stopwatch.resetTime();
		});

		xml().setTimeButton.setOnClickListener((view2) -> {
			if (stopwatch.getMode() == Stopwatch.WatchMode.DOWN) {
				stopwatch.pause();
				stopwatch.reset();
				stopwatch.resetTime();
				updateTime();
			}
		});

		xml().lapButton.setOnClickListener((view2) -> {
			stopwatch.addLap();
		});

		xml().page3.setOnItemSelectedListener((item) -> {
			SuggestionModule suggestionModule = Module.of(SuggestionModule.class);
			SuggestionModule.AssociatedModule associatedModule = suggestionModule.stringToModule(item.getModule());
			openAssociatedModule(associatedModule);
		});
	}
}
