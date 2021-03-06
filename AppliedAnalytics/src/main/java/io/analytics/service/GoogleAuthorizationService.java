package io.analytics.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * A service for obtaining Google credentials from Google (not the database), which are necessary to make
 * most Analytics calls.
 * 
 * @author Dave Wong
 *
 */
@Service
public class GoogleAuthorizationService implements ServletContextAware {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static String clientSecretLocation;
	private static final String defaultClientSecretLocation = "/resources/client_secrets.json";
	private static GoogleClientSecrets clientSecrets = null;
	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext sc) {
		this.servletContext = sc;
		String realPath = servletContext.getRealPath(defaultClientSecretLocation);
		clientSecretLocation = realPath;
		
	}
	
	public void setClientSecretLocation(String location) {
		clientSecretLocation =servletContext.getRealPath(location);
	}
	/**
	 * Requests a new Access token using the refresh token and packages it into a 
	 * Credential object. Note that even if the refresh token is invalid, this
	 * will still return a Credential object - but there won't be any access token set.
	 * 
	 * @param refreshToken The Google OAuth refresh token.
	 * @return A Credential containing the access token if the refresh token was valid,
	 * otherwise a Credential with a null access token, but the refresh token set.
	 */
	public Credential getAccountCredentials(String refreshToken) {
		Credential credential = createCredentialWithRefreshToken(HTTP_TRANSPORT, JSON_FACTORY,
				new TokenResponse().setRefreshToken(refreshToken));
		try {
			credential.refreshToken();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return credential;
	}

	private static Credential createCredentialWithRefreshToken(HttpTransport transport, JsonFactory jsonFactory,
			TokenResponse tokenResponse) {

		GoogleClientSecrets clientSecrets = loadClientSecrets();
		return new GoogleCredential.Builder().setTransport(transport).setJsonFactory(jsonFactory)
				.setClientSecrets(clientSecrets).build().setFromTokenResponse(tokenResponse);
	}

	/**
	 * Helper to load client ID/Secret from file.
	 */
	private static GoogleClientSecrets loadClientSecrets() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(clientSecretLocation));
			clientSecrets = GoogleClientSecrets.load(new JacksonFactory(), reader);
			return clientSecrets;
		} catch (Exception e) {
			System.err.println("Could not load client_secrets.json");
			e.printStackTrace();
		}
		return clientSecrets;
	}
}
