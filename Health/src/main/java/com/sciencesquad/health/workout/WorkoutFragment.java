package com.sciencesquad.health.workout;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.Spinner;

import com.sciencesquad.health.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author      Taylor T. Johnson
 * @since       2016--03-03
 */


public class WorkoutFragment extends Fragment {
    public static final String TAG = WorkoutFragment.class.getSimpleName();

    public static String routineName = "";     // Used for passing names of new routines b/w fragments
    public static String name = "";            // Used for passing names of new exercises b/w fragments
    public static String category = "";        // Used for passing exercise type (Cardio/Strength)
    public static String target = "";          // Used for passing target of exercise b/w fragments
    static List<ExerciseTypeModel> exerciseTypeModelList = new ArrayList<>();     // list of exercises added by user
    static List<RoutineModel> routineModelList = new ArrayList<>();           // list of routines created by user
    static HashSet<String> exerciseTargets = new HashSet<>();            // contains all exercise targets


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_workout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ExerciseTypeModel newExerciseA = new ExerciseTypeModel("Bench Press", "Strength", "Chest");
        //exerciseTypeModelList.add(newExerciseA);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.workoutFab);
        fab.setOnClickListener(view1 -> {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //      .setAction("Action", null).show();
            int selectedTab = tabLayout.getSelectedTabPosition();
            if(selectedTab == 0){
                // in exercise tab
                showNewExerciseDialog();
            }else if(selectedTab == 1){
                // in routine tab
                showNewRoutineDialog();
            }

        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){

            }
            switch(position){
                case 0:
                    return ExerciseTypeFragment.newInstance(position + 1);
                case 1:
                    return RoutineFragment.newInstance(position + 1);
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Exercises";
                case 1:
                    return "Routines";
            }
            return null;
        }
    }

    /**
     * @showNewExerciseDialog
     * This method launches a DialogFragment that allows
     * a user to create a new type of exercise
     */
    void showNewExerciseDialog() {
        DialogFragment newFragment = NewExerciseDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * @showNewRoutineDialog()
     * This method launches a DialogFragment that allows
     * a user to create a new Routine from existing types of
     * exercises
     */
    void showNewRoutineDialog() {
        DialogFragment newFragment = NameRoutineFragmentDialog.newInstance(
                R.string.title_new_routine_dialog);

        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * @showSetDialog
     * @param name is the name of exercise to become the title of the new dialog
     * This method launches a DialogFragment that allows
     * a user to log his or her selected exercise
     */
    void showSetDialog(String name) {
        SetDialogFragment newFragment = SetDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
        newFragment.titleThing = name;
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * @showRoutineBuilder
     * @param name is name of routine to becom the title of the dialog
     * This method launches a Dialog Fragment that allows
     * a select exercises to add to a routine
     */
    void showRoutineBuilder(String name) {
        BuildRoutineDialogFragment newFragment = BuildRoutineDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
            newFragment.titleThing = name;
            newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * @saveNewExerciseType
     * This method builds a new ExerciseTypeModel, populating its
     * variables with those stored in WorkoutFragment set by
     * a user selecting "Save" in the NewExerciseDialogFragment
     */
    public static void saveNewExerciseType() {
        Log.i("FragmentAlertDialog", "Positive click!" + "Name: " + name + " Category: " + category + " Target: " + target);
        if(name.equals("")){
            Snackbar.make(getView().findViewById(android.R.id.content), "Not added: Exercise name field blank!", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
        }else{

            ExerciseTypeModel newExercise = new ExerciseTypeModel(name, category, target);
            exerciseTargets.add(target);
            exerciseTypeModelList.add(newExercise);
            ListView exerciseListView = (ListView)getView().findViewById(R.id.exercise_model_list_view);
            ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1);
            exerciseTypeAdapter.clear();
            exerciseTypeAdapter.addAll(exerciseTypeModelList);
            exerciseListView.setAdapter(exerciseTypeAdapter);
        }
    }

    public static void cancelNewExerciseType() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }


    public static void saveRoutine() {
        // Do stuff here.
        if(routineName.equals("")){
            Snackbar.make(getView().findViewById(android.R.id.content), "Not added: Routine name field blank!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            RoutineModel newRoutine = new RoutineModel(routineName);
            routineModelList.add(newRoutine);
            ListView routineListView = (ListView)getView().findViewById(R.id.routine_model_list_view);
            ArrayAdapter<RoutineModel> routineModelArrayAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1);
            routineModelArrayAdapter.clear();
            routineModelArrayAdapter.addAll(routineModelList);
            routineListView.setAdapter(routineModelArrayAdapter);
        }
    }

    public static void cancelNewRoutine() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    /**
     * ExerciseTypeFragmet
     * This is a fragment for the Exercise tab in the WorkoutFragment
     * This Fragment contains a ListView that lists all exercises
     * added by the user
     */
    public static class ExerciseTypeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ExerciseTypeFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ExerciseTypeFragment newInstance(int sectionNumber) {
            ExerciseTypeFragment fragment = new ExerciseTypeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * Creates view for the ExerciseTypeFragmet
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
            ListView exerciseListView = (ListView) rootView.findViewById(R.id.exercise_model_list_view);        // create ListView for exercises
            ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<>(getActivity(),             // create an adapter to fill array
                    android.R.layout.simple_list_item_1, WorkoutFragment.exerciseTypeModelList);
            exerciseTypeAdapter.clear();                // first clear adapter
            exerciseTypeAdapter.addAll(WorkoutFragment.exerciseTypeModelList);        // add all exercises created by user to the adapter
            exerciseListView.setAdapter(exerciseTypeAdapter);       // bind the adapter to the listview
            exerciseListView.setOnItemClickListener(((parent, view, position, id) -> {
                ((WorkoutFragment) getParentFragment()).showSetDialog(exerciseTypeAdapter.getItem(position).getName());
            }));

            return rootView;
        }
    }



    /**
     * A Fragment for the Routine Modules
     */
    public static class RoutineFragment extends Fragment {
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
                ((WorkoutFragment) getParentFragment()).showRoutineBuilder(routineAdapter.getItem(position).getName());
            }));


            return rootView;
        }
    }


    /**
     * SetDialogFragment
     * This is a fragment view which allows a user to enter sets (reps, weight)
     * for a selected exercise
     */
    public static class SetDialogFragment extends DialogFragment {

        public String titleThing;
        public List<ExerciseSetModel> set = new ArrayList<>();

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
                        // TODO: create list of sets and store them somewhere useful
                    }
            );
            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                        ((WorkoutFragment) getParentFragment()).cancelNewExerciseType();
                    }
            );

            Dialog d = builder.create();
            return d;
        }
    }

    /**
     * NewExerciseDialogFragment
     * This Dialog allows a user to create a new type of Exercise
     */
    public static class NewExerciseDialogFragment extends DialogFragment {

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
                        WorkoutFragment.saveNewExerciseType();
                    }
            );
            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                        WorkoutFragment.cancelNewExerciseType();
                    }
            );

            Dialog d = builder.create();
            return d;
        }
    }


    /**
     * BuildRoutineDialogFragment
     * This Dialog allows a user to select exercises to create a routine
     */
    public static class BuildRoutineDialogFragment extends DialogFragment {
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

    /*
     * NameRoutineFragmentDialog
     * This dialog allows a user to create a new, empty, routine
     */
    public static class NameRoutineFragmentDialog extends DialogFragment {

        public static NameRoutineFragmentDialog newInstance(int title) {
            NameRoutineFragmentDialog frag = new NameRoutineFragmentDialog();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.name_routine_dialog_fragment, null);
            builder.setView(dialogLayout);
            builder.setTitle(title);

            EditText nameField = (EditText) dialogLayout.findViewById(R.id.routine_name_field);
            EditText targetField = (EditText) dialogLayout.findViewById(R.id.routine_name_field);
            nameField.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setPositiveButton("Save",
                    (dialog, whichButton) -> {
                        WorkoutFragment.routineName = nameField.getText().toString();
                        ((WorkoutFragment) getParentFragment()).saveRoutine();
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
}
