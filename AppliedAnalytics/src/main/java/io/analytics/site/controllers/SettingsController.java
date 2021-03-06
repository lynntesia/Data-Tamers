package io.analytics.site.controllers;

import io.analytics.domain.User;
import io.analytics.service.SessionService;
import io.analytics.service.interfaces.ISessionService;
import io.analytics.site.models.FilterModel;
import io.analytics.site.models.SessionModel;
import io.analytics.site.models.SettingsModel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.services.analytics.model.Profile;
import com.google.api.services.analytics.model.Profiles;

@Controller
public class SettingsController {

	private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

	@Autowired private ISessionService SessionService;
	
	/*
	 * SettingsView
	 */
	@RequestMapping(value = "/settings", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView settingsView(Model viewMap,  HttpServletRequest request, HttpServletResponse response, HttpSession session, 
			@RequestParam(value = "account", defaultValue = "none") String accountId,
			@RequestParam(value = "property", defaultValue = "none") String propertyId,
			@RequestParam(value = "profile", defaultValue = "none") String profileId,
			@RequestParam(value = "update", defaultValue = "") String update) 
					throws IOException {

		SettingsModel settings;
		FilterModel filter;
		try {
			
			if (!SessionService.checkAuthorization(session))
				throw new Exception("Check Authorization failed!");
			settings = SessionService.getUserSettings(session);
			filter = SessionService.getFilter(session);
			response.setContentType("text/html");

			//Only one change should be made/possible at a time.
			if (!accountId.equals("none"))
				settings.setAccountSelection(accountId);
			else if (!propertyId.equals("none"))
				settings.setPropertySelection(propertyId);
			else if (!profileId.equals("none"))
				settings.setProfileSelection(profileId);
			else if (!update.equals("")) {
				filter.setActiveProfile(settings.getActiveProfile());
				if (settings.setActiveProfile())
					viewMap.addAttribute("update", "Success!"); 
				else
					viewMap.addAttribute("update", "Failed to update."); 
			}
			
			//TODO: Change the above to be an update state within the model instead.
			SessionService.saveUserSettings(session, settings);
			User user = SessionService.getSessionModel(session).getUser();
			viewMap.addAttribute("user", user);
			viewMap.addAttribute("settings", settings);
		}
		catch(Exception e) {
			logger.info(e.getMessage());
			SessionService.redirectToLogin(session, request, response);
			return new ModelAndView("unavailable");
		}
		return new ModelAndView("settings");

	}

	/*
	 * FilterView 
	 */
	//For some reason, this wasn't picking up on just "/filter" anymore, and I had to add /application/filter.
	@RequestMapping(value = {"/filter", "/application/filter"} , method = {RequestMethod.POST, RequestMethod.GET})
	public void filterView(Model viewMap, HttpServletRequest request,  HttpServletResponse response, HttpSession session, 
			@RequestParam(value = "startDate") String startDate,
			@RequestParam(value = "endDate") String endDate) {

		FilterModel filter = null;
		if (SessionService.checkAuthorization(session)) {
			filter = SessionService.getFilter(session);
		} else {
			SessionService.redirectToLogin(session, request, response);
			//return new ModelAndView("unavailable");
		}
		//If a request for updating dates was made, update the dates in the FilterModel.
		if (startDate != null && endDate != null) {
			//Make a formatter to translate the date strings in the view to Date objects.
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			formatter.setTimeZone(TimeZone.getTimeZone(filter.getActiveProfile().getTimezone()));
			Date start = null;
			Date end = null;
			try {
				start = formatter.parse(startDate);
				end = formatter.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
				//return new ModelAndView("unavailable"); // Do nothing if you can't parse.
			}
			//Google only returns values for days which we have encompassed fully in our range.
			long MS_IN_DAY = 1000 * 60 * 60 * 24;
			filter.setActiveStartDate(new Date(MS_IN_DAY + start.getTime()));
			filter.setActiveEndDate(new Date(MS_IN_DAY + end.getTime()));
		}
		SessionService.saveFilter(session, filter);
		//return new ModelAndView("unavailable");
	}
	
	@RequestMapping(value = {"/settings/profiles"} , method = {RequestMethod.GET})
	public ModelAndView filterProfileView(Model viewMap, HttpServletRequest request,  HttpServletResponse response, HttpSession session) {

		SettingsModel settings = null;
		if (SessionService.checkAuthorization(session)) {
			settings = SessionService.getUserSettings(session);
		} else {
			SessionService.redirectToLogin(session, request, response);
			return new ModelAndView("unavailable");
		}
		
		Profiles profiles = settings.getCurrentProfiles();
		
		JSONArray data = new JSONArray();
		for(Profile p : profiles.getItems()) {
			JSONObject item = new JSONObject();
			try {
				item.put("name", p.getName());
				item.put("id", p.getId());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data.put(item);
		}
		viewMap.addAttribute("data", data.toString());
		
		return new ModelAndView("plaintext");
	}
	
}
