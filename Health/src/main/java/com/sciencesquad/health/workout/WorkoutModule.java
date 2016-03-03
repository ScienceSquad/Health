package com.sciencesquad.health.workout;

import android.util.Log;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;

/**
 * Created by mrjohnson on 3/1/16.
 */

public class WorkoutModule extends Module {
    public static final String TAG = WorkoutModule.class.getSimpleName();

    private RealmContext<RoutineModel> workoutRealm;

    /**
     * Constructs the module itself.
     * It also sets up a Realm Context for the Module.
     */

    public WorkoutModule() throws Exception{
        Log.d(TAG, "Constructing Workout Module");
        // Set up Workout Realm
        this.workoutRealm = new RealmContext<>();
        this.workoutRealm.init(BaseApplication.application(), ExerciseTypeModel.class, "workout.realm");
    }
}