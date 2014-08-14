package com.amalto.core.migration.tasks;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;

public class RemoveOldMigrationClusterTask extends AbstractMigrationTask{

	@Override
	protected Boolean execute() {
		
		
		try {
			
			ConfigurationHelper.removeCluster(null, "MIGRATION");
			
			
		} catch (Exception e) {
			String err = "Unable to Remove Old Migration Cluster";
			org.apache.log4j.Logger.getLogger(RemoveOldMigrationClusterTask.class).error(err, e);
			return false;
		}
		
		return true;
	}

}
