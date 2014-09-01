package com.amalto.core.migration.tasks;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.ConfigurationHelper;
import com.amalto.core.objects.universe.ejb.UniversePOJO;

public class CreateUniverseClusterTask extends AbstractMigrationTask{

	@Override
	protected Boolean execute() {
		
		//Create all clusters in case - these are not called statically in the classes any more
		org.apache.log4j.Logger.getLogger(CreateUniverseClusterTask.class).info("Creating Universe cluster");
		try {
			
			ConfigurationHelper.createCluster(null, UniversePOJO.class);
			
			
		} catch (Exception e) {
			String err = "Unable to Create Universe Cluster.";
			org.apache.log4j.Logger.getLogger(CreateUniverseClusterTask.class).error(err, e);
			return false;
		}
		org.apache.log4j.Logger.getLogger(CreateUniverseClusterTask.class).info("Done Creating Universe");
		
		return true;
	}

}
