package com.sciencesquad.health.nutrition;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sciencesquad.health.R;

/**
 * ViewModel for the Nutrition Module.
 */
public class NutritionViewModel extends AppCompatActivity {


    public NutritionModule nutritionModule;
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

        Button submitCaloriesButton = (Button) findViewById(R.id.submit_calories);
        Button saveProgress = (Button) findViewById(R.id.nutrition_save_progress);

    }

    public void submitCalories(View view){
        createCalorieDialog();
    }

    public void saveNutritionProgress(View view){
        //nutritionModule.addNutritionRecord();
        Toast toast = Toast.makeText(getApplication(), "Nutrition progressed saved", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void createCalorieDialog() {
        DialogFragment newFragment = CalorieDialogFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), "Hello");
    }

    public static class CalorieDialogFragment extends DialogFragment {

        public static CalorieDialogFragment newInstance() {
            CalorieDialogFragment frag = new CalorieDialogFragment();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
       }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.calorie_dialog_fragment_layout, null);
            builder.setView(dialogLayout);

            builder.setTitle("Calorie Menu");
            EditText calorieInputField = (EditText) dialogLayout.findViewById(R.id.num_calories);
            calorieInputField.setInputType(InputType.TYPE_CLASS_NUMBER);

            builder.setPositiveButton("Save",
                    (dialog, whichButton) -> {
                        ((NutritionViewModel) getActivity()).nutritionModule.setCalorieIntake(
                                Integer.parseInt(calorieInputField.getText().toString()));
                        ((NutritionViewModel) getActivity()).okayButtonAction();
                    }
            );

            builder.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                        ((NutritionViewModel) getActivity()).cancelButtonAction();

                    });

            Dialog d = builder.create();
            return d;
        }

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
