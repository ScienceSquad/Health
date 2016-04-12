package com.sciencesquad.health.nutrition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.util.StaticPagerAdapter;

public class CalorieDialogFragment extends DialogFragment {
    private static final String TAG = CalorieDialogFragment.class.getSimpleName();

    private boolean hadCaffeine;
    private boolean usedCheatDay;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        hadCaffeine = false;
        usedCheatDay = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.fragment_nutrition_calorie_dialog, null);
        builder.setView(dialogLayout);

        TabLayout tabLayout = (TabLayout) dialogLayout.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) dialogLayout.findViewById(R.id.calorie_pager);
        StaticPagerAdapter.install(pager);
        tabLayout.setupWithViewPager(pager);

        EditText calorieField = (EditText) dialogLayout.findViewById(R.id.num_calories);
        calorieField.setInputType(InputType.TYPE_CLASS_NUMBER);

        RadioButton caffeineButton = (RadioButton) dialogLayout.findViewById(R.id.caffeine_button);
        caffeineButton.setOnClickListener(v -> hadCaffeine = true);

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

        builder.setPositiveButton("Save",
                (dialog, whichButton) -> {
                    if (calorieField.getText().toString().equals("")) {
                        return;
                    }
                    ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().setCalorieIntake(
                            Integer.parseInt(calorieField.getText().toString()));
                    ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                            setHadCaffeine(hadCaffeine);
                    ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                            setCheated(usedCheatDay);
                    int numCheats = ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                            getNumCheatDays();
                    ((NutritionFragment) CalorieDialogFragment.this.getTargetFragment()).getNutritionModule().
                            setNumCheatDays(numCheats--);
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

    @Override
    public void onStart(){
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
    }
}
