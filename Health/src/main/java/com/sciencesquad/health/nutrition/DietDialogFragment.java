package com.sciencesquad.health.nutrition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
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
    ArrayList<String> favoriteFoods;
    RecyclerView recycleList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Diet);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_nutrition_diet_dialog, null);
        builder.setView(view);
        builder.setTitle("Diet Menu");

        favoriteFoods =
                ((NutritionFragment) getTargetFragment()).getNutritionModule().getFavoriteFoods();
        recycleList = (RecyclerView) view.findViewById(R.id.diet_recycler_view);
        NutritionRecycleAdapter adapter = new NutritionRecycleAdapter(favoriteFoods);
        recycleList.setAdapter(adapter);
        recycleList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        EditText newFavoriteFood = (EditText) view.findViewById(R.id.new_food);
        newFavoriteFood.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        Button addFood = (Button) view.findViewById(R.id.add_food_button);
        addFood.setOnClickListener(v -> {
            // refresh the recyclerview
            favoriteFoods.add(newFavoriteFood.getText().toString());
            recycleList.getAdapter().notifyDataSetChanged();
            ((NutritionFragment) getTargetFragment()).getNutritionModule().setFavoriteFoods(favoriteFoods);
        });

        builder.setPositiveButton("Save",
                (dialog, whichButton) -> {

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