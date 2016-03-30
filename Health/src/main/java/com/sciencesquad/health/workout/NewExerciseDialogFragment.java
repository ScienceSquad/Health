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
 * This Dialog allows a user to create a new type of Exercise
 */
public class NewExerciseDialogFragment extends DialogFragment {
	private static final String TAG = NewExerciseDialogFragment.class.getCanonicalName();

	// Key for Dialog's Title
	public static final String KEY_TITLE = TAG + ".TITLE";

	public static NewExerciseDialogFragment newInstance(String title) {
		NewExerciseDialogFragment frag = new NewExerciseDialogFragment();
		Bundle args = new Bundle();
		args.putString(KEY_TITLE, title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = this.getArguments().getString(KEY_TITLE);

		// Inflate dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogLayout = inflater.inflate(R.layout.new_exercise_dialog_layout, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);

		// Setup view
		Spinner kindSpinner = (Spinner) dialogLayout.findViewById(R.id.exercise_kind_spinner);
		kindSpinner.setAdapter(new ArrayAdapter<ExerciseKind>(getActivity(), android.R.layout.simple_list_item_1, ExerciseKind.values()));
		EditText nameField = (EditText) dialogLayout.findViewById(R.id.exercise_name_field);
		EditText targetField = (EditText) dialogLayout.findViewById(R.id.exercise_target_field);
		nameField.setInputType(InputType.TYPE_CLASS_TEXT);

		// Setup dialog buttons
		builder.setPositiveButton("Save", (dialog, whichButton) -> {
			WorkoutFragment.name = nameField.getText().toString();
			WorkoutFragment.category = kindSpinner.getSelectedItem().toString();
			WorkoutFragment.target = targetField.getText().toString();
			((WorkoutFragment)getTargetFragment()).saveNewExerciseType();
		});
		builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
			((WorkoutFragment)getTargetFragment()).cancelNewExerciseType();
		});
		return builder.create();
	}
}
