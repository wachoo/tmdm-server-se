package com.amalto.core.util;

import java.util.Map;

import com.amalto.xmlserver.interfaces.WhereCondition;

public abstract class WhereConditionFilter {
	
	protected Map context;
	
	public WhereConditionFilter(Map context) {
		this.context=context;
	}
	
	public abstract void doFilter(WhereCondition ws);

}
