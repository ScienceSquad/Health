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
import android.widget.EditText;
import android.widget.Spinner;
import com.sciencesquad.health.R;

/**
 * NewExerciseDialogFragment
 * This Dialog allows a user to create a new type of Exercise
 */
public class NewExerciseDialogFragment extends DialogFragment {

	public static NewExerciseDialogFragment newInstance(int title) {
		NewExerciseDialogFragment frag = new NewExerciseDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogLayout = inflater.inflate(R.layout.new_exercise_dialog_layout, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);

		Spinner kindSpinner = (Spinner) dialogLayout.findViewById(R.id.exercise_kind_spinner);
		kindSpinner.setAdapter(new ArrayAdapter<ExerciseKind>(getActivity(), android.R.layout.simple_list_item_1, ExerciseKind.values()));
		EditText nameField = (EditText) dialogLayout.findViewById(R.id.exercise_name_field);
		EditText targetField = (EditText) dialogLayout.findViewById(R.id.exercise_target_field);
		nameField.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setPositiveButton("Save",
				(dialog, whichButton) -> {
					WorkoutFragment.name = nameField.getText().toString();
					WorkoutFragment.category = kindSpinner.getSelectedItem().toString();
					WorkoutFragment.target = targetField.getText().toString();
					((WorkoutFragment)getTargetFragment()).saveNewExerciseType();
				}
		);
		builder.setNegativeButton("Cancel",
				(dialog, whichButton) -> {
					((WorkoutFragment)getTargetFragment()).cancelNewExerciseType();
				}
		);

		Dialog d = builder.create();
		return d;
	}
}
