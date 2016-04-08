package com.sciencesquad.health.nutrition;

import android.util.Log;
import android.util.Pair;

import com.sciencesquad.health.core.BaseApp;
import com.sciencesquad.health.core.Module;
import com.sciencesquad.health.core.RealmContext;

import java.util.ArrayList;
import java.util.Arrays;

import io.realm.RealmResults;

/**
 * Created by andrew on 4/7/16.
 */
public class ConditionModule extends Module {
	private static final String TAG = ConditionModule.class.getSimpleName();

	private RealmContext<ConditionModel> conditionRealm;

	private String name;
	private String restrictedFoods = "";

	/**
	 * Constructs the module itself.
	 * It also sets up a Realm Context for the Module.
	 */

	public ConditionModule() {
		Log.d(TAG, "Constructing Prescription Module");
		this.conditionRealm = new RealmContext<>();
		this.conditionRealm.init(BaseApp.app(), ConditionModel.class, "condition.realm");
	}

	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	public void setRestrictedFoods(String restrictedFoods) { this.restrictedFoods = restrictedFoods; }
	public String getRestrictedFoods() { return this.restrictedFoods; }

	public void addRestrictedFood(String restrictedFood) {
		// Convert String to ArrayList<String>
		ArrayList<String> stringArrayList = new ArrayList<>(Arrays.asList(this.restrictedFoods.split("\\s*,\\s*")));
		stringArrayList.add(restrictedFood);
		this.restrictedFoods = android.text.TextUtils.join(", ", stringArrayList);
	}

	public void removeRestrictedFood(String restrictedFood) {
		// Convert String[] to ArrayList<String>
		ArrayList<String> stringArrayList = new ArrayList<>(Arrays.asList(this.restrictedFoods.split("\\s*,\\s*")));
		if (stringArrayList.indexOf(restrictedFood) > -1) {
			stringArrayList.remove(restrictedFood);
		}
		this.restrictedFoods = android.text.TextUtils.join(", ", stringArrayList);
	}

	public void addCondition() {
		ConditionModel conditionModel = new ConditionModel();
		conditionModel.setName(this.name);
		conditionModel.setRestrictedFoods(this.restrictedFoods);
		conditionRealm.add(conditionModel);
	}

	public RealmResults<ConditionModel> getConditions() {
		return conditionRealm.query().findAll();
	}

	public void clearAllConditions() {
		conditionRealm.clear();
	}


	@Override
	public Pair<String, Integer> identifier() {
		return null;
	}

	@Override
	public void init() {

	}
}