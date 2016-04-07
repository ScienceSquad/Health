package com.sciencesquad.health.workout;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.BaseApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmQuery;
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

        addRecommendedWorkouts();

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
                } else {
                    // do something about it.
                }
            });
        });
    }

    public ArrayList<ExerciseTypeModel> getAllExerciseTypeModels() {
        ArrayList<ExerciseTypeModel> exercises = new ArrayList<>();
        RealmResults<ExerciseTypeModel> results = workoutRealm.query(ExerciseTypeModel.class).findAll();
        exercises.addAll(results);

        return exercises;
    }

    void addRecommendedWorkouts(){

        //StrongLifts 5x5
        ExerciseTypeModel squat = createNewExercise("Squat", "Strength", "Legs");
        ExerciseTypeModel benchPress = createNewExercise("Bench Press", "Strength", "Chest");
        ExerciseTypeModel barBellRow = createNewExercise("Barbell Row", "Strength", "Back");
        ExerciseTypeModel overHeadPress = createNewExercise("Overhead Press", "Strength", "Shoulders");
        ExerciseTypeModel deadLift = createNewExercise("Deadlift", "Strength", "Core");

        this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");
        RealmList<ExerciseTypeModel> sLAExercises = new RealmList<>();
        sLAExercises.add(squat);
        sLAExercises.add(benchPress);
        sLAExercises.add(barBellRow);

        for (ExerciseTypeModel m : sLAExercises)
            addExerciseTypeModel(m);

        RealmList<ExerciseTypeModel> sLBExercises = new RealmList<>();
        sLBExercises.add(squat);
        sLBExercises.add(overHeadPress);
        sLBExercises.add(deadLift);

        for (ExerciseTypeModel m : sLBExercises)
            addExerciseTypeModel(m);

        this.workoutRealm.init(BaseApp.app(), RoutineModel.class, "WorkoutRealm");

        RealmList<RealmString> exerciseNames = new RealmList<>();
        for(ExerciseTypeModel m : sLAExercises){
            RealmString newName = new RealmString();
            newName.setName(m.getName());
            exerciseNames.add(newName);
        }
        RoutineModel strongLiftsA = createNewRoutine("StrongLifts 5x5: A", exerciseNames);
        //addRoutineModel(strongLiftsA);

        exerciseNames.clear();
        for(ExerciseTypeModel m : sLAExercises){
            RealmString newName = new RealmString();
            newName.setName(m.getName());
            exerciseNames.add(newName);
        }
        RoutineModel strongLiftsB = createNewRoutine("StrongLifts 5x5: B", exerciseNames);
        //addRoutineModel(strongLiftsB);
    }


    @Override
    public Pair<String, Integer> identifier() {
        return null;
    }

    @Override
    public void init() {

    }

    /**
     *
     * @param newExercise
     * @return true on success, false on failure (duplicate)
     */
    public boolean addExerciseTypeModel(ExerciseTypeModel newExercise){
        this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");
        if(!isDuplicateExerciseType(newExercise)){
            workoutRealm.add(newExercise);
            return true;
        } else {
            return false;
        }

    }


    public boolean isDuplicateExerciseType(ExerciseTypeModel newExercise){
        this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");
        RealmQuery<ExerciseTypeModel> query = this.workoutRealm.query(ExerciseTypeModel.class);
        query.equalTo("name", newExercise.getName());

        if(query.findAll().size() == 0){
            return false;       // This exercise has NOT been previously added
        } else {
            Log.i(TAG, "duplicate exercise");
            return true;        // This exercise has been previously added
        }
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

    public static RoutineModel createNewRoutine(String name, RealmList<RealmString> exerciseList){
        RoutineModel newRoutine = new RoutineModel();
        Calendar rightNow = Calendar.getInstance();
        newRoutine.setDate(rightNow.getTime());
        newRoutine.setName(name);

        newRoutine.setExercises(exerciseList);

        return newRoutine;
    }


    public boolean isDuplicateRoutineType(RoutineModel newRoutine){
        try {
            RealmQuery<RoutineModel> query = this.workoutRealm.query(RoutineModel.class);
            query.equalTo("name", newRoutine.getName());

            if(query.findAll().size() == 0){
                return false;       // This exercise has NOT been previously added
            } else {
                return true;        // This exercise has been previously added
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     *
     * @param newRoutine
     * @return true on success, false on failure (duplicate)
     */

    public boolean addRoutineModel(RoutineModel newRoutine){
        this.workoutRealm.init(BaseApp.app(), RoutineModel.class, "WorkoutRealm");
        if(!isDuplicateRoutineType(newRoutine)){
            workoutRealm.add(newRoutine);
            return true;
        } else {
            return false;
        }

    }




}
