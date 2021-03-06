package io.analytics.service.interfaces;

import io.analytics.domain.WidgetLibrary;
import io.analytics.domain.WidgetLibraryType;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface IWidgetLibrariesService {

	/**
	 * Gets all the types of Widgets in a particular WidgetLibrary.
	 * 
	 * @param widgetLibraryId
	 * @return A list of WidgetLibraryTypes.
	 */
	public List<WidgetLibraryType> getTypesInLibrary(int widgetLibraryId) throws DataAccessException;
	
	/**
	 * Gets all the WidgetLibraries available.
	 * 
	 * @return A list of WidgetLibraries.
	 */
	public List<WidgetLibrary> getWidgetLibraries() throws DataAccessException;
	
	/**
	 * Gets a particular WidgetLibrary given its id.
	 * 
	 * @param widgetLibraryId
	 * @return A matching WidgetLibrary, if it exists, or null if no matching library could be found.
	 */
	public WidgetLibrary getWidgetLibrary(int widgetLibraryId) throws DataAccessException;
	
	/**
	 * Adds a WidgetLibraryType to a WidgetLibrary.
	 * 
	 * @param widgetLibraryType
	 * @param widgetLibraryId
	 * @return true if the widget type was added, false if the widget type does not exist.
	 */
	public boolean addWidgetTypeToLibrary(WidgetLibraryType widgetLibraryType, int widgetLibraryId) throws DataAccessException;
	
	/**
	 * Adds a new WidgetLibrary
	 * @param widgetLibrary
	 */
	public void addWidgetLibrary(WidgetLibrary widgetLibrary) throws DataAccessException;
}
