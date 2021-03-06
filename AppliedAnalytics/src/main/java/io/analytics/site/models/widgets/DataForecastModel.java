package io.analytics.site.models.widgets;

import io.analytics.service.interfaces.ICoreReportingService;
import io.analytics.service.interfaces.ISessionService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.json.*;


public class DataForecastModel extends ForecastWidgetModel implements Serializable {
	
	private JSONObject dataPoints;
	private Integer changePercentage;
	private String dimension;
	private ArrayList<Integer> changePercentageOptions; //Using ArrayList for future flexibility if necessary.
	private String activeProfile;
	
	private final String widgetClass = "dataForecast";
	private final String widgetTitle = "dataforecast.title";
	
	public DataForecastModel() {
		super();
	}
	
	/*
	 * For use in JSP file:
	 * 
	 * var historicalData = JSON.parse('${ hfModel.yValues() }');
	 * var forecastData = JSON.parse('${ hfModel.yValuesForecast() }');
	 * var hypotheticalData = JSON.parse('${ hfModel.???????? }');
	 * 
	 */
	public DataForecastModel(ISessionService sessionService, ICoreReportingService reportingService, HttpSession session) {	
		super(sessionService, reportingService, session);
		this.activeProfile = sessionService.getUserSettings(session).getActiveProfile().getId();
		this.changePercentage = 10;
		this.dataPoints = new JSONObject();
		Integer[] percentageOptions = { 5, 10, 15, 20, 25, 30 };
		changePercentageOptions = new ArrayList<Integer>(Arrays.asList(percentageOptions));
	}
	
	public String getActiveProfile() {
		return this.activeProfile;
	}
	
	
	public ArrayList<Integer> getChangePercentageOptions() {
		return changePercentageOptions;
	}
	
	@Override
	public String getName() {
		return "Hypothetical Future";
	}

	@Override
	public String getDescription() {
		return "View a forecast of your data based on historical data.";
	}
	
	@Override
	public String getHTMLClass() {
		return this.widgetClass;
	}
	
	@Override
	public String getTitle() {
		return this.widgetTitle;
	}
	
	public Integer getChangePercentage() {
		return this.changePercentage;
	}

	public void setChangePercentage(Integer changePercentage) {
		this.changePercentage = changePercentage;
	}
	
	public boolean setChangePercentage(String changePercentage) {
		try {
			this.changePercentage = Integer.parseInt(changePercentage);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public String getDimension() {
		//TODO: Add a check for valid dimensions.
		return this.dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	
	
	
	
	/* test method to pass data to javascript */
	public JSONObject getDataPoints() {
		 return this.dataPoints;
	}

}
