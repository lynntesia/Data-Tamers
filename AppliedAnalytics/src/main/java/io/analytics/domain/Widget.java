package io.analytics.domain;

import com.google.gson.Gson;

import io.analytics.site.models.JSONSerializable;

/**
 * Represents a single instance of a WidgetType that belongs to a Dashboard and can
 * be shown to the user.
 * 
 * @author Dave
 *
 */
public class Widget implements JSONSerializable {

	private int id;
	private int defaultFilterId;
	private int widgetTypeId;
	private int dashboardId;
	private int priority;
	
	public Widget(int id) {
		this.id = id;
	}
	public int getId() {
		return this.id;
	}
	public int getDefaultFilterId() {
		return defaultFilterId;
	}
	public void setDefaultFilterId(int defaultFilterId) {
		this.defaultFilterId = defaultFilterId;
	}
	public int getWidgetTypeId() {
		return widgetTypeId;
	}
	public void setWidgetTypeId(int widgetTypeId) {
		this.widgetTypeId = widgetTypeId;
	}
	public int getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(int dashboardId) {
		this.dashboardId = dashboardId;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public String getJSONSerialization() {
		Gson g = new Gson();
		return g.toJson(this);
	}
	
	
	
	
}
