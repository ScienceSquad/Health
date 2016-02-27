package com.sciencesquad.health.steps;

import android.util.Log;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.data.DataEmptyEvent;
import com.sciencesquad.health.data.DataFailureEvent;
import com.sciencesquad.health.data.DataUpdateEvent;
import com.sciencesquad.health.data.RealmContext;
import com.sciencesquad.health.events.BaseApplication;
import com.sciencesquad.health.steps.StepsModel;

import io.realm.RealmQuery;


/**
 * Steps Module itself.
 *
 * Note:
 * It must be expanded upon from this current baby state
 */

public class StepsModule extends Module {
    private static final String TAG = StepsModule.class.getSimpleName();
    private static final String REALMNAME = "steps.realm";

    private RealmContext<StepsModel> stepsRealm;

    /**
     * Constructs the module itself.
     * Subscribes to events necessary to maintaining its own model.
     */
    public StepsModule() throws Exception {
        this.stepsRealm = new RealmContext<>();
        this.stepsRealm.init(BaseApplication.application(), StepsModel.class, "steps.realm");

        this.subscribe(DataEmptyEvent.class, null, (DataEmptyEvent dataEmptyEvent) -> {
            Log.d(TAG, "Some realm was empty.");
        });
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


        try {
            this.testStepsModule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void testStepsModule() throws Exception {

    }

}
