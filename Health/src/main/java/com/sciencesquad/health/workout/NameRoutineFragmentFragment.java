package com.sciencesquad.health.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.sciencesquad.health.R;

/**
 * This dialog allows a user to create a new, empty, routine
 */
public class NameRoutineFragmentFragment extends DialogFragment {
	private static final String TAG = NameRoutineFragmentFragment.class.getCanonicalName();

	// Key for Dialog's Title
	public static final String KEY_TITLE = TAG + ".TITLE";

	public static NameRoutineFragmentFragment newInstance(int title) {
		NameRoutineFragmentFragment frag = new NameRoutineFragmentFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_TITLE, title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = this.getArguments().getString(KEY_TITLE);

		// Inflate dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogLayout = inflater.inflate(R.layout.name_routine_dialog_fragment, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);

		// Setup fields
		EditText nameField = (EditText) dialogLayout.findViewById(R.id.routine_name_field);
		nameField.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setPositiveButton("Save", (dialog, whichButton) -> {
			WorkoutFragment.routineName = nameField.getText().toString();
			((WorkoutFragment) getTargetFragment()).saveRoutine();
		});
		builder.setNegativeButton("Cancel", (dialog, whichButton) -> {});
		return builder.create();
	}
}
