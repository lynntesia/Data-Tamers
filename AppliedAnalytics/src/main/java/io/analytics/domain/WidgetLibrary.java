package io.analytics.domain;

import com.google.gson.Gson;

import io.analytics.site.models.JSONSerializable;

/**
 * Represents a library of WidgetTypes.
 * 
 * @author Dave Wong
 *
 */
public class WidgetLibrary implements JSONSerializable {
	
	private int id;
	private String name;
	private String description;
	
	public WidgetLibrary(int id) {
		this.id = id;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getJSONSerialization() {
		Gson g = new Gson();
		return g.toJson(this);
	}
	
}
