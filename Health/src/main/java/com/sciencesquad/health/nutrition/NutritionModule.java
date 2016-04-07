package com.sciencesquad.health.nutrition;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

import io.realm.RealmResults;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.util.ArrayList;

/**
 * Nutrition Module
 */
public class NutritionModule extends Module {
    public static final String TAG = NutritionModule.class.getSimpleName();

    private static final String REALMNAME = "nutrition.realm";

    //Important Data.
    private float calorieIntake;
    private boolean hadCaffeine;
    private int numCheatDays;
    private boolean cheated;
    private NutrientModel nutrients;
    private MineralModel minerals;
    private VitaminModel vitamins;
    private ArrayList<String> favoriteFoods;


    //Data context.
    private RealmContext<NutritionModel> nutritionRealm;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
    */
    public NutritionModule()  {

        this.nutritionRealm = new RealmContext<>();
        this.nutritionRealm.init(BaseApp.app(), NutritionModel.class, REALMNAME);
        this.nutritionRealm.clear();

        // default values
        this.favoriteFoods = new ArrayList<String>();
        this.hadCaffeine = false;
        this.calorieIntake = 0;
        this.numCheatDays = 5;
        this.cheated = false; // being positive and assuming no cheating :)

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
                if (e.get("key").equals(REALMNAME)) {
                    Log.d(TAG, "Ignoring " + this.getClass().getSimpleName() + "'s own data update");
                } else {
                    // do something about it.
                }
            });
        });
    }

    /**
     * Create models so that you can write stuff to the realm
     * dealing with all the nutrition information
     */

    public void createModels(){
        this.vitamins = new VitaminModel();
        this.nutrients = new NutrientModel();
        this.minerals = new MineralModel();
    }

    /**
     * Sets all the models to null to allow Java GC to delete no-longer needed models
     * TODO: Check to see if this the right way to do something like this, for I don't want to sin.
     */

    public void clearModels(){
        vitamins = null;
        nutrients = null;
        minerals = null;
    }

    /**
     * Method to write current Nutrition data to its realm.
     * Resets the data models to be used afresh.
     * Will be changed to fit the Dispatcher Pattern later.
     */
    public void addNutritionRecord(){

        NutritionModel newNutritionModel = new NutritionModel();
        newNutritionModel.setHadCaffeine(hadCaffeine);
        newNutritionModel.setCalorieIntake(calorieIntake);
        newNutritionModel.setNutrientModel(nutrients);
        newNutritionModel.setCheated(cheated);
        newNutritionModel.setNumCheatDays(numCheatDays);
        newNutritionModel.setVitaminModel(vitamins);
        newNutritionModel.setMineralModel(minerals);
        newNutritionModel.setDate(DateTimeUtils.toDate(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        newNutritionModel.setDateString("Date: " + LocalDateTime.now().getYear() + "-"
                + LocalDateTime.now().getMonth().getValue() + "-"
                + LocalDateTime.now().getDayOfMonth());
        nutritionRealm.add(newNutritionModel);

        // reset values
        clearModels();
        this.hadCaffeine = false;
        this.calorieIntake = 0;
        createModels();
    }

    public RealmResults<NutritionModel> queryNutrition(){
        return nutritionRealm.query().findAll();
    }

    public ArrayList<String> createNutritionLog(){
        ArrayList<String> log = new ArrayList<>();
        RealmResults<NutritionModel> results = nutritionRealm.query().findAll();
        for (int i = 0; i < results.size(); i++){
            NutritionModel model = results.get(i);
            String logEntry = "Calories: " + model.getCalorieIntake() + ", " +
                    LocalDateTime.now().getDayOfWeek().toString() + " "
                    + LocalDateTime.now().getMonth().toString() + " "
                    + LocalDateTime.now().getDayOfMonth() + " "
                    + LocalDateTime.now().getYear();
            log.add(logEntry);
        }
        return log;
    }

    public void setHadCaffeine(boolean hadCaffeine) {
        this.hadCaffeine = hadCaffeine;
    }

    /**
     * Getter for user's response to having caffeine today.
     */
    public boolean isCaffeinated() {
        return hadCaffeine;
    }

    public float getCalorieIntake() {
        return calorieIntake;
    }

    public void setCalorieIntake(int calorieIntake) {
        this.calorieIntake = calorieIntake;
    }


    public ArrayList<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(ArrayList<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public int getNumCheatDays() {
        return numCheatDays;
    }

    public void setNumCheatDays(int numCheatDays) {
        this.numCheatDays = numCheatDays;
    }

    public void generateData() {
        for (int i = 0; i < 10; i++){
            calorieIntake = i * 100 + 50;
            hadCaffeine = !hadCaffeine;
            addNutritionRecord();
        }
    }

    @Override
    public Pair<String, Integer> identifier() {
        return new Pair<>("Nutrition", R.drawable.ic_menu_nutrition);
    }

    @Override
    public void init() {
        Module.registerModule(this.getClass());

    }

    public boolean checkCheatDays() {
        RealmResults<NutritionModel> results = nutritionRealm.query().findAllSorted("dateString");
        NutritionModel mostRecentModel = results.last();

        String testString = "Date: " + LocalDateTime.now().getYear() + "-"
                + LocalDateTime.now().getMonth().getValue() + "-"
                + LocalDateTime.now().getDayOfMonth();

        if (testString.equals(mostRecentModel.getDateString()) && mostRecentModel.isCheated()){
            return false;

        }
        return numCheatDays > 0 && !cheated;
    }

    public boolean isCheated() {
        return cheated;
    }

    public void setCheated(boolean cheated) {
        this.cheated = cheated;
    }
}
