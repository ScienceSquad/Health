package com.sciencesquad.health.workout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sciencesquad.health.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author      Taylor T. Johnson
 * @since       2016--03-03
 */


public class WorkoutActivity extends AppCompatActivity {
    public static final String TAG = WorkoutActivity.class.getSimpleName();

    public String routineName = "";     // Used for passing names of new routines b/w fragments
    public String name = "";            // Used for passing names of new exercises b/w fragments
    public String category = "";        // Used for passing exercise type (Cardio/Strength)
    public String target = "";          // Used for passing target of exercise b/w fragments
    List<ExerciseTypeModel> exerciseTypeModelList = new ArrayList<>();     // list of exercises added by user
    List<RoutineModel> routineModelList = new ArrayList<>();           // list of routines created by user
    HashSet<String> exerciseTargets = new HashSet<>();            // contains all exercise targets


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

    /**
     * @onCreate called when WokoutActivtity is created
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        ExerciseTypeModel newExerciseA = new ExerciseTypeModel("Bench Press", "Strength", "Chest");
        exerciseTypeModelList.add(newExerciseA);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.workoutFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        newFragment.show(getSupportFragmentManager(), "dialog");
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

        newFragment.show(getSupportFragmentManager(), "dialog");
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
        newFragment.show(getSupportFragmentManager(), "dialog");
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
            newFragment.show(getSupportFragmentManager(), "dialog"
        );
    }

    /**
     * @saveNewExerciseType
     * This method builds a new ExerciseTypeModel, populating its
     * variables with those stored in WorkoutActivity set by
     * a user selecting "Save" in the NewExerciseDialogFragment
     */
    public void saveNewExerciseType() {
        Log.i("FragmentAlertDialog", "Positive click!" + "Name: " + name + " Category: " + category + " Target: " + target);
        if(name.equals("")){
            Snackbar.make(this.findViewById(android.R.id.content), "Not added: Exercise name field blank!", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
        }else{

            ExerciseTypeModel newExercise = new ExerciseTypeModel(name, category, target);
            exerciseTargets.add(target);
            exerciseTypeModelList.add(newExercise);
            ListView exerciseListView = (ListView) findViewById(R.id.exercise_model_list_view);
            ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<ExerciseTypeModel>(WorkoutActivity.this,
                    android.R.layout.simple_list_item_1);
            exerciseTypeAdapter.clear();
            exerciseTypeAdapter.addAll(exerciseTypeModelList);
            exerciseListView.setAdapter(exerciseTypeAdapter);
        }
    }

    public void cancelNewExerciseType() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }


    public void saveRoutine() {
        // Do stuff here.
        if(routineName.equals("")){
            Snackbar.make(this.findViewById(android.R.id.content), "Not added: Routine name field blank!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            RoutineModel newRoutine = new RoutineModel(routineName);
            routineModelList.add(newRoutine);
            ListView routineListView = (ListView) findViewById(R.id.routine_model_list_view);
            ArrayAdapter<RoutineModel> routineModelArrayAdapter = new ArrayAdapter<RoutineModel>(WorkoutActivity.this,
                    android.R.layout.simple_list_item_1);
            routineModelArrayAdapter.clear();
            routineModelArrayAdapter.addAll(routineModelList);
            routineListView.setAdapter(routineModelArrayAdapter);
        }
    }

    public void cancelNewRoutine() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    /**
     * ExerciseTypeFragmet
     * This is a fragment for the Exercise tab in the WorkoutActivity
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
            ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<ExerciseTypeModel>(getContext(),             // create an adapter to fill array
                    android.R.layout.simple_list_item_1, ((WorkoutActivity) getActivity()).exerciseTypeModelList);
            exerciseTypeAdapter.clear();                // first clear adapter
            exerciseTypeAdapter.addAll(((WorkoutActivity) getActivity()).exerciseTypeModelList);        // add all exercises created by user to the adapter
            exerciseListView.setAdapter(exerciseTypeAdapter);       // bind the adapter to the listview
            exerciseListView.setOnItemClickListener(((parent, view, position, id) -> {
                ((WorkoutActivity) getActivity()).showSetDialog(exerciseTypeAdapter.getItem(position).getName());
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
            ArrayAdapter<RoutineModel> routineAdapter = new ArrayAdapter<RoutineModel>(getContext(),
                    android.R.layout.simple_list_item_1, ((WorkoutActivity) getActivity()).routineModelList);
            routineAdapter.clear();
            routineAdapter.addAll(((WorkoutActivity) getActivity()).routineModelList);
            routineListView.setAdapter(routineAdapter);
            routineListView.setOnItemClickListener(((parent, view, position, id) -> {
                ((WorkoutActivity) getActivity()).showRoutineBuilder(routineAdapter.getItem(position).getName());
            }));


            return rootView;
        }
    }


    /**
     * SetDialogFragmet
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
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.set_dialog_fragment_layout, null);
            builder.setView(dialogLayout);
            builder.setTitle(title);

            EditText numRepsField = (EditText) dialogLayout.findViewById(R.id.num_rep_field);
            numRepsField.setInputType(InputType.TYPE_CLASS_NUMBER);
            EditText weightField = (EditText) dialogLayout.findViewById(R.id.amount_weight_field);
            weightField.setInputType(InputType.TYPE_CLASS_NUMBER);

            ListView completedSetListView = (ListView) dialogLayout.findViewById(R.id.list_complete_reps);
            ArrayAdapter<ExerciseSetModel> completedSetAdapter = new ArrayAdapter<>(getContext(),
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
                        ((WorkoutActivity) getActivity()).cancelNewExerciseType();
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
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.new_exercise_dialog_layout, null);
            builder.setView(dialogLayout);
            builder.setTitle(title);

            Spinner kindSpinner = (Spinner) dialogLayout.findViewById(R.id.exercise_kind_spinner);
            kindSpinner.setAdapter(new ArrayAdapter<ExerciseKind>(getContext(), android.R.layout.simple_list_item_1, ExerciseKind.values()));
            EditText nameField = (EditText) dialogLayout.findViewById(R.id.exercise_name_field);
            EditText targetField = (EditText) dialogLayout.findViewById(R.id.exercise_target_field);
            nameField.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setPositiveButton("Save",
                    (dialog, whichButton) -> {
                        ((WorkoutActivity) getActivity()).name = nameField.getText().toString();
                        ((WorkoutActivity) getActivity()).category = kindSpinner.getSelectedItem().toString();
                        ((WorkoutActivity) getActivity()).target = targetField.getText().toString();
                        ((WorkoutActivity) getActivity()).saveNewExerciseType();
                    }
            );
            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                        ((WorkoutActivity) getActivity()).cancelNewExerciseType();
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
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.build_routine_layout, null);
            builder.setView(dialogLayout);
            builder.setTitle(title);

            // fill spinner with all different workout "targets" with which a user can filter exercises
            List<String> filter = new ArrayList<String>(((WorkoutActivity) getActivity()).exerciseTargets);
            Spinner filterSpinner = (Spinner) dialogLayout.findViewById(R.id.filter_spinner);
            ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
            filterSpinner.setAdapter((filterAdapter));
            filterAdapter.clear();
            filterAdapter.addAll(filter);

            // list all user-created exercises in Dialog
            // TODO: Make this a multiple selection list and add list of exercises selected to routine
            ListView exerciseListView = (ListView) dialogLayout.findViewById(R.id.choose_exercises_view);
            ArrayAdapter<ExerciseTypeModel> completedSetAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1);
            exerciseListView.setAdapter(completedSetAdapter);
            completedSetAdapter.clear();
            completedSetAdapter.addAll(((WorkoutActivity) getActivity()).exerciseTypeModelList);
            exerciseListView.setItemsCanFocus(false);

            // Create button that filters listed exercises based on "target"
            Button button = (Button) dialogLayout.findViewById(R.id.filter_button);
            button.setOnClickListener(butt -> {
                String f = filterSpinner.getSelectedItem().toString();

                completedSetAdapter.clear(); // clear current list
                for (int i = 0; i < ((WorkoutActivity) getActivity()).exerciseTypeModelList.size(); i++) {
                    if (((WorkoutActivity) getActivity()).exerciseTypeModelList.get(i).getTarget().equals(f)) {
                        // only add exercises with matching "target" to the filter selected
                        completedSetAdapter.add(((WorkoutActivity) getActivity()).exerciseTypeModelList.get(i));
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
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((WorkoutActivity) getActivity()).routineName = nameField.getText().toString();
                            ((WorkoutActivity) getActivity()).saveRoutine();
                        }
                    }
            );
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }
            );

            Dialog d = builder.create();
            return d;
        }
    }
}
