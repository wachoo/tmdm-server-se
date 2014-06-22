package com.amalto.core.util;

import java.util.Map;

import com.amalto.xmlserver.interfaces.WhereCondition;

public class WhereConditionForcePivotFilter extends WhereConditionFilter{
	
	
	public static final String FORCE_PIVOT="FORCE_PIVOT";

	public WhereConditionForcePivotFilter(Map context) {
		super(context);
	}

	@Override
	public void doFilter(WhereCondition wc) {
		if(super.context.get(FORCE_PIVOT)==null)return;
		if(wc.getLeftPath()==null||wc.getLeftPath().length()==0)return;
		
		String forcePivot= (String) super.context.get(FORCE_PIVOT);
		String leftPath = wc.getLeftPath();
		
		if(!forcePivot.equals("/")&&!forcePivot.equals("//")){
			if(forcePivot.startsWith("/"))forcePivot=forcePivot.substring(1);
			if(forcePivot.startsWith("/"))forcePivot=forcePivot.substring(1);
		}
		
		if(!leftPath.startsWith(forcePivot)){
			//it is partial
			leftPath=forcePivot+"/"+leftPath;
			wc.setLeftPath(leftPath);
		}
		
	}

}
