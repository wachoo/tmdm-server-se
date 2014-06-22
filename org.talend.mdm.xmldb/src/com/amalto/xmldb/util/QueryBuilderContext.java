package com.amalto.xmldb.util;

public class QueryBuilderContext {
	
	private int start;
	private long limit;
	
	public QueryBuilderContext() {
		
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

}
