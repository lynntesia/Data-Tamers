package io.analytics.site.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.analytics.aspect.HeaderFooter;
import io.analytics.aspect.SidePanel;
import io.analytics.domain.Account;
import io.analytics.domain.Dashboard;
import io.analytics.domain.User;
import io.analytics.domain.Widget;
import io.analytics.enums.HeaderType;
import io.analytics.forms.DashboardForm;
import io.analytics.service.interfaces.IAccountService;
import io.analytics.service.interfaces.IDashboardService;
import io.analytics.service.interfaces.ISessionService;
import io.analytics.service.interfaces.IUserService;
import io.analytics.service.interfaces.IWidgetService;
import io.analytics.site.models.DashboardModel;
import io.analytics.site.models.FilterModel;
import io.analytics.site.models.SessionModel;
import io.analytics.site.models.SessionModel.CorruptedSessionException;
import io.analytics.site.models.SettingsModel;
import io.analytics.site.models.SidePanelModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {
	
private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
	
	/* Services */
	@Autowired private IAccountService AccountService;
	@Autowired private ISessionService SessionService;
	@Autowired private IDashboardService DashboardService;
	@Autowired private IUserService UserService;
	@Autowired private IWidgetService WidgetService;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@HeaderFooter(HeaderType.APPLICATION)
	@SidePanel(animate = true)
	@RequestMapping(value = "/application", method = RequestMethod.GET)
	public ModelAndView getDashboardContainer(Locale locale, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response,
			   @RequestParam(value = "did", defaultValue = "-1") String dashboardId) {
		
		
		boolean success = SessionService.checkAuthorization(session);

		/* Getting the currrent settings. */
		SettingsModel settings = null;
		if (success) {
			//If authorization succeeded, the following must succeed.
			settings = (SettingsModel) session.getAttribute("settings");
			model.addAttribute("settings", settings);
		} else if (SessionService.redirectToLogin(session, request, response)) {
			return null;
		} else {
			return new ModelAndView("unavailable");
		}
		
		SessionModel sessionModel = new SessionModel(session);
		
		try {
			if (sessionModel.getUser() == null) {
				User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				sessionModel.setUser(user);
			}
			if (sessionModel.getAccount() == null) {
				List<Account> accounts = AccountService.getAccountsOwnedByUser(sessionModel.getUser().getId());
				if (accounts.isEmpty()) {
					SessionService.redirectToRegularLogin(session, "?errors=1&noaccount=1", response);
					return null;
				}
				//Get the first account owned by the user - this could change in the future.
				sessionModel.setAccount(accounts.get(0));
			}
			
		} catch (CorruptedSessionException e1) {
			e1.printStackTrace();
			SessionService.redirectToLogin(session, request, response);
		}
		
		try {
			//Get a list of dashboards and make a list of ids.
			List<Dashboard> dashboards = DashboardService.getAccountDashboards(sessionModel.getAccount().getId());
			ArrayList<Integer> dashboardIds = new ArrayList<Integer>(dashboards.size());
			for(Dashboard d : dashboards)
				dashboardIds.add(d.getId());
			
			
			if (dashboardIds.isEmpty()) {
				//TODO: Create a dashboard if there aren't any, or display a "no dashboards" screen. I would vote for the latter.
				System.err.println("No dashboards for this account.");
			} else {
				int dId = -1;
				try {
					dId = Integer.parseInt(dashboardId);
				} catch (NumberFormatException e) {
					System.err.println("Dashboard ID was not valid.");
				}
				//If the id provided is valid, pass this on - otherwise, pass on the default dashboard id.
				if (dashboardIds.contains(dId))
					model.addAttribute("dashboardId", dId);
				else
					model.addAttribute("dashboardId", dashboardIds.get(0));
			}
		} catch (CorruptedSessionException e1) {
			e1.printStackTrace();
			SessionService.redirectToLogin(session, request, response);
		}
		
		
		/* Getting the currrent filter. */
		
		FilterModel filter = null;
		try {
			filter = (FilterModel) session.getAttribute("filter");
		} catch (ClassCastException e) {
			logger.info("Corrupted session information. See below for more info.");
			logger.info(e.getMessage());
			return new ModelAndView("unavailable");
		}
		if (filter == null) {
			filter = new FilterModel();
			session.setAttribute("filter", filter);
		}
		
		model.addAttribute("filter", filter);
		
		
		//What is this?
		request.setAttribute("state", "application");

		SidePanelModel sidePanelModel = new SidePanelModel(this.DashboardService, sessionModel);
		try {
			sidePanelModel.generateDashboardInfo();
		} catch (CorruptedSessionException e) {
			SessionService.redirectToLogin(session, request, response);
		}

		
		Map<String, Object> Sidepanel = new HashMap<String, Object>();
		
		Sidepanel.put("path", "/WEB-INF/views/includes/sidepanel.jsp");
		Sidepanel.put("animate", false);
		Sidepanel.put("model", sidePanelModel);

		model.addAttribute("SIDEPANEL", Sidepanel);
		
		return new ModelAndView("application/dashboard");
	}
	
	
	
	@RequestMapping(value = "/addDashboard", method = RequestMethod.POST)
	public ModelAndView showAddDashboardForm() {
		return new ModelAndView("addDashboard");
	}
	
	
	
	@ModelAttribute("dashboardForm")
	public DashboardForm getDashboardForm() {
		return new DashboardForm();
	}
	
	
	
	/**
	 * Create a new dashboard
	 * 
	 * @param accountId - current account Id
	 * @param userId - current User Id
	 * @param name - Name given to the newly created dashboard
	 */
	@ResponseBody
	@RequestMapping(value = "/application/createDashboard", method = RequestMethod.POST)
	private String createDashboard(@ModelAttribute("dashboardForm")DashboardForm form,
			  					   BindingResult result,
			  					   @RequestParam(value = "name", defaultValue = "") String name) 
	{ 
		
		JSONObject dashboardData = new JSONObject();
		
		try {

			if (result.hasErrors()) {
				//TODO: Do something
			}
			
			//TODO: Change account number to active account number.
			int newDashboardId = this.DashboardService.addNewDashboard(1, null, name);
			String url = "application/" + newDashboardId;
			
			dashboardData.put("id", newDashboardId);
			dashboardData.put("name", name);
			dashboardData.put("url", url);

			
		} catch(Exception e) {
			logger.info("Could not add new dashboard");
			logger.info(e.getMessage());
		}
		
		return dashboardData.toString();
	}
	
	
	
	
	/**
	 * Delete a dashboard. Could delete any dashboard in account.
	 * 
	 * 
	 * @param userId - current User Id
	 * @param dashboardId - Id of dashboard to be deleted.
	 */
	@ResponseBody
	@RequestMapping(value = "/application/deleteDashboard", method = RequestMethod.POST)
	private String removeDashboard (@RequestParam("dashboardId") int dashboardId,
								  HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		
		SessionModel sessionModel = new SessionModel(session);
		
		JSONObject result = new JSONObject();
		try {
			User user = sessionModel.getUser();
			this.DashboardService.deleteDashboard(user, dashboardId);
			
			result.put("dashboardId", dashboardId);
			
		} catch (CorruptedSessionException e) {
			e.printStackTrace();
			SessionService.redirectToLogin(session, request, response);
		} catch (JSONException e) {
			logger.info("Could not delete dashboard");
			e.printStackTrace();
		}
		
		return result.toString();
	}
	
	
	//TODO: I'm not sure what this method is for
	/**
	 * Update Widget Position on the Dashboard.
	 * 
	 * @param accountId - current account Id
	 * @param userId - current User Id
	 * @param dashboardId - current dashboard Id
	 */
	/*
	@RequestMapping(value = "/application/layout", method = RequestMethod.POST)
	private void updateWidgetPosition() 
	{
		try {
			// TODO: Need some data type representing Widget positions
			User user = this.UserService.getUserById(userId);
			this.DashboardService.updateDashboardWidgetLayout(user, dashboardId);
			
		} catch(Exception e) {
			logger.info("Could not update Widget Position");
			logger.info(e.getMessage());
		}
	}
	*/

	@RequestMapping(value = "/application/dashboards/{dashboardId}", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView getDashboard(@PathVariable String dashboardId, Model model) {
		int id;
		try {
			id = Integer.parseInt(dashboardId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return new ModelAndView("serialize");
		}
		//If the id does not match a valid dashboard, this list will be empty, but not null.
		List<Widget> widgets = WidgetService.getDashboardWidgets(id);
		if (widgets == null)
			return new ModelAndView("serialize");
		
		DashboardModel d = new DashboardModel(id);
		d.setWidgets(widgets);
		
		model.addAttribute("model", d);
		return new ModelAndView("serialize");
	}
	

}
