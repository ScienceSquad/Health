package com.sciencesquad.health.overview;

import android.util.Pair;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.nutrition.NutritionModule;
import com.sciencesquad.health.sleep.SleepDataModel;
import com.sciencesquad.health.steps.StepsModule;
import com.sciencesquad.health.sleep.SleepModule;
import com.sciencesquad.health.workout.WorkoutModule;
import com.sciencesquad.health.prescriptions.PrescriptionModule;

public class OverviewModule extends Module {
    public static final String TAG = OverviewModule.class.getSimpleName();
    static { Module.registerModule(OverviewModule.class); }

    private DataContext<OverviewModel> dataContext;

    public void init() {
        this.dataContext = new RealmContext<>();
        this.dataContext.init(BaseApp.app(), OverviewModel.class, "overview.realm");
    }

    @Override
    public Pair<String, Integer> identifier() {
        return new Pair<>("Overview", R.drawable.ic_menu_overview);
    }
}
