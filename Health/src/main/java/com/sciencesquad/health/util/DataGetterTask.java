package com.sciencesquad.health.util;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andrew on 4/7/16.
 */
public class DataGetterTask extends AsyncTask<String, Void, String> {
	private IOException io_e = null;

	protected String doInBackground(String... urls) {
		try {
			return DataGetter.getString(urls[0]);
		} catch (IOException e) {
			io_e = e;
		}
		return "";
	}

	protected void onPostExecute(String resultString) {
		if (this.io_e != null)
			this.io_e.printStackTrace();
	}
}
