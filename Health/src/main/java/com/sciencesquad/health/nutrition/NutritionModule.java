package com.sciencesquad.health.nutrition;

import android.util.Log;

import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.DataEmptyEvent;
import com.sciencesquad.health.data.DataFailureEvent;
import com.sciencesquad.health.data.DataUpdateEvent;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;


/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class    NutritionModule extends Module {
    private static final String TAG = NutritionModule.class.getSimpleName();
    private static final String REALMNAME = "nutrition.realm";

    //Data to be stored.
    private int calorieIntake;
    private boolean hadCaffeine;
    private NutrientModel nutrients;
    private MineralModel minerals;
    private VitaminModel vitamins;


    //Data context.
    private RealmContext<NutritionModel> nutritionRealm;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
    */
    public NutritionModule() throws Exception {
        this.nutritionRealm = new RealmContext<>();
        this.nutritionRealm.init(BaseApplication.application(), NutritionModel.class, REALMNAME);

        createModels();

        this.subscribe(DataEmptyEvent.class, null, (DataEmptyEvent dataEmptyEvent) -> Log.d(TAG, "Some realm was empty."));
        this.subscribe(DataFailureEvent.class, this, (DataFailureEvent dataFailureEvent1) -> {
            Log.d(TAG, "Nutrition realm failed in Realm Transaction!");

        });
        this.subscribe(DataFailureEvent.class, null, (DataFailureEvent dataFailureEvent) -> {
            Log.d(TAG, "Data failed somewhere.");

        });
        this.subscribe(DataUpdateEvent.class, null, (DataUpdateEvent dataUpdateEvent) -> {
            Log.d(TAG, "There was an update to a realm.");

            // maybe use the key as the realm name?
            if (dataUpdateEvent.key().equals(REALMNAME)){
                Log.d(TAG, "Ignoring " + this.getClass().getSimpleName() + "'s own data update");
            }
            else {
                // do something about it.
            }
        });
    }

    /**
     * Create models so that you can write stuff to the realm
     * dealing with all the nutrition information
     */

    public void createModels(){
        vitamins = new VitaminModel();
        nutrients = new NutrientModel();
        minerals = new MineralModel();
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
     * Will be changed to fit the Dispatcher Patter later.
     */
    public void addNutritionRecord(){
        NutritionModel newNutritionModel = new NutritionModel();
        newNutritionModel.setHadCaffeine(hadCaffeine);
        newNutritionModel.setCalorieIntake(calorieIntake);
        newNutritionModel.setNutrientModel(nutrients);
        newNutritionModel.setVitaminModel(vitamins);
        newNutritionModel.setMineralModel(minerals);
        newNutritionModel.setDate(DateTimeUtils.toDate(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        nutritionRealm.add(newNutritionModel);
        clearModels();
        createModels();
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

    public int getCalorieIntake() {
        return calorieIntake;
    }

    public void setCalorieIntake(int calorieIntake) {
        this.calorieIntake = calorieIntake;
    }
}
