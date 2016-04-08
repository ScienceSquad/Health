package com.sciencesquad.health.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.sciencesquad.health.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;

/**
 * SetDialogFragment
 * This is a fragment view which allows a user to enter sets (reps, weight)
 * for a selected exercise
 */
public class SetDialogFragment extends DialogFragment {

	public String titleThing;
	public RealmList<ExerciseSetModel> set = new RealmList<>();

	public static SetDialogFragment newInstance(int title) {
		SetDialogFragment frag = new SetDialogFragment();
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
		View dialogLayout = inflater.inflate(R.layout.set_dialog_fragment_layout, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);

		EditText numRepsField = (EditText) dialogLayout.findViewById(R.id.num_rep_field);
		numRepsField.setInputType(InputType.TYPE_CLASS_NUMBER);
		EditText weightField = (EditText) dialogLayout.findViewById(R.id.amount_weight_field);
		weightField.setInputType(InputType.TYPE_CLASS_NUMBER);

		ListView completedSetListView = (ListView) dialogLayout.findViewById(R.id.list_complete_reps);
		ArrayAdapter<ExerciseSetModel> completedSetAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_1);
	   completedSetListView.setAdapter(completedSetAdapter);        // When sets are completed, they are listed in the dialog

		// When a user choose to "Complete Set" a new ExerciseSetModel is created based on input
		Button button = (Button) dialogLayout.findViewById(R.id.complete_rep_button);
		button.setOnClickListener(butt -> {
			int numReps = new Integer(numRepsField.getText().toString());
			int weight = new Integer(weightField.getText().toString());

			ExerciseSetModel newSet = new ExerciseSetModel(numReps, weight);
			set.add(newSet);        // add set to the list of sets

			completedSetAdapter.clear();
			completedSetAdapter.addAll(set);        //repopulate the adapter
		});



		builder.setPositiveButton("Save",
				(dialog, whichButton) -> {
					// TODO: create list of sets and store them somewhere
                    CompletedExerciseModel completedExercise = new CompletedExerciseModel();
                    completedExercise.setExerciseName(title);
                    completedExercise.setSets(set);
                    Calendar rightNow = Calendar.getInstance();
                    completedExercise.setDate(rightNow.getTime());
                    ((WorkoutFragment)getTargetFragment()).saveCompletedExercise(completedExercise);

				}
		);
		builder.setNegativeButton("Cancel",
				(dialog, whichButton) -> {
					((WorkoutFragment) getTargetFragment()).cancelNewExerciseType();
				}
		);

		Dialog d = builder.create();
		return d;
	}
}
