package com.amalto.core.migration;

import org.w3c.dom.Document;

import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;


public abstract class AbstractDataModelMigrationTask extends AbstractMigrationTask{
    
    protected final String cluster = "amaltoOBJECTSDataModel";//$NON-NLS-1$
    
    
    /**
     * DOC HSHU Comment method "getDataModel".
     */
    protected abstract String getDataModel();
    
    protected Boolean execute() {
        
        
        try {
            String dataModelXml = ConfigurationHelper.getServer().getDocumentAsString(null, cluster, getDataModel());
            Document doc = Util.parse(dataModelXml);
            updateSchema(doc);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected abstract void updateSchema(Document doc) throws Exception;

}
