package com.sciencesquad.health.workout;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.BaseApp;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
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

        this.workoutRealm.getRealm().beginTransaction();
        this.workoutRealm.getRealm().deleteAll();
        this.workoutRealm.getRealm().commitTransaction();
        //this.workoutRealm.getRealm().refresh();

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

    public ArrayList<ExerciseTypeModel> getFilteredExerciseTypeModels(String target) {
        ArrayList<ExerciseTypeModel> exercises = new ArrayList<>();
        RealmResults<ExerciseTypeModel> results = workoutRealm.query(ExerciseTypeModel.class).equalTo("target", target).findAll();
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

        //this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");
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


        RealmList<RealmString> exerciseNames = new RealmList<>();
        Calendar rightNow = Calendar.getInstance();
        for(ExerciseTypeModel m : sLAExercises){
            RealmString newName = new RealmString();
            newName.setDate(rightNow.getTime());
            newName.setName(m.getName());
            exerciseNames.add(newName);
        }

        RoutineModel strongLiftsA = createNewRoutine("StrongLifts 5x5: A", exerciseNames);
        addRoutineModel(strongLiftsA);

        RealmList<RealmString> exerciseNamesB = new RealmList<>();
        for(ExerciseTypeModel m : sLBExercises){
            RealmString newName = new RealmString();
            newName.setDate(rightNow.getTime());
            newName.setName(m.getName());
            exerciseNamesB.add(newName);
        }

        RoutineModel strongLiftsB = createNewRoutine("StrongLifts 5x5: B", exerciseNamesB);
        addRoutineModel(strongLiftsB);

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
        if(!isDuplicateExerciseType(newExercise)){
            try {
                workoutRealm.add(newExercise);
            } catch (Exception e) {
                Log.i(TAG, "Error adding ExerciseTypeModel to Realm!");
                return false;
            }
            try {
                //Add exercise target
                ExerciseTargetModel newTarget = new ExerciseTargetModel();
                newTarget.setTarget(newExercise.getTarget());
                Calendar rightNow = Calendar.getInstance();
                newTarget.setDate(rightNow.getTime());
                workoutRealm.add(newTarget);
            } catch (Exception e){
                if(e.getMessage().contains("UNIQUE constraint failed")){
                    Log.e(TAG, "Existing target");
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<CompletedExerciseModel> getCompletedExercises(String exerciseName){
        ArrayList<CompletedExerciseModel> completed = new ArrayList<>();
        try {
            RealmQuery<CompletedExerciseModel> query = this.workoutRealm.query(CompletedExerciseModel.class).equalTo("exerciseName", exerciseName);
            RealmResults<CompletedExerciseModel> results = query.findAll();
            for(CompletedExerciseModel c : results)
                completed.add(c);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return completed;
    }


    public boolean isDuplicateExerciseType(ExerciseTypeModel newExercise){
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
        //Calendar rightNow = Calendar.getInstance();
        Date d = Calendar.getInstance().getTime();
        newRoutine.setDate(d);
        //Calendar rightNow = Calendar.getInstance();
        //newRoutine.setDate(rightNow.getTime());
        newRoutine.setName(name);
        newRoutine.setExercises(exerciseList);

        return newRoutine;
    }

    public ArrayList<ExerciseTargetModel> getAllTargets() {
        ArrayList<ExerciseTargetModel> targets = new ArrayList<>();
        try {
            RealmResults<ExerciseTargetModel> results = workoutRealm.query(ExerciseTargetModel.class).findAll();
            targets.addAll(results);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving targets from Realm");
        }
        return targets;
    }

    public ArrayList<RoutineModel> getAllRoutineModels() {
        ArrayList<RoutineModel> routines = new ArrayList<>();
        try {
            RealmResults<RoutineModel> results = workoutRealm.query(RoutineModel.class).findAll();
            Log.i(TAG, "Routine Results size " + results.size());
            routines.addAll(results);
        } catch (Exception e) {
            Log.e(TAG, "Error getting routines from Realm");
        }

        return routines;
    }

    public RoutineModel getRoutineModel(String routineName) {
        try {
            RealmResults<RoutineModel> result = workoutRealm.query(RoutineModel.class).equalTo("name", routineName).findAll();
            if(result.size() == 0){
                return null;
            } else {
                return result.first();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding RoutineModel by name " + e.getMessage());
        }
        return null;
    }

    /*
    public void updateRoutineExercises(String routineName, RealmList<RealmString> exercises){
        RoutineModel
        workoutRealm.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // begin & end transcation calls are done for you
                RoutineModel routine = workoutRealm.getRealm().where(RoutineModel.class).equalTo("name", routineName).findFirst();
                routine.setExercises(exercises);
            }
        }, new Realm.Transaction.Callback() {

        });
    }
    */


    public boolean isDuplicateRoutineType(RoutineModel newRoutine){
        try {
            RealmQuery<RoutineModel> query = this.workoutRealm.query(RoutineModel.class);
            query.equalTo("name", newRoutine.getName());

            if(query.findAll().size() == 0){
                return false;       // This exercise has NOT been previously added
            } else {
                Log.i(TAG, "Duplicate Routine");
                return true;        // This exercise has been previously added
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean addCompletedExercise(CompletedExerciseModel newCompletedExercise){
        try {
            workoutRealm.getRealm().beginTransaction();
            workoutRealm.getRealm().copyToRealm(newCompletedExercise);
            workoutRealm.getRealm().commitTransaction();
        } catch (Exception e){
            if (e.getMessage().contains("Trying to set non-nullable field date to null.")){
                Log.w(TAG, "Continuing to add completedExercise anyway");
                workoutRealm.getRealm().commitTransaction();
                return true;
            }
            else
                workoutRealm.getRealm().cancelTransaction();
            Log.e(TAG, "Error adding RoutineModel to Realm");
            Log.e(TAG, e.getMessage());
            return false;
        }
        return  true;
    }

    public boolean addWorkoutScheduleModel(WorkoutScheduleModel schedule){
        /*
        WorkoutScheduleModel currentWorkout = getWorkoutSchedule();
        if(currentWorkout != null){
            try{
                workoutRealm.query(WorkoutScheduleModel.class).findAll().first().removeFromRealm();
            } catch (Exception e){
                Log.e(TAG, "Error removing current schedule from Realm");
            }

        }
        */
        try {
            workoutRealm.getRealm().beginTransaction();
            workoutRealm.getRealm().copyToRealm(schedule);
            workoutRealm.getRealm().commitTransaction();

            //workoutRealm.add(schedule);
        } catch (Exception e){
            if (e.getMessage().contains("Trying to set non-nullable field date to null.")){
                Log.w(TAG, "Continuing to add schedule anyway");
                workoutRealm.getRealm().commitTransaction();
                return true;
            }
            else
                workoutRealm.getRealm().cancelTransaction();
            Log.e(TAG, "Error adding WorkoutSchedule to Realm");
            Log.e(TAG, e.getMessage());
            return false;
        }
        return  true;
    }

    public WorkoutScheduleModel getWorkoutSchedule(){
        WorkoutScheduleModel schedule = null;
        try {
            RealmResults<WorkoutScheduleModel> results = workoutRealm.query(WorkoutScheduleModel.class).findAll();
            schedule = results.first();
            Log.i(TAG, "Found a schedule! First Routine: " + schedule.getRoutineRotation().first().getName());
        } catch (Exception e) {
            Log.e(TAG, "Error getting WorkoutSchedule from Realm");
            schedule = null;
        }

        return schedule;
    }

    public RoutineModel getTodaysRoutine(){
        int offset = 0;
        WorkoutScheduleModel schedule = getWorkoutSchedule();
        Calendar rightNow = Calendar.getInstance();
        if(schedule != null){
            int startDOW  = schedule.getStartDate().getDay();
            //calculate number of workout days in a week
            int numWorkoutDaysInWeek = 0;
            Boolean[] dow = getWorkoutDays(schedule);
            if(dow[rightNow.getTime().getDay()] != true ){
                Log.i(TAG, "No scheduled workout: today is not a workout day");
                return null;
            }

            for(int i = 0; i < 7; i++) {
                if (dow[i] == true) {
                    numWorkoutDaysInWeek++;
                    if (i < startDOW)
                        offset++;
                }
            }
            //TODO: actually do this
            //calculate number of workout days passed since startdate
            //long numDaysSinceStart = getDayCount(schedule.getStartDate(), rightNow.getTime());
            Log.i(TAG, "ATTEMPTING TO RETRIEVE ROUTINE: " + schedule.getRoutineRotation().first().getName());

            return getRoutineModel(schedule.getRoutineRotation().first().getName());
        }

        return null;

    }

    public static long getDayCount(Date start, Date today) {
        long diff = -1;
        try {

            //time is always 00:00:00 so rounding should help to ignore the missing hour when going from winter to summer time as well as the extra hour in the other direction
            diff = Math.round((today.getTime() - start.getTime()) / (double) 86400000);
        } catch (Exception e) {
            Log.e(TAG, "Error Calculating number of days between start and today");

        }
        return diff;
    }

    public Boolean[] getWorkoutDays(WorkoutScheduleModel scheduleModel){
        Boolean[] dow = new Boolean[7];
        dow[0] = scheduleModel.getSunday();
        dow[1] = scheduleModel.getMonday();
        dow[2] = scheduleModel.getTuesday();
        dow[3] = scheduleModel.getWednesday();
        dow[4] = scheduleModel.getThursday();
        dow[5] = scheduleModel.getFriday();
        dow[6] = scheduleModel.getSaturday();
        return  dow;
    }


    /**
     *
     * @param newRoutine
     * @return true on success, false on failure (duplicate)
     */

    public boolean addRoutineModel(RoutineModel newRoutine){
        if(!isDuplicateRoutineType(newRoutine)){
            try {
                workoutRealm.getRealm().beginTransaction();
                workoutRealm.getRealm().copyToRealm(newRoutine);
                workoutRealm.getRealm().commitTransaction();
                //workoutRealm.add(newRoutine);
            } catch (Exception e){
                if (e.getMessage().contains("Trying to set non-nullable field date to null.")){
                    Log.w(TAG, "Continuing to add routine anyway");
                    workoutRealm.getRealm().commitTransaction();
                    return true;
                }
                else
                    workoutRealm.getRealm().cancelTransaction();
                Log.e(TAG, "Error adding RoutineModel to Realm");
                Log.e(TAG, e.getMessage());
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    public boolean isEmptyRoutine(String name){
        try {
            RealmQuery<RoutineModel> query = this.workoutRealm.query(RoutineModel.class);
            query.equalTo("name", name);

            if(query.findAll().first().getExercises().size() == 0){
                Log.i(TAG, "Routine has not yet been built");
                return true;       // This routine has not yet been built
            } else {
                return false;        // This exercise has been previously added
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public WorkoutScheduleModel createNewSchedule(Boolean[] workoutDays, Date startDate, RealmList<RealmString> routineRotation){
        WorkoutScheduleModel newSchedule = new WorkoutScheduleModel();
        newSchedule.setSunday(workoutDays[0]);
        newSchedule.setMonday(workoutDays[1]);
        newSchedule.setTuesday(workoutDays[2]);
        newSchedule.setWednesday(workoutDays[3]);
        newSchedule.setThursday(workoutDays[4]);
        newSchedule.setFriday(workoutDays[5]);
        newSchedule.setSaturday(workoutDays[6]);
        newSchedule.setRoutineRotation(routineRotation);
        newSchedule.setStartDate(startDate);
        Date d = Calendar.getInstance().getTime();
        newSchedule.setDate(d);

        return newSchedule;
    }


    public RealmContext<ExerciseTypeModel> getWorkoutRealm(){
        return this.workoutRealm;
    }

}
