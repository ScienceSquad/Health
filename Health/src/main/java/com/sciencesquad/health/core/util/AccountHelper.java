package com.sciencesquad.health.core.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;

import java.util.*;

public class AccountHelper {
	//public static final String ACCOUNT_TYPE_GITHUB = "com.github";
	public static final String ACCOUNT_TYPE_TWITTER = "com.twitter.android.auth.login";
	public static final String ACCOUNT_TYPE_FACEBOOK = "com.facebook.auth.login";

	final AccountManager accountManager;
	public AccountHelper(Context context) {
		accountManager = AccountManager.get(context);
	}

	public Map<String, AuthenticatorDescription> getAuthenticators() {
		AuthenticatorDescription[] authenticators = accountManager.getAuthenticatorTypes();
		HashMap<String, AuthenticatorDescription> result = new HashMap<>();
		for (AuthenticatorDescription authenticator : authenticators)
			result.put(authenticator.type, authenticator);
		return result;
	}

	public List<Account> getAccounts() {
		//Account[] github = accountManager.getAccountsByType(ACCOUNT_TYPE_GITHUB);
		Account[] twitter = accountManager.getAccountsByType(ACCOUNT_TYPE_TWITTER);
		Account[] facebook = accountManager.getAccountsByType(ACCOUNT_TYPE_FACEBOOK);

		ArrayList<Account> result = new ArrayList<>();
		//Collections.addAll(result, github);
		Collections.addAll(result, twitter);
		Collections.addAll(result, facebook);
		return result;
	}
}