package com.sciencesquad.health.workout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
	public CompletedExerciseModel mostRecent;
	public RealmList<ExerciseSetModel> set = new RealmList<>();
	private Context context;
	private TextInputEditText numRepsField;
	private TextInputEditText weightField;
	private TextInputLayout repLayout;
	private TextInputLayout weightLayout;
	private View dialogLayout;



	public static SetDialogFragment newInstance(int title) {
		SetDialogFragment frag = new SetDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		context.setTheme(R.style.WorkoutDialogCustom);
		this.context = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = this.titleThing;
		CompletedExerciseModel mostRecent = this.mostRecent;


		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.WorkoutDialogCustom);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogLayout = inflater.inflate(R.layout.set_dialog_fragment_layout, null);
		builder.setView(dialogLayout);
		builder.setTitle(title);


		repLayout = (TextInputLayout) dialogLayout.findViewById(R.id.num_rep_field_layout);
		numRepsField = (TextInputEditText) dialogLayout.findViewById(R.id.num_rep_field);
		numRepsField.setInputType(InputType.TYPE_CLASS_NUMBER);
		weightLayout = (TextInputLayout) dialogLayout.findViewById(R.id.num_rep_field_layout);
		weightField = (TextInputEditText) dialogLayout.findViewById(R.id.amount_weight_field);
		weightField.setInputType(InputType.TYPE_CLASS_NUMBER);

		ListView completedSetListView = (ListView) dialogLayout.findViewById(R.id.list_complete_reps);
		ArrayAdapter<ExerciseSetModel> completedSetAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_1);
	   completedSetListView.setAdapter(completedSetAdapter);        // When sets are completed, they are listed in the dialog


		// Set up increment/decrement buttons
		ImageView repIncButt = (ImageView) dialogLayout.findViewById(R.id.rep_increment_button);
		repIncButt.setOnClickListener(repInc -> {
			Integer numReps = 0;
			try{
				numReps = new Integer(numRepsField.getText().toString());
			} catch(NumberFormatException e){
				numReps = 0;
			}

			if( numReps < 9999){
				numReps++;
				numRepsField.setText(numReps.toString());
			}
		});
		ImageView repDecButt = (ImageView) dialogLayout.findViewById(R.id.rep_decrement_button);
		repDecButt.setOnClickListener(repDec -> {
			Integer numReps = 0;
			try{
				numReps = new Integer(numRepsField.getText().toString());
			} catch(NumberFormatException e){
				numReps = 0;
			}

			if( numReps > 0){
				numReps--;
				numRepsField.setText(numReps.toString());
			}
		});

		ImageView weightIncButt = (ImageView) dialogLayout.findViewById(R.id.weight_increment_button);
		weightIncButt.setOnClickListener(repInc -> {
			Integer weight = 0;
			try{
				weight = new Integer(weightField.getText().toString());
			} catch(NumberFormatException e){
				weight = 0;
			}

			if( weight < 9999){
				weight++;
				weightField.setText(weight.toString());
			}
		});
		ImageView weightDecButt = (ImageView) dialogLayout.findViewById(R.id.weight_decrement_button);
		weightDecButt.setOnClickListener(repDec -> {
			Integer weight = 0;
			try{
				weight = new Integer(weightField.getText().toString());
			} catch(NumberFormatException e){
				weight = 0;
			}

			if( weight > 0){
				weight--;
				weightField.setText(weight.toString());
			}

		});

		// Populate input fields with the values from the last time user completed this exercise
		if(mostRecent != null){
			Log.i("SETDIALOG", "SUCCESSFULLY FOUND A COMPLETEDEXERCISE OF SPECIFIED TYPE");
			ExerciseSetModel set = mostRecent.getSets().first();
			numRepsField.setText(set.getReps().toString());
			weightField.setText(set.getWeight().toString());
			hideKeyboard();
		} else {
			Log.i("SETDIALOG", "Didn't find any previous completedExercises of this type");
		}



		// When a user choose to "Complete Set" a new ExerciseSetModel is created based on input
		Button button = (Button) dialogLayout.findViewById(R.id.complete_rep_button);
		button.setOnClickListener(butt -> {
			if(weightField.getText().toString().equals("")){
				// set weightField error
				;
			} else if(numRepsField.getText().toString().equals("")){
				// set repsField error
				;
			} else {
				hideKeyboard();
				int numReps = new Integer(numRepsField.getText().toString());
				int weight = new Integer(weightField.getText().toString());
				ExerciseSetModel newSet = new ExerciseSetModel();
				newSet.setReps(numReps);
				newSet.setWeight(weight);
				newSet.setDate(Calendar.getInstance().getTime());
				set.add(newSet);        // add set to the list of sets

				completedSetAdapter.clear();
				completedSetAdapter.addAll(set);        //repopulate the adapter
			}
		});



		builder.setPositiveButton("Save",
				(dialog, whichButton) -> {
					// TODO: create list of sets and store them somewhere
                    CompletedExerciseModel completedExercise = new CompletedExerciseModel();
                    completedExercise.setExerciseName(title);
                    completedExercise.setSets(set);
                    Calendar rightNow = Calendar.getInstance();
                    completedExercise.setDate(rightNow.getTime());
                    ((WorkoutFragment)getTargetFragment()). saveCompletedExercise(completedExercise);
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

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager)
				context.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(dialogLayout.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}



}
