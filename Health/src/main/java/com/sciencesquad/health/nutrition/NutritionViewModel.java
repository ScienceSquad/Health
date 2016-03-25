package com.sciencesquad.health.nutrition;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sciencesquad.health.R;
import com.sciencesquad.health.events.BaseActivity;

/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionViewModel extends BaseActivity {


    NutritionModule nutritionModule;
    private static final String TAG = NutritionViewModel.class.getSimpleName();

    /**
     * Creates the Nutrition View.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nutritionModule = new NutritionModule();
        nutritionModule.createModels();
    }
}
