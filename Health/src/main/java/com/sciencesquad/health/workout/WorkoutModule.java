package com.sciencesquad.health.workout;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.BaseApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by mrjohnson on 3/1/16.
 */

public class WorkoutModule extends Module {
    public static final String TAG = WorkoutModule.class.getSimpleName();
    static { Module.registerModule(WorkoutModule.class); }

    //Data context.
    private RealmContext<ExerciseTypeModel> workoutRealm;

    //private RealmContext<RoutineModel> workoutRealm;

    /**
     * Constructs the module itself.
     * It also sets up a Realm Context for the Module.
     */


    public WorkoutModule()  {
        this.workoutRealm = new RealmContext<>();
        this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");


        bus(b -> {
            b.subscribe("DataEmptyEvent", null, e -> Log.d(TAG, "Some realm was empty."));
            b.subscribe("DataFailureEvent", this, e -> {
                Log.d(TAG, "Nutrition realm failed in Realm Transaction!");

            });
            b.subscribe("DataFailureEvent", null, e -> {
                Log.d(TAG, "Data failed somewhere.");

            });
            b.subscribe("DataUpdateEvent", null, e -> {
                Log.d(TAG, "There was an update to a realm.");

                // maybe use the key as the realm name?
                if (e.get("key").equals("WorkoutRealm")) {
                    Log.d(TAG, "Ignoring " + this.getClass().getSimpleName() + "'s own data update");
                }
                else {
                    // do something about it.
                }
            });
        });
    }

    public ArrayList<ExerciseTypeModel> getAllExerciseTypeModels() {
        ArrayList<ExerciseTypeModel> exercises = new ArrayList<>();
        RealmResults<ExerciseTypeModel> results = workoutRealm.query().findAll();
        exercises.addAll(results);

        return exercises;
    }


    @Override
    public Pair<String, Integer> identifier() {
        return null;
    }

    @Override
    public void init() {

    }
    public void addExerciseTypeModel(ExerciseTypeModel newExercise){
        workoutRealm.add(newExercise);
    }


    public static ExerciseTypeModel createNewExercise(String name, String category, String target){
        ExerciseTypeModel newExerciseType = new ExerciseTypeModel();
        newExerciseType.setName(name);
        newExerciseType.setCategory(category);
        newExerciseType.setTarget(target);
        newExerciseType.setMaxDistance(0.0);
        newExerciseType.setMaxDuration((long) 0);
        newExerciseType.setMaxWeight(0);
        Calendar rightNow = Calendar.getInstance();
        newExerciseType.setDate(rightNow.getTime());

        return newExerciseType;
    }


}
