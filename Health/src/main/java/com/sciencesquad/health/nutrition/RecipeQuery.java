package com.sciencesquad.health.nutrition;

import com.sciencesquad.health.util.DataGetter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andrew on 4/3/16.
 */
public class RecipeQuery {

	private final String API_URL = "http://www.recipepuppy.com/api/";

	private String ingredients;
	private String name;
	private int page;
	private boolean xmlFormat;

	public RecipeQuery() {
		this.ingredients = null;
		this.name = null;
		this.page = 0;
		this.xmlFormat = false;
	}

	public String getURLString() {
		String urlString = this.API_URL;
		if (this.ingredients != null) {
			urlString = DataGetter.addURLParameter(urlString, "i", this.ingredients);
		}
		if (this.name != null) {
			urlString = DataGetter.addURLParameter(urlString, "q", this.name);
		}
		if (this.page > 0) {
			urlString = DataGetter.addURLParameter(urlString, "p", String.valueOf(this.page));
		}
		if (this.xmlFormat) {
			urlString = DataGetter.addURLParameter(urlString, "format", "xml");
		}
		return urlString;
	}

	/**
	 * Gets recipes by ingredients
	 * Comma-separated
	 */
	public RecipeQuery setIngredients(String ingredients) {
		this.ingredients = ingredients;
		return this;
	}

	/**
	 * Pass in ingredients as ArrayList
	 * Converts into listString and passes to other function
	 */
	public RecipeQuery setIngredients(ArrayList<String> ingredients) {
		String listString = "";

		boolean comma = false;

		for (String i : ingredients) {
			if (comma) listString += ",";
			else comma = true;
			listString += i;
		}

		return this.setIngredients(listString);
	}

	public RecipeQuery setName(String name) {
		this.name = name;
		return this;
	}

	public RecipeQuery setPage(int page) {
		this.page = page;
		return this;
	}

	public Document getXMLResults() {
		this.xmlFormat = true;
		try {
			return DataGetter.getXML(this.getURLString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getJSONResults() {
		this.xmlFormat = false;
		try {
			return DataGetter.getJSON(this.getURLString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
