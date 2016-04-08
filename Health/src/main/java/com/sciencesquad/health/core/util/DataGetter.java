package com.sciencesquad.health.core.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andrew on 3/31/16.
 */
public class DataGetter {

	/**
	 * Parser functions
	 * Take in strings of data and output parsed representations
	 */

	public static JSONObject parseJSON(String data) throws JSONException {
		return new JSONObject(data);
	}

	public static Document parseXML(String data) throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(data)));

		/*XmlPullParserFactory factory;
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
		Log.d("DataGetter, parseXML:", "End document"); */
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

	public static Document getXML(String urlString) throws IOException, ParserConfigurationException, SAXException, JSONException {
		return parseXML(getString(urlString));
	}

	public static String addURLParameter(String url, String key, String value) {
		if (url.indexOf("?") >= 0) {
			url += "&";
		}
		else {
			url += "?";
		}
		url += key + "=" + value;
		return url;
	}
}
