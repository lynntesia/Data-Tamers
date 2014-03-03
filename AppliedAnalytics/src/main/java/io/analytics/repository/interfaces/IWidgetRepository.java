package io.analytics.repository.interfaces;

import java.util.List;

import io.analytics.domain.Widget;

public interface IWidgetRepository {

	/**
	 * Adds a new Widget to the database, returning the id of the newly added Widget.
	 * @param accountId
	 * @param defaultFilterId
	 * @param dashboardName
	 * @return The id of the new Dashboard.
	 */
	public int addNewWidget(int defaultFilterId, int widgetTypeId, int dashboardId, int priority);

	/**
	 * Adds a new Widget to the database, returning the id of the newly added Widget.
	 * @param accountId
	 * @param defaultFilterId
	 * @param dashboardName
	 * @return The id of the new Dashboard.
	 */
	public int addNewWidget(Widget w);
	
	/**
	 * Updates a widget in the database to match a Widget model.
	 * 
	 * @param w The Widget to update. The getId() property should be set to a valid id.
	 * @return false if there was no matching widget ID or if the widget failed to update.
	 */
	public boolean updateWidget(Widget w);
	
	/**
	 * Removes a Widget from the database.
	 * @param dashboardId
	 */
	public void deleteWidget(int widgetId);

	/**
	 * Retrieves all the Widgets that are listed under a particular Dashboard ID.
	 * @param accountId
	 * @return
	 */
	public List<Widget> getDashboardWidgets(int dashboardId);
	
}
