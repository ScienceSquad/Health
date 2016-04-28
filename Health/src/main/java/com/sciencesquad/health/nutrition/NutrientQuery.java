package com.sciencesquad.health.nutrition;

import com.sciencesquad.health.core.util.DataGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andrew on 4/4/16.
 *
 * If you have no idea how to use this, read this, it might help:
 * https://ndb.nal.usda.gov/ndb/doc/index
 *
 * Be sure to look at the four different query types:
 * https://ndb.nal.usda.gov/ndb/doc/apilist/API-FOOD-REPORT.md
 * https://ndb.nal.usda.gov/ndb/doc/apilist/API-LIST.md
 * https://ndb.nal.usda.gov/ndb/doc/apilist/API-NUTRIENT-REPORT.md
 * https://ndb.nal.usda.gov/ndb/doc/apilist/API-SEARCH.md
 *
 * If after that you don't know what to do:
 * ¯\_(ツ)_/¯
 *
 */
public class NutrientQuery {

	private final String API_KEY = "9zvHjWhDmcJruBZm97qzkjx22tVU81d4j4fv5WPG";

	private final String FOOD_REPORT_URL = "http://api.nal.usda.gov/ndb/reports/";
	private final String LIST_URL = "http://api.nal.usda.gov/ndb/list/";
	private final String NUTRIENT_REPORT_URL = "http://api.nal.usda.gov/ndb/nutrients/";
	private final String SEARCH_URL = "http://api.nal.usda.gov/ndb/search/";


	static class NutrientIDs {
		public final String ENERGY_KCAL = "208";
		public final String CALORIES = ENERGY_KCAL;
		public final String ENERGY_KJ = "268";
		public final String WATER = "255";
		public final String PROTEIN = "203";
		public final String FAT = "204";
		public final String CARBOHYDRATES = "205";
		public final String SUGARS = "269";
		public final String CALCIUM = "301";
		public final String IRON = "303";
		public final String MAGNESIUM = "304";

		public final String ASH = "207";
		public final String FIBER = "291";
		public final String SUCROSE = "210";
		public final String GLUCOSE = "211";
		public final String FRUCTOSE = "212";
		public final String LACTOSE = "213";
		public final String MALTOSE = "214";
		public final String GALACTOSE = "287";

	}

	public enum QueryType {
		FOOD_REPORT,
		LIST,
		NUTRIENT_REPORT,
		SEARCH
	}

	public enum ReportType {
		BASIC,
		FULL,
		STATS
	}

	public enum ListType {
		FOOD,
		ALL_NUTRIENTS,
		SPECIALTY,
		STANDARD,
		GROUP
	}

	private QueryType queryType;
	private boolean xmlFormat;

	// Food report parameters
	// api_key
	private String ndbno;
	private ReportType reportType;
	// format

	// List URL parameters
	// api_key
	private ListType listType;
	private int max;
	private int offset;
	private boolean sort; // false = name
	// format

	// Nutrient report URL
	// api_key
	private ArrayList<String> fgList;
	// format
	// max
	// offset
	// ndbno
	private ArrayList<Integer> nutrients;
	// sort
	private boolean subset;

	// Search parameters
	// api_key
	private String q;
	private String fg;
	// sort
	// max
	// offset
	// format


	public NutrientQuery(QueryType queryType) {
		this.queryType = queryType;
		this.xmlFormat = false;
		this.nutrients = new ArrayList<Integer>();
		this.ndbno = null;
		this.reportType = ReportType.BASIC;
		this.listType = listType.FOOD;
		this.max = -1;
		this.offset = -1;
		this.sort = false;
		this.fgList = new ArrayList<String>();
		this.subset = false;
		this.q = "";
		this.fg = "";
	}

	public void setQueryType(QueryType queryType) { this.queryType = queryType; }
	public void setXmlFormat(boolean xmlFormat) { this.xmlFormat = xmlFormat; }
	public void addNutrient(int nutrient) {
		int index = this.nutrients.indexOf(nutrient);
		if (index < 0) {
			this.nutrients.add(nutrient);
		}
	}
	public void removeNutrient(int nutrient) {
		int index = this.nutrients.indexOf(nutrient);
		if (index > -1)
			this.nutrients.remove(index);
	}
	public void setNdbNumber(String ndbNumber) { this.ndbno = ndbNumber; }
	public void setReportType(ReportType reportType) { this.reportType = reportType; }
	public void setListType(ListType listType) { this.listType = listType; }
	public void setMaxResults(int max) { this.max = max; }
	public void setOffset(int offset) { this.offset = offset; }
	public void setSort(boolean sort) { this.sort = sort; }
	public void addFoodGroupID(String fg) {
		int index = this.fgList.indexOf(fg);
		if (index < 0)
			this.fgList.add(fg);
	}
	public void removeFoodGroupID(String fg) {
		int index = this.fgList.indexOf(fg);
		if (index > -1)
			this.fgList.add(fg);
	}
	public void setSubset(boolean subset) { this.subset = subset; }
	public void setSearchQuery(String q) { this.q = q; }
	public void setFoodGroupID(String fg) { this.fg = fg; }


	public NutrientQuery() {
		this(QueryType.SEARCH);
	}

	private String getFoodReportURL() {
		String urlString = FOOD_REPORT_URL;
		// TODO Add food report parameters
		// 		api_key (CHECK) - required
		// 		ndbno - required - no default
		if (this.ndbno != null) {
			urlString = DataGetter.addURLParameter(urlString, "ndbno", this.ndbno);
		}
		else {
			// TODO complain
		}

		// 		type - not required
		String typeString = "";
		switch (reportType) {
			case FULL:
				typeString = "f";
				break;
			case STATS:
				typeString = "s";
				break;
			case BASIC:
			default:
				typeString = "b";
				break;
		}
		urlString = DataGetter.addURLParameter(urlString, "type", typeString);
		// 		format (CHECK)
		return urlString;
	}

	private String getListURL() {
		String urlString = LIST_URL;
		// TODO Add list parameters
		// 		api_key (CHECK)
		// 		lt
		String typeString = "";
		switch (this.listType) {
			case ALL_NUTRIENTS:
				typeString = "n";
				break;
			case SPECIALTY:
				typeString = "ns";
				break;
			case STANDARD:
				typeString = "nr";
				break;
			case GROUP:
				typeString = "g";
				break;
			case FOOD:
			default:
				typeString = "f";
				break;
		}
		urlString = DataGetter.addURLParameter(urlString, "lt", typeString);

		// 		max
		if (this.max > -1) {
			urlString = DataGetter.addURLParameter(urlString, "max", String.valueOf(this.max));
		}

		// 		offset
		if (this.offset > -1) {
			urlString = DataGetter.addURLParameter(urlString, "offset", String.valueOf(this.offset));
		}

		// 		sort
		if (this.sort) {
			urlString = DataGetter.addURLParameter(urlString, "sort", "id");
		}

		// 		format (CHECK)
		return urlString;
	}

	private String getNutrientReportURL() {
		String urlString = NUTRIENT_REPORT_URL;
		// TODO Add nutrient report parameters
		// 		api_key (CHECK)
		// 		fg
		if (this.fg.length() > 0) {
			urlString = DataGetter.addURLParameter(urlString, "fg", this.fg);
		}

		// 		format (CHECK)
		// 		max
		if (this.max > 0) {
			urlString = DataGetter.addURLParameter(urlString, "max", String.valueOf(this.max));
		}

		// 		offset
		if (this.offset > 0) {
			urlString = DataGetter.addURLParameter(urlString, "offset", String.valueOf(this.offset));
		}

		// 		ndbno
		if (this.ndbno != null) {
			urlString = DataGetter.addURLParameter(urlString, "ndbno", this.ndbno);
		}
		// 		nutrients
		if (this.nutrients.size() > 0) {
			for (int i = 0; i < 20; i++) {
				urlString = DataGetter.addURLParameter(urlString, "nutrients",
						String.valueOf(this.nutrients.get(i)));
			}
		}
		else {
			// TODO complain
		}

		// 		sort
		if (this.sort) {
			urlString = DataGetter.addURLParameter(urlString, "sort", "c");
		}

		// 		subset
		if (this.subset) {
			urlString = DataGetter.addURLParameter(urlString, "subset", "1");
		}
		return urlString;
	}

	private String getSearchURL() {
		String urlString = SEARCH_URL;
		// TODO Add search parameters
		// 		api_key (CHECK)
		// 		q
		if (this.q.length() > 0) {
			urlString = DataGetter.addURLParameter(urlString, "q", this.q);
		}

		// 		fg
		if (this.fg.length() > 0) {
			urlString = DataGetter.addURLParameter(urlString, "fg", this.fg);
		}

		// 		sort
		if (this.sort) {
			urlString = DataGetter.addURLParameter(urlString, "sort", "n");
		}

		// 		max
		if (this.max > -1) {
			urlString = DataGetter.addURLParameter(urlString, "max", String.valueOf(this.max));
		}

		// 		offset
		if (this.offset > -1) {
			urlString = DataGetter.addURLParameter(urlString, "offset", String.valueOf(this.offset));
		}

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

	public JSONObject getJSONResults() {
		this.xmlFormat = false;
		JSONObject jsonObject = null;
		try {
			jsonObject = DataGetter.getJSON(this.getURLString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public JSONArray getResultsArray() {
		try {
			switch (queryType) {
				case FOOD_REPORT:
					return this.getJSONResults()
							.getJSONObject("report")
							.getJSONObject("food")
							.getJSONArray("nutrients");
				case SEARCH:
				case LIST:
					return this.getJSONResults()
							.getJSONObject("list")
							.getJSONArray("item");
				case NUTRIENT_REPORT:
					return this.getJSONResults()
							.getJSONObject("report")
							.getJSONArray("foods");
				default:
					return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray queryByNDBNo(String ndbno) {
		NutrientQuery query = new NutrientQuery(QueryType.FOOD_REPORT);
		query.setNdbNumber(ndbno);
		return query.getResultsArray();
	}

	public Document getXMLResults() {
		this.xmlFormat = true;
		Document xmlObject = null;

		try {
			xmlObject = DataGetter.getXML(this.getURLString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return xmlObject;
	}

}
