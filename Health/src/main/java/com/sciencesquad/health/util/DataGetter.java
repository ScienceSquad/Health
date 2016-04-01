package com.sciencesquad.health.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by andrew on 3/31/16.
 */
public class DataGetter {

	static String getData(String urlString) {
		URL url;

		String resultString = "";

		try {
			url = new URL(urlString);
			Scanner s = new Scanner(url.openStream());
			while (s.hasNextLine()) {
				resultString += s.nextLine() + "\n";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return resultString;
	}
}
