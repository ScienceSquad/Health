package com.sciencesquad.health.nutrition;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.realm.implementation.RealmLineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.common.api.CommonStatusCodes;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseFragment;
import com.sciencesquad.health.core.util.StaticPagerAdapter;
import com.sciencesquad.health.databinding.FragmentNutritionBinding;


import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionFragment extends BaseFragment {
    public static final String TAG = NutritionFragment.class.getSimpleName();
    private static final int RC_BARCODE_CAPTURE = 9001;

    private NutritionModule nutritionModule;
    private LineChart nutritionChart;
    private RecyclerView recycleList;
    private RecyclerView foodList;
    private ArrayList<String> nutritionLog;
    private ArrayList<String> foodLog;

    private boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
	private Animation rotate_forward;
	private Animation rotate_backward;

    @Override
    protected Configuration getConfiguration() {
        return new Configuration(TAG, "Nutrition", R.drawable.ic_menu_nutrition,
                R.style.AppTheme_Nutrition, R.layout.fragment_nutrition);
    }

    /**
     * XML formatting for the Nutrition Fragment
     * @return
     */
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

		// Get drawables and manipulate
		Drawable plus = ContextCompat.getDrawable(getActivity(), R.drawable.ic_plus);

		// Initialize animations for fabs
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.fab_close);
		rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.rotate_forward);
		rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
				R.anim.rotate_backward);

        TabLayout tabLayout = xml().tabs;
        StaticPagerAdapter.install(xml().pager);
        tabLayout.setupWithViewPager(xml().pager);

        xml().toolbar.setNavigationOnClickListener(this.drawerToggleListener());

        // create FABs.
        xml().fabNutrition.setBackgroundDrawable(plus);
        xml().fabDiet.hide();
        xml().fabNutrient.hide();
        xml().fabSubmit.hide();
        xml().fabZxing.hide();

        // set FABs listeners.
        xml().fabNutrition.setOnClickListener(v -> {
            animateFab();
        });

        xml().fabDiet.setOnClickListener(v -> {
			animateFab();
            createDietDialog();
        });

        xml().fabNutrient.setOnClickListener(v -> {
			animateFab();
            createCalorieDialog();
        });

        xml().fabSubmit.setOnClickListener(v -> {
			animateFab();
            saveNutritionProgress(v);
        });

        xml().fabZxing.setOnClickListener(v -> {
			animateFab();
            useZxing();
        });

        nutritionLog = nutritionModule.createNutritionLog();
        recycleList = xml().nutritionRecyclerView;
        NutritionRecycleAdapter adapter = new NutritionRecycleAdapter(nutritionLog, "Nutrition Log");
        recycleList.setAdapter(adapter);
        recycleList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        foodLog = nutritionModule.createFoodLog();
        foodList = xml().nutritionHistoryView;
        NutritionRecycleAdapter foodAdapter = new NutritionRecycleAdapter(foodLog, "Food History");
        foodList.setAdapter(foodAdapter);
        foodList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // setting up the chart.
        nutritionChart = xml().nutritionChart;
        nutritionChart.setDescription("Calorie History");

        // getting the data to display.
        nutritionChart.setData(createLineData());
        nutritionChart.invalidate();

    }

    private void useZxing() {

        //IntentIntegrator.forFragment(this).initiateScan();
        // launch barcode activity.
        Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    //barcodeValue.setText(barcode.displayValue);

                    int selectedTab = xml().tabs.getSelectedTabPosition();
                    if (selectedTab == 0) {
                        Snackbar.make(xml().page1, "Barcode read: " + barcode.displayValue,
                                Snackbar.LENGTH_LONG).show();
                    }
                    else if (selectedTab == 1){
                        Snackbar.make(xml().page2, "Barcode read: " + barcode.displayValue,
                                Snackbar.LENGTH_LONG).show();
                    }


                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Log.d(TAG, CommonStatusCodes.getStatusCodeString(resultCode));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
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

    public void updateFood(FoodModel model){
        foodLog.add(model.getName());
        foodList.getAdapter().notifyDataSetChanged();
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

	/**
	 * Animations for when the overviewFab is pressed
	 */
	public void animateFab() {
		if (!isFabOpen) {
			xml().fabNutrition.startAnimation(rotate_forward);
			xml().fabDiet.startAnimation(fab_open);
			xml().fabNutrient.startAnimation(fab_open);
			xml().fabSubmit.startAnimation(fab_open);
			xml().fabZxing.startAnimation(fab_open);
			xml().fabDiet.show();
			xml().fabNutrient.show();
			xml().fabSubmit.show();
			xml().fabZxing.show();
			xml().fabDiet.setClickable(true);
			xml().fabNutrient.setClickable(true);
			xml().fabSubmit.setClickable(true);
			xml().fabZxing.setClickable(true);
			isFabOpen = true;
		} else {
			xml().fabNutrition.startAnimation(rotate_backward);
			xml().fabDiet.startAnimation(fab_close);
			xml().fabNutrient.startAnimation(fab_close);
			xml().fabSubmit.startAnimation(fab_close);
			xml().fabZxing.startAnimation(fab_close);
			xml().fabDiet.hide();
			xml().fabNutrient.hide();
			xml().fabSubmit.hide();
			xml().fabZxing.hide();
			xml().fabDiet.setClickable(false);
			xml().fabNutrient.setClickable(false);
			xml().fabSubmit.setClickable(false);
			xml().fabZxing.setClickable(false);
			isFabOpen = false;
		}
	}

}
