package com.sciencesquad.health.nutrition;

import com.sciencesquad.health.util.DataGetter;

import java.util.ArrayList;

/**
 * Created by andrew on 4/4/16.
 */
public class NutrientQuery {

	private final String API_KEY = "9zvHjWhDmcJruBZm97qzkjx22tVU81d4j4fv5WPG";

	private final String FOOD_REPORT_URL = "http://api.nal.usda.gov/ndb/reports/";
	private final String LIST_URL = "http://api.nal.usda.gov/ndb/list/";
	private final String NUTRIENT_REPORT_URL = "http://api.nal.usda.gov/ndb/nutrients/";
	private final String SEARCH_URL = "http://api.nal.usda.gov/ndb/search/";

	public enum QueryType {
		FOOD_REPORT,
		LIST,
		NUTRIENT_REPORT,
		SEARCH
	}

	private QueryType queryType;
	private boolean xmlFormat;
	private int ndbno;

	private ArrayList<Integer> nutrients;

	public NutrientQuery(QueryType queryType) {
		this.queryType = queryType;
		this.xmlFormat = false;
		this.nutrients = new ArrayList<Integer>();
	}

	public NutrientQuery() {
		this(QueryType.SEARCH);
	}

	private String getFoodReportURL() {
		String urlString = FOOD_REPORT_URL;
		// TODO Add food report parameters
		// 		api_key (CHECK)
		// 		ndbno
		// 		type
		// 		format (CHECK)
		return urlString;
	}

	private String getListURL() {
		String urlString = LIST_URL;
		// TODO Add list parameters
		// 		api_key (CHECK)
		// 		lt
		// 		max
		// 		offset
		// 		sort
		// 		format (CHECK)
		return urlString;
	}

	private String getNutrientReportURL() {
		String urlString = NUTRIENT_REPORT_URL;
		// TODO Add nutrient report parameters
		// 		api_key (CHECK)
		// 		fg
		// 		format (CHECK)
		// 		max
		// 		offset
		// 		ndbno
		// 		nutrients
		// 		sort
		// 		subset
		return urlString;
	}

	private String getSearchURL() {
		String urlString = SEARCH_URL;
		// TODO Add search parameters
		// 		api_key (CHECK)
		// 		q
		// 		fg
		// 		sort
		// 		max
		// 		offset
		// 		format (CHECK)
		return urlString;
	}

	public String getURLString() {
		String urlString;
		switch (queryType) {
			case FOOD_REPORT:
				urlString = this.getFoodReportURL();
				break;
			case LIST:
				urlString = this.getListURL();
				break;
			case NUTRIENT_REPORT:
				urlString = this.getNutrientReportURL();
				break;
			case SEARCH:
			default:
				urlString = this.getSearchURL();
				break;
		}

		if (this.xmlFormat) urlString = DataGetter.addURLParameter(urlString, "format", "xml");
		else urlString = DataGetter.addURLParameter(urlString, "format", "json");

		urlString = DataGetter.addURLParameter(urlString, "api_key", API_KEY);

		return urlString;
	}

}
