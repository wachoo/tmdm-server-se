package com.amalto.webapp.v3.hierarchical.bean;

public class Filters {
	
	public Filters() {
		
	}
	
	
	public Filters(FilterItem[] filterItems) {
		super();
		this.filterItems = filterItems;
	}

	private FilterItem[] filterItems;

	public FilterItem[] getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(FilterItem[] filterItems) {
		this.filterItems = filterItems;
	}

}
