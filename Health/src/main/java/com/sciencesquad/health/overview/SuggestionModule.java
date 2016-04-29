package com.sciencesquad.health.overview;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.assist.AssistContent;
import android.util.Log;

import com.sciencesquad.health.R;
import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;
import com.sciencesquad.health.core.util.Dispatcher;
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
public class SuggestionModule extends Module {
	private static final String TAG = SuggestionModule.class.getSimpleName();

	private RealmContext<SuggestionModel> suggestionRealm;

	public enum AssociatedModule {
		NUTRITION,
		OVERVIEW,
		PRESCRIPTIONS,
		RUN,
		SLEEP,
		STEPS,
		WORKOUT
	}

	private String suggestionText;
	private AssociatedModule module;

	public SuggestionModule() {
		Log.d(TAG, "Initiating Suggestion Module");
		suggestionRealm = new RealmContext<>();
		suggestionRealm.init(BaseApp.app(), SuggestionModel.class, "suggestion.realm");
	}

	public void setSuggestionText(String suggestionText) {
		this.suggestionText = suggestionText;
	}

	public void setSuggestionText(SuggestionModel suggestion, String suggestionText) {
		suggestionRealm.getRealm().beginTransaction();
		suggestion.setSuggestionText(suggestionText);
		suggestionRealm.getRealm().commitTransaction();
	}

	public String getSuggestionText() { return this.suggestionText; }

	public String moduleToString(AssociatedModule module) {
		return module.toString();
	}

	public AssociatedModule stringToModule(String module) {
		return AssociatedModule.valueOf(module);
	}

	public AssociatedModule getModule() { return this.module; }

	public void setModule(AssociatedModule module) {
		this.module = module;
	}

	public void setModule(SuggestionModel suggestion, AssociatedModule module) {
		suggestionRealm.getRealm().beginTransaction();
		suggestion.setModule(moduleToString(module));
		suggestionRealm.getRealm().commitTransaction();
	}

	public SuggestionModel addSuggestion() {
		SuggestionModel suggestionModel = new SuggestionModel();
		suggestionModel.setSuggestionText(this.suggestionText);
		suggestionModel.setModule(moduleToString(this.module));
		suggestionRealm.add(suggestionModel);
		return suggestionModel;
	}

	public void removeSuggestion(SuggestionModel item) {
		suggestionRealm.getRealm().beginTransaction();
		item.removeFromRealm();
		suggestionRealm.getRealm().commitTransaction();
	}

	public void removeAllSuggestions() {
		suggestionRealm.getRealm().beginTransaction();
		suggestionRealm.clear();
		suggestionRealm.getRealm().commitTransaction();
	}

	public void openAssociatedModule(Fragment fragment, String module) {
		openAssociatedModule(fragment, stringToModule(module));
	}

	public void openAssociatedModule(Fragment fragment, AssociatedModule module) {
		FragmentTransaction transaction;
		switch (module) {
			case NUTRITION:
				transaction = fragment.getFragmentManager().beginTransaction();
				new NutritionFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case OVERVIEW:
				transaction = fragment.getFragmentManager().beginTransaction();
				new OverviewFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case PRESCRIPTIONS:
				transaction = fragment.getFragmentManager().beginTransaction();
				new PrescriptionFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case RUN:
				transaction = fragment.getFragmentManager().beginTransaction();
				new RunFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case SLEEP:
				transaction = fragment.getFragmentManager().beginTransaction();
				new SleepFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case STEPS:
				transaction = fragment.getFragmentManager().beginTransaction();
				new StepsFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			case WORKOUT:
				transaction = fragment.getFragmentManager().beginTransaction();
				new WorkoutFragment().open(transaction, R.id.drawer_layout).commit();
				return;
			default:
		}
	}

	public RealmResults<SuggestionModel> getAllSuggestions() {
		return suggestionRealm.query(SuggestionModel.class).findAll();
	}

	@Override
	public void onStart() {
		Log.v(TAG, "Initiating Realm: Suggestions");
	}

	@Override
	public void onStop() {

	}
}
