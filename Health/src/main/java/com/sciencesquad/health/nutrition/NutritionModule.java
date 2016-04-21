package com.sciencesquad.health.nutrition;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.EventBus.Entry;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Nutrition Module. The big controller for Nutrition
 */
public class NutritionModule extends Module {
    public static final String TAG = NutritionModule.class.getSimpleName();

    public static final String REALMNAME = "nutrition.realm";

    //Important Data.
    private float calorieIntake;
    private boolean hadCaffeine;
    private int numCheatDays;
    private boolean cheated;
    private float waterIntake;
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
        this.favoriteFoods = new ArrayList<>();
        this.hadCaffeine = false;
        this.calorieIntake = 0;
        this.waterIntake = 0;
        this.numCheatDays = 5;
        this.cheated = false; // being positive and assuming no cheating :)

        bus().subscribe("DataEmptyEvent", null, e -> Log.d(TAG, "Some realm was empty."));
        bus().subscribe("DataFailureEvent", this, e -> {
            Log.d(TAG, "Nutrition realm failed in Realm Transaction!");

        });
        bus().subscribe("DataFailureEvent", null, e -> {
            Log.d(TAG, "Data failed somewhere.");

            });
            b.subscribe("DataUpdateEvent", null, e -> {
                Log.d(TAG, "There was an update.");
                    if (e.size() > 0) {
                        Map.Entry event = e.entrySet().iterator().next();
                        // maybe use the key as the realm name?
                        if (event.getKey().equals(REALMNAME)) {
                            Log.d(TAG, "Ignoring " + TAG + "'s own data update");
                        }
                        else if(event.getKey().equals("Favorite Foods")) {
                            Log.d(TAG, "Event value:" + event.getValue());
                            String foodName = (String) event.getValue();
                            handleDeleteEvent(foodName, FavoriteFoodModel.class);
                        }
                        else if (event.getKey().equals("Food History")){
                            String foodName = (String) event.getValue();
                            handleDeleteEvent(foodName, FoodModel.class);
                        }
                        else {
                            // do something about it.
                            Log.d(TAG, "OH NO!");
                        }
                    }
            });
        });
    }

    private void handleDeleteEvent(String name, Class modelClazz){
        RealmResults<FoodModel> results =
                nutritionRealm.query(modelClazz).equalTo("name", name).findAll();
        if (results.size() > 0){
            nutritionRealm.getRealm().beginTransaction();
            try {
                RealmObject model = results.first();
                model.removeFromRealm();
                nutritionRealm.getRealm().commitTransaction();
            } catch (Exception execption){
                execption.printStackTrace();
                nutritionRealm.getRealm().cancelTransaction();
            }
        }
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

        //Trying to send events.
        BaseApp.app().eventBus().publish("DataUpdateEvent", this,
                new Entry(REALMNAME, newNutritionModel));

        // reset values
        clearModels();
        this.hadCaffeine = false;
        this.calorieIntake = 0;
        this.waterIntake = 0;
        createModels();
    }

    public RealmResults<NutritionModel> queryNutrition(){
        return nutritionRealm.query(NutritionModel.class).findAll();
    }

    public ArrayList<String> createNutritionLog(){
        ArrayList<String> log = new ArrayList<>();
        RealmResults<NutritionModel> results = nutritionRealm.query(NutritionModel.class).findAll();
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

    public void setCalorieIntake(float calorieIntake) {
        this.calorieIntake = calorieIntake;
    }

    public NutrientModel getNutrients(){
        return this.nutrients;
    }

    public void setNutrients(NutrientModel model){
        this.nutrients = model;
    }

    public void setWaterIntake(float waterIntake) {
        this.waterIntake = waterIntake;
    }
    public float getWaterIntake() {
        return waterIntake;
    }


    public ArrayList<String> getFavoriteFoods() {
        RealmResults<FavoriteFoodModel> results = nutritionRealm.query(FavoriteFoodModel.class).findAll();
        if (results.size() != 0){
            favoriteFoods.clear();
            for (int i = 0 ; i < results.size(); i++){
                FavoriteFoodModel model = results.get(i);
                favoriteFoods.add(model.getName());
            }
        }
        return favoriteFoods;
    }

    public void setFavoriteFoods(ArrayList<String> favoriteFoods) {
        for (int i = 0 ; i < favoriteFoods.size(); i++){
            FavoriteFoodModel model = new FavoriteFoodModel(favoriteFoods.get(i));
            nutritionRealm.update(model);
        }
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

    public boolean checkCheatDays() {
        RealmResults<NutritionModel> results = nutritionRealm.query(NutritionModel.class).findAllSorted("dateString");
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

    public TreeSet<FoodModel> populateFoodTree() {
        TreeSet<FoodModel> tree = new TreeSet<>();
        try {
            RealmResults<FoodModel> results = nutritionRealm.query(FoodModel.class).findAll();
            for (int i = 0; i < results.size(); i++){
                FoodModel model = results.get(i);
                tree.add(model);
            }
            Log.v(TAG, "Food tree size: " + tree.size());
            return tree;
        } catch (Exception e){
            // better off with just a blank tree.
            return new TreeSet<>();

        }

    }

    public void addFood(FoodModel newFood){
        calorieIntake += newFood.getCalories();
        nutritionRealm.add(newFood);
    }

    public ArrayList<String> createFoodLog() {
        ArrayList<String> foodLog = new ArrayList<>();
        RealmResults<FoodModel> food= nutritionRealm.query(FoodModel.class).findAll();
        for (int i = 0 ; i < food.size(); i++){
            FoodModel item = food.get(i);
            foodLog.add(item.getName());
        }

        return foodLog;
    }

    /**
     * Overriding Module methods.
     */
    @Override
    public Pair<String, Integer> identifier() {
        return new Pair<>("Nutrition", R.drawable.ic_menu_nutrition);
    }

    @Override
    public void init() {
        Module.registerModule(this.getClass());

    }
}
