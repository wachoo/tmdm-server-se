package com.amalto.core.migration.tasks;

import org.talend.mdm.commmon.util.core.ICoreConstants;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.util.Util;

public class CreateClustersForCrossreferencingTask extends AbstractMigrationTask{

	@Override
	protected Boolean execute() {
		
		org.apache.log4j.Logger.getLogger(CreateClustersForCrossreferencingTask.class).debug("init() Cross-Referencing - checking cluster and data model");
    	//create the cluster and data model  if they do not exist
		try {
			DataClusterCtrlLocal local=com.amalto.core.util.Util.getDataClusterCtrlLocal();
			if (local.existsDataCluster(new DataClusterPOJOPK(ICoreConstants.CrossReferencing_datacluster)) ==null) {
				local.putDataCluster(new DataClusterPOJO(ICoreConstants.CrossReferencing_datacluster, "MDM Cross Referencing Data",""));
			}
			DataModelCtrlLocal datamodelLocal=Util.getDataModelCtrlLocal();
			if (datamodelLocal.existsDataModel(new DataModelPOJOPK(ICoreConstants.CrossReferencing_datamodel))==null) {
				datamodelLocal.putDataModel(
						new DataModelPOJO(ICoreConstants.CrossReferencing_datamodel, "MDM Cross Referencing Table Definitions",""));
			}
		} catch (Exception e) {
			String err = "Unable to initialize the crossreferencing data cluster and data model.";
			org.apache.log4j.Logger.getLogger(CreateClustersForCrossreferencingTask.class).error(err, e);
			return false;
		}
		
		return true;
	}

}
