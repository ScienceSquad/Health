package com.sciencesquad.health.workout;

import android.util.Log;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;

import java.util.Calendar;

/**
 * Created by mrjohnson on 3/1/16.
 */

public class WorkoutModule extends Module {
    public static final String TAG = WorkoutModule.class.getSimpleName();

    //private RealmContext<RoutineModel> workoutRealm;

    /**
     * Constructs the module itself.
     * It also sets up a Realm Context for the Module.
     */

    public WorkoutModule(){}

    /*
    public static ExerciseTypeModel createNewExercise(String name, ExerciseKind kind, String target){
        ExerciseTypeModel newExerciseType = new ExerciseTypeModel();
        newExerciseType.setName(name);
        newExerciseType.setCategory(kind.toString());
        newExerciseType.setTarget(target);
        newExerciseType.setMaxDistance(0.0);
        newExerciseType.setMaxDuration((long) 0);
        newExerciseType.setMaxWeight(0);
        Calendar rightNow = Calendar.getInstance();
        newExerciseType.setDate(rightNow.getTime());

        return newExerciseType;
    }
    */

}
