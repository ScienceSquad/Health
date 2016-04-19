package com.sciencesquad.health.overview;

import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.DataContext;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

public class OverviewModule extends Module {
    public static final String TAG = OverviewModule.class.getSimpleName();
    static { Module.register(OverviewModule.class); }

    private DataContext<OverviewModel> dataContext;

    public void init() {
        this.dataContext = new RealmContext<>();
        this.dataContext.init(BaseApp.app(), OverviewModel.class, "overview.realm");
    }
}
