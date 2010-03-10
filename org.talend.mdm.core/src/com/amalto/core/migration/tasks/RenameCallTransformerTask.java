package com.amalto.core.migration.tasks;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class RenameCallTransformerTask extends AbstractMigrationTask {

	public RenameCallTransformerTask() {
		super();
	}
	
	protected Boolean execute() {
		if(MDMConfiguration.isExistDb()){
		
		StringBuffer sb= new StringBuffer();	
			sb.append("update replace /routing-rule-pOJO//service-jNDI[text()='amalto/local/service/calltransformer'] with <service-jNDI>amalto/local/service/callprocess</service-jNDI>, "); 
			sb.append("for $routingRule in /routing-rule-pOJO "); 
			sb.append("let $param:=$routingRule//parameters "); 
			sb.append("let $newparam:=(replace($param, \"transformer\\s*=\", \"process=\")) "); 
			sb.append("return update replace $routingRule//parameters with "); 
			sb.append("<parameters>{$newparam}</parameters> ");
			
		String xquery = sb.toString();
			try {
				Util.getXmlServerCtrlLocal().runQuery(null, "", xquery, null);
			} catch (XtentisException e) {
				org.apache.log4j.Logger.getLogger(this.getClass()).error(
						e.getMessage());
				return false;
			}
		}
		return true;
	}

}
