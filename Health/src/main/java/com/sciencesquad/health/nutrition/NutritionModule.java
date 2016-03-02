package com.sciencesquad.health.nutrition;

import android.util.Pair;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

/**
 * Nutrition Module
 */
public class NutritionModule extends Module {
    private static final String TAG = NutritionModule.class.getSimpleName();
    static { Module.registerModule(NutritionModule.class); }

    private DataContext<NutritionModel> dataContext;

    public void init() {
        this.dataContext = new RealmContext<>();
        this.dataContext.init(BaseApp.app(), NutritionModel.class, "nutrition.realm");
    }

    @Override
    public Pair<String, Integer> identifier() {
        return new Pair<>("Nutrition", R.drawable.ic_music_circle);
    }
}
