package com.sciencesquad.health.nutrition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sciencesquad.health.R;

import java.util.ArrayList;


public class DietDialogFragment extends DialogFragment {
    public static final String TAG = DietDialogFragment.class.getSimpleName();
    ArrayList<String> favoriteFoods;
    RecyclerView recycleList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_nutrition_diet_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext(), R.style.AppTheme_Diet);
        builder.setView(view);
        builder.setTitle("Diet Menu");

        favoriteFoods =
                ((NutritionFragment) getTargetFragment()).getNutritionModule().getFavoriteFoods();
        recycleList = (RecyclerView) view.findViewById(R.id.diet_recycler_view);
        NutritionRecycleAdapter adapter = new NutritionRecycleAdapter(favoriteFoods, "Favorite Foods");
        recycleList.setAdapter(adapter);
        recycleList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        EditText newFavoriteFood = (EditText) view.findViewById(R.id.new_food);
        newFavoriteFood.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        Button addFood = (Button) view.findViewById(R.id.add_food_button);
        addFood.setOnClickListener(v -> {
            // refresh the recycler view
            favoriteFoods.add(newFavoriteFood.getText().toString());
            recycleList.getAdapter().notifyDataSetChanged();
            ((NutritionFragment) getTargetFragment()).getNutritionModule().setFavoriteFoods(favoriteFoods);
            newFavoriteFood.setText("");
        });

        Button setReminders = (Button) view.findViewById(R.id.reminders);
        setReminders.setOnClickListener(v -> {
            DialogFragment newFragment = setRemindersDialog.newInstance();
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getFragmentManager(), "dialog");
        });

        builder.setPositiveButton("Save",
                (dialog, whichButton) -> {
                    if (!newFavoriteFood.getText().toString().equals("")){
                        favoriteFoods.add(newFavoriteFood.getText().toString());
                        recycleList.getAdapter().notifyDataSetChanged();
                        ((NutritionFragment) getTargetFragment()).getNutritionModule().setFavoriteFoods(favoriteFoods);
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

    public static class setRemindersDialog extends DialogFragment {

        public static DialogFragment newInstance() {
            return new setRemindersDialog();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.diet_reminders, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext(), R.style.AppTheme_Diet);
            builder.setView(dialogLayout);
            builder.setTitle("Set Reminder");

            EditText reminder = (EditText) dialogLayout.findViewById(R.id.reminder_note);
            reminder.setInputType(InputType.TYPE_CLASS_TEXT);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String text = reminder.getText().toString();
                NotificationCompat.Builder notify = new NotificationCompat.Builder(inflater.getContext());
                notify.setSmallIcon(R.drawable.ic_menu_nutrition);
                notify.setContentTitle("Diet Reminder");
                notify.setContentText("Don't forget about " + text + "!");
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(001, notify.build());
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // do nothing
            });

            return builder.create();
        }

    }
}
