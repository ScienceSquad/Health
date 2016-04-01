package com.sciencesquad.health.nutrition;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sciencesquad.health.R;

import java.util.ArrayList;

import io.realm.RealmResults;

/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionFragment extends Fragment {
    public static final String TAG = NutritionFragment.class.getSimpleName();

    private NutritionModule nutritionModule;



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


        ListView nutritionLogView = (ListView) view.findViewById(R.id.nutrition_log);
        ArrayAdapter<String> nutritionLogAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1);
        nutritionLogAdapter.clear();
        nutritionLogAdapter.addAll(nutritionModule.createNutritionLog());
        nutritionLogView.setAdapter(nutritionLogAdapter);

        // setting up the chart.
        LineChart nutritionChart = (LineChart) view.findViewById(R.id.nutrition_chart);
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
