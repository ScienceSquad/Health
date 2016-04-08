package com.sciencesquad.health.nutrition;

import io.realm.RealmObject;

/**
 * Created by andrew on 4/7/16.
 */
public class ConditionModel extends RealmObject {

	private String name;
	private String restrictedFoods;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setRestrictedFoods(String restrictedFoods) { this.restrictedFoods = restrictedFoods; }

	public String getRestrictedFoods() { return this.restrictedFoods; }
}
