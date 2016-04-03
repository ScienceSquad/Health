package com.sciencesquad.health.nutrition;

import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sciencesquad.health.R;

import java.util.ArrayList;


/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionFragment extends Fragment {
    public static final String TAG = NutritionFragment.class.getSimpleName();

    private NutritionModule nutritionModule;
    private LineChart nutritionChart;


    /**
     * Creates the Nutrition View.
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrition, container, false);
    }

    /**
     * Further creating the UI and setting up buttons and their listeners.
     * @param savedInstanceState
     */
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
        saveProgress.setOnClickListener(v1 -> saveNutritionProgress(getView()));

        Button editDiet = (Button) view.findViewById(R.id.diet_button);
        editDiet.setOnClickListener(v1 -> createDietDialog());

        ArrayList<String> nutritionLog = nutritionModule.createNutritionLog();
        RecyclerView recycleList = (RecyclerView) view.findViewById(R.id.nutrition_recycler_view);
        NutritionRecycleAdapter adapter = new NutritionRecycleAdapter(nutritionLog);
        recycleList.setAdapter(adapter);
        recycleList.setLayoutManager(new LinearLayoutManager(view.getContext()));


        // setting up the chart.
        nutritionChart = (LineChart) view.findViewById(R.id.nutrition_chart);
        nutritionChart.setDescription("Calorie History");

        //Gathering and manipulating data.
        RealmLineDataSet<NutritionModel> nutritionDataSet = new RealmLineDataSet<NutritionModel>(
                nutritionModule.queryNutrition(), "calorieIntake");
        ArrayList<ILineDataSet> dataSetList = new ArrayList<ILineDataSet>();
        dataSetList.add(nutritionDataSet);
        RealmLineData data = new RealmLineData(nutritionModule.queryNutrition(), "dateString", dataSetList);

        // getting the data to display.
        nutritionChart.setData(data);
        nutritionChart.invalidate();


    }

    /**
     * Creates the Diet Dialog where all things Diet related will go.
     * TODO: Do things that set up and create a Diet Dialog Fragment.
     */

    public void createDietDialog() {

    }

    /**
     * Creates the Nutrient Menu which was originally the Calorie Menu.
     */

    public void submitCalories(){
        createCalorieDialog();
    }

    /**
     * Saves the progress in Nutrition
     * TODO: Make the graph update after database update.
     * @param view
     */

    public void saveNutritionProgress(View view){
        nutritionModule.addNutritionRecord();
        nutritionChart.invalidate();
        Snackbar snackbar = Snackbar.make(view, "Nutrition Info saved.", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void createCalorieDialog() {
        Log.v(TAG, "Creating Calorie Dialog");
        CalorieDialogFragment newFrag = new CalorieDialogFragment();
        newFrag.setTargetFragment(this, 0);
        newFrag.show(getFragmentManager(), "calorie dialog");
    }

    public NutritionModule getNutritionModule(){
        return nutritionModule;
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
