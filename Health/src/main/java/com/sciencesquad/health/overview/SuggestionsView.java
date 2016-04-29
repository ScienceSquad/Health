package com.sciencesquad.health.overview;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.HostActivity;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.nutrition.DatabaseFragment;
import com.sciencesquad.health.nutrition.NutritionFragment;
import com.sciencesquad.health.prescriptions.PrescriptionFragment;
import com.sciencesquad.health.prescriptions.PrescriptionModel;
import com.sciencesquad.health.run.RunFragment;
import com.sciencesquad.health.sleep.SleepFragment;
import com.sciencesquad.health.steps.StepsFragment;
import com.sciencesquad.health.workout.WorkoutFragment;

import io.realm.RealmResults;

/**
 * Created by andrew on 4/28/16.
 */
public class SuggestionsView extends LinearLayout {

	SuggestionModule suggestionModule;
	RecyclerView suggestionsList;
	Context context;

	OnSuggestionSelectedListener onSuggestionSelectedListener = (item) -> {};

	public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

		private RealmResults<SuggestionModel> mSuggestions;

		public class ViewHolder extends RecyclerView.ViewHolder {

			public LinearLayout mLinearLayout;

			public ViewHolder(LinearLayout v) {
				super(v);
				mLinearLayout = v;
			}

			public void setContent(SuggestionModel item) {
				TextView suggestionTextView = (TextView) mLinearLayout.findViewById(R.id.suggestion_text);
				TextView moduleTextView = (TextView) mLinearLayout.findViewById(R.id.module_text);
				ImageView imageView = (ImageView) mLinearLayout.findViewById(R.id.remove_suggestion);
				suggestionTextView.setText(item.getSuggestionText());
				moduleTextView.setText(item.getModule());
				mLinearLayout.setOnClickListener((v) -> onSuggestionSelectedListener.onItemSelected(item));
				imageView.setOnClickListener((v) -> {
					suggestionModule.removeSuggestion(item);
					updateSuggestionList();
				});
			}
		}

		public SuggestionsAdapter(RealmResults<SuggestionModel> myDataset) { mSuggestions = myDataset; }

		@Override
		public SuggestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout v = (LinearLayout) inflater.inflate(R.layout.suggestions_list_item, parent, false);

			ViewHolder vh = new ViewHolder(v);
			return vh;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.setContent(mSuggestions.get(position));
		}

		@Override
		public int getItemCount() { return mSuggestions.size(); }
	}

	public interface OnSuggestionSelectedListener {
		void onItemSelected(SuggestionModel item);
	}

	public void setOnItemSelectedListener(OnSuggestionSelectedListener listener) {
		this.onSuggestionSelectedListener = listener;
	}

	public SuggestionsView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx, attrs);
	}

	private void init(Context ctx, AttributeSet attrs) {
		context = ctx;

		suggestionModule = Module.of(SuggestionModule.class);

		suggestionsList = new RecyclerView(ctx);
		RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
		suggestionsList.setLayoutParams(lp);

		suggestionsList.setLayoutManager(new LinearLayoutManager(ctx));

		addView(suggestionsList);

		RealmResults<SuggestionModel> suggestions = suggestionModule.getAllSuggestions();
		if (suggestions.size() == 0) {
			suggestionModule.setSuggestionText("Do more exercises");
			suggestionModule.setModule(SuggestionModule.AssociatedModule.WORKOUT);
			suggestionModule.addSuggestion();
			suggestionModule.setSuggestionText("Stop eating bad food");
			suggestionModule.setModule(SuggestionModule.AssociatedModule.NUTRITION);
			suggestionModule.addSuggestion();
		}

		updateSuggestionList();
	}

	public void updateSuggestionList() {
		RealmResults<SuggestionModel> suggestions = suggestionModule.getAllSuggestions();

		SuggestionsAdapter adapter = new SuggestionsAdapter(suggestions);
		suggestionsList.setAdapter(adapter);
	}
}
