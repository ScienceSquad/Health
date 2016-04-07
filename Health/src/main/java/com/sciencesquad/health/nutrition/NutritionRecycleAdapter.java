package com.sciencesquad.health.nutrition;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sciencesquad.health.R;

import java.util.List;


public class NutritionRecycleAdapter extends RecyclerView.Adapter<NutritionRecycleAdapter.NutritionViewHolder> {

    private List<String> nutritionLog;

    public NutritionRecycleAdapter (List<String> nutritionLog) {
        this.nutritionLog = nutritionLog;
    }


    @Override
    public int getItemCount() {
        return nutritionLog.size();
    }

    @Override
    public void onBindViewHolder(NutritionViewHolder nutritionViewHolder, int i) {
        String logEntry = nutritionLog.get(i);
        nutritionViewHolder.nutritionLog.setText(logEntry);
    }

    @Override
    public NutritionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View logView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.nutrition_log_card_layout, viewGroup, false);

        return new NutritionViewHolder(logView);
    }

    public static class NutritionViewHolder extends RecyclerView.ViewHolder {
        protected TextView nutritionLog;
        public NutritionViewHolder(View v) {
            super(v);
            nutritionLog = (TextView) v.findViewById(R.id.log_text);

        }
    }
}
