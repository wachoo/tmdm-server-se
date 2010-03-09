package com.amalto.core.migration.tasks;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class TiscallRenameToCallJobTask extends AbstractMigrationTask {

	public TiscallRenameToCallJobTask() {
		super();
	}
	
	protected Boolean execute() {
		if(MDMConfiguration.isExistDb()){
		String xquery = "update replace /transformer-v2-pOJO//plugin-jNDI[text()='amalto/local/transformer/plugin/tisCall'] with <plugin-jNDI>amalto/local/transformer/plugin/callJob</plugin-jNDI>";
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
