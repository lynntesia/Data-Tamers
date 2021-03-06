package io.analytics.repository.interfaces;

import io.analytics.domain.GoogleUserData;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;

public interface IManagementRepository {

	/**
	 * Gets information about a Google user.
	 * 
	 * @return
	 */
	public GoogleUserData getGoogleUserData(Credential credential);

	/**
	 * Gets a list of accounts and relevant data.
	 * 
	 * @return
	 */
	public Accounts getAccounts(Credential credential);
	
	public Webproperties getWebproperties(Account a, Credential credential);
	
	public Profiles getProfiles(Account a, Webproperty w, Credential credential);

	/**
	 * Gets a list of web properties for an account.
	 * 
	 * @param a
	 * @return
	 */
	public Webproperties getWebproperties(String a, Credential credential);
	
	public Profiles getProfiles(String a, String w, Credential credential);
}
