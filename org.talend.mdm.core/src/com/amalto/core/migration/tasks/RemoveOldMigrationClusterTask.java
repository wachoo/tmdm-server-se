package com.amalto.core.migration.tasks;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.ConfigurationHelper;

public class RemoveOldMigrationClusterTask extends AbstractMigrationTask{

	@Override
	protected Boolean execute() {
		
		
		try {
			
			ConfigurationHelper.removeCluster("MIGRATION");
			
			
		} catch (Exception e) {
			String err = "Unable to Remove Old Migration Cluster";
			org.apache.log4j.Logger.getLogger(RemoveOldMigrationClusterTask.class).error(err, e);
			return false;
		}
		
		return true;
	}

}
