package com.amalto.core.migration.tasks;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import org.talend.mdm.server.api.DataCluster;
import org.talend.mdm.server.api.DataModel;
import com.amalto.core.util.Util;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.ICoreConstants;

public class CreateClustersForCrossreferencingTask extends AbstractMigrationTask {

    public static final Logger LOGGER = Logger.getLogger(CreateClustersForCrossreferencingTask.class);

    @Override
    protected Boolean execute() {
        LOGGER.debug("init() Cross-Referencing - checking cluster and data model");
        //create the cluster and data model  if they do not exist
        try {
            DataCluster local = com.amalto.core.util.Util.getDataClusterCtrlLocal();
            if (local.existsDataCluster(new DataClusterPOJOPK(ICoreConstants.CrossReferencing_datacluster)) == null) {
                local.putDataCluster(new DataClusterPOJO(ICoreConstants.CrossReferencing_datacluster, "MDM Cross Referencing Data", ""));
            }
            DataModel datamodelLocal = Util.getDataModelCtrlLocal();
            if (datamodelLocal.existsDataModel(new DataModelPOJOPK(ICoreConstants.CrossReferencing_datamodel)) == null) {
                datamodelLocal.putDataModel(
                        new DataModelPOJO(ICoreConstants.CrossReferencing_datamodel, "MDM Cross Referencing Table Definitions", ""));
            }
        } catch (Exception e) {
            String err = "Unable to initialize the crossreferencing data cluster and data model.";
            LOGGER.error(err, e);
            return false;
        }
        return true;
    }

}
