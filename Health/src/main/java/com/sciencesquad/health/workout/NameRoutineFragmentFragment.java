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

/*
 * NameRoutineFragmentFragment
 * This dialog allows a user to create a new, empty, routine
 */
public class NameRoutineFragmentFragment extends DialogFragment {

	public static NameRoutineFragmentFragment newInstance(int title) {
		NameRoutineFragmentFragment frag = new NameRoutineFragmentFragment();
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
		View dialogLayout = inflater.inflate(R.layout.name_routine_dialog_fragment, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);

		EditText nameField = (EditText) dialogLayout.findViewById(R.id.routine_name_field);
		EditText targetField = (EditText) dialogLayout.findViewById(R.id.routine_name_field);
		nameField.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setPositiveButton("Save",
				(dialog, whichButton) -> {
					WorkoutFragment.routineName = nameField.getText().toString();
					((WorkoutFragment) getTargetFragment()).saveRoutine();
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
