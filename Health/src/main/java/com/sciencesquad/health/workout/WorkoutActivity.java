package com.sciencesquad.health.workout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.sciencesquad.health.AlarmActivity;
import com.sciencesquad.health.ClockActivity;
import com.sciencesquad.health.R;
import com.sciencesquad.health.steps.StepsViewModel;
import com.sciencesquad.health.activity.MapsActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = WorkoutActivity.class.getSimpleName();

    public String routineName = "";
    public String name = "";
    public String category = "";
    public String target = "";
    List<ExerciseTypeModel> exerciseModelList = new ArrayList<ExerciseTypeModel>();
    List<RoutineModel> routineModelList = new ArrayList<RoutineModel>();
    HashSet<String> exerciseTargets = new HashSet<String>();
    int selectedExercise = 0;


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
     * Set up WorkoutModule
     */




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        ExerciseTypeModel newExerciseA = new ExerciseTypeModel("Bench Press", "Strength", "Chest");
        exerciseModelList.add(newExerciseA);

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
                    Log.i(TAG, "Clicked FAB on Routine Tab");
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

    void showNewExerciseDialog() {
        DialogFragment newFragment = NewExerciseDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    void showNewRoutineDialog() {
        DialogFragment newFragment = NameRoutineFragmentDialog.newInstance(
                R.string.title_new_routine_dialog);

        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    void showRepDialog(String name) {
        PerfomExerciseDialogFragment newFragment = PerfomExerciseDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
        newFragment.titleThing = name;
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    void showRoutineBuilder(String name) {
        BuildRoutineDialogFragment newFragment = BuildRoutineDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
            newFragment.titleThing = name;
            newFragment.show(getSupportFragmentManager(), "dialog"
        );
    }

    public void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!" + "Name: " + name + " Category: " + category + " Target: " + target);
        if(name.equals("")){
            Snackbar.make(this.findViewById(android.R.id.content), "Not added: Exercise name field blank!", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();
        }else{

            ExerciseTypeModel newExercise = new ExerciseTypeModel(name, category, target);
            exerciseTargets.add(target);
            exerciseModelList.add(newExercise);
            ListView exerciseListView = (ListView) findViewById(R.id.exercise_model_list_view);
            ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<ExerciseTypeModel>(WorkoutActivity.this,
                    android.R.layout.simple_list_item_1);
            exerciseTypeAdapter.clear();
            exerciseTypeAdapter.addAll(exerciseModelList);
            exerciseListView.setAdapter(exerciseTypeAdapter);
        }
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    public void doPositiveClickRoutineName() {
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

    public void doNegativeClickRoutineName() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    /**
     * A placeholder fragment containing a simple view.
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            ListView exerciseListView = (ListView) rootView.findViewById(R.id.exercise_model_list_view);
            ArrayAdapter<ExerciseTypeModel> exerciseTypeAdapter = new ArrayAdapter<ExerciseTypeModel>(getContext(),
                    android.R.layout.simple_list_item_1, ((WorkoutActivity) getActivity()).exerciseModelList);
            exerciseTypeAdapter.clear();
            exerciseTypeAdapter.addAll(((WorkoutActivity) getActivity()).exerciseModelList);
            exerciseListView.setAdapter(exerciseTypeAdapter);
            exerciseListView.setOnItemClickListener(((parent, view, position, id) -> {
                Toast toast = Toast.makeText(getContext(), "Shit was clicked, yo", Toast.LENGTH_SHORT);
                toast.show();
                ((WorkoutActivity) getActivity()).showRepDialog(exerciseTypeAdapter.getItem(position).getName());

            }));



            Toast toast = Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_SHORT);
            toast.show();
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
                Toast toast = Toast.makeText(getContext(), "Shit was clicked, yo", Toast.LENGTH_SHORT);
                toast.show();
                ((WorkoutActivity) getActivity()).showRoutineBuilder(routineAdapter.getItem(position).getName());
            }));


            return rootView;
        }
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

    public static class PerfomExerciseDialogFragment extends DialogFragment {

        public String titleThing;
        public List<ExerciseSetModel> set = new ArrayList<>();

        public static PerfomExerciseDialogFragment newInstance(int title) {
            PerfomExerciseDialogFragment frag = new PerfomExerciseDialogFragment();
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
            View dialogLayout = inflater.inflate(R.layout.rep_dialog_fragment_layout, null);
            builder.setView(dialogLayout);
            builder.setTitle(title);

            TextView nameField = (TextView) dialogLayout.findViewById(R.id.exercise_name_view);

            EditText numRepsField = (EditText) dialogLayout.findViewById(R.id.num_rep_field);
            numRepsField.setInputType(InputType.TYPE_CLASS_NUMBER);
            EditText weightField = (EditText) dialogLayout.findViewById(R.id.amount_weight_field);
            weightField.setInputType(InputType.TYPE_CLASS_NUMBER);

            ListView completedSetListView = (ListView) dialogLayout.findViewById(R.id.list_complete_reps);
            ArrayAdapter<ExerciseSetModel> completedSetAdapter = new ArrayAdapter<ExerciseSetModel>(getContext(),
                    android.R.layout.simple_list_item_1);
           completedSetListView.setAdapter(completedSetAdapter);



            Button button = (Button) dialogLayout.findViewById(R.id.complete_rep_button);
            button.setOnClickListener(butt -> {
                int numReps = new Integer(numRepsField.getText().toString());
                int weight = new Integer(weightField.getText().toString());

                ExerciseSetModel newSet = new ExerciseSetModel(numReps, weight);
                set.add(newSet);

                completedSetAdapter.clear();
                completedSetAdapter.addAll(set);
            });

            builder.setPositiveButton("Save",
                    (dialog, whichButton) -> {
                        // Create list of reps[]
                    }
            );
            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                        ((WorkoutActivity) getActivity()).doNegativeClick();
                    }
            );

            Dialog d = builder.create();
            return d;
        }
    }

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
                        ((WorkoutActivity) getActivity()).doPositiveClick();
                    }
            );
            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                        ((WorkoutActivity) getActivity()).doNegativeClick();
                    }
            );

            Dialog d = builder.create();
            return d;
        }
    }



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

            List<String> filter = new ArrayList<String>(((WorkoutActivity) getActivity()).exerciseTargets);

            Spinner filterSpinner = (Spinner) dialogLayout.findViewById(R.id.filter_spinner);
            ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
            filterSpinner.setAdapter((filterAdapter));
            filterAdapter.clear();
            filterAdapter.addAll(filter);

            ListView exerciseListView = (ListView) dialogLayout.findViewById(R.id.choose_exercises_view);
            ArrayAdapter<ExerciseTypeModel> completedSetAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1);
            exerciseListView.setAdapter(completedSetAdapter);
            completedSetAdapter.clear();
            completedSetAdapter.addAll(((WorkoutActivity) getActivity()).exerciseModelList);
            exerciseListView.setItemsCanFocus(false);

            Button button = (Button) dialogLayout.findViewById(R.id.filter_button);
            button.setOnClickListener(butt -> {
                String f = filterSpinner.getSelectedItem().toString();

                completedSetAdapter.clear();
                for (int i = 0; i < ((WorkoutActivity) getActivity()).exerciseModelList.size(); i++) {
                    if (((WorkoutActivity) getActivity()).exerciseModelList.get(i).getTarget().equals(f)) {
                        completedSetAdapter.add(((WorkoutActivity) getActivity()).exerciseModelList.get(i));
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
                            ((WorkoutActivity) getActivity()).doPositiveClickRoutineName();
                        }
                    }
            );
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((WorkoutActivity) getActivity()).doNegativeClickRoutineName();
                        }
                    }
            );

            Dialog d = builder.create();
            return d;
        }
    }

    /**
     * I have not been tested :-)
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_run) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sleep) {

        } else if (id == R.id.nav_steps) {
            Intent intent = new Intent(this, StepsViewModel.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_workout){
            Intent intent = new Intent(this, WorkoutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(this, ClockActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_alarm) {
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
