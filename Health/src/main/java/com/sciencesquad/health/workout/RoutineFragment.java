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
 * A Fragment for the Routine Modules
 */
public class RoutineFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	public RoutineFragment() {
	}

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static RoutineFragment newInstance(int sectionNumber) {
		RoutineFragment fragment = new RoutineFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.routine_fragment_layout, container, false);
		ListView routineListView = (ListView) rootView.findViewById(R.id.routine_model_list_view);
		ArrayAdapter<RoutineModel> routineAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_1, WorkoutFragment.routineModelList);
		routineAdapter.clear();
		routineAdapter.addAll(WorkoutFragment.routineModelList);
		routineListView.setAdapter(routineAdapter);
		routineListView.setOnItemClickListener(((parent, view, position, id) -> {
			((WorkoutFragment) getTargetFragment()).showRoutineBuilder(routineAdapter.getItem(position).getName());
		}));


		return rootView;
	}
}
