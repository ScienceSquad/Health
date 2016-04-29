package com.sciencesquad.health.workout;

import android.util.Log;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Coefficient;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.util.Dispatcher;

import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mrjohnson on 3/1/16.
 */

public class WorkoutModule extends Module implements Coefficient {
    public static final String TAG = WorkoutModule.class.getSimpleName();
    private RealmContext<ExerciseTypeModel> workoutRealm;

	/**
	 * Stuff for overview module
	 */
	private double workoutTotal;
	private double workoutGoal;

	/**
	 * Workout coefficient
	 */
	private double workoutCoefficient;

    //Data context.
    //private RealmContext<RoutineModel> workoutRealm;

	public WorkoutModule() {
		/*this.workoutRealm = new RealmContext<>();
		this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");

		//this.workoutRealm.getRealm().beginTransaction();
		//this.workoutRealm.getRealm().deleteAll();
		//this.workoutRealm.getRealm().commitTransaction();
		//this.workoutRealm.getRealm().refresh();

		if(getExerciseTypeModel("Abductor Machine") == null){
			Log.i(TAG, "ADDING BASE EXERCISES");
            /*
            Dispatcher.BACKGROUND.run(() -> {
                addBaseExercises();
                addRecommendedWorkouts();
            });

			addBaseExercises();
			addRecommendedWorkouts();
		} else Log.i(TAG, "We good!");

		//addBaseExercises();
		//addRecommendedWorkouts();*/
	}

	/**
	 * Calculates workout coefficient for use in overview module
	 * @return calculated workout coefficient
	 */
	public double calculateCoefficient() {
		double coefficient = (workoutTotal / workoutGoal) * 100;
		return Math.round(coefficient * 10) / 10;
	}

	/**
	 * Retrieves workout coefficient
	 * @return workoutCoefficient
	 */
	@Override
	public double getCoefficient() {
		return this.workoutCoefficient;
	}

    /**
     * Sets workout coefficient
     * TODO: Implement!
	 * @param coefficient
     * @see Coefficient
     */
    @Override
    public void setCoefficient(double coefficient) {
		this.workoutCoefficient = coefficient;
    }

    /**
     * Constructs the module itself.
     * It also sets up a Realm Context for the Module.
     */
	@Override
	public void onStart() {
        Log.d(TAG, "Starting Workout Module on UI");
        Dispatcher.UI.run(() -> {
            workoutRealm = new RealmContext<>();
            workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");
            if(getExerciseTypeModel("Abductor Machine") == null){
                Log.i(TAG, "ADDING BASE EXERCISES");

                addBaseExercises();
                addRecommendedWorkouts();
            } else Log.i(TAG, "We good!");
        });

        // Overview stuff
		workoutTotal = 100;
		workoutGoal = 235;
		setCoefficient(calculateCoefficient());
		//setCoefficient(0);

        //this.workoutRealm = new RealmContext<>();
        //this.workoutRealm.init(BaseApp.app(), ExerciseTypeModel.class, "WorkoutRealm");

        //this.workoutRealm.getRealm().beginTransaction();
        //this.workoutRealm.getRealm().deleteAll();
        //this.workoutRealm.getRealm().commitTransaction();
        //this.workoutRealm.getRealm().refresh();

        /*if(getExerciseTypeModel("Abductor Machine") == null){
            Log.i(TAG, "ADDING BASE EXERCISES");
            /*
            Dispatcher.BACKGROUND.run(() -> {
                addBaseExercises();
                addRecommendedWorkouts();
            });

            addBaseExercises();
            addRecommendedWorkouts();
        } else Log.i(TAG, "We good!");*/

        //addBaseExercises();
        //addRecommendedWorkouts();

		bus().subscribe("DataEmptyEvent", null, e -> Log.d(TAG, "Some realm was empty."));
		bus().subscribe("DataFailureEvent", this, e -> {
			Log.d(TAG, "Nutrition realm failed in Realm Transaction!");

		});
		bus().subscribe("DataFailureEvent", null, e -> {
			Log.d(TAG, "Data failed somewhere.");
		});
		bus().subscribe("DataUpdateEvent", null, e -> {
			Log.d(TAG, "There was an update to a realm. " + e);

			// maybe use the key as the realm name?
			if ("WorkoutRealm".equals(e.get("key"))) {
				Log.d(TAG, "Ignoring " + this.getClass().getSimpleName() + "'s own data update");
			} else {
				// do something about it.
			}
		});
	}

	@Override
	public void onStop() {

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

    void addBaseExercises(){
        ArrayList<ExerciseTypeModel> baseExercises = new ArrayList<>();
        // Abs
        baseExercises.add(createNewExercise("Cable Crunch", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Crunch", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Crunch Machine", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Decline Crunch", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Hanging Knee Raise", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Hanging Leg Raise", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Plank", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Decline Crunch", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Dragon Flag", "Strength", "Abs"));
        baseExercises.add(createNewExercise("Side Plank", "Strength", "Abs"));
        //baseExercises.add(createNewExercise("Timed Plank", "Strength", "Abs"));
        //TODO: add functionality for timed "Strength" exercises

        // Back
        baseExercises.add(createNewExercise("Barbell Row", "Strength", "Back"));
        baseExercises.add(createNewExercise("Barbell Shrug", "Strength", "Back"));
        baseExercises.add(createNewExercise("Chin Up", "Strength", "Back"));
        baseExercises.add(createNewExercise("DeadLift", "Strength", "Back"));
        baseExercises.add(createNewExercise("Dumbbell Row", "Strength", "Back"));
        baseExercises.add(createNewExercise("Fixed Machine Lat Pull Down", "Strength", "Back"));
        baseExercises.add(createNewExercise("Good Morning", "Strength", "Back"));
        baseExercises.add(createNewExercise("Hammer Strength Row", "Strength", "Back"));
        baseExercises.add(createNewExercise("Lat Pull Down", "Strength", "Back"));
        baseExercises.add(createNewExercise("Machine Shrug", "Strength", "Back"));
        baseExercises.add(createNewExercise("Pull Up", "Strength", "Back"));
        baseExercises.add(createNewExercise("Seated Cable Row", "Strength", "Back"));
        baseExercises.add(createNewExercise("Straight-Arm Cable Pushdown", "Strength", "Back"));
        baseExercises.add(createNewExercise("T-Bar Row", "Strength", "Back"));

        // Biceps
        baseExercises.add(createNewExercise("Barbell Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("Cable Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("Dumbbell Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("Dumbbell Hammer Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("Dumbbell Preacher Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("EZ-Bar Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("EZ-Bar Preacher Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("Seated Incline Dumbbell Curl", "Strength", "Biceps"));
        baseExercises.add(createNewExercise("Seated Machine Curl", "Strength", "Biceps"));

        // Cardio
        baseExercises.add(createNewExercise("Cycling", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Elliptical Training", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Rowing Machine", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Running (Outdoor)", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Running (Treadmill)", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Stair Machine", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Stationary Bike", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Swimming", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Variable Elliptical Machine", "Cardio", "Cardio"));
        baseExercises.add(createNewExercise("Walking", "Cardio", "Cardio"));

        // Sports
        baseExercises.add(createNewExercise("Basketball", "Cardio", "Sports"));

        // Chest
        baseExercises.add(createNewExercise("Cable Crossover", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Decline Barbell Bench Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Decline Dumbbell Bench Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Decline Hammer Strength Chest Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Barbell Bench Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Flat Chest Cable Machine", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Dumbbell Bench Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Flat Dumbbell Fly", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Incline Barbell Bench Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Incline Dumbbell Bench Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Incline Barbell Fly", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Incline Hammer Strength Chest Press", "Strength", "Chest"));
        baseExercises.add(createNewExercise("Seated Machine Fly", "Strength", "Chest"));

        // Legs
        baseExercises.add(createNewExercise("Abductor Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Barbell Calf Raise", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Barbell Front Squat", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Barbell Glute Bridge", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Barbell Squat", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Leg Curl Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Leg Extension Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Leg Press", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Lying Leg Curl Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Romanian Deadlift", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Seated Calf Raise Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Seated Leg Curl Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Standing Calf Raise Machine", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Stiff-Legged Deadlift", "Strength", "Legs"));
        baseExercises.add(createNewExercise("Sumo Deadlift", "Strength", "Legs"));

        // Shoulders
        baseExercises.add(createNewExercise("Arnold Dumbbell Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Behind-the-Neck Barbell Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Cable Face Pull", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Dumbbell Shrug", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Front Dumbbell Raise", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Hammer Strength Shoulder Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Lateral Dumbbell Raise", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Lateral Machine Raise", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Log Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("One-Arm Standing Dumbbell Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Overhead Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Push Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Rear Delt Cable Fly", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Rear Delt Dumbbell Raise", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Rear Delt Machine Fly", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Seated Dumbbell Lateral Raise", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Seated Dumbbell Press", "Strength", "Shoulders"));
        baseExercises.add(createNewExercise("Smith Machine Overhead Press", "Strength", "Shoulders"));

        // Triceps
        baseExercises.add(createNewExercise("Cable Overhead Triceps Extension", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Close Grip Barbell Bench Press", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Dumbbell Overhead Triceps Extension", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("EZ-Bar Skullcrusher", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Lying Triceps Extension", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Machine Triceps Extension", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Parallel Bar Triceps Dip", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Ring Dip", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Rope Push Down", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("Smith Machine Close Grip Bench Press", "Strength", "Triceps"));
        baseExercises.add(createNewExercise("V-Bar Push Down", "Strength", "Triceps"));

        for (ExerciseTypeModel m : baseExercises)
            addExerciseTypeModel(m);

    }


    void addRecommendedWorkouts(){

        //StrongLifts 5x5
        ExerciseTypeModel squat = createNewExercise("Barbell Squat", "Strength", "Legs");
        ExerciseTypeModel benchPress = createNewExercise("Barbell Bench Press", "Strength", "Chest");
        ExerciseTypeModel barBellRow = createNewExercise("Barbell Row", "Strength", "Back");
        ExerciseTypeModel overHeadPress = createNewExercise("Overhead Press", "Strength", "Shoulders");
        ExerciseTypeModel deadLift = createNewExercise("Romanian Deadlift", "Strength", "Core");

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

    public CompletedExerciseModel getMostRecentCompletedExerciseModel(String exerciseName){
        CompletedExerciseModel mostRecent = null;
        try {
            RealmQuery<CompletedExerciseModel> query = this.workoutRealm.query(CompletedExerciseModel.class).equalTo("exerciseName", exerciseName);
            RealmResults<CompletedExerciseModel> results = query.findAll();
            if(results.size() == 0){
                Log.i(TAG, "Didn't find any completed exercises of type " + exerciseName);
                return null;
            }
            Date date = results.maxDate("date");
            mostRecent = query.equalTo("date", date).findFirst();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return mostRecent;
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

    public RealmResults<CompletedExerciseModel> getCompletedExercisesQuery(String exerciseName){
        RealmResults<CompletedExerciseModel> results;
        try {
            RealmQuery<CompletedExerciseModel> query = this.workoutRealm.query(CompletedExerciseModel.class).equalTo("exerciseName", exerciseName);
            results = query.findAll();
            return results;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
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
            RealmResults<ExerciseTargetModel> results = workoutRealm.query(ExerciseTargetModel.class).findAllSorted("target", Sort.ASCENDING);
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

    public ExerciseTypeModel getExerciseTypeModel(String exerciseName) {
        try {
            RealmResults<ExerciseTypeModel> result = workoutRealm.query(ExerciseTypeModel.class).equalTo("name", exerciseName).findAll();
            if(result.size() == 0){
                return null;
            } else {
                return result.first();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding ExerciseTypeModel by name " + e.getMessage());
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
    public String[] getAllExercisesByTarget(String target){
        ArrayList<String> exercises = new ArrayList<>();
        try {
            RealmResults<ExerciseTypeModel> results = workoutRealm.query(ExerciseTypeModel.class).equalTo("target", target).findAll();
            for(ExerciseTypeModel e : results){
                exercises.add(e.getName());
            }
        } catch (Exception e) {
        }

        return exercises.toArray(new String[exercises.size()]);
    }


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
        float orm = newCompletedExercise.get1RMax().floatValue();
        newCompletedExercise.setOneRepMax(orm);

        String Date = "Date: "  + LocalDateTime.now().getYear() + "-"
                + LocalDateTime.now().getMonth().getValue() + "-"
                + LocalDateTime.now().getDayOfMonth();

        newCompletedExercise.setDateString(Date);
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
            Log.e(TAG, "Found no workout schedule in Realm");
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
            String lastCompletedWorkout = schedule.getLastCompletedRoutine();
            if(lastCompletedWorkout == null || lastCompletedWorkout.equals("")){
                RoutineModel todaysRoutine;
                RealmList<RealmString> routineRotation = schedule.getRoutineRotation();
                int numRoutines = routineRotation.size();
                // TODO: Make this work lol
            }
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

    public String[] getAllCategories(){
        ArrayList<ExerciseTargetModel> targets = getAllTargets();
        ArrayList<String> categories = new ArrayList<>();
        for(ExerciseTargetModel t : targets){
            categories.add(t.getTarget());
        }
        return categories.toArray(new String[categories.size()]);
    }

    public String[][] groupExercisesByTargetAlpha(String[] categories){
        String[][] groupedExercises = new String[categories.length][];
        int i;
        for(i = 0; i < categories.length; i++){
            groupedExercises[i] = getAllExercisesByTarget(categories[i]);
        }

        return groupedExercises;
    }


    public Double calculateCaloriesBurned(CompletedExerciseModel exercise, double age, String sex, double height, double weight, double heartrate){
        double calories = 0;
        if(sex.equals("Male")){
            calories = ((age * 0.2017) - (weight * 0.08036) + (heartrate * 0.6309) - 55.0969);
        } else {
            // Female
            calories = ((age * 0.074) - (weight * 0.05741) + (heartrate * 0.4472) - 20.4022);
        }
        return calories;
    }

    public RealmContext<ExerciseTypeModel> getWorkoutRealm(){
        return this.workoutRealm;
    }

}
