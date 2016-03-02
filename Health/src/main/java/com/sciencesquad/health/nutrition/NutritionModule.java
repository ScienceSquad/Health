package com.sciencesquad.health.nutrition;

import android.util.Log;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import io.realm.RealmQuery;

import java.util.Calendar;

/**
 * Nutrition Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */
public class NutritionModule extends Module {
    private static final String TAG = NutritionModule.class.getSimpleName();

    private RealmContext<NutritionModel> nutritionRealm;

    /**
     * Constructs the module itself.
     * It also sets up a Realm Context for the Module.
    */

    public NutritionModule() throws Exception{
        Log.d(TAG, "Constructing Nutrition Module");
        this.nutritionRealm = new RealmContext<>();
        this.nutritionRealm.init(BaseApplication.application(), NutritionModel.class, "nutrition.realm");
    }

}
