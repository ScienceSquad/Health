package com.sciencesquad.health.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import com.sciencesquad.health.R;

import java.util.ArrayList;
import java.util.List;

/**
 * BuildRoutineDialogFragment
 * This Dialog allows a user to select exercises to create a routine
 */
public class BuildRoutineDialogFragment extends DialogFragment {
	public String titleThing;


	public static BuildRoutineDialogFragment newInstance(int title) {
	   BuildRoutineDialogFragment frag = new BuildRoutineDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = this.titleThing;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogLayout = inflater.inflate(R.layout.build_routine_layout, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);


		// fill spinner with all different workout "targets" with which a user can filter exercises
		List<String> filter = new ArrayList<String>(WorkoutFragment.exerciseTargets);
		Spinner filterSpinner = (Spinner) dialogLayout.findViewById(R.id.filter_spinner);
		ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		filterSpinner.setAdapter((filterAdapter));
		filterAdapter.clear();
		filterAdapter.addAll(filter);

		// list all user-created exercises in Dialog
		// TODO: Make this a multiple selection list and add list of exercises selected to routine
		ListView exerciseListView = (ListView) dialogLayout.findViewById(R.id.choose_exercises_view);
		ArrayAdapter<ExerciseTypeModel> exerciseListAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.select_dialog_multichoice);
		exerciseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		exerciseListView.setAdapter(exerciseListAdapter);
		exerciseListAdapter.clear();
		exerciseListAdapter.addAll(WorkoutFragment.exerciseTypeModelList);


		// Create button that filters listed exercises based on "target"
		Button button = (Button) dialogLayout.findViewById(R.id.filter_button);
		button.setOnClickListener(butt -> {
			String f = filterSpinner.getSelectedItem().toString();

			exerciseListAdapter.clear(); // clear current list
			for (int i = 0; i < WorkoutFragment.exerciseTypeModelList.size(); i++) {
				if (WorkoutFragment.exerciseTypeModelList.get(i).getTarget().equals(f)) {
					// only add exercises with matching "target" to the filter selected
					exerciseListAdapter.add(WorkoutFragment.exerciseTypeModelList.get(i));
				}
			}
		});

		builder.setPositiveButton("Save",
				(dialog, whichButton) -> {

				}
		);
		builder.setNegativeButton("Cancel",
				(dialog, whichButton) -> {

				}
		);

		Dialog d = builder.create();
		return d;
	}
}
