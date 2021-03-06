package io.analytics.service;

import java.io.IOException;
import java.util.HashMap;

import io.analytics.repository.ManagementRepository.CredentialException;
import io.analytics.service.interfaces.IManagementService;
import io.analytics.service.interfaces.ISessionService;
import io.analytics.site.models.FilterModel;
import io.analytics.site.models.SessionModel;
import io.analytics.site.models.SettingsModel;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextFactoryBean;

import com.google.api.client.auth.oauth2.Credential;

@Service
public class SessionService implements ISessionService {

	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
	
	@Autowired private IManagementService ManagementService;
	private ServletContext servletContext;
	

	/**
	 * Checks to see if the user's current session is authorized.
	 * Also double-checks to make sure the user's settings are loaded into the session and
	 * loads them in if they are missing.
	 * If there is a problem, it returns false. Otherwise if the user is authorized, returns true.
	 * @param session
	 * @return
	 */
	public boolean checkAuthorization(HttpSession session) {
		Credential credentials = null;
		SettingsModel settings = null;
		try {
			 credentials = (Credential) session.getAttribute("credentials");
			 settings = (SettingsModel) session.getAttribute("settings");
		} catch (ClassCastException e) {
			//These attributes weren't null, but they could not be cast to their appropriate types.
			logger.info("Corrupted session information. See below for more info.");
			logger.info(e.getMessage());
			return false;
		}
		if (credentials == null) {
			return false;
		} else if (credentials.getAccessToken() == null) {
			return false;
		}
		else if (settings == null) {
			settings = new SettingsModel(this, ManagementService, session);
			session.setAttribute("settings", settings);
			
		}

		//Try get filter.
		FilterModel filter = null;
		try {
			 filter = (FilterModel) session.getAttribute("filter");
			if (filter == null) {
				filter = new FilterModel(settings.getActiveProfile());
				session.setAttribute("filter", filter);
			}
		} catch (ClassCastException e) {
			filter = new FilterModel(settings.getActiveProfile());
			session.setAttribute("filter", filter);
		}
		
		return true;
		
	}

	public SettingsModel getUserSettings(HttpSession session) {
		return (SettingsModel) session.getAttribute("settings");
	}
	
	public void saveUserSettings(HttpSession session, SettingsModel settings) {
		session.setAttribute("settings", settings);
	}

	public FilterModel getFilter(HttpSession session) {
		return (FilterModel) session.getAttribute("filter");
	}
	
	public void saveFilter(HttpSession session, FilterModel filter) {
		session.setAttribute("filter", filter);
	}

	public Credential getCredentials(HttpSession session) {
		return (Credential) session.getAttribute("credentials");
	}
	
	public SessionModel getSessionModel(HttpSession session) {
		return new SessionModel(session);
	}
	
	public HashMap<String, Object> getModels(HttpSession session) {
		HashMap<String, Object> models;
		try {
			models = (HashMap<String, Object>) session.getAttribute("models");
			if (models == null) {
				models = new HashMap<String, Object>();
				session.setAttribute("models", models);
			}
		} catch (ClassCastException e) {
			models = new HashMap<String, Object>();
			session.setAttribute("models", models);
		}
		return models;
	}
	
	/**
	 * Safely gets a model from the session if it is available.
	 * 
	 * @param session
	 * @param s
	 * @param c
	 * @return
	 */
	public <T> T getModel(HttpSession session, String s, Class<T> c) {
		T model;
		try {
			model = (T) getModels(session).get(s);
		} catch (ClassCastException e) {
			return null;
		}
		return model;
	}
	
	public void saveModel(HttpSession session, String s, Object model) {
		HashMap<String, Object> models = getModels(session);
		models.put(s, model);
		saveModels(session, models);
	}
	
	private static void saveModels(HttpSession session, HashMap<String, Object> models) {
		session.setAttribute("models", models);
	}
	
	/**
	 * This will redirect the user to the Google Analytics login page, keeping track of the URL the user
	 * attempted to access, as indicated in the HttpServletRequest. WARNING: This will not,
	 * however, keep track of any query parameters in that URL.
	 * 
	 */

	public boolean redirectToLogin(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String contextPath = session.getServletContext().getContextPath();
		try {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED );
			response.sendRedirect(contextPath + "/galogin" + "?destinationURL=" + request.getRequestURL());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean redirectToRegularLogin(HttpSession session, String queryString, HttpServletResponse response) {
		String contextPath = session.getServletContext().getContextPath();
		try {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED );
			response.sendRedirect(contextPath + "/login" + queryString);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}



}
