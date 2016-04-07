package com.sciencesquad.health.workout;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.DialogFragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.app.FragmentManager;
import android.os.Bundle;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ArrayAdapter;

import android.widget.ListView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.ui.RevealTransition;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentWorkoutBinding;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

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
		String _ = WorkoutModule.TAG; // instantiates the Module...
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
		xml().setModule(Module.moduleForClass(WorkoutModule.class));



        //ExerciseTypeModel newExerciseA = new ExerciseTypeModel("Bench Press", "Strength", "Chest");
        //exerciseTypeModelList.add(newExerciseA);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        // mViewPager = xml().pager; //(ViewPager)view.findViewById(R.id.container);
        //mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = xml().tabs; //(TabLayout)view.findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);
        StaticPagerAdapter.install(xml().pager);
        xml().tabs.setupWithViewPager(xml().pager);

        FloatingActionButton fab = xml().fab; //(FloatingActionButton)view.findViewById(R.id.workoutFab);

        fab.setOnClickListener(view1 -> {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //      .setAction("Action", null).show();
            int selectedTab = tabLayout.getSelectedTabPosition();
            if (selectedTab == 0) {
                // in exercise tab
                showNewExerciseDialog();
            } else if (selectedTab == 1) {
                // in routine tab
                showNewRoutineDialog();
            }
        });

        xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

        // Bind data to view
        WorkoutModule mod = Module.moduleForClass(WorkoutModule.class);
        ArrayAdapter<String> exerciseTypeAdapter = new ArrayAdapter<>(getContext(),             // create an adapter to fill array
                android.R.layout.simple_list_item_1);

        exerciseTypeAdapter.clear();                // first clear adapter
        //exerciseTypeAdapter.addAll(mod.getAllExerciseTypeModels());        // add all exercises created by user to the adapter
        for (ExerciseTypeModel m : mod.getAllExerciseTypeModels())
            exerciseTypeAdapter.add(m.getName());
        xml().exerciseModelListView.setAdapter(exerciseTypeAdapter);


        //xml().exerciseModelListView.setOnItemClickListener(this, );
        /*
        xml().exerciseModelListView.setOnItemClickListener(((parent, views, position, id) -> {
            ((WorkoutFragment) getTargetFragment()).showSetDialog(exerciseTypeAdapter.getItem(position));
        }));
        */
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    /*
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment source = null;

        public SectionsPagerAdapter(FragmentManager fm, Fragment source) {
            super(fm);
            this.source = source;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){

            }
            switch(position){
                case 0:
                    ExerciseTypeFragment a = ExerciseTypeFragment.newInstance();
                    a.setTargetFragment(source, 0);
                    return a;
                case 1:
                    RoutineFragment b = RoutineFragment.newInstance();
                    b.setTargetFragment(source, 0);
                    return b;
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
    */
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
        DialogFragment newFragment = NameRoutineFragmentFragment.newInstance(
                R.string.title_new_routine_dialog);
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
        SetDialogFragment newFragment = SetDialogFragment.newInstance(
                R.string.title_new_exercise_dialog);
        newFragment.titleThing = name;
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * @showRoutineBuilder
     * @param name is name of routine to becom the title of the dialog
     * This method launches a Dialog Fragment that allows
     * a select exercises to add to a routine
     */
    void showRoutineBuilder(String name) {
        BuildRoutineDialogFragment newFragment = BuildRoutineDialogFragment.newInstance(getString(R.string.title_new_exercise_dialog));
        //newFragment.titleThing = name; // FIXME: Is this what we pass in?
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog");
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

            /*
            ExerciseTypeModel newExercise = new ExerciseTypeModel();
            newExercise.setName(name);
            newExercise.setCategory(category);
            newExercise.setTarget(target);
            newExercise.setMaxDistance(0.0);
            newExercise.setMaxDuration((long) 0);
            newExercise.setMaxWeight(0);
            Calendar rightNow = Calendar.getInstance();
            newExercise.setDate(rightNow.getTime());
            exerciseTargets.add(target);
            */

            ExerciseTypeModel newExercise = WorkoutModule.createNewExercise(name, category, target);
            //Add to Realm
            exerciseTypeModelList.add(newExercise);
            WorkoutModule mod = Module.moduleForClass(WorkoutModule.class);
            mod.addExerciseTypeModel(newExercise);


			// FIXME: THIS ID DOES NOT EXIST, CALL WILL RETURN NULL

            //ListView exerciseListView = (ListView)getView().findViewById(R.id.exercise_model_list_view);

            ArrayAdapter<String> exerciseTypeModelAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1);
            exerciseTypeModelAdapter.clear();
            //exerciseTypeModelAdapter.addAll(mod.getAllExerciseTypeModels());
            for (ExerciseTypeModel m : mod.getAllExerciseTypeModels())
                exerciseTypeModelAdapter.add(m.getName());
            xml().exerciseModelListView.setAdapter(exerciseTypeModelAdapter);
        }
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
            RoutineModel newRoutine = new RoutineModel();
            newRoutine.setName(routineName);
            routineModelList.add(newRoutine);
			// FIXME: THIS ID DOES NOT EXIST, CALL WILL RETURN NULL
            ListView routineListView = (ListView)getView().findViewById(R.id.routine_model_list_view);
            ArrayAdapter<RoutineModel> routineModelArrayAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1);
            routineModelArrayAdapter.clear();
            routineModelArrayAdapter.addAll(routineModelList);
            routineListView.setAdapter(routineModelArrayAdapter);
        }
    }

	/**
     *
     */
    public void cancelNewRoutine() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }
}
