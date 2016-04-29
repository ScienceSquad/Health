package com.sciencesquad.health.core.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.Intent;
import com.sciencesquad.health.core.BaseApp;

import java.util.*;

public class AccountHelper {
	//public static final String ACCOUNT_TYPE_GITHUB = "com.github";
	public static final String ACCOUNT_TYPE_GOOGLE = "com.google";
	public static final String ACCOUNT_TYPE_TWITTER = "com.twitter.android.auth.login";
	public static final String ACCOUNT_TYPE_FACEBOOK = "com.facebook.auth.login";

	private static final AccountManager accountManager = AccountManager.get(BaseApp.app());

	private AccountHelper() {}

	public static Map<String, AuthenticatorDescription> getAuthenticators() {
		AuthenticatorDescription[] authenticators = accountManager.getAuthenticatorTypes();
		HashMap<String, AuthenticatorDescription> result = new HashMap<>();
		for (AuthenticatorDescription authenticator : authenticators)
			result.put(authenticator.type, authenticator);
		return result;
	}

	public static String[] accountTypes() {
		return new String[] {
				AccountHelper.ACCOUNT_TYPE_FACEBOOK,
				AccountHelper.ACCOUNT_TYPE_GOOGLE,
				AccountHelper.ACCOUNT_TYPE_TWITTER
		};
	}

	public static List<Account> getAccounts() {
		//Account[] github = accountManager.getAccountsByType(ACCOUNT_TYPE_GITHUB);
		Account[] google = accountManager.getAccountsByType(ACCOUNT_TYPE_GOOGLE);
		Account[] twitter = accountManager.getAccountsByType(ACCOUNT_TYPE_TWITTER);
		Account[] facebook = accountManager.getAccountsByType(ACCOUNT_TYPE_FACEBOOK);

		ArrayList<Account> result = new ArrayList<>();
		//Collections.addAll(result, github);
		Collections.addAll(result, google);
		Collections.addAll(result, twitter);
		Collections.addAll(result, facebook);
		return result;
	}

	public static Intent chooserIntent(String txt) {
		return AccountManager.newChooseAccountIntent(
				null, null, accountTypes(), false, txt, null, null, null);
	}

	public static void handleResult(int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			BaseApp.app().display("Logged in! " + accountName, false);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			BaseApp.app().display("No account selected", false);
		}
	}
}