package io.analytics.domain;

import java.util.Calendar;

/**
 * Represents an Account with our application.
 * An Account is independent of a Google Account or a Google Analytics Account,
 * this is specifically an Account with our application. An Account has few properties
 * on its own, and is mostly defined by the relationships it has with other entities.
 * 
 * @author Dave Wong
 *
 */
public class Account {

	//TODO: Ensure that the database stores an int32
	private int id;
	private int ownerId;
	private int defaultFilterId;
	private Calendar creationDate;
	
	// list of dashboards associated with the account
	private Dashboard[] dashboardList; 
	
	public Account(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
	public int getDefaultFilterId() {
		return defaultFilterId;
	}
	
	public void setDefaultFilterId(int defaultFilterId) {
		this.defaultFilterId = defaultFilterId;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
	
	public void setDashboardList(Dashboard[] dashboards) {
		this.dashboardList = dashboards;
	}
	
	public Dashboard[] getDashboardList() {
		return this.dashboardList;
	}
	
}
