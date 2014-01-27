package io.analytics.site.controllers;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.analytics.service.CoreReportingService;
import io.analytics.service.ICoreReportingService;
import io.analytics.service.ISessionService;
import io.analytics.service.SessionService;
import io.analytics.site.models.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.auth.oauth2.Credential;

@Controller
public class WidgetController {
	
	@Autowired private ICoreReportingService CoreReportingService;
	@Autowired private ISessionService SessionService;
	
	@RequestMapping(value = "/HypotheticalFuture", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView hypotheticalFutureView(Model viewMap, HttpServletRequest request, HttpServletResponse response, HttpSession session,	
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
			SessionService.redirectToLogin(session, request, response);
			return new ModelAndView("unavailable");
		}
		if (settings.getActiveProfile() == null) {
			
			//TODO: Make an informative view for when widgets don't have an active profile to get data from.
			return new ModelAndView("unavailable");
		}
		
		
		
		HypotheticalFutureModel hypotheticalFuture = SessionService.getModel(session, "hypotheticalFuture", HypotheticalFutureModel.class);
		
		//If there is no model available, or if the active profile changed, create a new model.
		if ((hypotheticalFuture == null) || !(settings.getActiveProfile().equals(hypotheticalFuture.getActiveProfile()))) {
			//CoreReportingService reportingService = null;
			/*try {
				// this is naughty. We need to be using the interface
				//reportingService = new CoreReportingService(credential, settings.getActiveProfile().getId());
			} catch (io.analytics.repository.CoreReportingRepository.CredentialException e) {
				e.printStackTrace();
				SessionService.redirectToLogin(session, response);
				return new ModelAndView("unavailable");
			}*/
			//hypotheticalFuture = new HypotheticalFutureModel(reportingService);
			hypotheticalFuture = new HypotheticalFutureModel(this.SessionService, this.CoreReportingService);
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
		
		//Save the model to the session.
		SessionService.saveModel(session, "hypotheticalFuture", hypotheticalFuture);
		viewMap.addAttribute("hfModel", hypotheticalFuture);
		viewMap.addAttribute("filterModel", filter);
		return new ModelAndView("HypotheticalFuture");

	}
	
	
	
	@RequestMapping(value = "/RevenueSources", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView revenueSourcesView(Model viewMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		Credential credential;
		SettingsModel settings;
		FilterModel filter;
		if (SessionService.checkAuthorization(session)) {
			credential = SessionService.getCredentials();
			filter = SessionService.getFilter();
			settings = SessionService.getUserSettings();
		} else {
			SessionService.redirectToLogin(session, request, response);
			return new ModelAndView("unavailable");
		}
		
		if (settings.getActiveProfile() == null) {
			//TODO: Make an informative view for when widgets don't have an active profile to get data from.
			return new ModelAndView ("unavailable");
		}
		
		return new ModelAndView("RevenueSources");
	}
	

	@RequestMapping(value = "/WebsitePerformance", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView websitePerformanceView(Model viewMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		Credential credential;
		SettingsModel settings;
		FilterModel filter;
		if (SessionService.checkAuthorization(session)) {
			credential = SessionService.getCredentials();
			filter = SessionService.getFilter();
			settings = SessionService.getUserSettings();
		} else {
			SessionService.redirectToLogin(session, request, response);
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
			/*CoreReportingService reportingService = null;
			try {
				//Get the current active profile from the settings.
				reportingService = new CoreReportingService(credential, settings.getActiveProfile().getId());
			} catch (io.analytics.repository.CoreReportingRepository.CredentialException e) {
				e.printStackTrace();
				SessionService.redirectToLogin(session, response);
				return new ModelAndView("unavailable");
			}*/
			//webPerform = new WebsitePerformanceModel(reportingService);
			webPerform = new WebsitePerformanceModel(this.SessionService, this.CoreReportingService);
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
