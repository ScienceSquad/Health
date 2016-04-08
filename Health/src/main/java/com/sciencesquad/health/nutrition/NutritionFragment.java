package com.sciencesquad.health.nutrition;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.databinding.FragmentNutritionBinding;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;


/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionFragment extends BaseFragment {
    public static final String TAG = NutritionFragment.class.getSimpleName();

    private NutritionModule nutritionModule;
    private LineChart nutritionChart;
    private RecyclerView recycleList;
    private ArrayList<String> nutritionLog;

    private FloatingActionButton fab; // overall
    private FloatingActionButton fab2; // diet
    private FloatingActionButton fab3; // nutrient
    private FloatingActionButton fab4; // submit
    private FloatingActionButton fab5; // ZXing.

    @Override
    protected Configuration getConfiguration() {
        return new Configuration(TAG, "Nutrition", R.drawable.ic_menu_nutrition,
                R.style.AppTheme_Nutrition, R.layout.fragment_nutrition);
    }

    // Our generated binding class is different...
    @Override @SuppressWarnings("unchecked")
    protected FragmentNutritionBinding xml() {
        return super.xml();
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

        // create FABs.
        fab = xml().fabNutrition;
        fab2 = xml().fabDiet;
        fab2.hide();
        fab3 = xml().fabNutrient;
        fab3.hide();
        fab4 = xml().fabSubmit;
        fab4.hide();
        fab5 = xml().fabZxing;
        fab5.hide();

        // set FABs listeners.
        fab.setOnClickListener(v -> {
            fab.hide();
            fab2.show();
            fab3.show();
            fab4.show();
            fab5.show();
        });

        fab2.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            fab5.hide();
            createDietDialog();
        });

        fab3.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            fab5.hide();
            createCalorieDialog();
        });

        fab4.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            fab5.hide();
            saveNutritionProgress(v);
        });

        fab5.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            fab5.hide();
            useZxing();
        });

        nutritionLog = nutritionModule.createNutritionLog();
        recycleList = (RecyclerView) view.findViewById(R.id.nutrition_recycler_view);
        NutritionRecycleAdapter adapter = new NutritionRecycleAdapter(nutritionLog);
        recycleList.setAdapter(adapter);
        recycleList.setLayoutManager(new LinearLayoutManager(view.getContext()));


        // setting up the chart.
        nutritionChart = (LineChart) view.findViewById(R.id.nutrition_chart);
        nutritionChart.setDescription("Calorie History");

        // getting the data to display.
        nutritionChart.setData(createLineData());
        nutritionChart.invalidate();

    }

    private void useZxing() {
        IntentIntegrator.forFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result= IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){
                Snackbar snackbar = Snackbar.make(xml().getRoot(),
                        "Scanned: " + result.getContents(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        else {
            // shit broke.
        }

    }

    /**
     * Creates the Diet Dialog where all things Diet related will go.
     */

    public void createDietDialog() {
        Log.v(TAG, "Creating Diet Dialog");
        DietDialogFragment newFrag = new DietDialogFragment();
        newFrag.setTargetFragment(this, 0);
        newFrag.show(getFragmentManager(), "diet dialog");

    }

    /**
     * Creates the Nutrient Menu which was originally the Calorie Menu.
     */

    public void submitCalories(){
        createCalorieDialog();
    }

    /**
     * Saves the progress in Nutrition in the dirtiest way possible.
     * This entire function needs a major sponge bath.
     * @param view
     */

    public void saveNutritionProgress(View view){
        if (nutritionModule.getCalorieIntake() == 0){
            Snackbar snackbar = Snackbar.make(view, "No info submitted. Input calories", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        String logEntry = "Calories: " + nutritionModule.getCalorieIntake() + ", " +
                LocalDateTime.now().getDayOfWeek().toString() + " "
                + LocalDateTime.now().getMonth().toString() + " "
                + LocalDateTime.now().getDayOfMonth() + " "
                + LocalDateTime.now().getYear();
        nutritionLog.add(logEntry);
        nutritionModule.addNutritionRecord();
        recycleList.getAdapter().notifyDataSetChanged();

        nutritionChart.setData(createLineData());
        nutritionChart.invalidate();
        Snackbar snackbar = Snackbar.make(view, "Nutrition Info saved.", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Because Realm doesn't cooperate as well with MPAndroid chart as I like it to be.
     * I need to remake the data set it works with and redraw it.
     * Because my life doesn't like me enough.
     * @return
     */

    private RealmLineData createLineData(){
        RealmLineDataSet<NutritionModel> nutritionDataSet = new RealmLineDataSet<NutritionModel>(
                nutritionModule.queryNutrition(), "calorieIntake");
        ArrayList<ILineDataSet> dataSetList = new ArrayList<ILineDataSet>();
        dataSetList.add(nutritionDataSet);
        RealmLineData data = new RealmLineData(nutritionModule.queryNutrition(), "dateString", dataSetList);
        return data;
    }

    private void createCalorieDialog() {
        Log.v(TAG, "Creating Calorie Dialog");
        CalorieDialogFragment newFrag = new CalorieDialogFragment();
        newFrag.setTargetFragment(this, 0);
        newFrag.show(getFragmentManager(), "calorie dialog");
    }

    /**
     * Used for Dialogs.
     * @return
     */

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
