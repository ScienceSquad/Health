package com.sciencesquad.health.workout;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.ui.Stopwatch;
import com.sciencesquad.health.core.util.SimpleExpandableListAdapter;
import com.sciencesquad.health.core.util.AnimatedExpandableListView;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentWorkoutBinding;
import io.realm.RealmList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import io.realm.RealmList;

public class WorkoutFragment extends BaseFragment {
    public static final String TAG = WorkoutFragment.class.getSimpleName();

    // FIXME: Should be switched out for a database later.
    public static String routineName = "";     // Used for passing names of new routines b/w fragments
    public static String name = "";            // Used for passing names of new exercises b/w fragments
    public static String category = "";        // Used for passing exercise type (Cardio/Strength)
    public static String target = "";          // Used for passing target of exercise b/w fragments
    public static List<ExerciseTypeModel> exerciseTypeModelList = new ArrayList<>();     // list of exercises added by user
    public static List<RoutineModel> routineModelList = new ArrayList<>();           // list of routines created by user
    public static HashSet<String> exerciseTargets = new HashSet<>();            // contains all exercise targets
    public static CompletedExerciseModel completedExercise;
    public static ArrayAdapter<String> routineModelAdapter;
    public static ArrayAdapter<String> exerciseTypeAdapter;
    public static SimpleExpandableListAdapter categoryAdapter;
    public static ArrayAdapter<String> currentRoutineExerciseAdapter;
    public static String[] categories;
    public static String[][] groupedExercises;
    WorkoutModule mod = Module.of(WorkoutModule.class);


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    // private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    //  private ViewPager mViewPager;

    @Override
    protected Configuration getConfiguration() {
        String notAnUnderscore = WorkoutModule.TAG; // instantiates the Module...
        return new Configuration(
                TAG, "Workout", R.drawable.ic_fitness_center_24dp,
                R.style.AppTheme_Workout, R.layout.fragment_workout
        );
    }

    // Our generated binding class is different...
    @Override @SuppressWarnings("unchecked")
    protected FragmentWorkoutBinding xml() {
        return super.xml();
    }

    @Override
    public void onSetupTransition() {
        this.setEnterTransition(new RevealTransition(Visibility.MODE_IN));
        this.setExitTransition(new RevealTransition(Visibility.MODE_OUT));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        xml().setModule(Module.of(WorkoutModule.class));

        TabLayout tabLayout = xml().tabs;
        StaticPagerAdapter.install(xml().pager);
        xml().tabs.setupWithViewPager(xml().pager);

        FloatingActionButton fab = xml().fab; //(FloatingActionButton)view.findViewById(R.id.workoutFab);
        //Drawable plus = getResources().getDrawable(android.R.drawable.ic_input_add);
        //Drawable whitePlus = plus.getConstantState().newDrawable();
        //whitePlus.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        //xml().fab.setImageDrawable(whitePlus);
        fab.setOnClickListener(view1 -> {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //      .setAction("Action", null).show();
            int selectedTab = tabLayout.getSelectedTabPosition();
            if (selectedTab == 0) {
                // in current workout tab
                showDOWScheduleBuilder();
            } else if (selectedTab == 1) {
                // in routine tab
                showNewRoutineDialog();
            } else if (selectedTab == 2) {
                // in exercise tab
                showNewExerciseDialog();
            }
        });

        xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

        // Bind data to currentWorkoutTab
        categories = mod.getAllCategories();


        WorkoutScheduleModel schedule = mod.getWorkoutSchedule();
        currentRoutineExerciseAdapter = new ArrayAdapter<String>(getInflater().getContext(),
                android.R.layout.simple_expandable_list_item_1);
        //RoutineModel todaysRoutine = mod.getTodaysRoutine();
        if(schedule == null){
            Log.i(TAG, "DID NOT FIND A SCHEDULE");
            xml().currentWorkoutHeader.setText("No Scheduled Routines");
        } else {
            Log.i(TAG, "FOUND SOME SORT OF ROUTINE");
            RoutineModel todaysRoutine = mod.getRoutineModel(schedule.getRoutineRotation().first().getName());
            updateCurrentWorkout(todaysRoutine);
        }

        // Bind data to expandable list of categories
        categories = mod.getAllCategories();
        groupedExercises = mod.groupExercisesByTargetAlpha(categories);
        categoryAdapter = new SimpleExpandableListAdapter(getInflater().getContext(), categories, groupedExercises);
        xml().categoryExpandableView.setAdapter(categoryAdapter);
        xml().categoryExpandableView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                ExerciseTypeModel exercise = mod.getExerciseTypeModel(categoryAdapter.getChild(groupPosition, childPosition));

                if(exercise == null){
                    Log.e(TAG, "Exercise retrieved from Realm was null!");
                } else if( exercise.getCategory().equalsIgnoreCase("Cardio")){
                    // exercise is Cardio
                    showCardioDialog(exercise.getName());
                } else {
                    showSetDialog(exercise.getName());
                }


                return false;
            }
        });

        xml().categoryExpandableView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (xml().categoryExpandableView.isGroupExpanded(groupPosition)) {
                    xml().categoryExpandableView.collapseGroupWithAnimation(groupPosition);
                } else {
                    xml().categoryExpandableView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });


        //xml().categoryExpandableView.setAdapter(new SimpleExpandableListAdapter(getInflater().getContext(), categories, groupedExercises));

        // Bind data to view (ExerciseTypeModels)
/*
        exerciseTypeAdapter = new ArrayAdapter<>(getInflater().getContext(),             // create an adapter to fill array
                android.R.layout.simple_list_item_1);
        exerciseTypeAdapter.clear();                // first clear adapter
        for (ExerciseTypeModel m : mod.getAllExerciseTypeModels())
            exerciseTypeAdapter.add(m.getName());
        /*exerciseTypeAdapter.sort(new Comparator<String>() {
           @Override
           public int compare(String lhs, String rhs) {
               return lhs.compareTo(rhs);   //or whatever your sorting algorithm
           }
        });*/
        /*
        exerciseTypeAdapter.notifyDataSetChanged();
        xml().exerciseModelListView.setAdapter(exerciseTypeAdapter);
        xml().exerciseModelListView.setOnItemClickListener(((parent, views, position, id) -> {

            ExerciseTypeModel exercise = mod.getExerciseTypeModel(exerciseTypeAdapter.getItem(position));

            if(exercise == null){
                Log.e(TAG, "Exercise retrieved from Realm was null!");
            } else if( exercise.getCategory().equalsIgnoreCase("Cardio")){
                // exercise is Cardio
                showCardioDialog(exercise.getName());
            } else {
                showSetDialog(exercise.getName());
            }

        }));

        xml().exerciseModelListView.setOnItemLongClickListener(((parent1, views1, position1, id1) -> {
            this.showExerciseHistoryDialog(exerciseTypeAdapter.getItem(position1));
            return true;
        }));
*/

        // Bind data to view (RoutineModels)
        routineModelAdapter = new ArrayAdapter<>(getInflater().getContext(),             // create an adapter to fill array
                android.R.layout.simple_list_item_1);
        routineModelAdapter.clear();                // first clear adapter
        for (RoutineModel m : mod.getAllRoutineModels())
            routineModelAdapter.add(m.getName());

        routineModelAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);   //or whatever your sorting algorithm
            }
        });
        xml().routineModelListView.setAdapter(routineModelAdapter);
        xml().routineModelListView.setOnItemClickListener(((parent, views, position, id) -> {

            String clickedRoutineName = routineModelAdapter.getItem(position).toString();
            Log.i(TAG, "Selected routine: " + clickedRoutineName);
            RoutineModel currentRoutine = mod.getRoutineModel(clickedRoutineName);
            Log.i(TAG, "CONTAINS" + currentRoutine.getExercises().first().getName());

            if(currentRoutine != null){
                Log.i(TAG, "Retrieved Routine: " + currentRoutine.getName());
                //Check if routine is already populated with exercises
                if(currentRoutine.getExercises().size() != 0){
                    // update current workout and switch to current workout tab


                    updateCurrentWorkout(currentRoutine);
                    xml().pager.setCurrentItem(0);
                } else {
                    Log.i(TAG, "Routine has not yet been built");
                    // open buildRoutineFragmentDialog
                    showRoutineBuilder(currentRoutine.getName());
                }
            }

        }));
    }

    public void updateCurrentWorkout(RoutineModel currentRoutine){
        //WorkoutModule mod = Module.of(WorkoutModule.class);
        xml().currentWorkoutHeader.setText("");

        currentRoutineExerciseAdapter.clear();                // first clear adapter
        for (RealmString m : currentRoutine.getExercises())
            currentRoutineExerciseAdapter.add(m.getName());
        xml().currentRoutineListView.setAdapter(currentRoutineExerciseAdapter);
        xml().currentRoutineListView.setOnItemClickListener(((parent, views, position, id) -> {
            ExerciseTypeModel exercise = mod.getExerciseTypeModel(currentRoutineExerciseAdapter.getItem(position));
            if(exercise == null){
                Log.e(TAG, "Exercise retrieved from Realm was null!");
            } else if( exercise.getCategory().equalsIgnoreCase("Cardio")){
                // exercise is Cardio
                showCardioDialog(exercise.getName());
            } else {
                showSetDialog(exercise.getName());
            }
        }));
    }


    /**
     * @showNewExerciseDialog
     * This method launches a DialogFragment that allows
     * a user to create a new type of exercise
     */
    void showNewExerciseDialog() {
        DialogFragment newFragment = NewExerciseDialogFragment.newInstance(getString(R.string.title_new_exercise_dialog));
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * @showNewRoutineDialog()
     * This method launches a DialogFragment that allows
     * a user to create a new Routine from existing types of
     * exercises
     */
    void showNewRoutineDialog() {
        DialogFragment newFragment = NameRoutineFragment.newInstance();
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog");

    }

    /**
     * @showSetDialog
     * @param name is the name of exercise to become the title of the new dialog
     * This method launches a DialogFragment that allows
     * a user to log his or her selected exercise
     */
    void showSetDialog(String name) {
        CompletedExerciseModel mostRecent = mod.getMostRecentCompletedExerciseModel(name);


        SetDialogFragment newFragment = SetDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
        newFragment.titleThing = name;
        newFragment.mostRecent = mostRecent;
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog");
    }



    void showCardioDialog(String exerciseName) {
        RealmList<ExerciseSetModel> set = new RealmList<>();
        //Build Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getInflater().getContext());
        LayoutInflater inflater = (LayoutInflater) getInflater().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.cardio_set_dialog, null);
        builder.setView(dialogLayout);
        builder.setTitle(exerciseName);
        TextView timerText = (TextView) dialogLayout.findViewById(R.id.cardio_stopwatch_text);
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.setMode(Stopwatch.WatchMode.UP);
        stopwatch.setOnTimeChange(() -> {
            timerText.setText(stopwatch.getPrettyElapsed(false).replaceAll(" ", ":") + stopwatch.getMilliString(stopwatch.getElapsedDuration()));
        });

        ListView completedSetListView = (ListView) dialogLayout.findViewById(R.id.list_complete_cardio_reps);
        ArrayAdapter<ExerciseSetModel> completedSetAdapter = new ArrayAdapter<>(getInflater().getContext(),
                android.R.layout.simple_list_item_1);
        completedSetListView.setAdapter(completedSetAdapter);        // When sets are completed, they are listed in the dialog

        Button timerButton = (Button) dialogLayout.findViewById(R.id.cardio_timer_button);

        timerButton.setOnClickListener( butt -> {
            if(stopwatch.isRunning()){
                stopwatch.pause();
                timerButton.setText("Resume");
            } else {
                stopwatch.resume();
                timerButton.setText("Pause");
            }
        });

        Button resetTimerButton = (Button) dialogLayout.findViewById(R.id.reset_cardio_timer_button);

        resetTimerButton.setOnClickListener( butts -> {
            if(!(stopwatch.isRunning())){
                stopwatch.reset();
                timerText.setText(stopwatch.getPrettyElapsed(false).replaceAll(" ", ":") + stopwatch.getMilliString(stopwatch.getElapsedDuration()));
            }
        });

        Button button = (Button) dialogLayout.findViewById(R.id.complete_cardio_set_button);
        button.setOnClickListener(butt -> {
            if(stopwatch.getMillisElapsed(true) == 0){
                // set timerfield error
                ;
            } else {

                long duration = stopwatch.getMillisElapsed(true);
                ExerciseSetModel newSet = new ExerciseSetModel();
                newSet.setDuration(duration);
                newSet.setDate(Calendar.getInstance().getTime());
                set.add(newSet);        // add set to the list of sets

                completedSetAdapter.clear();
                completedSetAdapter.addAll(set);        //repopulate the adapter
            }
        });

        // Add Dialog buttons
        builder.setPositiveButton("Save", (dialog, whichButton) -> {
            // Get items selected and update Routine Model
            CompletedExerciseModel completedExercise = new CompletedExerciseModel();
            completedExercise.setExerciseName(exerciseName);
            completedExercise.setSets(set);
            Calendar rightNow = Calendar.getInstance();
            completedExercise.setDate(rightNow.getTime());
            ((WorkoutFragment)getTargetFragment()).saveCompletedExercise(completedExercise);

        });
        builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });


        builder.create().show();
    }

    /**
     * @showRoutineBuilder
     * @param name is name of routine to becom the title of the dialog
     * This method launches a Dialog Fragment that allows
     * a select exercises to add to a routine
     */
    void showRoutineBuilder(String name) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getInflater().getContext());
        LayoutInflater inflater = (LayoutInflater) getInflater().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.fragment_workout_build_routine_layout, null);
        builder.setView(dialogLayout);
        builder.setTitle(name);

        // fill spinner with all different workout "targets" with which a user can filter exercises
        //WorkoutModule mod = Module.of(WorkoutModule.class);
        List<ExerciseTargetModel> filters = mod.getAllTargets();
        Spinner filterSpinner = (Spinner) dialogLayout.findViewById(R.id.filter_spinner);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getInflater().getContext(), android.R.layout.simple_list_item_1);
        filterSpinner.setAdapter((filterAdapter));
        filterAdapter.clear();
        filterAdapter.add("None");
        for(ExerciseTargetModel m : filters)
            filterAdapter.add(m.getTarget());

        // list all user-created exercises in Dialog
        ListView exerciseListView = (ListView) dialogLayout.findViewById(R.id.choose_exercises_view);
        ArrayAdapter<String> exerciseListAdapter = new ArrayAdapter<>(getInflater().getContext(),
                android.R.layout.select_dialog_multichoice);
        exerciseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        exerciseListView.setAdapter(exerciseListAdapter);
        exerciseListAdapter.clear();

        for(ExerciseTypeModel m : mod.getAllExerciseTypeModels())
            exerciseListAdapter.addAll(m.getName());

        // Create button that filters listed exercises based on "target"
        Button button = (Button) dialogLayout.findViewById(R.id.filter_button);
        button.setOnClickListener(_button -> {
            String f = filterSpinner.getSelectedItem().toString();
            exerciseListAdapter.clear();
            if (f.equals("None")) {
                // Remove any applied filters (i.e., show all exercises)
                for (ExerciseTypeModel m : mod.getAllExerciseTypeModels())
                    exerciseListAdapter.add(m.getName());
            } else {
                for (ExerciseTypeModel m : mod.getFilteredExerciseTypeModels(f))
                    exerciseListAdapter.add(m.getName());
            }
        });

        // Add Dialog buttons
        builder.setPositiveButton("Save", (dialog, whichButton) -> {
            // Get items selected and update Routine Model
            Calendar rightNow = Calendar.getInstance();
            RealmList<RealmString> exercises = new RealmList<RealmString>();
            if (exerciseListAdapter.getCount() > 0) {
                for (int i = 0; i < exerciseListAdapter.getCount(); i++) {
                    if (exerciseListView.isItemChecked(i)) {
                        RealmString newExercise = new RealmString();
                        newExercise.setName(exerciseListAdapter.getItem(i).toString());
                        rightNow = Calendar.getInstance();
                        newExercise.setDate(rightNow.getTime());
                        exercises.add(newExercise);
                    }
                }
                RoutineModel newRoutine = mod.createNewRoutine(name, exercises);
                mod.addRoutineModel(newRoutine);
                routineModelAdapter.clear();                // first clear adapter
                for (RoutineModel m : mod.getAllRoutineModels())
                    routineModelAdapter.add(m.getName());
            } else {
                Snackbar.make(getView(), "No exercises selected!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
        builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        builder.create().show();
    }

    private RealmLineData createLineData(String exerciseName){
        RealmLineDataSet<CompletedExerciseModel> dataSet = new RealmLineDataSet<>(
                mod.getCompletedExercisesQuery(exerciseName), "oneRepMax");
        ArrayList<ILineDataSet> dataSetList = new ArrayList<ILineDataSet>();
        dataSetList.add(dataSet);
        RealmLineData data = new RealmLineData(mod.getCompletedExercisesQuery(exerciseName), "dateString" , dataSetList);
        return data;
    }

    void showExerciseHistoryDialog(String exerciseName){
        // Get Exercise History
        //WorkoutModule mod = Module.of(WorkoutModule.class);
        ArrayList<CompletedExerciseModel> history =  mod.getCompletedExercises(exerciseName);
        Log.i(TAG, "Num Completed Exercises: " + history.size());
        int max = 0;
        int totalWeightLifted = 0;
        for(CompletedExerciseModel c : history){
            if(c.get1RMax() > max){
                max = c.get1RMax();
            }
            for(ExerciseSetModel set : c.getSets()){
                totalWeightLifted += set.getWeight() * set.getReps();
            }
        }

        //Build Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getInflater().getContext());
        LayoutInflater inflater = (LayoutInflater) getInflater().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.exercise_history_dialog, null);
        builder.setView(dialogLayout);
        builder.setTitle(exerciseName + " History");

        LineChart graph = (LineChart) dialogLayout.findViewById(R.id.history_graph);
        graph.setDescription("One Rep Max over Time");
        graph.setData(createLineData(exerciseName));
        graph.invalidate();
        //RealmLineDataSet<CompletedExerciseModel> dataSetList = new RealmLineDataSet<CompletedExerciseModel>(
                //mod.getCompletedExercises()

       // graph.setData()

        TextView oneRMax = (TextView) dialogLayout.findViewById(R.id.oneRMax);
        oneRMax.setText("One Rep Max: " + max);
        TextView totalWeight = (TextView) dialogLayout.findViewById(R.id.integratedWeight);
        totalWeight.setText("Total weight lifted: " + totalWeightLifted );

        builder.create().show();


    }


    /**
     * @saveNewExerciseType
     * This method builds a new ExerciseTypeModel, populating its
     * variables with those stored in WorkoutFragment set by
     * a user selecting "Save" in the NewExerciseDialogFragment
     */
    public void saveNewExerciseType() {
        Log.i("FragmentAlertDialog", "Positive click!" + "Name: " + name + " Category: " + category + " Target: " + target);
        if(name.equals("")){
            Snackbar.make(getView(), "Not added: Exercise name field blank!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{

            ExerciseTypeModel newExercise = WorkoutModule.createNewExercise(name, category, target);
            //Add to Realm
            exerciseTypeModelList.add(newExercise);
            //WorkoutModule mod = Module.of(WorkoutModule.class);
            mod.addExerciseTypeModel(newExercise);

            categories = null;
            categories = mod.getAllCategories();
            groupedExercises = null;
            groupedExercises = mod.groupExercisesByTargetAlpha(categories);
            categoryAdapter = new SimpleExpandableListAdapter(getInflater().getContext(), categories, groupedExercises);
            xml().categoryExpandableView.setAdapter(categoryAdapter);
            //exerciseTypeModelAdapter.addAll(mod.getAllExerciseTypeModels());
            /*
            for (ExerciseTypeModel m : mod.getAllExerciseTypeModels())
                exerciseTypeAdapter.add(m.getName());

            exerciseTypeAdapter.sort(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);   //or whatever your sorting algorithm
                }
            });
            exerciseTypeAdapter.notifyDataSetChanged();
            */
        }
    }

    /**
     * @showDOWScheduleBuilder
     */
    public void showDOWScheduleBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getInflater().getContext());
        LayoutInflater inflater = (LayoutInflater) getInflater().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.schedule_builder_dow_dialog, null);
        builder.setView(dialogLayout);
        builder.setTitle("Select Workout Days");

        // list days of week
        ListView dowListView = (ListView) dialogLayout.findViewById(R.id.dow_list);
        ArrayAdapter<String> dowListAdapter = new ArrayAdapter<>(getInflater().getContext(),
                android.R.layout.select_dialog_multichoice);
        dowListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dowListView.setAdapter(dowListAdapter);
        dowListAdapter.clear();
        dowListAdapter.addAll(getResources().getStringArray(R.array.days_of_week));


        // Add Dialog buttons
        builder.setPositiveButton("Save", (dialog, whichButton) -> {
            // Get items selected and update Routine Model
            Boolean[] dow = new Boolean[7];
            for(int i = 0; i < 7; i++)
                dow[i] = new Boolean(dowListView.isItemChecked(i));
            showRoutineScheduleBuilder(dow);

        });
        builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        builder.create().show();
    }

    /**
     * @showRoutineScheduleBuilder
     */
    public void showRoutineScheduleBuilder(Boolean[] workoutDays){
        AlertDialog.Builder builder = new AlertDialog.Builder(getInflater().getContext());
        LayoutInflater inflater = (LayoutInflater) getInflater().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.schedule_builder_routine_dialog, null);
        builder.setView(dialogLayout);
        builder.setTitle("Select Routine Rotation");

        // list all user-created routines in Dialog
        ListView routineListView = (ListView) dialogLayout.findViewById(R.id.routine_schedule_list);
        ArrayAdapter<String> routineListAdapter = new ArrayAdapter<>(getInflater().getContext(),
                android.R.layout.select_dialog_multichoice);
        routineListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        routineListView.setAdapter(routineListAdapter);
        routineListAdapter.clear();

        //WorkoutModule mod = Module.of(WorkoutModule.class);
        for(RoutineModel m : mod.getAllRoutineModels())
            routineListAdapter.addAll(m.getName());

        // Add Dialog buttons
        builder.setPositiveButton("Save", (dialog, whichButton) -> {
            Calendar rightNow = Calendar.getInstance();
            RealmList<RealmString> routines = new RealmList<RealmString>();
            if(routineListAdapter.getCount() > 0) {
                for (int i = 0; i < routineListAdapter.getCount(); i++) {
                    if (routineListView.isItemChecked(i)) {
                        RealmString newRoutine = new RealmString();
                        newRoutine.setName(routineListAdapter.getItem(i).toString());
                        newRoutine.setDate(Calendar.getInstance().getTime());
                        Log.i(TAG, "Building rotation: added " + newRoutine.getName());
                        routines.add(newRoutine);
                    }
                }
            }

            WorkoutScheduleModel newSchedule = mod.createNewSchedule(workoutDays, Calendar.getInstance().getTime(), routines);
            mod.addWorkoutScheduleModel(newSchedule);

            if(newSchedule.getFriday() == true){
                RoutineModel routine = mod.getRoutineModel(newSchedule.getRoutineRotation().first().getName());
                updateCurrentWorkout(routine);
            }

        });
        builder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        builder.create().show();
    }

    /**
     *
     */
    public void cancelNewExerciseType() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }


    /**
     *
     */
    public void saveRoutine() {
        // Do stuff here.
        if (routineName.equals("")) {
            Snackbar.make(getView(), "Not added: Routine name field blank!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            //WorkoutModule mod = Module.of(WorkoutModule.class);
            showRoutineBuilder(routineName);

            // Bind data to view (RoutineModels)
            routineModelAdapter.clear();                // first clear adapter
            for (RoutineModel m : mod.getAllRoutineModels())
                routineModelAdapter.add(m.getName());
            //xml().routineModelListView.setAdapter(routineModelAdapter);
            routineModelAdapter.sort(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);   //or whatever your sorting algorithm
                }
            });
            routineModelAdapter.notifyDataSetChanged();
        }
    }

    /**
     *
     */
    public void cancelNewRoutine() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }




    public void saveCompletedExercise(CompletedExerciseModel newCompletedExercise) {
        //WorkoutModule mod = Module.of(WorkoutModule.class);
        mod.addCompletedExercise(newCompletedExercise);
    }

}