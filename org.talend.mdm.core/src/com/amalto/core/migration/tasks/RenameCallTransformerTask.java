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
		String xquery = "update replace /routing-rule-pOJO//service-jNDI[text()='amalto/local/service/calltransformer'] with <service-jNDI>amalto/local/service/callprocess</service-jNDI>";
			try {
				Util.getXmlServerCtrlLocal().runQuery(null, "", xquery, null);
			} catch (XtentisException e) {
				// TODO Auto-generated catch block
				org.apache.log4j.Logger.getLogger(this.getClass()).error(
						e.getMessage());
				return false;
			}
		}
		return true;
	}

}
