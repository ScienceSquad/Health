package com.sciencesquad.health.nutrition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.util.StaticPagerAdapter;

import java.util.Iterator;
import java.util.TreeSet;

public class CalorieDialogFragment extends DialogFragment {
    private static final String TAG = CalorieDialogFragment.class.getSimpleName();

    private float newCalorieIntake;
    private boolean hadCaffeine;
    private boolean usedCheatDay;
    private float waterIntake;

    // Nutrients
    private float totalFat;
    private float totalCarbs;
    private float dietaryFiber;
    private float sugars;
    private float protein;
    private float cholesterol;
    private float sodium;

    private NutrientModel modelDefault;

    private TreeSet<FoodModel> foodTreeSet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // default nutrients
        newCalorieIntake = ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                getNutritionModule().getCalorieIntake();
        hadCaffeine = false;
        usedCheatDay = false;
        waterIntake = ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                getNutritionModule().getWaterIntake();

        modelDefault = ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                getNutritionModule().getNutrients();

        // default nutrients
        totalFat = modelDefault.getTotalFat();
        totalCarbs = modelDefault.getTotalCarbs();
        dietaryFiber = modelDefault.getDietaryFiber();
        sugars = modelDefault.getSugars();
        protein = modelDefault.getProtein();
        cholesterol = modelDefault.getCholesterol();
        sodium = modelDefault.getSodium();

        foodTreeSet = (((NutritionFragment) getTargetFragment()).
                getNutritionModule().populateFoodTree());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.fragment_nutrition_calorie_dialog, null);
        builder.setView(dialogLayout);

        TabLayout tabLayout = (TabLayout) dialogLayout.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) dialogLayout.findViewById(R.id.calorie_pager);
        StaticPagerAdapter.install(pager);
        tabLayout.setupWithViewPager(pager);

        // Basic Menu
        EditText foodField = (EditText) dialogLayout.findViewById(R.id.food_eaten);
        foodField.setInputType(InputType.TYPE_CLASS_TEXT);


        // Advanced Menu
        EditText calorieField = (EditText) dialogLayout.findViewById(R.id.num_calories);
        calorieField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText waterField = (EditText) dialogLayout.findViewById(R.id.water_intake);
        waterField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText fatField = (EditText) dialogLayout.findViewById(R.id.num_fat);
        fatField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText carbField = (EditText) dialogLayout.findViewById(R.id.num_carbs);
        carbField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText fiberField = (EditText) dialogLayout.findViewById(R.id.fiber_intake);
        fiberField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText proteinField = (EditText) dialogLayout.findViewById(R.id.protien_intake);
        proteinField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText sugarField = (EditText) dialogLayout.findViewById(R.id.sugar_intake);
        sugarField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText sodiumField = (EditText) dialogLayout.findViewById(R.id.sodium_intake);
        sodiumField.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText cholesterolField = (EditText) dialogLayout.findViewById(R.id.cholesterol_intake);
        cholesterolField.setInputType(InputType.TYPE_CLASS_NUMBER);

        RadioButton caffeineButton = (RadioButton) dialogLayout.findViewById(R.id.caffeine_button);
        caffeineButton.setOnClickListener(v -> {
            Log.d(TAG, "caffiene button set: "+ caffeineButton.isChecked());
            if (!hadCaffeine) {
                hadCaffeine = true;
            }
            else {
                caffeineButton.setChecked(false);
                hadCaffeine = false;
            }

        });

        TextView numCheatDaysView = (TextView) dialogLayout.findViewById(R.id.cheat_view);
        numCheatDaysView.setText("Cheat days left: " +
                (((NutritionFragment) getTargetFragment()).getNutritionModule().getNumCheatDays()));

        Button cheatButton = (Button) dialogLayout.findViewById(R.id.cheat_button);
        cheatButton.setOnClickListener(v -> {

            if (((NutritionFragment) getTargetFragment()).getNutritionModule().checkCheatDays()
                    && !usedCheatDay) {
                usedCheatDay = true;
                Snackbar snackbar = Snackbar.make(v, "Success: You've used a cheat day.",
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
                numCheatDaysView.setText("Cheat days left: " + (((NutritionFragment)
                        getTargetFragment()).getNutritionModule().getNumCheatDays() - 1));
            } else {
                Snackbar snackbar = Snackbar.make(v, "You can't use more cheat days today.",
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        // setting up positive/negative buttons
        builder.setPositiveButton("Save",
                (dialog, whichButton) -> {

                    int selectedTab = tabLayout.getSelectedTabPosition();
                    if (selectedTab == 0) {
                        String foodName = foodField.getText().toString();
                        Iterator<FoodModel> treeIterator = foodTreeSet.iterator();
                        while (treeIterator.hasNext()) {
                            FoodModel item = treeIterator.next();
                            if (foodName.equals(item.getName())) {
                                Log.d(TAG, "Item found!");
                                newCalorieIntake += item.getCalories();
                                ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                                        getNutritionModule().setCalorieIntake(newCalorieIntake);

                                Snackbar snackbar = Snackbar.make(dialogLayout, "Success: Food added!",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                return;
                            }
                        }
                        Log.e(TAG, "Item Not found");
                        // was not previously recorded. Using more dialogs...
                        DialogFragment newFragment = AddFoodFragment.newInstance(foodName);
                        newFragment.setTargetFragment(this, 0);
                        newFragment.show(getFragmentManager(), "dialog");

                    } else if (selectedTab == 1) {

                        if (calorieField.getText().toString().equals("")) {
                            return;
                        }

                        newCalorieIntake += Float.valueOf(calorieField.getText().toString() + newCalorieIntake);
                        waterIntake = Float.valueOf(waterField.getText().toString());
                        totalCarbs = Float.valueOf(carbField.getText().toString());
                        totalFat = Float.valueOf(fatField.getText().toString());
                        dietaryFiber = Float.valueOf(fiberField.getText().toString());
                        protein = Float.valueOf(proteinField.getText().toString());
                        sugars = Float.valueOf(sugarField.getText().toString());
                        sodium = Float.valueOf(sodiumField.getText().toString());
                        cholesterol = Float.valueOf(cholesterolField.getText().toString());


                        setNutrients(); // method setting the nutrient model

                        ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                                getNutritionModule().setCalorieIntake(newCalorieIntake);
                        ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                                getNutritionModule().setWaterIntake(waterIntake);

                        ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                                setHadCaffeine(hadCaffeine);

                        ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                                setCheated(usedCheatDay);

                        int numCheats = ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                                getNumCheatDays();
                        ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                                setNumCheatDays(numCheats--);
                    }
                }
        );
        builder.setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                    // do nothing.
                }
        );

        Dialog d = builder.create();
        return d;
    }

    private void setNutrients() {

        modelDefault.setTotalCarbs(totalCarbs);
        modelDefault.setSugars(sugars);
        modelDefault.setSodium(sodium);
        modelDefault.setTotalFat(totalFat);
        modelDefault.setProtein(protein);
        modelDefault.setDietaryFiber(dietaryFiber);
        modelDefault.setCholesterol(cholesterol);

        ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).
                getNutritionModule().setNutrients(modelDefault);
    }


    /**
     * Sets positive/ negative buttons colors.
     */
    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
    }

    public static class AddFoodFragment extends DialogFragment {

        private String foodName;

        public static AddFoodFragment newInstance(String name) {
            Log.d(TAG, "AddFoodFrag name: " + name);
            AddFoodFragment frag = new AddFoodFragment();
            Bundle args = new Bundle();
            args.putString("foodName", name);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String foodname = getArguments().getString("foodName");
            Log.d(TAG, "foodname: " + foodname);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.add_food_fragment, null);
            builder.setView(dialogLayout);
            builder.setTitle("Specified Food is Missing");

            EditText newCalories = (EditText) dialogLayout.findViewById(R.id.add_food_calorie);

            builder.setPositiveButton("Save",
                    (dialog, whichButton) -> {
                        Log.d(TAG, "Adding food");
                        float calories = Float.valueOf(newCalories.getText().toString());
                        if (calories == 0) {
                            return;
                        }
                        FoodModel newFood = new FoodModel(foodname, calories);
                        ((CalorieDialogFragment) getTargetFragment()).addFood(newFood);
                        Snackbar snackbar = Snackbar.make(dialogLayout, "Success: " + newFood.getName() +
                                " added!", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                    }
            );
            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {

                    }
            );

            Dialog d = builder.create();
            return d;
        }

        /**
         * Sets positive/ negative buttons colors.
         */
        @Override
        public void onStart() {
            super.onStart();
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
        }
    }

    private void addFood(FoodModel newFood) {
        ((NutritionFragment) getTargetFragment()).getNutritionModule().addFood(newFood);
    }
}