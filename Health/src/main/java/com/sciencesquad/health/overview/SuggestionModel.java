package com.sciencesquad.health.overview;

import io.realm.RealmObject;

/**
 * Created by andrew on 4/28/16.
 */
public class SuggestionModel extends RealmObject {
	private String suggestionText;
	private String module;

	public void setSuggestionText(String suggestionText) {
		this.suggestionText = suggestionText;
	}

	public String getSuggestionText() {
		return this.suggestionText;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModule() {
		return this.module;
	}
}