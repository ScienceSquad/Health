package com.sciencesquad.health.nutrition;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.EventBus;

import java.util.ArrayList;
import java.util.List;



public class NutritionRecycleAdapter extends RecyclerView.Adapter<NutritionRecycleAdapter.NutritionViewHolder> {

    public static final String TAG = NutritionRecycleAdapter.class.getSimpleName();

    private String name;
    private List<String> nutritionLog;
    public NutritionRecycleAdapter (List<String> nutritionLog, String name) {
        this.nutritionLog = nutritionLog != null ? nutritionLog : new ArrayList<>();
        this.name = name != null ? name : "";
    }


    @Override
    public int getItemCount() {
        return nutritionLog.size();
    }

    @Override
    public void onBindViewHolder(NutritionViewHolder nutritionViewHolder, int i) {
        String logEntry = nutritionLog.get(i);
        nutritionViewHolder.nutritionLogView.setText(logEntry);
    }

    @Override
    public NutritionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View logView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.nutrition_log_card_layout, viewGroup, false);

        return new NutritionViewHolder(logView);
    }

    public class NutritionViewHolder extends RecyclerView.ViewHolder {
        protected TextView nutritionLogView;
        public NutritionViewHolder(View v)   {
            super(v);
            nutritionLogView = (TextView) v.findViewById(R.id.log_text);
            nutritionLogView.setOnLongClickListener(v1 -> {
                remove(getAdapterPosition());
                return true;
            });
        }
    }

    public void remove(int position){
        String removedItem = nutritionLog.remove(position);
        notifyItemRemoved(position);
        BaseApp.app().eventBus().publish("DataUpdateEvent", this,
                new EventBus.Entry(name, removedItem));

    }

}
