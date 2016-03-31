package com.sciencesquad.health.nutrition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sciencesquad.health.R;

public class CalorieDialogFragment extends DialogFragment {

    private boolean hadCaffeine;

    private static final String TAG = CalorieDialogFragment.class.getSimpleName();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        hadCaffeine = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.calorie_dialog_fragment_layout, null);
        builder.setView(dialogLayout);
        builder.setTitle("Nutrient Menu");

        EditText calorieField = (EditText) dialogLayout.findViewById(R.id.num_calories);
        calorieField.setInputType(InputType.TYPE_CLASS_NUMBER);

        Button caffeineButton = (Button) dialogLayout.findViewById(R.id.caffeine_button);
        caffeineButton.setOnClickListener(v -> {
            hadCaffeine = true;
        });

        builder.setPositiveButton("Save",
                (dialog, whichButton) -> {
                    ((NutritionViewModel) getTargetFragment()).getNutritionModule().setCalorieIntake(
                            Integer.parseInt(calorieField.getText().toString()));
                    ((NutritionViewModel) getTargetFragment()).getNutritionModule().
                            setHadCaffeine(hadCaffeine);

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
}