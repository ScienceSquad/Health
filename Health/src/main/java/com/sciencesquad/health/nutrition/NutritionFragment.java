package com.sciencesquad.health.nutrition;

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
import com.sciencesquad.health.R;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;


/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionFragment extends Fragment {
    public static final String TAG = NutritionFragment.class.getSimpleName();

    private NutritionModule nutritionModule;
    private LineChart nutritionChart;
    private RecyclerView recycleList;
    private ArrayList<String> nutritionLog;

    private FloatingActionButton fab; // overall
    private FloatingActionButton fab2; // diet
    private FloatingActionButton fab3; // nutrient
    private FloatingActionButton fab4; // submit

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

        // create FABs.
        fab = (FloatingActionButton) view.findViewById(R.id.fab_nutrition);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab_diet);
        fab2.hide();
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab_nutrient);
        fab3.hide();
        fab4 = (FloatingActionButton) view.findViewById(R.id.fab_submit);
        fab4.hide();

        // set FABs listeners.
        fab.setOnClickListener(v -> {
            fab.hide();
            fab2.show();
            fab3.show();
            fab4.show();
        });

        fab2.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            createDietDialog();
        });

        fab3.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            createCalorieDialog();
        });

        fab4.setOnClickListener(v -> {
            fab.show();
            fab2.hide();
            fab3.hide();
            fab4.hide();
            saveNutritionProgress(v);
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
        String logEntry = "Calories: " + nutritionModule.getCalorieIntake() + ", Date: " +
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
