package com.sciencesquad.health.util;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by andrew on 3/31/16.
 */
public class DataGetter {


	private static void printToLog(String toPrint) {
		Log.d("DataGetter, parseJSON", toPrint);
	}
	/**
	 * Parser functions
	 * Take in strings of data and output parsed representations
	 */

	public static JSONObject parseJSON(String data) throws JSONException {
		return new JSONObject(data);
	}

	public static void parseXML(String data) {
		try {
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();

			factory.setNamespaceAware(true); // Don't know what this does.

			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader(data));

			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
					Log.d("DataGetter, parseXML:", "Start document");
				} else if (eventType == XmlPullParser.START_TAG) {
					Log.d("DataGetter, parseXML:", "Start tag " + xpp.getName());
				} else if (eventType == XmlPullParser.END_TAG) {
					Log.d("DataGetter, parseXML:", "End tag " + xpp.getName());
				} else if (eventType == XmlPullParser.TEXT) {
					Log.d("DataGetter, parseXML:", "Text " + xpp.getText());
				}
				eventType = xpp.next();
			}
			Log.d("DataGetter, parseXML:", "End document");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter functions
	 * If necessary, call the parser functions to get parsed versions of data
	 */

	public static String getString(String urlString) throws MalformedURLException, IOException {
		URL url;

		String resultString = "";

		url = new URL(urlString);
		Scanner s = new Scanner(url.openStream());
		while (s.hasNextLine()) {
			resultString += s.nextLine() + "\n";
		}

		return resultString;
	}

	public static JSONObject getJSON(String urlString) throws JSONException, MalformedURLException, IOException {
		return parseJSON(getString(urlString));
	}

	public static void getXML(String urlString) throws MalformedURLException, IOException, XmlPullParserException {
		parseXML(getString(urlString));
	}
}
