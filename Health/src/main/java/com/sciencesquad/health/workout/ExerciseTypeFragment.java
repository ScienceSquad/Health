package com.sciencesquad.health.workout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.sciencesquad.health.R;

/**
 * ExerciseTypeFragmet
 * This is a fragment for the Exercise tab in the WorkoutFragment
 * This Fragment contains a ListView that lists all exercises
 * added by the user
 */
public class ExerciseTypeFragment extends Fragment {

	public ExerciseTypeFragment() {
	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static ExerciseTypeFragment newInstance() {
		return new ExerciseTypeFragment();
	}

	/**
	 * Creates view for the ExerciseTypeFragmet
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_workout_type, container, false);
		ListView exerciseListView = (ListView) rootView.findViewById(R.id.exercise_model_list_view);        // create ListView for exercises
		ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<>(getActivity(),             // create an adapter to fill array
				android.R.layout.simple_list_item_1, WorkoutFragment.exerciseTypeModelList);
		exerciseTypeAdapter.clear();                // first clear adapter
		exerciseTypeAdapter.addAll(WorkoutFragment.exerciseTypeModelList);        // add all exercises created by user to the adapter
		exerciseListView.setAdapter(exerciseTypeAdapter);       // bind the adapter to the listview
		exerciseListView.setOnItemClickListener(((parent, view, position, id) -> {
			((WorkoutFragment) getTargetFragment()).showSetDialog(exerciseTypeAdapter.getItem(position).getName());
		}));

		return rootView;
	}
}
