package io.analytics.site.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.analytics.domain.CoreReportingData;
import io.analytics.repository.ManagementRepository.CredentialException;
import io.analytics.service.CoreReportingService;
import io.analytics.service.ManagementService;
import io.analytics.service.SessionService;
import io.analytics.site.models.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;

@Controller
public class WidgetController {
	
	
	@RequestMapping(value = "/HypotheticalFuture", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView hypotheticalFutureView(Model viewMap, HttpServletResponse response, HttpSession session,	
												@RequestParam(value = "change", defaultValue = "none") String changePercentage,
												@RequestParam(value = "dimension", defaultValue = "none") String dimension) {

		
		Credential credential;
		SettingsModel settings;
		FilterModel filter;
		if (SessionService.checkAuthorization(session)) {
			credential = SessionService.getCredentials();
			filter = SessionService.getFilter();
			settings = SessionService.getUserSettings();
		} else {
			SessionService.redirectToLogin(session, response);
			return new ModelAndView("unavailable");
		}
		if (settings.getActiveProfile() == null) {
			
			//TODO: Make an informative view for when widgets don't have an active profile to get data from.
			return new ModelAndView("unavailable");
		}
		
		
		
		HypotheticalFutureModel hypotheticalFuture = SessionService.getModel(session, "hypotheticalFuture", HypotheticalFutureModel.class);
		
		//If there is no model available, or if the active profile changed, create a new model.
		if ((hypotheticalFuture == null) || !(settings.getActiveProfile().equals(hypotheticalFuture.getActiveProfile()))) {
			CoreReportingService reportingService = null;
			try {
				reportingService = new CoreReportingService(credential, settings.getActiveProfile().getId());
			} catch (io.analytics.repository.CoreReportingRepository.CredentialException e) {
				e.printStackTrace();
				SessionService.redirectToLogin(session, response);
				return new ModelAndView("unavailable");
			}
			hypotheticalFuture = new HypotheticalFutureModel(reportingService);
		}
		
		if (filter != null) {
			hypotheticalFuture.setStartDate(filter.getActiveStartDate());
			hypotheticalFuture.setEndDate(filter.getActiveEndDate());
		}
		
		
		//Execute API commands to change the model
		if (!changePercentage.equals("none"))
			hypotheticalFuture.setChangePercentage(changePercentage);
		if (!dimension.equals("none"))
			hypotheticalFuture.setDimension(dimension);
		hypotheticalFuture.updateData();
		SessionService.saveModel(session, "hypotheticalFuture", hypotheticalFuture);
		viewMap.addAttribute("hfModel", hypotheticalFuture);
		viewMap.addAttribute("filterModel", filter);
		/*
		HypotheticalFutureModel hypotheticalFuture = new HypotheticalFutureModel(adjustBy, source);
		
		viewMap.addAttribute("hfModel", hypotheticalFuture);
		viewMap.addAttribute("changeOptions", hypotheticalFuture.getChangePercentOptions());
		viewMap.addAttribute("DATA", hypotheticalFuture.getVisualization());
		 */
		return new ModelAndView("HypotheticalFuture");

	}
	
	
	
	@RequestMapping(value = "/RevenueSources", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView revenueSourcesView(Model viewMap, HttpServletResponse response, HttpSession session) {
		
		Credential credential;
		SettingsModel settings;
		FilterModel filter;
		if (SessionService.checkAuthorization(session)) {
			credential = SessionService.getCredentials();
			filter = SessionService.getFilter();
			settings = SessionService.getUserSettings();
		} else {
			SessionService.redirectToLogin(session, response);
			return new ModelAndView("unavailable");
		}
		
		if (settings.getActiveProfile() == null) {
			//TODO: Make an informative view for when widgets don't have an active profile to get data from.
			return new ModelAndView ("unavailable");
		}
		
		return new ModelAndView("RevenueSources");
	}
	

	@RequestMapping(value = "/WebsitePerformance", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView websitePerformanceView(Model viewMap, HttpServletResponse response, HttpSession session) {
		
		Credential credential;
		SettingsModel settings;
		FilterModel filter;
		if (SessionService.checkAuthorization(session)) {
			credential = SessionService.getCredentials();
			filter = SessionService.getFilter();
			settings = SessionService.getUserSettings();
		} else {
			SessionService.redirectToLogin(session, response);
			return new ModelAndView("unavailable");
		}
		
		if (settings.getActiveProfile() == null) {
			//TODO: Make an informative view for when widgets don't have an active profile to get data from.
			return new ModelAndView ("unavailable");
		}
		
		
		/*
		 * Setting up the model.
		 */
		
		WebsitePerformanceModel webPerform = SessionService.getModel(session, "webPerform", WebsitePerformanceModel.class);
		
		//If there is no model available, or if the active profile changed, create a new model.
		if ((webPerform == null) || !(settings.getActiveProfile().equals(webPerform.getActiveProfile()))) {
			CoreReportingService reportingService = null;
			try {
				//Get the current active profile from the settings.
				reportingService = new CoreReportingService(credential, settings.getActiveProfile().getId());
			} catch (io.analytics.repository.CoreReportingRepository.CredentialException e) {
				e.printStackTrace();
				SessionService.redirectToLogin(session, response);
				return new ModelAndView("unavailable");
			}
			webPerform = new WebsitePerformanceModel(reportingService);
		}
		
		if (filter != null) {
			webPerform.setStartDate(filter.getActiveStartDate());
			webPerform.setEndDate(filter.getActiveEndDate());
		}
		
		/*
		 * Here's where we start making queries.
		 */
		
		
		
		/*
		 * Save the updated model to the session and send it to the view.
		 */
		
		SessionService.saveModel(session, "webPerform", webPerform);
		viewMap.addAttribute("webPerform", webPerform);
		viewMap.addAttribute("filterModel", filter); //Maybe we can eliminate this in the future.
		
		return new ModelAndView("WebsitePerformance");
	}

}
