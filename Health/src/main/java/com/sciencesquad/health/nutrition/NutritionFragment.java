package com.sciencesquad.health.nutrition;

import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sciencesquad.health.R;

/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionFragment extends Fragment {

    private NutritionModule nutritionModule;
    private static final String TAG = NutritionFragment.class.getSimpleName();

    LineChartView calorieHistory;
    LineSet calorieSet;

    /**
     * Creates the Nutrition View.
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrition, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nutritionModule = new NutritionModule();
        nutritionModule.createModels();

        nutritionModule.generateData();

        Button submitCaloriesButton = (Button) view.findViewById(R.id.submit_calories);
        submitCaloriesButton.setOnClickListener(v1 -> {
            submitCalories();
        });

        Button saveProgress = (Button) view.findViewById(R.id.nutrition_save_progress);
        saveProgress.setOnClickListener(v1 -> {
            saveNutritionProgress(getView());
        });

        Button editDiet = (Button) view.findViewById(R.id.diet_button);
        editDiet.setOnClickListener(v1 -> createDietDialog());

        //calorieHistory = new LineChartView(getActivity());
        //calorieSet= createCalorieSet();

        //calorieHistory.addData(calorieSet);

        //calorieHistory.show();

    }

    private LineSet createCalorieSet() {
        float[] calorieSet = nutritionModule.queryCalories();
        String[] dateSet = nutritionModule.queryDates();
        return new LineSet(dateSet, calorieSet);
    }

    /**
     * TODO: Do things that set up and create a Diet Dialog Fragment.
     */

    public void createDietDialog() {

    }

    public void submitCalories(){
        createCalorieDialog();
    }

    public void saveNutritionProgress(View view){
        nutritionModule.addNutritionRecord();
        Snackbar snackbar = Snackbar.make(view, "Nutrition Info saved.", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void createCalorieDialog() {
        Log.v(TAG, "Creating Calorie Dialog");
        CalorieDialogFragment newFrag = new CalorieDialogFragment();
        newFrag.setTargetFragment(this, 0);
        newFrag.show(getFragmentManager(), "calorie dialog");
        //getActivity().setContentView(R.layout.fragment_nutrition_calorie_dialog);
    }

    public NutritionModule getNutritionModule(){
        return nutritionModule;
    }

    private void cancelButtonAction() {
        // Do other things that exit out of the action.
        Log.i(TAG, "Cancelling action");
    }

    private void okayButtonAction() {
        // do things that are considered an okay.
        Log.i(TAG, "Saving action");

    }

    /**
     * Before we destroy the Nutrition Activity, we should save first.
     *
     * Note:
     * Would it be better to prompt user for exit or just save for them?
     */
    @Override
    public void onDestroy(){
        nutritionModule.addNutritionRecord();
        super.onDestroy();
    }

}
